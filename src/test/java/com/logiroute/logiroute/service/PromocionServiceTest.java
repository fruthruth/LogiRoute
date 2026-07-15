package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PromocionDTO;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Promocion;
import com.logiroute.logiroute.repository.PromocionRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionService promocionService;

    @Test
    void listarTodos_deberiaRetornarLista() {
        Promocion p1 = Promocion.builder().id(1L).titulo("Promo 1").activa(true).build();
        Promocion p2 = Promocion.builder().id(2L).titulo("Promo 2").activa(false).build();
        when(promocionRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Promocion> resultado = promocionService.listarTodos();

        assertEquals(2, resultado.size());
        verify(promocionRepository).findAll();
    }

    @Test
    void listarActivasVigentes_deberiaRetornarSoloActivas() {
        Promocion activa = Promocion.builder().id(1L).titulo("Activa").activa(true).build();
        when(promocionRepository.findActivasVigentes(any(LocalDateTime.class))).thenReturn(List.of(activa));

        List<Promocion> resultado = promocionService.listarActivasVigentes();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getActiva());
    }

    @Test
    void obtenerPorId_deberiaRetornarPromocion() {
        Promocion promocion = Promocion.builder().id(1L).titulo("Promo Test").build();
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));

        Optional<Promocion> resultado = promocionService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals("Promo Test", resultado.get().getTitulo());
    }

    @Test
    void obtenerPorId_deberiaRetornarVacioSiNoExiste() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Promocion> resultado = promocionService.obtenerPorId(99L);

        assertFalse(resultado.isPresent());
    }

    @Test
    void crear_deberiaCrearPromocion() {
        PromocionDTO dto = PromocionDTO.builder()
                .titulo("Nueva Promo")
                .descripcion("Descripción test")
                .descuentoPorcentaje(new BigDecimal("15.00"))
                .fechaInicio(LocalDateTime.of(2026, 1, 1, 0, 0))
                .fechaFin(LocalDateTime.of(2026, 12, 31, 23, 59))
                .build();

        Promocion guardada = Promocion.builder()
                .id(1L)
                .titulo("Nueva Promo")
                .descripcion("Descripción test")
                .descuentoPorcentaje(new BigDecimal("15.00"))
                .activa(true)
                .build();

        when(promocionRepository.save(any(Promocion.class))).thenReturn(guardada);

        Promocion resultado = promocionService.crear(dto);

        assertNotNull(resultado);
        assertEquals("Nueva Promo", resultado.getTitulo());
        assertTrue(resultado.getActiva());
        verify(promocionRepository).save(any(Promocion.class));
    }

    @Test
    void crear_deberiaLanzarExcepcionSiFechaFinAnterior() {
        PromocionDTO dto = PromocionDTO.builder()
                .titulo("Promo mala")
                .descripcion("Test")
                .descuentoPorcentaje(new BigDecimal("10.00"))
                .fechaInicio(LocalDateTime.of(2026, 12, 31, 0, 0))
                .fechaFin(LocalDateTime.of(2026, 1, 1, 0, 0))
                .build();

        assertThrows(EstadoInvalidoException.class, () -> promocionService.crear(dto));
        verify(promocionRepository, never()).save(any());
    }

    @Test
    void eliminar_deberiaEliminarSiExiste() {
        when(promocionRepository.existsById(1L)).thenReturn(true);

        boolean resultado = promocionService.eliminar(1L);

        assertTrue(resultado);
        verify(promocionRepository).deleteById(1L);
    }

    @Test
    void eliminar_deberiaRetornarFalsoSiNoExiste() {
        when(promocionRepository.existsById(99L)).thenReturn(false);

        boolean resultado = promocionService.eliminar(99L);

        assertFalse(resultado);
        verify(promocionRepository, never()).deleteById(any());
    }

    @Test
    void actualizarEstado_deberiaActivarPromocion() {
        Promocion promocion = Promocion.builder().id(1L).titulo("Promo").activa(false).build();
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promocion));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promocion);

        Promocion resultado = promocionService.actualizarEstado(1L, true);

        assertTrue(resultado.getActiva());
        verify(promocionRepository).save(promocion);
    }

    @Test
    void actualizarEstado_deberiaLanzarExcepcionSiNoExiste() {
        when(promocionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> promocionService.actualizarEstado(99L, true));
    }
}
