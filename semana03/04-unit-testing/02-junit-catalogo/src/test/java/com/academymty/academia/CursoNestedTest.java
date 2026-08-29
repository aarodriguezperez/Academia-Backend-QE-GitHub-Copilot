package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 04 de la guia 02 -- @Nested: agrupar por ESTADO, no por metodo.
 *
 * El error comun al organizar tests es hacer una clase por metodo:
 * InscribirTest, CerrarTest, LugaresDisponiblesTest. Suena ordenado y
 * no lo es: inscribir() se comporta de tres maneras distintas segun
 * como este el curso, y en una sola clase esas tres se mezclan.
 *
 * @Nested agrupa por SITUACION. Cada clase interna monta un estado y
 * ahi dentro prueba todo lo que aplica a ese estado.
 *
 * Corre esta clase en Eclipse o mira la salida de Surefire y fijate en
 * como se lee el arbol -- casi una frase en espanol:
 *
 *   Un curso
 *     Cuando acaba de crearse
 *       tiene todos sus lugares libres
 *       acepta al primer inscrito
 *     Cuando ya esta lleno
 *       rechaza al siguiente con CupoLlenoException
 *       ...
 *
 * Requisito tecnico: las clases internas NO pueden ser static. @Nested
 * necesita una instancia interna ligada a la externa -- y es justo eso
 * lo que hace que hereden el @BeforeEach de afuera.
 */
@DisplayName("Un curso")
class CursoNestedTest {

    private Curso curso;

    /** Este @BeforeEach corre ANTES que el de cualquier clase interna. */
    @BeforeEach
    void crearCurso() {
        curso = new Curso("JAVA-101", 2);
    }

    @Nested
    @DisplayName("Cuando acaba de crearse")
    class ReciencCreado {

        @Test
        @DisplayName("tiene todos sus lugares libres")
        void lugaresLibres() {
            assertEquals(2, curso.lugaresDisponibles());
            assertFalse(curso.estaLleno());
        }

        @Test
        @DisplayName("esta abierto a inscripciones")
        void abierto() {
            assertTrue(curso.estaAbierto());
        }

        @Test
        @DisplayName("acepta al primer inscrito")
        void aceptaAlPrimero() {
            curso.inscribir("A01");

            assertEquals(1, curso.inscritos().size());
            assertEquals(1, curso.lugaresDisponibles());
        }
    }

    @Nested
    @DisplayName("Cuando ya esta lleno")
    class Lleno {

        /**
         * Este @BeforeEach corre DESPUES del de la clase externa.
         * Orden real: crearCurso() -> llenarlo() -> el @Test.
         *
         * Por eso 'curso' ya existe aqui: lo monto el de afuera.
         */
        @BeforeEach
        void llenarlo() {
            curso.inscribir("A01");
            curso.inscribir("A02");
        }

        @Test
        @DisplayName("se reporta como lleno y sin lugares")
        void seSabeLleno() {
            assertTrue(curso.estaLleno());
            assertEquals(0, curso.lugaresDisponibles());
        }

        @Test
        @DisplayName("rechaza al siguiente con CupoLlenoException")
        void rechazaAlTercero() {
            assertThrows(CupoLlenoException.class, () -> curso.inscribir("A03"));
        }

        @Test
        @DisplayName("sigue abierto: lleno y cerrado no son lo mismo")
        void llenoNoEsCerrado() {
            // Distincion facil de pasar por alto, y la clase interna la
            // hace evidente porque este test SOLO tiene sentido aqui.
            assertTrue(curso.estaAbierto());
        }
    }

    @Nested
    @DisplayName("Cuando ya cerro inscripciones")
    class Cerrado {

        @BeforeEach
        void cerrarlo() {
            curso.inscribir("A01");
            curso.cerrar();
        }

        @Test
        @DisplayName("rechaza a cualquiera, aunque queden lugares")
        void rechazaAunqueHayaLugar() {
            assertEquals(1, curso.lugaresDisponibles(), "todavia sobra un lugar");

            assertThrows(IllegalStateException.class, () -> curso.inscribir("A02"));
        }

        @Test
        @DisplayName("conserva a los que ya estaban")
        void conservaLosInscritos() {
            assertEquals(1, curso.inscritos().size());
            assertTrue(curso.inscritos().contains("A01"));
        }
    }
}
