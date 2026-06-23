package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.model.*;
import com.logiroute.logiroute.repository.ClienteRepository;
import com.logiroute.logiroute.repository.PedidoRepository;
import com.logiroute.logiroute.repository.RepartidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Cliente clienteMock;
    private Pedido pedidoMock;

    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Cliente Test")
                .email("cliente@test.com")
                .rol(Usuario.Rol.USUARIO)
                .build();

        clienteMock = Cliente.builder()
                .id(1L)
                .usuario(usuario)
                .telefono("999888777")
                .direccion("Av. Principal 123")
                .build();

        pedidoMock = Pedido.builder()
                .id(1L)
                .codigo("PED12345678")
                .cliente(clienteMock)
                .direccionOrigen("Av. Principal 123")
                .direccionDestino("Jr. Comercio 456")
                .peso(new BigDecimal("5.00"))
                .tipoPaquete("CAJA")
                .estado(EstadoPedido.PENDIENTE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void crearPedido_debeGuardarPedidoConEstadoPendiente() {
        PedidoDTO dto = PedidoDTO.builder()
                .clienteId(1L)
                .direccionOrigen("Av. Principal 123")
                .direccionDestino("Jr. Comercio 456")
                .peso(new BigDecimal("5.00"))
                .tipoPaquete("CAJA")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(clienteMock));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> {
            Pedido p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        Pedido resultado = pedidoService.crear(dto);

        assertNotNull(resultado);
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado());
        assertTrue(resultado.getCodigo().startsWith("PED"));
        assertEquals(clienteMock, resultado.getCliente());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void actualizarEstado_debeCambiarEstadoDelPedido() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Pedido> resultado = pedidoService.actualizarEstado(1L, "ASIGNADO");

        assertTrue(resultado.isPresent());
        assertEquals(EstadoPedido.ASIGNADO, resultado.get().getEstado());
        verify(pedidoRepository, times(1)).save(pedidoMock);
    }

    @Test
    void obtenerPorCodigo_debeRetornarPedido() {
        when(pedidoRepository.findByCodigo("PED12345678")).thenReturn(Optional.of(pedidoMock));

        Optional<Pedido> resultado = pedidoService.obtenerPorCodigo("PED12345678");

        assertTrue(resultado.isPresent());
        assertEquals("PED12345678", resultado.get().getCodigo());
    }

    @Test
    void eliminar_pedidoExistente_debeRetornarTrue() {
        when(pedidoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(pedidoRepository).deleteById(1L);

        boolean resultado = pedidoService.eliminar(1L);

        assertTrue(resultado);
        verify(pedidoRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_pedidoNoExistente_debeRetornarFalse() {
        when(pedidoRepository.existsById(99L)).thenReturn(false);

        boolean resultado = pedidoService.eliminar(99L);

        assertFalse(resultado);
        verify(pedidoRepository, never()).deleteById(anyLong());
    }
}
