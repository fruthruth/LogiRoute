package com.logiroute.logiroute.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CodigoGeneratorTest {

    private final CodigoGenerator codigoGenerator = new CodigoGenerator();

    @Test
    void generarCodigoPedido_debeEmpezarConPED() {
        String codigo = codigoGenerator.generarCodigoPedido();
        assertTrue(codigo.startsWith("PED"), "El código debe empezar con 'PED'");
    }

    @Test
    void generarCodigoPedido_debeTener11Caracteres() {
        String codigo = codigoGenerator.generarCodigoPedido();
        assertEquals(11, codigo.length(), "El código debe tener 11 caracteres (PED + 8 dígitos)");
    }

    @Test
    void generarCodigoRuta_debeEmpezarConRUT() {
        String codigo = codigoGenerator.generarCodigoRuta();
        assertTrue(codigo.startsWith("RUT"), "El código debe empezar con 'RUT'");
    }

    @Test
    void generarCodigoRuta_debeTener11Caracteres() {
        String codigo = codigoGenerator.generarCodigoRuta();
        assertEquals(11, codigo.length(), "El código debe tener 11 caracteres (RUT + 8 dígitos)");
    }

    @Test
    void generarCodigosPedido_debenSerUnicos() {
        String codigo1 = codigoGenerator.generarCodigoPedido();
        String codigo2 = codigoGenerator.generarCodigoPedido();
        assertNotEquals(codigo1, codigo2, "Dos códigos generados no deben ser iguales");
    }

    @Test
    void generarCodigoPedido_debeContenerSoloDigitosDespuesDelPrefijo() {
        String codigo = codigoGenerator.generarCodigoPedido();
        String sufijo = codigo.substring(3);
        assertDoesNotThrow(() -> Long.parseLong(sufijo), "Los 8 caracteres después de 'PED' deben ser numéricos");
    }
}
