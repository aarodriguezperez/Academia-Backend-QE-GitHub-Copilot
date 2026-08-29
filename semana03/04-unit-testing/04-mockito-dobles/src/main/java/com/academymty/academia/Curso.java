package com.academymty.academia;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Un curso con cupo. El mismo del proyecto 02, recortado a lo que hace falta.
 *
 * ESTA CLASE NO SE DEBE MOCKEAR, y ese es el tema de la seccion 09.
 *
 * Motivo: es rapida, no toca nada de fuera, y sobre todo DECIDE. Toda la
 * regla de negocio del cupo vive en estaLleno(). Un test que la sustituye
 * por un mock deja de probar la regla y pasa a probar la respuesta que el
 * propio test escribio. El script scripts/la-mentira.sh lo demuestra.
 */
public class Curso {

    private final String clave;
    private final int cupo;
    private final Set<String> inscritos = new LinkedHashSet<>();

    public Curso(String clave, int cupo) {
        if (cupo <= 0) {
            throw new IllegalArgumentException("Un curso sin cupo no es un curso: " + cupo);
        }
        this.clave = clave;
        this.cupo = cupo;
    }

    public void inscribir(String matricula) {
        if (estaLleno()) {
            throw new CupoLlenoException(clave, cupo);
        }
        inscritos.add(matricula);
    }

    /** La regla del cupo. Un solo operador, y es el que rompe la-mentira.sh. */
    public boolean estaLleno() {
        return lugaresDisponibles() == 0;
    }

    public int lugaresDisponibles() {
        return cupo - inscritos.size();
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
}
