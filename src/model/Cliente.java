package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cliente {
    private static final Logger logger = LoggerFactory.getLogger(Cliente.class);

    private int id;
    private String nombre;
    private String email;
    private String telefono;
    private String direccion;

    // Constructor vacío (Ya lo tenías excelente)
    public Cliente() {
        logger.info("Se ha creado una instancia de Cliente vacío");
    }

    // ====================================================================
    // CONSTRUCTOR CON PARÁMETROS 
    // ====================================================================
    public Cliente(int id, String nombre, String email, String telefono, String direccion) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
    }

    // Getters y Setters (Se quedan igual)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}