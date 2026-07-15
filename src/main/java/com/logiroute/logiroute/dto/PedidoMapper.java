package com.logiroute.logiroute.dto;

import com.logiroute.logiroute.dto.response.PedidoResponseDTO;
import com.logiroute.logiroute.model.Pedido;

public final class PedidoMapper {

    private PedidoMapper() {
    }

    public static PedidoResponseDTO toResponseDTO(Pedido p) {
        return PedidoResponseDTO.builder()
                .id(p.getId())
                .codigo(p.getCodigo())
                .clienteNombre(p.getCliente() != null ? p.getCliente().getUsuario().getNombre() : null)
                .repartidorNombre(p.getRepartidor() != null ? p.getRepartidor().getUsuario().getNombre() : null)
                .direccionOrigen(p.getDireccionOrigen())
                .direccionDestino(p.getDireccionDestino())
                .peso(p.getPeso())
                .tipoPaquete(p.getTipoPaquete())
                .estado(p.getEstado().name())
                .costo(p.getCosto())
                .fechaEstimada(p.getFechaEstimada())
                .fechaEntrega(p.getFechaEntrega())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
