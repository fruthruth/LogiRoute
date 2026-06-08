package com.logiroute.logiroute.service;

import com.logiroute.logiroute.dto.PedidoDTO;
import com.logiroute.logiroute.dto.SeguimientoDTO;
import com.logiroute.logiroute.model.*;
import com.logiroute.logiroute.repository.*;
import com.logiroute.logiroute.utils.CodigoGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final RepartidorRepository repartidorRepository;

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Optional<Pedido> obtenerPorCodigo(String codigo) {
        return pedidoRepository.findByCodigo(codigo);
    }

}
