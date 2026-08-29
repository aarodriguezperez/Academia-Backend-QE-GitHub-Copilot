package com.academymty.academia;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Un curso de la academia con cupo limitado.
 *
 * El codigo bajo prueba del proyecto 02. Elegido a proposito porque
 * casi todo lo que hace es RECHAZAR cosas -- y probar rechazos es
 * justo lo que ensena este proyecto.
 *
 *   inscribir()  puede fallar de tres maneras distintas
 *   cerrar()     cambia el estado y con el, que operaciones son validas
 */
public class Curso {

    private final String clave;
    private final int cupo;
    private final Set<String> inscritos = new LinkedHashSet<>();
    private boolean abierto = true;

    public Curso(String clave, int cupo) {
        if (cupo <= 0) {
            throw new IllegalArgumentException("Un curso sin cupo no es un curso: " + cupo);
        }
        this.clave = clave;
        this.cupo = cupo;
    }

    /**
     * @throws IllegalStateException    si las inscripciones ya cerraron
     * @throws CupoLlenoException       si no quedan lugares
     * @throws IllegalArgumentException si esa matricula ya estaba inscrita
     */
    public void inscribir(String matricula) {
        if (!abierto) {
            throw new IllegalStateException("El curso " + clave + " ya cerro inscripciones");
        }
        if (inscritos.size() >= cupo) {
            throw new CupoLlenoException(clave, cupo);
        }
        if (!inscritos.add(matricula)) {
            throw new IllegalArgumentException("La matricula " + matricula + " ya estaba inscrita");
        }
    }

    public void cerrar() {
        abierto = false;
    }

    public boolean estaAbierto() {
        return abierto;
    }

    public int lugaresDisponibles() {
        return cupo - inscritos.size();
    }

    public boolean estaLleno() {
        return lugaresDisponibles() == 0;
    }

    public String clave() {
        return clave;
    }

    public int cupo() {
        return cupo;
    }

    public Set<String> inscritos() {
        return Collections.unmodifiableSet(inscritos);
    }

    /**
     * Genera el acta en texto. Simula el tiempo que tarda un reporte real
     * (consultar la base, formatear, escribir): 40 ms por alumno.
     *
     * Existe para la seccion del tiempo. Es lo unico lento del proyecto.
     */
    public String actaDeInscripcion() {
        StringBuilder sb = new StringBuilder("ACTA " + clave + "\n");
        for (String matricula : inscritos) {
            dormir(40);
            sb.append(" - ").append(matricula).append('\n');
        }
        return sb.toString();
    }

    private static void dormir(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrumpido generando el acta", e);
        }
    }
}
