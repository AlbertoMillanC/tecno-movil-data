package edu.iudigital.tecnomovil.analisis;

import edu.iudigital.tecnomovil.modelo.Registro;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Medicion {

    private Medicion() {
    }

    public static Map<String, Long> afluenciaEnParalelo(List<Registro> registros) {
        return registros.parallelStream()
                .filter(Registro::esEntrada)
                .collect(Collectors.collectingAndThen(
                        Collectors.groupingByConcurrent(Registro::estacion, Collectors.counting()),
                        Collections::unmodifiableMap));
    }

    public static void calentar(List<Registro> registros) {
        IntStream.range(0, 5).forEach(vuelta -> {
            AnalisisTransporte.afluenciaPorEstacion(registros);
            afluenciaEnParalelo(registros);
        });
    }

    public static <T> long milisegundosDe(Function<List<Registro>, T> operacion,
                                          List<Registro> registros) {
        long inicio = System.nanoTime();
        operacion.apply(registros);
        return (System.nanoTime() - inicio) / 1_000_000;
    }

}
