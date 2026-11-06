package edu.iudigital.tecnomovil.modelo;

public record Tramo(String origen, String destino) {

    public String etiqueta() {
        return origen + " hacia " + destino;
    }
}
