package com.logiroute.logiroute.exception;

/**
 * Se lanza cuando una asignación no cumple las reglas operativas del sistema.
 */
public class AsignacionInvalidaException extends RuntimeException {

    public AsignacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}
