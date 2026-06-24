package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Pedido;
import com.logiroute.logiroute.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final PedidoRepository pedidoRepository;

    public List<Pedido> filtrar(Integer dia, Integer mes, Integer anio,
                                String responsable, Double costoMin, Double costoMax) {
        return pedidoRepository.findAll().stream()
                .filter(p -> dia == null || p.getCreatedAt().getDayOfMonth() == dia)
                .filter(p -> mes == null || p.getCreatedAt().getMonthValue() == mes)
                .filter(p -> anio == null || p.getCreatedAt().getYear() == anio)
                .filter(p -> responsable == null || responsable.isBlank() ||
                        (p.getRepartidor() != null &&
                                p.getRepartidor().getUsuario().getNombre()
                                        .toLowerCase().contains(responsable.toLowerCase())))
                .filter(p -> costoMin == null || (p.getCosto() != null &&
                        p.getCosto().doubleValue() >= costoMin))
                .filter(p -> costoMax == null || (p.getCosto() != null &&
                        p.getCosto().doubleValue() <= costoMax))
                .collect(Collectors.toList());
    }
}
