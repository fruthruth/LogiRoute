package com.logiroute.logiroute.utils;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class CodigoGenerator {

    private static final String PREFIJOS = "PEDRUT";
    private static final long MIN_NUMERICO = 10000000L;
    private static final long MAX_NUMERICO = 99999999L;

    public String generarCodigoPedido() {
        String prefijo = PREFIJOS.substring(0, 3);
        long numero = ThreadLocalRandom.current().nextLong(MIN_NUMERICO, MAX_NUMERICO);
        return prefijo + numero;
    }

    public String generarCodigoRuta() {
        String prefijo = PREFIJOS.substring(3, 6);
        long numero = ThreadLocalRandom.current().nextLong(MIN_NUMERICO, MAX_NUMERICO);
        return prefijo + numero;
    }
}
