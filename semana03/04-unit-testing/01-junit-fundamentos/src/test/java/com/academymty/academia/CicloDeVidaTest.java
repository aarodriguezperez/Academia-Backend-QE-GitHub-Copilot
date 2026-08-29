package com.academymty.academia;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * SECCION 02 de la guia -- El ciclo de vida, y la regla que casi nadie sabe.
 *
 * Corre esta clase y mira el orden en la consola:
 *
 *   @BeforeAll      una vez, antes de todo
 *     @BeforeEach   antes de CADA test
 *       @Test       test 1
 *     @AfterEach    despues de CADA test
 *     @BeforeEach
 *       @Test       test 2
 *     @AfterEach
 *   @AfterAll       una vez, al final
 *
 * @BeforeAll y @AfterAll tienen que ser static. Y hay un motivo, no es
 * un capricho de la API: JUnit crea UNA INSTANCIA NUEVA de esta clase por
 * cada @Test. Si no fueran static, no habria ninguna instancia a la que
 * pertenecieran cuando corren.
 *
 * Esa instancia nueva por test es la regla que casi nadie sabe, y es lo
 * que prueba el ultimo metodo de esta clase.
 */
class CicloDeVidaTest {

    /** Compartido entre todos los tests: es static, sobrevive a las instancias. */
    private static int testsEjecutados = 0;

    /** NO es static: cada test recibe uno recien nacido en cero. */
    private int contadorDeInstancia = 0;

    /** El escenario limpio que @BeforeEach vuelve a montar cada vez. */
    private Boleta boleta;

    @BeforeAll
    static void abrirElSemestre() {
        System.out.println("== @BeforeAll  -- una sola vez, antes de todo");
    }

    @BeforeEach
    void matricularAlumno() {
        System.out.println("   -> @BeforeEach -- boleta nueva y limpia");
        boleta = new Boleta(new Alumno("A01", "Ana Torres"));
    }

    @AfterEach
    void limpiar() {
        testsEjecutados++;
        System.out.println("   <- @AfterEach  -- llevamos " + testsEjecutados + " test(s)");
    }

    @AfterAll
    static void cerrarElSemestre() {
        System.out.println("== @AfterAll   -- una sola vez, al final. Corrieron "
                + testsEjecutados + " tests");
    }

    @Test
    @DisplayName("Test 1: registra Java y comprueba el promedio")
    void primerTest() {
        boleta.registrar("Java", 80);
        assertEquals(80.0, boleta.promedio());
    }

    @Test
    @DisplayName("Test 2: la boleta llego VACIA, no trae lo del test 1")
    void segundoTest() {
        // Si @BeforeEach no existiera, o si JUnit reutilizara la instancia,
        // aqui seguiria la materia "Java" del test anterior y esto seria 1.
        assertEquals(0, boleta.totalMaterias(),
                "Cada test arranca con una boleta nueva: los tests NO se heredan estado");

        boleta.registrar("SQL", 60);
        assertEquals(60.0, boleta.promedio());
    }

    /**
     * La demostracion. Este metodo incrementa un campo de instancia y luego
     * comprueba que vale 1 -- no 2, no 3, por muchas veces que corra la clase.
     *
     * Es la prueba de que JUnit te dio una instancia nueva. Y de ahi sale la
     * regla practica mas importante de este proyecto:
     *
     *   NO uses campos de instancia para pasarte datos entre tests.
     *   No funciona, y el dia que parezca funcionar es porque el orden
     *   de ejecucion te dio la razon por accidente.
     */
    @Test
    @DisplayName("Cada test corre sobre una INSTANCIA NUEVA de la clase")
    void cadaTestEsUnaInstanciaNueva() {
        contadorDeInstancia++;
        assertEquals(1, contadorDeInstancia,
                "Si esto fuera 2, JUnit habria reutilizado la instancia entre tests");
    }
}
