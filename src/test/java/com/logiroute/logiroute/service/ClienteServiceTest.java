package com.logiroute.logiroute.service;

import com.logiroute.logiroute.model.Cliente;
import com.logiroute.logiroute.model.Usuario;
import com.logiroute.logiroute.repository.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    @Test
    void listarTodos_debeRetornarListaDeClientes() {
        Usuario usuario1 = Usuario.builder().id(1L).nombre("Cliente 1").build();
        Usuario usuario2 = Usuario.builder().id(2L).nombre("Cliente 2").build();

        Cliente cliente1 = Cliente.builder().id(1L).usuario(usuario1).telefono("999111222").direccion("Av. A").build();
        Cliente cliente2 = Cliente.builder().id(2L).usuario(usuario2).telefono("999333444").direccion("Av. B").build();

        when(clienteRepository.findAll()).thenReturn(List.of(cliente1, cliente2));

        List<Cliente> resultado = clienteService.listarTodos();

        assertEquals(2, resultado.size());
        verify(clienteRepository, times(1)).findAll();
    }

    @Test
    void obtenerPorId_debeRetornarCliente() {
        Usuario usuario = Usuario.builder().id(1L).nombre("Cliente Test").build();
        Cliente cliente = Cliente.builder()
                .id(1L)
                .usuario(usuario)
                .telefono("999888777")
                .direccion("Av. Principal 123")
                .build();

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("Av. Principal 123", resultado.get().getDireccion());
    }

    @Test
    void obtenerPorUsuarioId_debeRetornarCliente() {
        Usuario usuario = Usuario.builder().id(5L).nombre("Cliente Usuario").build();
        Cliente cliente = Cliente.builder()
                .id(10L)
                .usuario(usuario)
                .telefono("988777666")
                .direccion("Jr. Comercio 456")
                .build();

        when(clienteRepository.findByUsuarioId(5L)).thenReturn(Optional.of(cliente));

        Optional<Cliente> resultado = clienteService.obtenerPorUsuarioId(5L);

        assertTrue(resultado.isPresent());
        assertEquals(10L, resultado.get().getId());
        assertEquals(usuario, resultado.get().getUsuario());
    }
}
