package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.AsignacionDTO;
import com.logiroute.logiroute.model.*;
import com.logiroute.logiroute.repository.EntregaRepository;
import com.logiroute.logiroute.repository.PedidoRepository;
import com.logiroute.logiroute.repository.RepartidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsignacionServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private RepartidorRepository repartidorRepository;

    @Mock
    private EntregaRepository entregaRepository;

    @InjectMocks
    private AsignacionService asignacionService;

    private Pedido pedidoMock;
    private Repartidor repartidorMock;

    @BeforeEach
    void setUp() {
        Usuario usuarioRepartidor = Usuario.builder()
                .id(2L)
                .nombre("Repartidor Test")
                .email("repartidor@test.com")
                .rol(Usuario.Rol.REPARTIDOR)
                .build();

        repartidorMock = Repartidor.builder()
                .id(1L)
                .usuario(usuarioRepartidor)
                .telefono("988777666")
                .licencia("LIC-001")
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        pedidoMock = Pedido.builder()
                .id(1L)
                .codigo("PED12345678")
                .direccionOrigen("Av. Principal 123")
                .direccionDestino("Jr. Comercio 456")
                .peso(new BigDecimal("5.00"))
                .tipoPaquete("CAJA")
                .estado(EstadoPedido.PENDIENTE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void asignar_debeAsignarRepartidorYCrearEntrega() {
        AsignacionDTO dto = AsignacionDTO.builder()
                .pedidoId(1L)
                .repartidorId(1L)
                .build();

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidorMock));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(entregaRepository.save(any(Entrega.class))).thenAnswer(invocation -> invocation.getArgument(0));

        asignacionService.asignar(dto);

        assertEquals(repartidorMock, pedidoMock.getRepartidor());
        assertEquals(EstadoPedido.ASIGNADO, pedidoMock.getEstado());

        ArgumentCaptor<Entrega> entregaCaptor = ArgumentCaptor.forClass(Entrega.class);
        verify(entregaRepository, times(1)).save(entregaCaptor.capture());

        Entrega entregaGuardada = entregaCaptor.getValue();
        assertEquals(pedidoMock, entregaGuardada.getPedido());
        assertEquals(repartidorMock, entregaGuardada.getRepartidor());
        assertEquals(Entrega.EstadoEntrega.PENDIENTE, entregaGuardada.getEstado());
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
        verify(pedidoRepository, times(1)).save(pedidoMock);
        verify(entregaRepository, times(1)).save(entregaMock);
    }

    @Test
    void asignar_pedidoNoExistente_debeLanzarExcepcion() {
        AsignacionDTO dto = AsignacionDTO.builder()
                .pedidoId(99L)
                .repartidorId(1L)
                .build();

        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> asignacionService.asignar(dto));
        verify(entregaRepository, never()).save(any(Entrega.class));
    }
}
