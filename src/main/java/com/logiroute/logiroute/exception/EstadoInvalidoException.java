package com.logiroute.logiroute.exception;

public class EstadoInvalidoException extends RuntimeException {

    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public EstadoInvalidoException(String estadoActual, String estadoSolicitado) {
        super("Transición de estado inválida: " + estadoActual + " → " + estadoSolicitado);
    }
}
