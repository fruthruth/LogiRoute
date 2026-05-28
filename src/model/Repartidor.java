package model;

public class Repartidor {

    private int id;
    private String nombre;
    private String email;
    private String password;
    private String telefono;
    private String vehiculo;
    private String estado;

    public Repartidor() {
        this.estado = "DISPONIBLE";
    }

    // Getters y Setters
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