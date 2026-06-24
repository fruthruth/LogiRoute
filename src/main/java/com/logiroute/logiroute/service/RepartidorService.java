package com.logiroute.logiroute.service;

import com.logiroute.logiroute.exception.DatoDuplicadoException;
import com.logiroute.logiroute.exception.EstadoInvalidoException;
import com.logiroute.logiroute.exception.RecursoNoEncontradoException;
import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.repository.RepartidorRepository;
import com.logiroute.logiroute.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepartidorService implements IRepartidorService {

    private static final Logger log = LoggerFactory.getLogger(RepartidorService.class);

    private final RepartidorRepository repartidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<Repartidor> listarTodos() {
        log.debug("Listando todos los repartidores");
        return repartidorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Repartidor> obtenerPorId(Long id) {
        log.debug("Buscando repartidor con id: {}", id);
        return repartidorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Repartidor> obtenerPorLicencia(String licencia) {
        log.debug("Buscando repartidor con licencia: {}", licencia);
        return repartidorRepository.findByLicencia(licencia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Repartidor> listarDisponibles() {
        log.debug("Listando repartidores disponibles");
        return repartidorRepository.findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
    }

    @Override
    @Transactional
    public Repartidor crear(String nombre, String email, String password,
                            String telefono, String licencia) {
        log.info("Creando repartidor con email: {}", email);

        if (usuarioRepository.existsByEmail(email)) {
            throw new DatoDuplicadoException("El email ya está registrado: " + email);
        }

        if (repartidorRepository.findByLicencia(licencia).isPresent()) {
            throw new DatoDuplicadoException("La licencia ya está registrada: " + licencia);
        }

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .email(email)
                .password(passwordEncoder.encode(password))
                .rol(Usuario.Rol.REPARTIDOR)
                .activo(true)
                .build();
        usuario = usuarioRepository.save(usuario);

        Repartidor repartidor = Repartidor.builder()
                .usuario(usuario)
                .telefono(telefono)
                .licencia(licencia)
                .estado(Repartidor.EstadoRepartidor.DISPONIBLE)
                .build();

        Repartidor guardado = repartidorRepository.save(repartidor);
        log.info("Repartidor creado con id: {}", guardado.getId());
        return guardado;
    }

    @Override
    @Transactional
    public Repartidor actualizar(Long id, String nombre, String email,
                                 String telefono, String licencia) {
        log.info("Actualizando repartidor id: {}", id);
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Repartidor", id));

        Usuario usuario = repartidor.getUsuario();
        usuario.setNombre(nombre);

        if (!usuario.getEmail().equals(email)) {
            if (usuarioRepository.existsByEmail(email)) {
                throw new DatoDuplicadoException("El email ya está registrado: " + email);
            }
            usuario.setEmail(email);
        }

        if (!repartidor.getLicencia().equals(licencia)) {
            if (repartidorRepository.findByLicencia(licencia).isPresent()) {
                throw new DatoDuplicadoException("La licencia ya está registrada: " + licencia);
            }
            repartidor.setLicencia(licencia);
        }

        usuarioRepository.save(usuario);
        repartidor.setTelefono(telefono);

        Repartidor actualizado = repartidorRepository.save(repartidor);
        log.info("Repartidor actualizado: {}", actualizado.getId());
        return actualizado;
    }

    @Override
    @Transactional
    public Repartidor actualizarEstado(Long id, String estado) {
        log.info("Actualizando estado del repartidor id: {} a {}", id, estado);

        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Repartidor", id));

        Repartidor.EstadoRepartidor nuevoEstado;
        try {
            nuevoEstado = Repartidor.EstadoRepartidor.valueOf(estado);
        } catch (IllegalArgumentException e) {
            throw new EstadoInvalidoException("Estado inválido: " + estado +
                    ". Valores válidos: " + Arrays.toString(Repartidor.EstadoRepartidor.values()));
        }

        if (repartidor.getEstado() == nuevoEstado) {
            return repartidor;
        }

        repartidor.setEstado(nuevoEstado);
        Repartidor actualizado = repartidorRepository.save(repartidor);
        log.info("Estado del repartidor {} actualizado a {}", id, estado);
        return actualizado;
    }

    @Override
    @Transactional
    public boolean eliminar(Long id) {
        log.info("Eliminando repartidor id: {}", id);
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Repartidor", id));

        repartidorRepository.deleteById(id);
        usuarioRepository.deleteById(repartidor.getUsuario().getId());
        log.info("Repartidor {} y usuario asociado eliminados", id);
        return true;
    }
}
