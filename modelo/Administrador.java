package modelo;

public class Administrador {
    // Atributos exactos según el Diagrama MER
    private int idAdministrador;
    private String nombres;
    private String apellidos;
    private String correo;
    private String contrasenia;

    // Constructor vacío
    public Administrador() {
    }

    // Constructor con parámetros
    public Administrador(int idAdministrador, String nombres, String apellidos, String correo, String contrasenia) {
        this.idAdministrador = idAdministrador;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasenia = contrasenia;
    }

    // Métodos Getter y Setter
    public int getIdAdministrador() {
        return idAdministrador;
    }

    public void setIdAdministrador(int idAdministrador) {
        this.idAdministrador = idAdministrador;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
}