package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Repartidor;
import com.logiroute.logiroute.repository.RepartidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepartidorService {

    private final RepartidorRepository repartidorRepository;

    public List<Repartidor> listarTodos() {
        return repartidorRepository.findAll();
    }

    public Optional<Repartidor> obtenerPorId(Long id) {
        return repartidorRepository.findById(id);
    }

    public List<Repartidor> listarDisponibles() {
        return repartidorRepository.findByEstado(Repartidor.EstadoRepartidor.DISPONIBLE);
    }

    public Optional<Repartidor> actualizarEstado(Long id, String estado) {
        return repartidorRepository.findById(id).map(repartidor -> {
            repartidor.setEstado(Repartidor.EstadoRepartidor.valueOf(estado));
            return repartidorRepository.save(repartidor);
        });
    }
}
