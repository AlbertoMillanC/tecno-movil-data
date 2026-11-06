package edu.iudigital.tecnomovil.modelo;

public record FranjaHoraria(int hora, long flujo) {

    public String etiqueta() {
        return String.format("%02d:00 a %02d:59", hora, hora);
    }
}
