package com.academymty.academia;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * La boleta de un alumno: sus materias con calificacion, el promedio
 * y la decision de si aprueba el semestre.
 *
 * Esta clase es el "codigo bajo prueba" de todo el proyecto 01.
 * Tiene exactamente tres reglas de negocio, y las tres se pueden romper:
 *
 *   1. Una calificacion valida va de 0 a 100.
 *   2. El promedio es la media de las materias registradas.
 *   3. Se aprueba con 70 o mas.  <- la regla del limite, la que se rompe sola
 */
public class Boleta {

    public static final int CALIFICACION_MINIMA = 0;
    public static final int CALIFICACION_MAXIMA = 100;

    /** Con 70 se aprueba. Con 69.9 no. Ese "o mas" es el corazon del proyecto. */
    public static final double MINIMA_APROBATORIA = 70.0;

    private final Alumno alumno;
    private final Map<String, Integer> calificaciones = new LinkedHashMap<>();

    public Boleta(Alumno alumno) {
        if (alumno == null) {
            throw new IllegalArgumentException("Una boleta necesita un alumno");
        }
        this.alumno = alumno;
    }

    /**
     * Registra la calificacion de una materia. Si la materia ya estaba,
     * la sobrescribe.
     *
     * @throws IllegalArgumentException si la calificacion se sale de 0..100
     */
    public void registrar(String materia, int calificacion) {
        if (materia == null || materia.isBlank()) {
            throw new IllegalArgumentException("La materia no puede venir vacia");
        }
        if (calificacion < CALIFICACION_MINIMA || calificacion > CALIFICACION_MAXIMA) {
            throw new IllegalArgumentException(
                    "Calificacion fuera de rango: " + calificacion
                    + " (valido: " + CALIFICACION_MINIMA + " a " + CALIFICACION_MAXIMA + ")");
        }
        calificaciones.put(materia, calificacion);
    }

    /** Media de las materias registradas. Una boleta sin materias promedia 0. */
    public double promedio() {
        if (calificaciones.isEmpty()) {
            return 0.0;
        }
        double suma = 0;
        for (int c : calificaciones.values()) {
            suma += c;
        }
        return suma / calificaciones.size();
    }

    /**
     * La regla del limite.
     *
     * Borrar el '=' de ese >= es UN SOLO caracter, y es el que separa aprobar
     * de reprobar a quien saco exactamente 70. Hazlo y casi todos los tests siguen en
     * verde: solo cae el que prueba el limite. Eso es la seccion 05 de la guia.
     */
    public boolean aprobado() {
        return promedio() >= MINIMA_APROBATORIA;
    }

    public int totalMaterias() {
        return calificaciones.size();
    }

    public Alumno alumno() {
        return alumno;
    }

    public Map<String, Integer> calificaciones() {
        return Collections.unmodifiableMap(calificaciones);
    }
}
