package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.repository.PedidoRepository;
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
class ReporteServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private ReporteService reporteService;

    private Pedido pedidoMock;

    @BeforeEach
    void setUp() {
        Usuario usuarioRepartidor = Usuario.builder()
                .id(2L)
                .nombre("Juan Perez")
                .email("repartidor@test.com")
                .rol(Usuario.Rol.REPARTIDOR)
                .build();

        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .usuario(usuarioRepartidor)
                .build();

        pedidoMock = Pedido.builder()
                .id(1L)
                .codigo("PED12345678")
                .direccionOrigen("Av. Principal 123")
                .direccionDestino("Jr. Comercio 456")
                .peso(new BigDecimal("5.00"))
                .tipoPaquete("CAJA")
                .costo(new BigDecimal("25.00"))
                .repartidor(repartidor)
                .createdAt(LocalDateTime.of(2026, 6, 21, 10, 30))
                .build();
    }

    @Test
    void filtrar_porDia_debeRetornarSoloPedidosDeEseDia() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoMock));

        List<Pedido> resultado = reporteService.filtrar(21, null, null, null, null, null);

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
    }

    @Test
    void filtrar_porResponsable_debeRetornarPedidosDeEseRepartidor() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoMock));

        List<Pedido> resultado = reporteService.filtrar(null, null, null, "Juan", null, null);

        assertEquals(1, resultado.size());
    }

    @Test
    void filtrar_porCostoMin_debeRetornarPedidosConCostoMayor() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoMock));

        List<Pedido> resultado = reporteService.filtrar(null, null, null, null, 20.0, null);

        assertEquals(1, resultado.size());
    }

    @Test
    void filtrar_sinFiltros_debeRetornarTodos() {
        when(pedidoRepository.findAll()).thenReturn(List.of(pedidoMock));

        List<Pedido> resultado = reporteService.filtrar(null, null, null, null, null, null);

        assertEquals(1, resultado.size());
    }
}
