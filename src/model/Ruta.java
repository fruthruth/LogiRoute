package model;

public class Ruta {
    private int idRuta;
    private String fechaRuta;
    private double distanciaEstimada;
    private String tiempoEstimado;
    private String estado;
    private int idRepartidor;

    // Constructor vacío
    public Ruta() {
    }

    // Constructor con parámetros
    public Ruta(int idRuta, String fechaRuta, double distanciaEstimada, String tiempoEstimado, String estado, int idRepartidor) {
        this.idRuta = idRuta;
        this.fechaRuta = fechaRuta;
        this.distanciaEstimada = distanciaEstimada;
        this.tiempoEstimado = tiempoEstimado;
        this.estado = estado;
        this.idRepartidor = idRepartidor;
    }

    // Métodos Getter y Setter
    public int getIdRuta() { return idRuta; }
    public void setIdRuta(int idRuta) { this.idRuta = idRuta; }

    public String getFechaRuta() { return fechaRuta; }
    public void setFechaRuta(String fechaRuta) { this.fechaRuta = fechaRuta; }

    public double getDistanciaEstimada() { return distanciaEstimada; }
    public void setDistanciaEstimada(double distanciaEstimada) { this.distanciaEstimada = distanciaEstimada; }

    public String getTiempoEstimado() { return tiempoEstimado; }
    public void setTiempoEstimado(String tiempoEstimado) { this.tiempoEstimado = tiempoEstimado; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getIdRepartidor() { return idRepartidor; }
    public void setIdRepartidor(int idRepartidor) { this.idRepartidor = idRepartidor; }
}