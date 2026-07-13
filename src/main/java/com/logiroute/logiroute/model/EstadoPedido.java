package com.logiroute.logiroute.model;

import java.util.Set;

public enum EstadoPedido {
    PENDIENTE,
    ASIGNADO,
    EN_RECOJO,
    EN_TRANSITO,
    ENTREGADO,
    CANCELADO;

    private static final java.util.Map<EstadoPedido, Set<EstadoPedido>> TRANSICIONES = java.util.Map.of(
            PENDIENTE, Set.of(ASIGNADO, CANCELADO),
            ASIGNADO, Set.of(EN_RECOJO, CANCELADO),
            EN_RECOJO, Set.of(EN_TRANSITO, CANCELADO),
            EN_TRANSITO, Set.of(ENTREGADO, CANCELADO),
            ENTREGADO, Set.of(),
            CANCELADO, Set.of()
    );

    public boolean esTransicionValida(EstadoPedido nuevoEstado) {
        return TRANSICIONES.getOrDefault(this, Set.of()).contains(nuevoEstado);
    }
}
