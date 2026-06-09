package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.repository.RepartidorRepository;
import com.logiroute.logiroute.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Repartidor> listarTodos() {
        return repartidorRepository.findAll();
    }

    public Optional<Repartidor> obtenerPorId(Long id) {
        return repartidorRepository.findById(id);
    }

    public List<Repartidor> listarDisponibles() {
        return repartidorRepository.findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
    }

    @Transactional
    public Repartidor crear(String nombre, String email, String password,
                            String telefono, String licencia) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("El email ya está registrado");
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

        return repartidorRepository.save(repartidor);
    }

    @Transactional
    public Repartidor actualizar(Long id, String nombre, String email,
                                 String telefono, String licencia) {
        Repartidor repartidor = repartidorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repartidor no encontrado"));

        Usuario usuario = repartidor.getUsuario();
        usuario.setNombre(nombre);
        if (!usuario.getEmail().equals(email)) {
            if (usuarioRepository.existsByEmail(email)) {
                throw new RuntimeException("El email ya está registrado");
            }
            usuario.setEmail(email);
        }
        usuarioRepository.save(usuario);

        repartidor.setTelefono(telefono);
        repartidor.setLicencia(licencia);

        return repartidorRepository.save(repartidor);
    }

    public Optional<Repartidor> actualizarEstado(Long id, String estado) {
        return repartidorRepository.findById(id).map(repartidor -> {
            repartidor.setEstado(Repartidor.EstadoRepartidor.valueOf(estado));
            return repartidorRepository.save(repartidor);
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (repartidorRepository.existsById(id)) {
            Repartidor repartidor = repartidorRepository.findById(id).orElse(null);
            if (repartidor != null) {
                repartidorRepository.deleteById(id);
                usuarioRepository.deleteById(repartidor.getUsuario().getId());
                return true;
            }
        }
        return false;
    }
}
