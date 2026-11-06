package edu.iudigital.tecnomovil.modelo;

public record RutaCritica(String ruta, long ocupacion, long umbral) {

    public double porcentajeSobreUmbral() {
        return ((double) ocupacion / umbral - 1) * 100;
    }
}
