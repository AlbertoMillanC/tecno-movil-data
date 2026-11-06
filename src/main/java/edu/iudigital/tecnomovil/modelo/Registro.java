package edu.iudigital.tecnomovil.modelo;

import java.time.LocalDateTime;

public record Registro(String idUsuario,
                       String ruta,
                       String estacion,
                       Accion accion,
                       LocalDateTime timestamp) {

    public boolean esEntrada() {
        return accion == Accion.ENTRADA;
    }

    public boolean esSalida() {
        return accion == Accion.SALIDA;
    }

    public int hora() {
        return timestamp.getHour();
    }
}
