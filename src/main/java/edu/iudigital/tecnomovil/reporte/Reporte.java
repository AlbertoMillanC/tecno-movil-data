package edu.iudigital.tecnomovil.reporte;

import edu.iudigital.tecnomovil.modelo.FranjaHoraria;
import edu.iudigital.tecnomovil.modelo.RutaCritica;
import edu.iudigital.tecnomovil.modelo.Tramo;
import edu.iudigital.tecnomovil.modelo.UsoRuta;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Reporte {

    private Reporte() {
    }

    public static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("=".repeat(texto.length()));
    }

    public static void afluencia(Map<String, Long> conteos) {
        conteos.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entrada -> System.out.printf("  %-16s %8d ingresos%n",
                        entrada.getKey(), entrada.getValue()));
    }

    public static void horasPico(List<FranjaHoraria> franjas) {
        franjas.forEach(franja -> System.out.printf("  %-18s %8d validaciones%n",
                franja.etiqueta(), franja.flujo()));
    }

    public static void rutas(List<UsoRuta> rutas) {
        rutas.forEach(ruta -> System.out.printf("  %-28s %8d registros%n",
                ruta.ruta(), ruta.validaciones()));
    }

    public static void patrones(Map<String, List<String>> patrones, int cuantos) {
        patrones.entrySet().stream()
                .sorted(Comparator.comparingInt(entrada -> -entrada.getValue().size()))
                .limit(cuantos)
                .forEach(entrada -> System.out.printf("  %-12s %d estaciones  %s%n",
                        entrada.getKey(),
                        entrada.getValue().size(),
                        String.join(" -> ", entrada.getValue())));
    }

    public static void tramos(Map<Tramo, Duration> promedios, int cuantos) {
        promedios.entrySet().stream()
                .sorted(Comparator.comparing(entrada -> entrada.getKey().etiqueta()))
                .limit(cuantos)
                .forEach(entrada -> System.out.printf("  %-38s %5d min%n",
                        entrada.getKey().etiqueta(), entrada.getValue().toMinutes()));
    }

    public static void criticas(List<RutaCritica> rutas) {
        if (rutas.isEmpty()) {
            System.out.println("  Ninguna ruta supero el umbral.");
            return;
        }
        rutas.forEach(ruta -> System.out.printf("  CRITICA  %-28s %8d  (%+.1f%% sobre el umbral)%n",
                ruta.ruta(), ruta.ocupacion(), ruta.porcentajeSobreUmbral()));
    }

    public static void lineas(List<String> textos) {
        textos.forEach(texto -> System.out.println("  " + texto));
    }
}
