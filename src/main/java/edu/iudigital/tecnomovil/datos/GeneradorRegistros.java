/**
 * GeneradorRegistros.java
 */
package edu.iudigital.tecnomovil.datos;

import edu.iudigital.tecnomovil.modelo.Accion;
import edu.iudigital.tecnomovil.modelo.Registro;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class GeneradorRegistros {

    private static final LocalDate DIA = LocalDate.of(2026, 10, 26);
    private static final int[] PESO_POR_HORA = {
            1, 1, 1, 1, 2, 6, 16, 24, 18, 9, 6, 6,
            8, 7, 6, 7, 13, 22, 16, 9, 5, 3, 2, 1};

    private GeneradorRegistros() {
    }

    public static List<Registro> generar(int cantidadUsuarios, long semilla) {
        Random aleatorio = new Random(semilla);
        return IntStream.range(0, cantidadUsuarios)
                .boxed()
                .flatMap(indice -> viajesDeUnUsuario(indice, aleatorio))
                .toList();
    }

    private static Stream<Registro> viajesDeUnUsuario(int indice, Random aleatorio) {
        String usuario = String.format("U-%06d", indice);
        int cuantosViajes = 1 + aleatorio.nextInt(3);
        List<Registro> registros = new ArrayList<>();
        LocalDateTime disponibleDesde = primeraSalida(aleatorio);

        for (int viaje = 0; viaje < cuantosViajes; viaje++) {
            String ruta = rutaSegunDemanda(aleatorio);
            List<String> estaciones = CatalogoRed.estacionesDe(ruta);
            int origen = aleatorio.nextInt(estaciones.size());
            int saltos = 1 + aleatorio.nextInt(estaciones.size() - 1);
            int destino = (origen + saltos) % estaciones.size();
            int minutos = 4 + saltos * 7 + aleatorio.nextInt(6);

            LocalDateTime entrada = disponibleDesde;
            LocalDateTime salida = entrada.plusMinutes(minutos);

            registros.add(new Registro(usuario, ruta, estaciones.get(origen), Accion.ENTRADA, entrada));
            registros.add(new Registro(usuario, ruta, estaciones.get(destino), Accion.SALIDA, salida));

            disponibleDesde = salida.plusMinutes(45 + aleatorio.nextInt(300));
            if (disponibleDesde.toLocalDate().isAfter(DIA)) {
                break;
            }
        }
        return registros.stream();
    }

    private static String rutaSegunDemanda(Random aleatorio) {
        int total = CatalogoRed.RUTAS.stream()
                .mapToInt(ruta -> CatalogoRed.PESO_DE_DEMANDA.get(ruta))
                .sum();
        int dardo = aleatorio.nextInt(total);
        int acumulado = 0;
        for (String ruta : CatalogoRed.RUTAS) {
            acumulado += CatalogoRed.PESO_DE_DEMANDA.get(ruta);
            if (dardo < acumulado) {
                return ruta;
            }
        }
        return CatalogoRed.RUTAS.get(0);
    }

    private static LocalDateTime primeraSalida(Random aleatorio) {
        int total = IntStream.of(PESO_POR_HORA).sum();
        int dardo = aleatorio.nextInt(total);
        int acumulado = 0;
        int hora = 0;
        for (int i = 0; i < PESO_POR_HORA.length; i++) {
            acumulado += PESO_POR_HORA[i];
            if (dardo < acumulado) {
                hora = i;
                break;
            }
        }
        return DIA.atTime(hora, aleatorio.nextInt(60), aleatorio.nextInt(60));
    }
}
