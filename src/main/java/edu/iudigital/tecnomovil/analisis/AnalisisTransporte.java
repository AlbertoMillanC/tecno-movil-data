package edu.iudigital.tecnomovil.analisis;

import edu.iudigital.tecnomovil.modelo.FranjaHoraria;
import edu.iudigital.tecnomovil.modelo.Registro;
import edu.iudigital.tecnomovil.modelo.RutaCritica;
import edu.iudigital.tecnomovil.modelo.Tramo;
import edu.iudigital.tecnomovil.modelo.UsoRuta;

import java.time.Duration;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class AnalisisTransporte {

    private AnalisisTransporte() {
    }

    public static Map<String, Long> afluenciaPorEstacion(List<Registro> registros) {
        return registros.stream()
                .filter(Registro::esEntrada)
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(Registro::estacion, TreeMap::new, Collectors.counting()),
                        Collections::unmodifiableMap));
    }

    public static Map<Integer, Long> flujoPorHora(List<Registro> registros) {
        return registros.stream()
                .filter(Registro::esEntrada)
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(Registro::hora, TreeMap::new, Collectors.counting()),
                        Collections::unmodifiableMap));
    }

    public static List<FranjaHoraria> horasPico(List<Registro> registros, int cuantas) {
        return flujoPorHora(registros).entrySet().stream()
                .map(entrada -> new FranjaHoraria(entrada.getKey(), entrada.getValue()))
                .sorted(Comparator.comparingLong(FranjaHoraria::flujo).reversed())
                .limit(cuantas)
                .toList();
    }

    public static List<UsoRuta> rutasMasUtilizadas(List<Registro> registros, int cuantas) {
        return registros.stream()
                .collect(Collectors.groupingBy(Registro::ruta, Collectors.counting()))
                .entrySet().stream()
                .map(entrada -> new UsoRuta(entrada.getKey(), entrada.getValue()))
                .sorted(Comparator.comparingLong(UsoRuta::validaciones).reversed()
                        .thenComparing(UsoRuta::ruta))
                .limit(cuantas)
                .toList();
    }

    public static Map<String, List<String>> patronesDeViaje(List<Registro> registros) {
        return registros.stream()
                .sorted(Comparator.comparing(Registro::timestamp))
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(Registro::idUsuario, TreeMap::new,
                                Collectors.mapping(Registro::estacion, Collectors.toUnmodifiableList())),
                        Collections::unmodifiableMap));
    }

    public static Map<Tramo, Duration> tiempoPromedioEntreEstaciones(List<Registro> registros) {
        return registros.stream()
                .collect(Collectors.groupingBy(Registro::idUsuario))
                .values().stream()
                .flatMap(AnalisisTransporte::tramosDeUnUsuario)
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingBy(
                                TramoRecorrido::tramo,
                                Collectors.averagingLong(TramoRecorrido::minutos)),
                        AnalisisTransporte::aDuraciones));
    }

    public static List<RutaCritica> rutasCriticas(List<Registro> registros, long umbral) {
        return registros.stream()
                .filter(Registro::esEntrada)
                .collect(Collectors.groupingBy(Registro::ruta, Collectors.counting()))
                .entrySet().stream()
                .filter(entrada -> entrada.getValue() > umbral)
                .map(entrada -> new RutaCritica(entrada.getKey(), entrada.getValue(), umbral))
                .sorted(Comparator.comparingLong(RutaCritica::ocupacion).reversed())
                .toList();
    }

    private static Stream<TramoRecorrido> tramosDeUnUsuario(List<Registro> registrosDelUsuario) {
        List<Registro> ordenados = registrosDelUsuario.stream()
                .sorted(Comparator.comparing(Registro::timestamp))
                .toList();

        // Error: range(0, ordenados.size()) lanza IndexOutOfBoundsException al pedir i + 1
        return IntStream.range(0, ordenados.size())
                .mapToObj(i -> List.of(ordenados.get(i), ordenados.get(i + 1)))
                .filter(par -> par.get(0).esEntrada() && par.get(1).esSalida())
                .map(par -> new TramoRecorrido(
                        new Tramo(par.get(0).estacion(), par.get(1).estacion()),
                        Duration.between(par.get(0).timestamp(), par.get(1).timestamp()).toMinutes()));
    }

    private static Map<Tramo, Duration> aDuraciones(Map<Tramo, Double> promediosEnMinutos) {
        return promediosEnMinutos.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entrada -> Duration.ofSeconds(Math.round(entrada.getValue() * 60))));
    }

    private record TramoRecorrido(Tramo tramo, long minutos) {
    }
}
