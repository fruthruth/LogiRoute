package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Repartidor;

import java.util.List;
import java.util.Optional;

public interface IRepartidorService {

    List<Repartidor> listarTodos();

    Optional<Repartidor> obtenerPorId(Long id);

    Optional<Repartidor> obtenerPorLicencia(String licencia);

    List<Repartidor> listarDisponibles();

    Repartidor crear(String nombre, String email, String password,
                     String telefono, String licencia);

    Repartidor actualizar(Long id, String nombre, String email,
                          String telefono, String licencia);

    Repartidor actualizarEstado(Long id, String estado);

    boolean eliminar(Long id);
}
