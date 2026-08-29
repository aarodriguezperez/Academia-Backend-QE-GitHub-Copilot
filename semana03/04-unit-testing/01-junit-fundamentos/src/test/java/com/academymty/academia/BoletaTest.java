package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 05 de la guia -- El test que atrapa el bug.
 *
 * Esta clase prueba Boleta de verdad. Y esta escrita para demostrar una sola
 * idea, la mas importante de todo el proyecto 01:
 *
 *   NO TODOS LOS TESTS VALEN LO MISMO.
 *
 * Corre `./scripts/ver-fallar.sh`. Ese script cambia UN caracter en
 * Boleta.aprobado() -- el >= pasa a ser > -- y vuelve a correr la suite.
 * Resultado: de los tests de esta clase, casi todos siguen en VERDE.
 * Solo cae uno: elLimiteExacto().
 *
 * Los tests de los casos comodos (90 aprueba, 50 reprueba) no sirvieron de
 * nada. El que valio es el que se paro justo encima de la frontera.
 *
 * De ahi la regla: prueba los LIMITES, no los ejemplos bonitos.
 * El limite, el limite menos uno, el limite mas uno, el vacio y el nulo.
 * Ahi es donde vive practicamente todo bug real.
 */
class BoletaTest {

    private Boleta boleta;

    @BeforeEach
    void nuevaBoleta() {
        boleta = new Boleta(new Alumno("A01", "Ana Torres"));
    }

    // ---------------------------------------------------------------
    //  Los casos comodos. Estan bien, pero no protegen casi nada.
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Un promedio holgado aprueba")
    void promedioHolgadoAprueba() {
        boleta.registrar("Java", 90);
        boleta.registrar("SQL", 88);

        assertTrue(boleta.aprobado());
    }

    @Test
    @DisplayName("Un promedio bajo reprueba")
    void promedioBajoReprueba() {
        boleta.registrar("Java", 50);
        boleta.registrar("SQL", 48);

        assertFalse(boleta.aprobado());
    }

    // ---------------------------------------------------------------
    //  EL TEST QUE IMPORTA. Este es el unico que se entera del bug.
    // ---------------------------------------------------------------

    /**
     * La regla escrita en el reglamento de la academia dice:
     * "se aprueba con 70 o mas". Ese "o mas" INCLUYE el 70.
     *
     * Con promedio exactamente 70:
     *      >= 70   ->  true    (correcto, aprueba)
     *      >  70   ->  false   (el bug: reprueba a quien saco justo 70)
     *
     * Los tres casos van juntos en el mismo test a proposito: el limite
     * solo tiene sentido si se mira con lo que hay a cada lado.
     */
    @Test
    @DisplayName("EL LIMITE: con exactamente 70 se aprueba, con 69 no")
    void elLimiteExacto() {
        Boleta justo = new Boleta(new Alumno("A02", "Beto Ruiz"));
        justo.registrar("Java", 70);
        assertTrue(justo.aprobado(),
                "70 es aprobatorio: el reglamento dice '70 o mas'");

        Boleta porDebajo = new Boleta(new Alumno("A03", "Carla Diaz"));
        porDebajo.registrar("Java", 69);
        assertFalse(porDebajo.aprobado(),
                "69 no alcanza");

        Boleta porEncima = new Boleta(new Alumno("A04", "Dario Luna"));
        porEncima.registrar("Java", 71);
        assertTrue(porEncima.aprobado(),
                "71 aprueba de sobra");
    }

    /**
     * El otro limite del mismo bug, y este es mas sutil: dos materias que
     * PROMEDIAN 70 sin que ninguna valga 70. Si alguien "arregla" el bug
     * comparando calificaciones sueltas en vez del promedio, este lo caza.
     */
    @Test
    @DisplayName("EL LIMITE, version promedio: 65 y 75 promedian 70 y aprueban")
    void elLimiteAlcanzadoPorPromedio() {
        boleta.registrar("Java", 65);
        boleta.registrar("SQL", 75);

        assertEquals(70.0, boleta.promedio());
        assertTrue(boleta.aprobado(), "El promedio es 70 clavado: aprueba");
    }

    // ---------------------------------------------------------------
    //  Los otros limites: el vacio y el rango.
    // ---------------------------------------------------------------

    @Test
    @DisplayName("Una boleta vacia promedia 0 y no aprueba")
    void boletaVacia() {
        assertEquals(0.0, boleta.promedio());
        assertFalse(boleta.aprobado());
    }

    @Test
    @DisplayName("Los extremos validos, 0 y 100, se aceptan")
    void extremosDelRango() {
        boleta.registrar("Java", 0);
        boleta.registrar("SQL", 100);

        assertEquals(50.0, boleta.promedio());
    }

    /**
     * assertThrows tiene el tema completo en el proyecto 02. Aqui va solo
     * la forma minima, porque el limite del rango no estaria probado sin ella.
     */
    @Test
    @DisplayName("Fuera de rango, 101 y -1, lanza IllegalArgumentException")
    void fueraDeRango() {
        assertThrows(IllegalArgumentException.class, () -> boleta.registrar("Java", 101));
        assertThrows(IllegalArgumentException.class, () -> boleta.registrar("Java", -1));
    }
}
