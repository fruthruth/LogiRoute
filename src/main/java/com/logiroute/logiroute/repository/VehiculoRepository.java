package com.logiroute.logiroute.repository;

import com.logiroute.logiroute.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    Optional<Vehiculo> findByPlaca(String placa);

    List<Vehiculo> findByEstado(Vehiculo.EstadoVehiculo estado);

    List<Vehiculo> findByRepartidorAsignadoId(Long repartidorId);
}
