package edu.iudigital.tecnomovil;

import edu.iudigital.tecnomovil.analisis.AnalisisTransporte;
import edu.iudigital.tecnomovil.analisis.Medicion;
import edu.iudigital.tecnomovil.analisis.Operadores;
import edu.iudigital.tecnomovil.datos.CatalogoRed;
import edu.iudigital.tecnomovil.datos.GeneradorRegistros;
import edu.iudigital.tecnomovil.modelo.Registro;
import edu.iudigital.tecnomovil.reporte.Reporte;

import java.util.List;
import java.util.Map;

public class Main {

    private static final int USUARIOS = 120_000;
    private static final long SEMILLA = 2026;
    private static final long UMBRAL_CRITICO = 60_000;

    public static void main(String[] args) {

        List<Registro> registros = GeneradorRegistros.generar(USUARIOS, SEMILLA);

        Reporte.titulo("TECNOMOVIL DATA  procesamiento funcional del dia");
        System.out.printf("  Usuarios simulados  %,10d%n", USUARIOS);
        System.out.printf("  Registros totales   %,10d%n", registros.size());
        System.out.printf("  Rutas en operacion  %,10d%n", CatalogoRed.RUTAS.size());

        Reporte.titulo("a) Afluencia por estacion");
        Reporte.afluencia(AnalisisTransporte.afluenciaPorEstacion(registros));

        Reporte.titulo("b) Horas pico");
        Reporte.horasPico(AnalisisTransporte.horasPico(registros, 5));

        Reporte.titulo("c) Rutas mas utilizadas");
        Reporte.rutas(AnalisisTransporte.rutasMasUtilizadas(registros, 5));

        Reporte.titulo("d) Patrones de viaje por usuario");
        Reporte.patrones(AnalisisTransporte.patronesDeViaje(registros), 5);

        Reporte.titulo("e) Tiempo promedio entre estaciones");
        Reporte.tramos(AnalisisTransporte.tiempoPromedioEntreEstaciones(registros), 8);

        Reporte.titulo("f) Rutas en sobrecarga");
        Reporte.criticas(AnalisisTransporte.rutasCriticas(registros, UMBRAL_CRITICO));

        Reporte.titulo("Funcion de orden superior, la misma operacion con otro criterio");
        Map<String, Long> porAccion = Operadores.contarPor(registros, registro -> registro.accion().name());
        porAccion.forEach((clave, valor) -> System.out.printf("  %-10s %,10d%n", clave, valor));

        Reporte.titulo("Composicion de funciones");
        Reporte.lineas(Operadores.rankingDeEstaciones(3).apply(registros));

        Reporte.titulo("Filtro compuesto, hora pico de la manana en la Troncal Norte");
        List<Registro> recorte = Operadores.filtrar(registros,
                Operadores.esEntrada()
                        .and(Operadores.enFranja(6, 8))
                        .and(Operadores.deRuta("T-01 Troncal Norte")));
        System.out.printf("  Registros que cumplen los tres criterios  %,10d%n", recorte.size());

        Reporte.titulo("Secuencial contra paralelo");
        System.out.printf("  Nucleos disponibles %4d%n", Runtime.getRuntime().availableProcessors());
        Medicion.calentar(registros);
        long secuencial = Medicion.milisegundosDe(AnalisisTransporte::afluenciaPorEstacion, registros);
        long paralelo = Medicion.milisegundosDe(Medicion::afluenciaEnParalelo, registros);
        System.out.printf("  stream()          %6d ms%n", secuencial);
        System.out.printf("  parallelStream()  %6d ms%n", paralelo);
        System.out.println();
        System.out.println("  Los dos devuelven el mismo resultado: "
                + AnalisisTransporte.afluenciaPorEstacion(registros)
                .equals(Medicion.afluenciaEnParalelo(registros)));

        Reporte.titulo("La lista original nunca cambio");
        System.out.printf("  Registros al terminar  %,10d%n", registros.size());
    }
}