package edu.iudigital.tecnomovil.analisis;

import edu.iudigital.tecnomovil.modelo.Registro;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Operadores {

    private Operadores() {
    }

    public static <K> Map<K, Long> contarPor(List<Registro> registros,
                                             Function<Registro, K> clasificador) {
        return registros.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(clasificador, Collectors.counting()),
                        Collections::unmodifiableMap));
    }

    public static Predicate<Registro> enFranja(int horaInicio, int horaFin) {
        return registro -> registro.hora() >= horaInicio && registro.hora() <= horaFin;
    }

    public static Predicate<Registro> deRuta(String ruta) {
        return registro -> registro.ruta().equals(ruta);
    }

    public static Predicate<Registro> esEntrada() {
        return Registro::esEntrada;
    }

    public static List<Registro> filtrar(List<Registro> registros, Predicate<Registro> criterio) {
        return registros.stream()
                .filter(criterio)
                .toList();
    }

    public static Function<List<Registro>, List<Registro>> soloEntradas() {
        return registros -> filtrar(registros, esEntrada());
    }

    public static Function<List<Registro>, Map<String, Long>> agruparPorEstacion() {
        return registros -> registros.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(Registro::estacion, TreeMap::new, Collectors.counting()),
                        Collections::unmodifiableMap));
    }

    public static Function<Map<String, Long>, List<String>> topEstaciones(int cuantas) {
        return conteos -> conteos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(cuantas)
                .map(entrada -> entrada.getKey() + " con " + entrada.getValue() + " ingresos")
                .toList();
    }

    public static Function<List<Registro>, List<String>> rankingDeEstaciones(int cuantas) {
        return soloEntradas()
                .andThen(agruparPorEstacion())
                .andThen(topEstaciones(cuantas));
    }
}
