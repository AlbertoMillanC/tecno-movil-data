/**
 * CatalogoRed.java
 */
package edu.iudigital.tecnomovil.datos;

import java.util.List;
import java.util.Map;

public final class CatalogoRed {

    public static final List<String> RUTAS = List.of(
            "T-01 Troncal Norte",
            "T-02 Troncal Sur",
            "A-11 Alimentador Oriente",
            "A-12 Alimentador Occidente",
            "C-21 Circular Centro");

    public static final Map<String, List<String>> ESTACIONES_POR_RUTA = Map.of(
            "T-01 Troncal Norte", List.of("Portal Norte", "La Ceiba", "San Jose", "Centro Mayor"),
            "T-02 Troncal Sur", List.of("Portal Sur", "El Mirador", "Las Palmas", "Centro Mayor"),
            "A-11 Alimentador Oriente", List.of("Loma Verde", "El Rosal", "San Jose"),
            "A-12 Alimentador Occidente", List.of("Villa Luz", "El Prado", "Las Palmas"),
            "C-21 Circular Centro", List.of("Centro Mayor", "Plaza Bolivar", "Estadio", "Universidad"));

    public static final Map<String, Integer> PESO_DE_DEMANDA = Map.of(
            "T-01 Troncal Norte", 32,
            "T-02 Troncal Sur", 28,
            "A-11 Alimentador Oriente", 16,
            "A-12 Alimentador Occidente", 14,
            "C-21 Circular Centro", 10);

    private CatalogoRed() {
    }

    public static List<String> estacionesDe(String ruta) {
        return ESTACIONES_POR_RUTA.getOrDefault(ruta, List.of());
    }
}
