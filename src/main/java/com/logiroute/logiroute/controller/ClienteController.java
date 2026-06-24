package com.logiroute.logiroute.controller;

import com.logiroute.logiroute.dto.response.ClienteResponseDTO;
import com.logiroute.logiroute.dto.response.PedidoResponseDTO;
import com.logiroute.logiroute.model.Cliente;
import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.service.IClienteService;
import com.logiroute.logiroute.service.IPedidoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private static final Logger log = LoggerFactory.getLogger(ClienteController.class);

    private final IClienteService clienteService;
    private final IPedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<ClienteResponseDTO>> listarTodos() {
        log.debug("API: Listando todos los clientes");
        List<ClienteResponseDTO> clientes = clienteService.listarTodos().stream()
                .map(this::toResponseDTO)
                .toList();
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorId(@PathVariable Long id) {
        log.debug("API: Buscando cliente id: {}", id);
        return clienteService.obtenerPorId(id)
                .map(c -> ResponseEntity.ok(toResponseDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<ClienteResponseDTO> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        log.debug("API: Buscando cliente por usuario id: {}", usuarioId);
        return clienteService.obtenerPorUsuarioId(usuarioId)
                .map(c -> ResponseEntity.ok(toResponseDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<PedidoResponseDTO>> listarPedidosPorCliente(@PathVariable Long id) {
        log.debug("API: Listando pedidos del cliente id: {}", id);
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos().stream()
                .filter(p -> p.getCliente() != null && p.getCliente().getId().equals(id))
                .map(this::toPedidoResponseDTO)
                .toList();
        return ResponseEntity.ok(pedidos);
    }

    private ClienteResponseDTO toResponseDTO(Cliente c) {
        return ClienteResponseDTO.builder()
                .id(c.getId())
                .nombre(c.getUsuario().getNombre())
                .email(c.getUsuario().getEmail())
                .telefono(c.getTelefono())
                .direccion(c.getDireccion())
                .build();
    }

    private PedidoResponseDTO toPedidoResponseDTO(Pedido p) {
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
