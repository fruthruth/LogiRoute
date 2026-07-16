package com.logiroute.logiroute.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "incidentes")
public class Incidente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoIncidente tipo;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public String getTipoTexto() {
        return tipo == null ? "" : tipo.getTexto();
    }

    public enum TipoIncidente {
        RETRASO("Retraso"),
        DANO("Daño"),
        ROBO("Robo"),
        DIRECCION_INCORRECTA("Dirección incorrecta"),
        CLIENTE_AUSENTE("Cliente ausente");

        private final String texto;

        TipoIncidente(String texto) {
            this.texto = texto;
        }

        public String getTexto() {
            return texto;
        }
    }
}
