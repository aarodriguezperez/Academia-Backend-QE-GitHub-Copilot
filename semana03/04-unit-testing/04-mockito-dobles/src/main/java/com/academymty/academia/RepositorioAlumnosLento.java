package com.academymty.academia;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * La implementacion "real". Un Map con una siesta de 300 ms por consulta.
 *
 * Los 300 ms son una simulacion, pero el problema que representan es real:
 * una consulta a una base de datos por la red no cuesta microsegundos.
 *
 * Existe para el experimento de scripts/por-que-mockear.sh: la misma suite
 * contra esta clase y contra un mock, cronometrada. Es la unica forma
 * honesta de justificar por que existe Mockito.
 */
public class RepositorioAlumnosLento implements RepositorioAlumnos {

    public static final long LATENCIA_MS = 300;

    private final Map<String, Alumno> tabla = new LinkedHashMap<>();

    public RepositorioAlumnosLento() {
        guardar(new Alumno("A01", "Ana Torres", "ana@academymty.mx"));
        guardar(new Alumno("A02", "Beto Ruiz", "beto@academymty.mx"));
        guardar(new Alumno("A03", "Carla Diaz", "carla@academymty.mx"));
    }

    @Override
    public Optional<Alumno> buscar(String matricula) {
        dormir();
        return Optional.ofNullable(tabla.get(matricula));
    }

    @Override
    public void guardar(Alumno alumno) {
        tabla.put(alumno.matricula(), alumno);
    }

    private static void dormir() {
        try {
            Thread.sleep(LATENCIA_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrumpido consultando la base", e);
        }
    }
}
