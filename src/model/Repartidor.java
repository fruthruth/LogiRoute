package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Repartidor {
    private static final Logger logger = LoggerFactory.getLogger(Repartidor.class);
    
    private int id;
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String vehiculo;
    private String estado;

    // 1. CONSTRUCTOR VACÍO (Actualizado para registrar el Log)
    public Repartidor() {
        this.estado = "DISPONIBLE";
        logger.info("Se ha creado una instancia de Repartidor vacío");
    }

    // 2. CONSTRUCTOR CON PARÁMETROS (Faltaba para cuando uses la Base de Datos)
    public Repartidor(int id, String nombre, String email, String password, String telefono, String vehiculo, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.vehiculo = vehiculo;
        this.estado = estado;
    }

    // Getters y Setters (Se quedan exactamente como los tenías)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getVehiculo() { return vehiculo; }
    public void setVehiculo(String vehiculo) { this.vehiculo = vehiculo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}