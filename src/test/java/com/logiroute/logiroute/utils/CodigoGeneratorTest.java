package com.logiroute.logiroute.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodigoGeneratorTest {

    @Test
    void generarCodigoPedido_debeEmpezarConPED() {
        String codigo = CodigoGenerator.generarCodigoPedido();
        assertTrue(codigo.startsWith("PED"), "El código debe empezar con 'PED'");
    }

    @Test
    void generarCodigoPedido_debeTener11Caracteres() {
        String codigo = CodigoGenerator.generarCodigoPedido();
        assertEquals(11, codigo.length(), "El código debe tener 11 caracteres (PED + 8 dígitos)");
    }

    @Test
    void generarCodigoRuta_debeEmpezarConRUT() {
        String codigo = CodigoGenerator.generarCodigoRuta();
        assertTrue(codigo.startsWith("RUT"), "El código debe empezar con 'RUT'");
    }

    @Test
    void generarCodigoRuta_debeTener11Caracteres() {
        String codigo = CodigoGenerator.generarCodigoRuta();
        assertEquals(11, codigo.length(), "El código debe tener 11 caracteres (RUT + 8 dígitos)");
    }

    @Test
    void generarCodigosPedido_debenSerUnicos() {
        String codigo1 = CodigoGenerator.generarCodigoPedido();
        String codigo2 = CodigoGenerator.generarCodigoPedido();
        assertNotEquals(codigo1, codigo2, "Dos códigos generados no deben ser iguales");
    }

    @Test
    void generarCodigoPedido_debeContenerSoloDigitosDespuesDelPrefijo() {
        String codigo = CodigoGenerator.generarCodigoPedido();
        String sufijo = codigo.substring(3);
        assertDoesNotThrow(() -> Long.parseLong(sufijo), "Los 8 caracteres después de 'PED' deben ser numéricos");
    }
}
