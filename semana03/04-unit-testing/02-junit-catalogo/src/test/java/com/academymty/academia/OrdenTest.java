package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 07 de la guia 02 -- El orden, y por que casi siempre es un error.
 *
 * Por defecto JUnit ejecuta los metodos en un orden DETERMINISTA pero
 * deliberadamente no obvio. No es un descuido de diseno: es una defensa.
 * Si el orden fuera alfabetico o el del archivo, se escribirian tests
 * que dependen del anterior sin querer, y esa suite deja de servir --
 * el dia que uno falla, arrastra a diez y no sabes cual se rompio.
 *
 * En la guia 01 ya viste la otra mitad de la misma defensa: JUnit crea
 * una INSTANCIA NUEVA de la clase por cada test, asi que ni siquiera
 * puedes pasarte estado por un campo.
 *
 * Esta clase desactiva las dos protecciones a la vez. Es el UNICO caso
 * en el que vale la pena, y va acompanado del aviso correspondiente.
 *
 *   @TestInstance(PER_CLASS)  una sola instancia -> los campos sobreviven
 *   @TestMethodOrder(...)     el orden lo mandas tu con @Order
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("El ciclo de vida de un curso, contado en orden")
class OrdenTest {

    /**
     * Este campo NO se reinicia entre tests. Con el ciclo de vida por
     * defecto (PER_METHOD) seria null en el segundo test.
     */
    private Curso curso;

    @Test
    @Order(1)
    @DisplayName("1. Se abre el curso con cupo para dos")
    void seAbre() {
        curso = new Curso("JAVA-101", 2);

        assertTrue(curso.estaAbierto());
        assertEquals(2, curso.lugaresDisponibles());
    }

    @Test
    @Order(2)
    @DisplayName("2. Se inscriben los dos alumnos y se llena")
    void seLlena() {
        curso.inscribir("A01");
        curso.inscribir("A02");

        assertTrue(curso.estaLleno());
    }

    @Test
    @Order(3)
    @DisplayName("3. Un tercero ya no cabe")
    void noCabeUnTercero() {
        assertThrows(CupoLlenoException.class, () -> curso.inscribir("A03"));
    }

    @Test
    @Order(4)
    @DisplayName("4. Se cierran inscripciones y el curso queda como esta")
    void seCierra() {
        curso.cerrar();

        assertTrue(!curso.estaAbierto());
        assertEquals(2, curso.inscritos().size());
    }

    /**
     * EL AVISO, y va en serio.
     *
     * Lo que acabas de leer es una NARRACION, no cuatro tests. Tiene un
     * precio que se paga el dia que algo falla:
     *
     *   - El test 3 no se puede correr solo. En el IDE, ejecutar solo
     *     "noCabeUnTercero" truena con NullPointerException.
     *   - Si el test 1 falla, los otros tres fallan tambien y el reporte
     *     ensena cuatro rojos donde hay UN problema.
     *   - Reordenar es cambiar el significado. Nadie se atreve a tocarlo.
     *
     * Cuando de verdad quieras contar una secuencia, la forma sana es
     * UN SOLO test con los pasos dentro, o @Nested como en la seccion 04.
     *
     * MethodOrderer trae ademas Random (revuelve a proposito, para cazar
     * dependencias ocultas), MethodName y DisplayName.
     *
     * PER_CLASS tiene, eso si, un uso legitimo y comun: permite que
     * @BeforeAll NO sea static. Eso importa cuando el montaje caro
     * (levantar un contenedor, abrir una conexion) necesita campos de
     * instancia. Ahi PER_CLASS se usa por el @BeforeAll, no por el estado
     * compartido entre tests.
     */
    @Test
    @Order(5)
    @DisplayName("5. El precio: este test no se puede correr solo")
    void elPrecio() {
        assertEquals("JAVA-101", curso.clave(),
                "Depende de que 'seAbre' haya corrido antes. Eso es la deuda.");
    }
}
