package com.academymty.academia;

/**
 * Traduce una calificacion numerica al nivel del reglamento.
 *
 *   90 a 100  EXCELENTE
 *   80 a  89  BUENO
 *   70 a  79  SUFICIENTE
 *    0 a  69  INSUFICIENTE
 */
public final class Calificador {

    private Calificador() {
    }

    public static Nivel nivelDe(int calificacion) {
        if (calificacion < 0 || calificacion > 100) {
            throw new IllegalArgumentException("Calificacion fuera de rango: " + calificacion);
        }
        if (calificacion >= 90) {
            return Nivel.EXCELENTE;
        }
        if (calificacion >= 80) {
            return Nivel.BUENO;
        }
        if (calificacion >= 70) {
            return Nivel.SUFICIENTE;
        }
        return Nivel.INSUFICIENTE;
    }
}
