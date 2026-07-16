package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.exception.AsignacionInvalidaException;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Entrega;
import com.logiroute.logiroute.model.EstadoPedido;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.model.Vehiculo;
import com.logiroute.logiroute.repository.EntregaRepository;
import com.logiroute.logiroute.repository.PedidoRepository;
import com.logiroute.logiroute.repository.RepartidorRepository;
import com.logiroute.logiroute.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AsignacionService implements IAsignacionService {

    private static final Logger log = LoggerFactory.getLogger(AsignacionService.class);

    private static final Set<EstadoPedido> ESTADOS_ACTIVOS = EnumSet.of(
            EstadoPedido.ASIGNADO,
            EstadoPedido.EN_RECOJO,
            EstadoPedido.EN_TRANSITO
    );

    private final PedidoRepository pedidoRepository;
    private final RepartidorRepository repartidorRepository;
    private final EntregaRepository entregaRepository;
    private final VehiculoRepository vehiculoRepository;

    @Override
    @Transactional
    public void asignar(AsignacionDTO dto) {
        log.info("Asignando repartidor {} al pedido {}", dto.getRepartidorId(), dto.getPedidoId());

        Pedido pedido = obtenerPedido(dto.getPedidoId());
        Repartidor repartidor = repartidorRepository.findById(dto.getRepartidorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Repartidor", dto.getRepartidorId()));

        validarPedidoReasignable(pedido);
        validarRepartidorDisponible(repartidor);

        Vehiculo vehiculo = seleccionarVehiculoValido(repartidor, pedido, dto.getVehiculoId());
        aplicarAsignacion(pedido, repartidor);

        log.info(
                "Asignación completada: pedido={}, repartidor={}, vehículo={}, capacidad={} kg",
                pedido.getId(), repartidor.getId(), vehiculo.getId(), vehiculo.getCapacidadKg()
        );
    }

    @Override
    @Transactional
    public Repartidor autoAsignar(Long pedidoId) {
        log.info("Iniciando auto-asignación para el pedido {}", pedidoId);

        Pedido pedido = obtenerPedido(pedidoId);
        validarPedidoPendienteSinAsignar(pedido);

        List<CandidatoAsignacion> candidatos = repartidorRepository
                .findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .stream()
                .filter(this::usuarioActivo)
                .map(repartidor -> construirCandidato(repartidor, pedido))
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparingInt(CandidatoAsignacion::pedidosActivos)
                        .thenComparing(CandidatoAsignacion::pesoActivo)
                        .thenComparingDouble(CandidatoAsignacion::distanciaKm)
                        .thenComparing(CandidatoAsignacion::capacidadRestante, Comparator.reverseOrder())
                        .thenComparing(c -> c.repartidor().getId()))
                .toList();

        if (candidatos.isEmpty()) {
            throw new AsignacionInvalidaException(
                    "No hay repartidores disponibles con un vehículo capaz de transportar el pedido"
            );
        }

        CandidatoAsignacion elegido = candidatos.get(0);
        aplicarAsignacion(pedido, elegido.repartidor());

        log.info(
                "Auto-asignación completada: pedido={}, repartidor={}, vehículo={}, pedidosActivos={}, " +
                        "pesoActivo={} kg, distancia={} km, capacidadRestante={} kg",
                pedido.getId(),
                elegido.repartidor().getId(),
                elegido.vehiculo().getId(),
                elegido.pedidosActivos(),
                elegido.pesoActivo(),
                formatearDistancia(elegido.distanciaKm()),
                elegido.capacidadRestante()
        );

        return elegido.repartidor();
    }

    @Override
    @Transactional
    public void completarEntrega(Long pedidoId) {
        log.info("Completando entrega del pedido {}", pedidoId);
        Pedido pedido = obtenerPedido(pedidoId);

        if (!pedido.getEstado().esTransicionValida(EstadoPedido.ENTREGADO)) {
            throw new EstadoInvalidoException(
                    pedido.getEstado().name(),
                    EstadoPedido.ENTREGADO.name()
            );
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
        pedidoRepository.save(pedido);

        entregaRepository.findByPedidoId(pedidoId).ifPresent(entrega -> {
            entrega.setEstado(Entrega.EstadoEntrega.ENTREGADO);
            entrega.setFechaEntrega(java.time.LocalDateTime.now());
            entregaRepository.save(entrega);
        });
        log.info("Entrega del pedido {} completada", pedidoId);
    }

    private Pedido obtenerPedido(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido", pedidoId));
    }

    private void validarPedidoPendienteSinAsignar(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.PENDIENTE || pedido.getRepartidor() != null) {
            throw new AsignacionInvalidaException(
                    "Solo se pueden auto-asignar pedidos pendientes y sin repartidor"
            );
        }
    }

    /**
     * La asignación manual permite una primera asignación o una reasignación mientras
     * el pedido aún no inició el recojo. Se bloquean pedidos en ruta, entregados o cancelados.
     */
    private void validarPedidoReasignable(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.PENDIENTE && pedido.getEstado() != EstadoPedido.ASIGNADO) {
            throw new AsignacionInvalidaException(
                    "El pedido solo puede asignarse o reasignarse cuando está PENDIENTE o ASIGNADO"
            );
        }
    }

    private void validarRepartidorDisponible(Repartidor repartidor) {
        if (repartidor.getEstado() != Repartidor.EstadoRepartidor.DISPONIBLE) {
            throw new AsignacionInvalidaException("El repartidor seleccionado no está disponible");
        }
        if (!usuarioActivo(repartidor)) {
            throw new AsignacionInvalidaException("La cuenta del repartidor está inactiva");
        }
    }

    private boolean usuarioActivo(Repartidor repartidor) {
        return repartidor.getUsuario() != null && Boolean.TRUE.equals(repartidor.getUsuario().getActivo());
    }

    private Vehiculo seleccionarVehiculoValido(Repartidor repartidor, Pedido pedido, Long vehiculoId) {
        List<Vehiculo> vehiculos = vehiculoRepository.findByRepartidorAsignadoId(repartidor.getId());

        if (vehiculoId != null) {
            Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Vehículo", vehiculoId));

            if (vehiculo.getRepartidorAsignado() == null
                    || !Objects.equals(vehiculo.getRepartidorAsignado().getId(), repartidor.getId())) {
                throw new AsignacionInvalidaException("El vehículo no pertenece al repartidor seleccionado");
            }

            validarCapacidadVehiculo(vehiculo, repartidor, pedido);
            return vehiculo;
        }

        return vehiculos.stream()
                .filter(this::vehiculoOperativo)
                .filter(v -> tieneCapacidad(v, repartidor, pedido))
                .min(Comparator.comparing(Vehiculo::getCapacidadKg))
                .orElseThrow(() -> new AsignacionInvalidaException(
                        "El repartidor no tiene un vehículo operativo con capacidad suficiente"
                ));
    }

    private void validarCapacidadVehiculo(Vehiculo vehiculo, Repartidor repartidor, Pedido pedido) {
        if (!vehiculoOperativo(vehiculo)) {
            throw new AsignacionInvalidaException("El vehículo seleccionado está en mantenimiento");
        }
        if (!tieneCapacidad(vehiculo, repartidor, pedido)) {
            BigDecimal cargaFinal = pesoActivo(repartidor.getId(), pedido.getId()).add(pesoSeguro(pedido));
            throw new AsignacionInvalidaException(
                    "Capacidad insuficiente: la carga resultante sería " + cargaFinal +
                            " kg y el vehículo soporta " + vehiculo.getCapacidadKg() + " kg"
            );
        }
    }

    private boolean vehiculoOperativo(Vehiculo vehiculo) {
        return vehiculo.getEstado() != Vehiculo.EstadoVehiculo.EN_MANTENIMIENTO;
    }

    private boolean tieneCapacidad(Vehiculo vehiculo, Repartidor repartidor, Pedido pedido) {
        if (vehiculo.getCapacidadKg() == null) {
            return false;
        }
        BigDecimal cargaFinal = pesoActivo(repartidor.getId(), pedido.getId()).add(pesoSeguro(pedido));
        return vehiculo.getCapacidadKg().compareTo(cargaFinal) >= 0;
    }

    private CandidatoAsignacion construirCandidato(Repartidor repartidor, Pedido pedido) {
        List<Pedido> activos = pedidosActivosExcluyendo(repartidor.getId(), pedido.getId());
        BigDecimal pesoActivo = activos.stream()
                .map(this::pesoSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return vehiculoRepository.findByRepartidorAsignadoId(repartidor.getId())
                .stream()
                .filter(this::vehiculoOperativo)
                .filter(v -> v.getCapacidadKg() != null)
                .filter(v -> v.getCapacidadKg().compareTo(pesoActivo.add(pesoSeguro(pedido))) >= 0)
                .map(v -> new CandidatoAsignacion(
                        repartidor,
                        v,
                        activos.size(),
                        pesoActivo,
                        calcularDistanciaKm(repartidor, pedido),
                        v.getCapacidadKg().subtract(pesoActivo.add(pesoSeguro(pedido)))
                ))
                .min(Comparator.comparing(CandidatoAsignacion::capacidadRestante))
                .orElse(null);
    }

    private List<Pedido> pedidosActivosExcluyendo(Long repartidorId, Long pedidoExcluidoId) {
        return pedidoRepository.findByRepartidorIdAndEstadoIn(repartidorId, ESTADOS_ACTIVOS)
                .stream()
                .filter(p -> pedidoExcluidoId == null || !Objects.equals(p.getId(), pedidoExcluidoId))
                .toList();
    }

    private BigDecimal pesoActivo(Long repartidorId, Long pedidoExcluidoId) {
        return pedidosActivosExcluyendo(repartidorId, pedidoExcluidoId)
                .stream()
                .map(this::pesoSeguro)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal pesoSeguro(Pedido pedido) {
        return pedido.getPeso() == null ? BigDecimal.ZERO : pedido.getPeso();
    }

    private void aplicarAsignacion(Pedido pedido, Repartidor repartidor) {
        pedido.setRepartidor(repartidor);
        pedido.setEstado(EstadoPedido.ASIGNADO);
        pedidoRepository.save(pedido);

        Entrega entrega = entregaRepository.findByPedidoId(pedido.getId())
                .orElseGet(() -> Entrega.builder()
                        .pedido(pedido)
                        .estado(Entrega.EstadoEntrega.PENDIENTE)
                        .build());

        entrega.setRepartidor(repartidor);
        entrega.setEstado(Entrega.EstadoEntrega.PENDIENTE);
        entregaRepository.save(entrega);
    }

    /**
     * Distancia Haversine entre la ubicación del repartidor y el destino del pedido.
     * Cuando faltan coordenadas se devuelve un valor alto para usar la distancia solo
     * como criterio secundario, sin descartar al candidato.
     */
    private double calcularDistanciaKm(Repartidor repartidor, Pedido pedido) {
        if (repartidor.getLatitude() == null || repartidor.getLongitude() == null
                || pedido.getLatitude() == null || pedido.getLongitude() == null) {
            return Double.MAX_VALUE;
        }

        double lat1 = Math.toRadians(repartidor.getLatitude().doubleValue());
        double lon1 = Math.toRadians(repartidor.getLongitude().doubleValue());
        double lat2 = Math.toRadians(pedido.getLatitude().doubleValue());
        double lon2 = Math.toRadians(pedido.getLongitude().doubleValue());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return 6371.0 * c;
    }

    private String formatearDistancia(double distanciaKm) {
        if (distanciaKm == Double.MAX_VALUE) {
            return "sin coordenadas";
        }
        return BigDecimal.valueOf(distanciaKm)
                .setScale(2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private record CandidatoAsignacion(
            Repartidor repartidor,
            Vehiculo vehiculo,
            int pedidosActivos,
            BigDecimal pesoActivo,
            double distanciaKm,
            BigDecimal capacidadRestante
    ) {
    }
}
