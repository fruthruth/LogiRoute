package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.IncidenteDTO;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Incidente;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.repository.IncidenteRepository;
import com.logiroute.logiroute.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidenteServiceTest {

    @Mock
    private IncidenteRepository incidenteRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private IncidenteService incidenteService;

    @Test
    void registrar_debeGuardarIncidenteValido() {
        Pedido pedido = Pedido.builder().id(7L).codigo("TRK-2026-007").build();
        IncidenteDTO dto = IncidenteDTO.builder()
                .pedidoId(7L)
                .tipo(Incidente.TipoIncidente.RETRASO)
                .descripcion("Tráfico intenso en la avenida principal")
                .build();

        when(pedidoRepository.findById(7L)).thenReturn(Optional.of(pedido));
        when(incidenteRepository.save(any(Incidente.class))).thenAnswer(invocation -> {
            Incidente incidente = invocation.getArgument(0);
            incidente.setId(1L);
            return incidente;
        });

        Incidente resultado = incidenteService.registrar(dto);

        assertEquals(1L, resultado.getId());
        assertEquals(pedido, resultado.getPedido());
        assertEquals(Incidente.TipoIncidente.RETRASO, resultado.getTipo());
        verify(incidenteRepository).save(any(Incidente.class));
    }

    @Test
    void registrar_debeRecortarEspaciosDeDescripcion() {
        Pedido pedido = Pedido.builder().id(2L).codigo("TRK-2026-002").build();
        IncidenteDTO dto = IncidenteDTO.builder()
                .pedidoId(2L)
                .tipo(Incidente.TipoIncidente.CLIENTE_AUSENTE)
                .descripcion("  El cliente no respondió  ")
                .build();

        when(pedidoRepository.findById(2L)).thenReturn(Optional.of(pedido));
        when(incidenteRepository.save(any(Incidente.class))).thenAnswer(i -> i.getArgument(0));

        Incidente resultado = incidenteService.registrar(dto);

        assertEquals("El cliente no respondió", resultado.getDescripcion());
    }

    @Test
    void registrar_conPedidoInexistente_debeLanzarExcepcion() {
        IncidenteDTO dto = IncidenteDTO.builder()
                .pedidoId(99L)
                .tipo(Incidente.TipoIncidente.ROBO)
                .descripcion("Incidente de prueba")
                .build();
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> incidenteService.registrar(dto));
        verify(incidenteRepository, never()).save(any());
    }

    @Test
    void registrar_sinDescripcion_debeLanzarExcepcion() {
        IncidenteDTO dto = IncidenteDTO.builder()
                .pedidoId(1L)
                .tipo(Incidente.TipoIncidente.DANO)
                .descripcion("   ")
                .build();

        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> incidenteService.registrar(dto)
        );

        assertTrue(error.getMessage().contains("descripción"));
        verifyNoInteractions(pedidoRepository, incidenteRepository);
    }

    @Test
    void listarPorPedido_debeRetornarIncidentesOrdenados() {
        Incidente incidente = Incidente.builder().id(3L).tipo(Incidente.TipoIncidente.RETRASO).build();
        when(pedidoRepository.existsById(4L)).thenReturn(true);
        when(incidenteRepository.findByPedidoIdOrderByCreatedAtDesc(4L)).thenReturn(List.of(incidente));

        List<Incidente> resultado = incidenteService.listarPorPedido(4L);

        assertEquals(1, resultado.size());
        assertEquals(3L, resultado.get(0).getId());
    }

    @Test
    void listarTodos_debeUsarOrdenDescendente() {
        when(incidenteRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<Incidente> resultado = incidenteService.listarTodos();

        assertNotNull(resultado);
        verify(incidenteRepository).findAllByOrderByCreatedAtDesc();
    }
}
