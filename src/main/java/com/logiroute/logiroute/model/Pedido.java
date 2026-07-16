package com.logiroute.logiroute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id")
    private Ruta ruta;

    @Column(nullable = false)
    private String direccionOrigen;

    @Column(nullable = false)
    private String direccionDestino;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal peso;

    @Column(nullable = false, length = 50)
    private String tipoPaquete;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @Column(precision = 10, scale = 2)
    private BigDecimal costo;

    @Column(name = "fecha_estimada")
    private LocalDateTime fechaEstimada;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getEstadoNombre() {
        return estado == null ? "" : estado.name();
    }

    public boolean isPendiente() { return estado == EstadoPedido.PENDIENTE; }
    public boolean isAsignado() { return estado == EstadoPedido.ASIGNADO; }
    public boolean isEnRecojo() { return estado == EstadoPedido.EN_RECOJO; }
    public boolean isEnTransito() { return estado == EstadoPedido.EN_TRANSITO; }
    public boolean isEntregado() { return estado == EstadoPedido.ENTREGADO; }
    public boolean isCancelado() { return estado == EstadoPedido.CANCELADO; }

    /**
     * Retorna la clase CSS según el estado del pedido.
     * Para agregar un nuevo estado, solo agregar un case aquí.
     */
    public String getEstadoCssClass() {
        if (estado == null) return "";
        return switch (estado) {
            case ENTREGADO -> "estado-entregado";
            case PENDIENTE -> "estado-pendiente";
            case CANCELADO -> "estado-cancelado";
            default -> "estado-transito";
        };
    }

    /**
     * Retorna el texto visible según el estado del pedido.
     * Para agregar un nuevo estado, solo agregar un case aquí.
     */
    public String getEstadoTexto() {
        if (estado == null) return "";
        return switch (estado) {
            case PENDIENTE -> "Pendiente";
            case ASIGNADO -> "Asignado";
            case EN_RECOJO -> "En recojo";
            case EN_TRANSITO -> "En camino";
            case ENTREGADO -> "Entregado";
            case CANCELADO -> "Cancelado";
        };
    }
}
