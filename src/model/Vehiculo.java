package model;

//IMPORTAMOS LAS LIBRERÍAS 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vehiculo {
    
    private static final Logger logger = LoggerFactory.getLogger(Vehiculo.class);

    private int idVehiculo;
    private String tipo;
    private String placa;
    private double capacidad;
    private String estado;
    private int idRepartidor; // Clave foránea del repartidor asignado

    
    public Vehiculo() {
        logger.info("Se ha creado una instancia de Vehiculo vacío");
    }

    // Constructor con parámetros (Se queda exactamente igual)
    public Vehiculo(int idVehiculo, String tipo, String placa, double capacidad, String estado, int idRepartidor) {
        this.idVehiculo = idVehiculo;
        this.tipo = tipo;
        this.placa = placa;
        this.capacidad = capacidad;
        this.estado = estado;
        this.idRepartidor = idRepartidor;
    }

    // Métodos Getter y Setter (Se quedan exactamente igual)
    public int getIdVehiculo() { return idVehiculo; }
    public void setIdVehiculo(int idVehiculo) { this.idVehiculo = idVehiculo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public double getCapacidad() { return capacidad; }
    public void setCapacidad(double capacidad) { this.capacidad = capacidad; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(int idRepartidor) { this.idRepartidor = idRepartidor; }
}