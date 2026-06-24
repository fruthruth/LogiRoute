package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService implements IReporteService {

    private static final Logger log = LoggerFactory.getLogger(ReporteService.class);

    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> filtrar(Integer dia, Integer mes, Integer anio,
                                String responsable, Double costoMin, Double costoMax) {
        log.debug("Filtrando reportes: dia={}, mes={}, anio={}, responsable={}, costoMin={}, costoMax={}",
                dia, mes, anio, responsable, costoMin, costoMax);
        return pedidoRepository.findAll().stream()
                .filter(p -> dia == null || p.getCreatedAt().getDayOfMonth() == dia)
                .filter(p -> mes == null || p.getCreatedAt().getMonthValue() == mes)
                .filter(p -> anio == null || p.getCreatedAt().getYear() == anio)
                .filter(p -> responsable == null || responsable.isBlank() ||
                        (p.getRepartidor() != null &&
                                p.getRepartidor().getUsuario().getNombre()
                                        .toLowerCase().contains(responsable.toLowerCase())))
                .filter(p -> costoMin == null || (p.getCosto() != null &&
                        p.getCosto().doubleValue() >= costoMin))
                .filter(p -> costoMax == null || (p.getCosto() != null &&
                        p.getCosto().doubleValue() <= costoMax))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SeguimientoDTO> seguimiento(String codigo) {
        log.debug("Buscando seguimiento del pedido con código: {}", codigo);
        return pedidoRepository.findByCodigo(codigo).map(pedido -> SeguimientoDTO.builder()
                .pedidoId(pedido.getId())
                .codigo(pedido.getCodigo())
                .estado(pedido.getEstado().name())
                .direccionOrigen(pedido.getDireccionOrigen())
                .direccionDestino(pedido.getDireccionDestino())
                .repartidorNombre(pedido.getRepartidor() != null
                        ? pedido.getRepartidor().getUsuario().getNombre()
                        : null)
                .fechaEstimada(pedido.getFechaEstimada())
                .fechaEntrega(pedido.getFechaEntrega())
                .build());
    }
}
