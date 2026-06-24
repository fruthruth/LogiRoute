package com.logiroute.logiroute.service;

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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceExtendedTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private PedidoService pedidoService;

    private Pedido pedidoMock;

    @BeforeEach
    void setUp() {
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nombre("Cliente Test")
                .email("cliente@test.com")
                .rol(Usuario.Rol.USUARIO)
                .build();

        Cliente cliente = Cliente.builder()
                .id(1L)
                .usuario(usuario)
                .telefono("999888777")
                .direccion("Av. Principal 123")
                .build();

        pedidoMock = Pedido.builder()
                .id(1L)
                .codigo("PED12345678")
                .cliente(cliente)
                .direccionOrigen("Av. Principal 123")
                .direccionDestino("Jr. Comercio 456")
                .peso(new BigDecimal("5.00"))
                .tipoPaquete("CAJA")
                .estado(EstadoPedido.PENDIENTE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void listarTodos_debeRetornarListaDePedidos() {
        Pedido pedido2 = Pedido.builder()
                .id(2L)
                .codigo("PED99999999")
                .direccionOrigen("Origen 2")
                .direccionDestino("Destino 2")
                .peso(new BigDecimal("3.00"))
                .tipoPaquete("Sobre")
                .estado(EstadoPedido.ASIGNADO)
                .build();

        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoMock, pedido2));

        List<Pedido> resultado = pedidoService.listarTodos();

        assertEquals(2, resultado.size());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_debeRetornarPedido() {
        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoMock));

        Optional<Pedido> resultado = pedidoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("PED12345678", resultado.get().getCodigo());
    }

}
