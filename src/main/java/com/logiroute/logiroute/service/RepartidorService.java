package com.logiroute.logiroute.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.repository.RepartidorRepository;
import com.logiroute.logiroute.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

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

}
