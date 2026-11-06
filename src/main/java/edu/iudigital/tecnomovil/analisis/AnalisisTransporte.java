package edu.iudigital.tecnomovil.analisis;

import edu.iudigital.tecnomovil.modelo.FranjaHoraria;
import edu.iudigital.tecnomovil.modelo.Registro;
import edu.iudigital.tecnomovil.modelo.UsoRuta;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

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
}
