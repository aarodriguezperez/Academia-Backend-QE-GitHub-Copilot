package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 01 de la guia 02 -- Probar que algo TRUENA.
 *
 * Un test normal comprueba que el codigo hace lo que debe.
 * Este comprueba lo contrario: que se NIEGA a hacer lo que no debe.
 *
 * Y es igual de importante. La mitad del valor de una clase esta en lo
 * que rechaza -- inscribir a alguien en un curso lleno, aceptar una
 * calificacion de 150, cobrar dos veces la misma tarjeta.
 *
 * La forma es siempre la misma:
 *
 *     assertThrows(LaExcepcionQueEsperas.class, () -> elCodigoQueDebeTronar());
 *                  ^ tipo esperado              ^ lambda: NO se ejecuta hasta
 *                                                 que assertThrows la llama
 */
class ExcepcionesTest {

    private Curso java101;

    @BeforeEach
    void cursoDeDosLugares() {
        java101 = new Curso("JAVA-101", 2);
    }

    /**
     * La forma minima. Se lee entero de izquierda a derecha:
     * "afirmo que lanza CupoLlenoException al inscribir al tercero".
     *
     * Ojo con el motivo de la lambda: si escribieras
     *
     *     assertThrows(CupoLlenoException.class, java101.inscribir("A03"));
     *
     * ni siquiera compilaria, porque inscribir() se ejecutaria ANTES de
     * entrar a assertThrows y la excepcion tumbaria el test. La lambda es
     * codigo EMPAQUETADO, todavia sin ejecutar. Ese es el truco.
     */
    @Test
    @DisplayName("Inscribir en un curso lleno lanza CupoLlenoException")
    void cupoLleno() {
        java101.inscribir("A01");
        java101.inscribir("A02");        // cupo 2: ya esta lleno

        assertThrows(CupoLlenoException.class, () -> java101.inscribir("A03"));
    }

    /**
     * assertThrows DEVUELVE la excepcion que atrapo. Y eso cambia todo:
     * ya no compruebas solo el tipo, compruebas el contenido.
     *
     * Esta es la razon por la que el dominio tiene su propia
     * CupoLlenoException con campos dentro en vez de un RuntimeException
     * generico: los campos se pueden afirmar, el texto del mensaje no
     * deberia (cambiar una palabra romperia el test sin que nada falle).
     */
    @Test
    @DisplayName("La excepcion se puede interrogar: assertThrows la devuelve")
    void interrogarLaExcepcion() {
        java101.inscribir("A01");
        java101.inscribir("A02");

        CupoLlenoException ex = assertThrows(CupoLlenoException.class,
                () -> java101.inscribir("A03"));

        assertAll("lo que trae la excepcion dentro",
                () -> assertEquals("JAVA-101", ex.claveCurso()),
                () -> assertEquals(2, ex.cupo()),
                () -> assertTrue(ex.getMessage().contains("JAVA-101"),
                        "El mensaje deberia nombrar el curso, para el log"));
    }

    /**
     * TRES fallos distintos, TRES excepciones distintas.
     *
     * Un solo assertThrows(RuntimeException.class, ...) los aprobaria los
     * tres, porque las tres heredan de RuntimeException -- y entonces el
     * test no distinguiria "curso lleno" de "curso cerrado". Se especifico
     * es mejor: pide el tipo exacto que el contrato promete.
     */
    @Test
    @DisplayName("Cada motivo de rechazo tiene su propia excepcion")
    void cadaFalloConSuTipo() {
        assertAll(
                () -> {
                    Curso lleno = new Curso("A", 1);
                    lleno.inscribir("A01");
                    assertThrows(CupoLlenoException.class, () -> lleno.inscribir("A02"));
                },
                () -> {
                    Curso cerrado = new Curso("B", 5);
                    cerrado.cerrar();
                    assertThrows(IllegalStateException.class, () -> cerrado.inscribir("A01"));
                },
                () -> {
                    Curso repetido = new Curso("C", 5);
                    repetido.inscribir("A01");
                    assertThrows(IllegalArgumentException.class, () -> repetido.inscribir("A01"));
                });
    }

    /**
     * TRAMPA CLASICA. CupoLlenoException extiende RuntimeException, asi que
     * assertThrows(RuntimeException.class, ...) tambien pasa: assertThrows
     * acepta el tipo pedido Y SUS SUBCLASES.
     *
     * Comodo a veces, peligroso casi siempre: un NullPointerException tambien
     * es RuntimeException, y ese test lo daria por bueno. Habrias escrito un
     * test que aprueba precisamente el bug que querias evitar.
     *
     * assertInstanceOf te deja afirmar la jerarquia a proposito, cuando la
     * jerarquia ES lo que quieres probar.
     */
    @Test
    @DisplayName("TRAMPA: assertThrows acepta subclases del tipo que pides")
    void aceptaSubclases() {
        java101.inscribir("A01");
        java101.inscribir("A02");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> java101.inscribir("A03"));

        // Pasa... pero no probaste lo que creias. Por eso lo estrechamos aqui:
        assertInstanceOf(CupoLlenoException.class, ex,
                "Pediste RuntimeException y te dieron algo mas concreto");
    }

    /**
     * El reverso: assertDoesNotThrow.
     *
     * Uselo con cuidado. Cualquier test que pase YA demuestra que no
     * hubo excepcion -- si la hubiera, el test estaria en rojo. Solo vale
     * la pena cuando lo que quieres documentar ES la ausencia del fallo:
     * "el ultimo lugar todavia se puede ocupar, no se cae por uno de mas".
     */
    @Test
    @DisplayName("El ultimo lugar SI se puede ocupar")
    void elUltimoLugarNoTruena() {
        java101.inscribir("A01");

        assertDoesNotThrow(() -> java101.inscribir("A02"),
                "Quedaba un lugar: inscribir al segundo no deberia fallar");

        assertTrue(java101.estaLleno());
        assertEquals(0, java101.lugaresDisponibles());
    }
}
