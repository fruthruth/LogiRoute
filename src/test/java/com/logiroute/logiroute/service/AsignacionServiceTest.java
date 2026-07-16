package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.exception.AsignacionInvalidaException;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.model.Entrega;
import com.logiroute.logiroute.model.EstadoPedido;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.model.Vehiculo;
import com.logiroute.logiroute.repository.EntregaRepository;
import com.logiroute.logiroute.repository.PedidoRepository;
import com.logiroute.logiroute.repository.RepartidorRepository;
import com.logiroute.logiroute.repository.VehiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RepartidorRepository repartidorRepository;

    @Mock
    private EntregaRepository entregaRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    private Pedido pedidoMock;
    private Repartidor repartidorMock;
    private Vehiculo vehiculoMock;

    @BeforeEach
    void setUp() {
        Usuario usuarioRepartidor = crearUsuario(2L, "Repartidor Test");

        repartidorMock = Repartidor.builder()
                .id(1L)
                .usuario(usuarioRepartidor)
                .telefono("988777666")
                .licencia("LIC-001")
                .latitude(new BigDecimal("-12.1191427"))
                .longitude(new BigDecimal("-77.0298243"))
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        vehiculoMock = crearVehiculo(1L, repartidorMock, "10.00");

        pedidoMock = Pedido.builder()
                .id(1L)
                .codigo("PED12345678")
                .direccionOrigen("Av. Principal 123")
                .direccionDestino("Jr. Comercio 456")
                .latitude(new BigDecimal("-12.1200000"))
                .longitude(new BigDecimal("-77.0300000"))
                .peso(new BigDecimal("5.00"))
                .tipoPaquete("CAJA")
                .estado(EstadoPedido.PENDIENTE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void asignar_debeValidarCapacidadAsignarRepartidorYCrearEntrega() {
        AsignacionDTO dto = AsignacionDTO.builder()
                .pedidoId(1L)
                .repartidorId(1L)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorMock));
        when(vehiculoRepository.findByRepartidorAsignadoId(1L)).thenReturn(List.of(vehiculoMock));
        when(pedidoRepository.findByRepartidorIdAndEstadoIn(any(), anyCollection())).thenReturn(List.of());
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(entregaRepository.findByPedidoId(1L)).thenReturn(Optional.empty());
        when(entregaRepository.save(any(Entrega.class))).thenAnswer(invocation -> invocation.getArgument(0));

        asignacionService.asignar(dto);

        assertEquals(repartidorMock, pedidoMock.getRepartidor());
        assertEquals(EstadoPedido.ASIGNADO, pedidoMock.getEstado());

        ArgumentCaptor<Entrega> entregaCaptor = ArgumentCaptor.forClass(Entrega.class);
        verify(entregaRepository).save(entregaCaptor.capture());

        Entrega entregaGuardada = entregaCaptor.getValue();
        assertEquals(pedidoMock, entregaGuardada.getPedido());
        assertEquals(repartidorMock, entregaGuardada.getRepartidor());
        assertEquals(Entrega.EstadoEntrega.PENDIENTE, entregaGuardada.getEstado());
    }

    @Test
    void asignar_reasignacionDebeActualizarEntregaExistenteSinDuplicarla() {
        Repartidor anterior = Repartidor.builder().id(9L).build();
        pedidoMock.setEstado(EstadoPedido.ASIGNADO);
        pedidoMock.setRepartidor(anterior);

        Entrega entregaExistente = Entrega.builder()
                .id(7L)
                .pedido(pedidoMock)
                .repartidor(anterior)
                .estado(Entrega.EstadoEntrega.PENDIENTE)
                .build();

        AsignacionDTO dto = AsignacionDTO.builder()
                .pedidoId(1L)
                .repartidorId(1L)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorMock));
        when(vehiculoRepository.findByRepartidorAsignadoId(1L)).thenReturn(List.of(vehiculoMock));
        when(pedidoRepository.findByRepartidorIdAndEstadoIn(any(), anyCollection())).thenReturn(List.of());
        when(entregaRepository.findByPedidoId(1L)).thenReturn(Optional.of(entregaExistente));

        asignacionService.asignar(dto);

        assertSame(repartidorMock, entregaExistente.getRepartidor());
        assertEquals(Entrega.EstadoEntrega.PENDIENTE, entregaExistente.getEstado());
        verify(entregaRepository, times(1)).save(entregaExistente);
    }

    @Test
    void asignar_repartidorNoDisponibleDebeRechazarAsignacion() {
        repartidorMock.setEstado(Repartidor.EstadoRepartidor.INACTIVO);
        AsignacionDTO dto = AsignacionDTO.builder().pedidoId(1L).repartidorId(1L).build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorMock));

        AsignacionInvalidaException ex = assertThrows(
                AsignacionInvalidaException.class,
                () -> asignacionService.asignar(dto)
        );

        assertEquals("El repartidor seleccionado no está disponible", ex.getMessage());
        verify(pedidoRepository, never()).save(any());
        verify(entregaRepository, never()).save(any());
    }

    @Test
    void asignar_capacidadAcumuladaInsuficienteDebeRechazarAsignacion() {
        Pedido pedidoActivo = Pedido.builder()
                .id(20L)
                .peso(new BigDecimal("7.00"))
                .estado(EstadoPedido.ASIGNADO)
                .build();
        AsignacionDTO dto = AsignacionDTO.builder().pedidoId(1L).repartidorId(1L).build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorMock));
        when(vehiculoRepository.findByRepartidorAsignadoId(1L)).thenReturn(List.of(vehiculoMock));
        when(pedidoRepository.findByRepartidorIdAndEstadoIn(any(), anyCollection()))
                .thenReturn(List.of(pedidoActivo));

        assertThrows(AsignacionInvalidaException.class, () -> asignacionService.asignar(dto));
        verify(pedidoRepository, never()).save(any());
        verify(entregaRepository, never()).save(any());
    }

    @Test
    void autoAsignar_debePriorizarMenorCargaActiva() {
        Repartidor conCarga = repartidorMock;
        Repartidor sinCarga = crearRepartidor(2L, "Sin carga", "-12.1800000", "-77.0800000");
        Vehiculo vehiculoSinCarga = crearVehiculo(2L, sinCarga, "12.00");
        Pedido activo = Pedido.builder()
                .id(30L)
                .peso(new BigDecimal("1.00"))
                .estado(EstadoPedido.ASIGNADO)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE))
                .thenReturn(List.of(conCarga, sinCarga));
        when(pedidoRepository.findByRepartidorIdAndEstadoIn(eq(1L), anyCollection()))
                .thenReturn(List.of(activo));
        when(pedidoRepository.findByRepartidorIdAndEstadoIn(eq(2L), anyCollection()))
                .thenReturn(List.of());
        when(vehiculoRepository.findByRepartidorAsignadoId(1L)).thenReturn(List.of(vehiculoMock));
        when(vehiculoRepository.findByRepartidorAsignadoId(2L)).thenReturn(List.of(vehiculoSinCarga));
        when(entregaRepository.findByPedidoId(1L)).thenReturn(Optional.empty());

        Repartidor elegido = asignacionService.autoAsignar(1L);

        assertSame(sinCarga, elegido);
        assertSame(sinCarga, pedidoMock.getRepartidor());
        verify(entregaRepository).save(any(Entrega.class));
    }

    @Test
    void autoAsignar_conMismaCargaDebePriorizarCercania() {
        Repartidor cercano = crearRepartidor(2L, "Cercano", "-12.1201000", "-77.0301000");
        Repartidor lejano = crearRepartidor(3L, "Lejano", "-12.2500000", "-77.1500000");
        Vehiculo vehiculoCercano = crearVehiculo(2L, cercano, "10.00");
        Vehiculo vehiculoLejano = crearVehiculo(3L, lejano, "10.00");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE))
                .thenReturn(List.of(lejano, cercano));
        when(pedidoRepository.findByRepartidorIdAndEstadoIn(any(), anyCollection())).thenReturn(List.of());
        when(vehiculoRepository.findByRepartidorAsignadoId(2L)).thenReturn(List.of(vehiculoCercano));
        when(vehiculoRepository.findByRepartidorAsignadoId(3L)).thenReturn(List.of(vehiculoLejano));
        when(entregaRepository.findByPedidoId(1L)).thenReturn(Optional.empty());

        Repartidor elegido = asignacionService.autoAsignar(1L);

        assertSame(cercano, elegido);
    }

    @Test
    void autoAsignar_sinCandidatoCapazDebeLanzarExcepcion() {
        Vehiculo pequeno = crearVehiculo(5L, repartidorMock, "2.00");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE))
                .thenReturn(List.of(repartidorMock));
        when(pedidoRepository.findByRepartidorIdAndEstadoIn(any(), anyCollection())).thenReturn(List.of());
        when(vehiculoRepository.findByRepartidorAsignadoId(1L)).thenReturn(List.of(pequeno));

        assertThrows(AsignacionInvalidaException.class, () -> asignacionService.autoAsignar(1L));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void completarEntrega_debeMarcarPedidoYEntregaComoEntregados() {
        pedidoMock.setEstado(EstadoPedido.EN_TRANSITO);

        Entrega entregaMock = Entrega.builder()
                .id(1L)
                .pedido(pedidoMock)
                .repartidor(repartidorMock)
                .estado(Entrega.EstadoEntrega.EN_CAMINO)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(entregaRepository.findByPedidoId(1L)).thenReturn(Optional.of(entregaMock));
        when(entregaRepository.save(any(Entrega.class))).thenAnswer(invocation -> invocation.getArgument(0));

        asignacionService.completarEntrega(1L);

        assertEquals(EstadoPedido.ENTREGADO, pedidoMock.getEstado());
        assertEquals(Entrega.EstadoEntrega.ENTREGADO, entregaMock.getEstado());
        assertNotNull(entregaMock.getFechaEntrega());
        verify(pedidoRepository).save(pedidoMock);
        verify(entregaRepository).save(entregaMock);
    }

    @Test
    void completarEntrega_desdePendienteDebeRechazarTransicion() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));

        assertThrows(EstadoInvalidoException.class, () -> asignacionService.completarEntrega(1L));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void asignar_pedidoNoExistenteDebeLanzarExcepcion() {
        AsignacionDTO dto = AsignacionDTO.builder()
                .pedidoId(99L)
                .repartidorId(1L)
                .build();

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> asignacionService.asignar(dto));
        verify(entregaRepository, never()).save(any(Entrega.class));
    }

    private Usuario crearUsuario(Long id, String nombre) {
        return Usuario.builder()
                .id(id)
                .nombre(nombre)
                .email("usuario" + id + "@test.com")
                .rol(Usuario.Rol.REPARTIDOR)
                .activo(true)
                .build();
    }

    private Repartidor crearRepartidor(Long id, String nombre, String latitude, String longitude) {
        return Repartidor.builder()
                .id(id)
                .usuario(crearUsuario(id + 10, nombre))
                .telefono("90000000" + id)
                .licencia("LIC-" + id)
                .latitude(new BigDecimal(latitude))
                .longitude(new BigDecimal(longitude))
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();
    }

    private Vehiculo crearVehiculo(Long id, Repartidor repartidor, String capacidad) {
        return Vehiculo.builder()
                .id(id)
                .placa("TEST-" + id)
                .marca("Honda")
                .modelo("Test")
                .anio(2026)
                .capacidadKg(new BigDecimal(capacidad))
                .estado(Vehiculo.EstadoVehiculo.DISPONIBLE)
                .repartidorAsignado(repartidor)
                .build();
    }

}
