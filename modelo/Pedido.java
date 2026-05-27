package modelo;

import java.time.LocalDateTime;

public class Pedido {
    // Atributos principales según el Modelo Relacional
    private int idPedido;
    private String codigoSeguimiento;
    private String direccionDestino;
    private double peso;
    private String ventanaHoraria;
    private String estado;
    private LocalDateTime fechaRegistro; 

    // Claves foráneas (Relaciones)
    private int idCliente;
    private int idRepartidor;

    // Constructor vacío
    public Pedido() {
    }

    // Constructor con parámetros
    public Pedido(int idPedido, String codigoSeguimiento, String direccionDestino, double peso, 
                  String ventanaHoraria, String estado, LocalDateTime fechaRegistro, 
                  int idCliente, int idRepartidor) {
        this.idPedido = idPedido;
        this.codigoSeguimiento = codigoSeguimiento;
        this.direccionDestino = direccionDestino;
        this.peso = peso;
        this.ventanaHoraria = ventanaHoraria;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.idCliente = idCliente;
        this.idRepartidor = idRepartidor;
    }

    // Getters y Setters
    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public String getCodigoSeguimiento() { return codigoSeguimiento; }
    public void setCodigoSeguimiento(String codigoSeguimiento) { this.codigoSeguimiento = codigoSeguimiento; }

    public String getDireccionDestino() { return direccionDestino; }
    public void setDireccionDestino(String direccionDestino) { this.direccionDestino = direccionDestino; }

    public double getPeso() { return peso; }
    public void setPeso(double peso) { this.peso = peso; }

    public String getVentanaHoraria() { return ventanaHoraria; }
    public void setVentanaHoraria(String ventanaHoraria) { this.ventanaHoraria = ventanaHoraria; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }

    public int getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(int idRepartidor) { this.idRepartidor = idRepartidor; }
}