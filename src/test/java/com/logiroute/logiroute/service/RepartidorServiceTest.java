package com.logiroute.logiroute.service;

import com.logiroute.logiroute.exception.DatoDuplicadoException;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.repository.RepartidorRepository;
import com.logiroute.logiroute.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RepartidorServiceTest {

    @Mock
    private RepartidorRepository repartidorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RepartidorService repartidorService;

    @Test
    void crearRepartidor_debeGuardarUsuarioYRepartidor() {
        when(passwordEncoder.encode("123456")).thenReturn("$2a$10$encoded");
        when(usuarioRepository.existsByEmail("repartidor@test.com")).thenReturn(false);
        when(repartidorRepository.findByLicencia("LIC-001")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(repartidorRepository.save(any(Repartidor.class))).thenAnswer(invocation -> {
            Repartidor r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        Repartidor resultado = repartidorService.crear(
                "Juan Perez", "repartidor@test.com", "123456",
                "988777666", "LIC-001"
        );

        assertNotNull(resultado);
        assertEquals(Repartidor.EstadoRepartidor.DISPONIBLE, resultado.getEstado());
        assertEquals("LIC-001", resultado.getLicencia());
        assertEquals("988777666", resultado.getTelefono());
        assertEquals(Usuario.Rol.REPARTIDOR, resultado.getUsuario().getRol());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(repartidorRepository, times(1)).save(any(Repartidor.class));
    }

    @Test
    void crearRepartidor_emailDuplicado_debeLanzarExcepcion() {
        when(usuarioRepository.existsByEmail("duplicado@test.com")).thenReturn(true);

        DatoDuplicadoException exception = assertThrows(DatoDuplicadoException.class, () ->
                repartidorService.crear(
                        "Juan Perez", "duplicado@test.com", "123456",
                        "988777666", "LIC-002"
                )
        );

        assertEquals("El email ya está registrado: duplicado@test.com", exception.getMessage());
        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }

    @Test
    void crearRepartidor_licenciaDuplicada_debeLanzarExcepcion() {
        when(usuarioRepository.existsByEmail("repartidor@test.com")).thenReturn(false);
        when(repartidorRepository.findByLicencia("LIC-001")).thenReturn(Optional.of(
                Repartidor.builder().id(1L).licencia("LIC-001").build()
        ));

        DatoDuplicadoException exception = assertThrows(DatoDuplicadoException.class, () ->
                repartidorService.crear(
                        "Juan Perez", "repartidor@test.com", "123456",
                        "988777666", "LIC-001"
                )
        );

        assertEquals("La licencia ya está registrada: LIC-001", exception.getMessage());
        verify(repartidorRepository, never()).save(any(Repartidor.class));
    }

    @Test
    void listarDisponibles_debeRetornarSoloDisponibles() {
        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        when(repartidorRepository.findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE))
                .thenReturn(java.util.List.of(repartidor));

        var resultado = repartidorService.listarDisponibles();

        assertEquals(1, resultado.size());
        assertEquals(Repartidor.EstadoRepartidor.DISPONIBLE, resultado.get(0).getEstado());
    }

    @Test
    void obtenerPorLicencia_debeRetornarRepartidor() {
        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .licencia("LIC-001")
                .build();

        when(repartidorRepository.findByLicencia("LIC-001")).thenReturn(Optional.of(repartidor));

        Optional<Repartidor> resultado = repartidorService.obtenerPorLicencia("LIC-001");

        assertTrue(resultado.isPresent());
        assertEquals("LIC-001", resultado.get().getLicencia());
    }

    @Test
    void actualizarEstado_estadoValido_debeActualizar() {
        Usuario usuario = Usuario.builder().id(1L).nombre("Test").build();
        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .usuario(usuario)
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidor));
        when(repartidorRepository.save(any(Repartidor.class))).thenAnswer(inv -> inv.getArgument(0));

        Repartidor resultado = repartidorService.actualizarEstado(1L, "EN_RUTA");

        assertEquals(Repartidor.EstadoRepartidor.EN_RUTA, resultado.getEstado());
        verify(repartidorRepository, times(1)).save(repartidor);
    }

    @Test
    void actualizarEstado_estadoInvalido_debeLanzarExcepcion() {
        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidor));

        assertThrows(EstadoInvalidoException.class, () ->
                repartidorService.actualizarEstado(1L, "ESTADO_FALSO")
        );
    }

    @Test
    void actualizarEstado_mismoEstado_noDebeGuardar() {
        Usuario usuario = Usuario.builder().id(1L).nombre("Test").build();
        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .usuario(usuario)
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidor));

        Repartidor resultado = repartidorService.actualizarEstado(1L, "DISPONIBLE");

        assertEquals(Repartidor.EstadoRepartidor.DISPONIBLE, resultado.getEstado());
        verify(repartidorRepository, never()).save(any());
    }

    @Test
    void eliminar_repartidorExistente_debeEliminarUsuarioTambien() {
        Usuario usuario = Usuario.builder().id(1L).build();
        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .usuario(usuario)
                .build();

        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidor));
        doNothing().when(repartidorRepository).deleteById(1L);
        doNothing().when(usuarioRepository).deleteById(1L);

        boolean resultado = repartidorService.eliminar(1L);

        assertTrue(resultado);
        verify(repartidorRepository, times(1)).deleteById(1L);
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_repartidorNoExistente_debeLanzarExcepcion() {
        when(repartidorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                repartidorService.eliminar(99L)
        );

        verify(repartidorRepository, never()).deleteById(anyLong());
    }

    @Test
    void actualizarUbicacion_coordenadasValidas_debeActualizar() {
        Usuario usuario = Usuario.builder().id(1L).nombre("Test").build();
        Repartidor repartidor = Repartidor.builder()
                .id(1L)
                .usuario(usuario)
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        when(repartidorRepository.findById(1L)).thenReturn(Optional.of(repartidor));
        when(repartidorRepository.save(any(Repartidor.class))).thenAnswer(inv -> inv.getArgument(0));

        Repartidor resultado = repartidorService.actualizarUbicacion(1L,
                new BigDecimal("-12.1234567"),
                new BigDecimal("-77.1234567"));

        assertNotNull(resultado.getLatitude());
        assertNotNull(resultado.getLongitude());
        assertEquals(0, new BigDecimal("-12.1234567").compareTo(resultado.getLatitude()));
        verify(repartidorRepository, times(1)).save(any(Repartidor.class));
    }

    @Test
    void actualizarUbicacion_repartidorNoExistente_debeLanzarExcepcion() {
        when(repartidorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () ->
                repartidorService.actualizarUbicacion(99L,
                        new BigDecimal("-12.0"),
                        new BigDecimal("-77.0"))
        );
    }
}
