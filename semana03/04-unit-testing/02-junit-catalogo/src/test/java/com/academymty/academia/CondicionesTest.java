package com.academymty.academia;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.api.Assumptions.assumingThat;

/**
 * SECCION 03 de la guia 02 -- Cuando un test NO deberia correr.
 *
 * Hay tres estados, no dos. Y confundirlos es la causa numero uno de
 * suites que mienten:
 *
 *   PASSED   el test corrio y la afirmacion se cumplio.
 *   FAILED   el test corrio y la afirmacion NO se cumplio.  -> hay un bug
 *   ABORTED  el test NI SIQUIERA CORRIO.                    -> no se sabe nada
 *
 * assertTrue(false)  -> FAILED   "esto esta mal"
 * assumeTrue(false)  -> ABORTED  "aqui no aplica, ni lo intento"
 *
 * La diferencia importa porque un build con 300 tests abortados sale
 * VERDE. Y verde, para quien lo mira de lejos, significa "todo probado".
 */
class CondicionesTest {

    /**
     * Una assumption es un requisito del ENTORNO, no una afirmacion sobre
     * el codigo. Se lee: "esto solo tiene sentido si...".
     *
     * Aqui la variable no existe casi nunca, asi que este test aparece
     * como skipped en el reporte. Eso no es un fallo: es honestidad.
     */
    @Test
    @DisplayName("assumeTrue: si no se cumple, el test se ABORTA (no falla)")
    void soloEnElEntornoDeIntegracion() {
        assumeTrue("integracion".equals(System.getenv("ACADEMY_ENV")),
                "No estamos en el entorno de integracion: no aplica");

        // Todo lo de abajo solo corre si la assumption se cumplio.
        Curso c = new Curso("INT-101", 1);
        c.inscribir("A01");
        assertTrue(c.estaLleno());
    }

    @Test
    @DisplayName("assumeFalse: la misma idea, al reves")
    void noEnIntegracionContinua() {
        assumeFalse(System.getenv("CI") != null,
                "En CI este test no corre: depende de rutas de la maquina local");

        assertEquals(3, new Curso("X", 3).lugaresDisponibles());
    }

    /**
     * assumingThat es distinto de los dos anteriores y casi nadie lo usa bien.
     *
     * assumeTrue     ABORTA el test entero si no se cumple.
     * assumingThat   ejecuta SOLO ESE BLOQUE si se cumple, y el resto del
     *                test sigue corriendo pase lo que pase.
     *
     * Sirve para: "estas comprobaciones valen en todas partes; ESTA otra
     * solo cuando estoy en la maquina de desarrollo".
     */
    @Test
    @DisplayName("assumingThat: solo el BLOQUE es condicional, el test entero no")
    void unBloqueCondicional() {
        Curso c = new Curso("JAVA-101", 2);
        c.inscribir("A01");

        assumingThat("integracion".equals(System.getenv("ACADEMY_ENV")),
                () -> {
                    // Este bloque casi nunca corre.
                    assertEquals(1, c.lugaresDisponibles(), "comprobacion extra de integracion");
                });

        // ...pero ESTO corre siempre, y es lo que de verdad protege la clase.
        assertEquals(1, c.lugaresDisponibles());
        assertTrue(c.estaAbierto());
    }

    /**
     * @Disabled apaga el test. Siempre, en todas partes, para todos.
     *
     * El texto NO es opcional en la practica: es la unica pista que tendra
     * quien lo encuentre dentro de seis meses. Un @Disabled sin motivo es
     * basura que nadie se atreve a borrar.
     *
     * Y la regla dura: un @Disabled es DEUDA, con fecha y con dueno. Si no
     * piensas volver, borra el test -- un test apagado para siempre solo
     * sirve para inflar el numero de tests del reporte.
     */
    @Test
    @Disabled("2026-08-26 (mike): pendiente de la regla de lista de espera, ticket ACAD-142")
    @DisplayName("Un curso lleno deberia mandar a lista de espera")
    void listaDeEspera() {
        // Todavia no existe la funcionalidad. El test esta escrito primero
        // a proposito: describe lo que falta.
    }

    /**
     * Las condiciones declarativas: mismo efecto que una assumption, pero
     * a la vista en la firma del test en vez de escondidas en la primera
     * linea del cuerpo.
     *
     * Existen tambien @DisabledOnOs, @EnabledOnJre, @EnabledForJreRange,
     * @DisabledIfSystemProperty y @EnabledIf (con un metodo propio).
     */
    @Test
    @EnabledOnOs({ OS.MAC, OS.LINUX })
    @DisplayName("Solo en Mac o Linux: rutas con / que en Windows no valen")
    void soloEnUnixLike() {
        assertTrue(System.getProperty("file.separator").equals("/"));
    }

    @Test
    @EnabledIfSystemProperty(named = "academy.lento", matches = "true")
    @DisplayName("Solo con -Dacademy.lento=true")
    void soloSiLoPidesExplicitamente() {
        Curso c = new Curso("LENTO", 5);
        c.inscribir("A01");
        assertTrue(c.actaDeInscripcion().contains("A01"));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "ACADEMY_ENV", matches = "integracion")
    @DisplayName("La version declarativa del primer test de esta clase")
    void mismaIdeaSinAssumption() {
        assertTrue(new Curso("INT-102", 1).estaAbierto());
    }
}
