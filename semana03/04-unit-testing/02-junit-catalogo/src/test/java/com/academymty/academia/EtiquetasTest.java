package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 05 de la guia 02 -- @Tag: partir la suite en dos velocidades.
 *
 * El problema aparece solo, sin que nadie lo busque: la suite empieza en
 * dos segundos, crece, y un dia tarda once minutos. Entonces la gente
 * deja de correrla antes de subir codigo. Y una suite que nadie corre
 * no protege nada.
 *
 * La salida no es tener menos tests: es poder correr UNA PARTE.
 *
 * Desde la terminal:
 *
 *     mvn test                        todos
 *     mvn test -Dgroups=rapido        solo los rapidos     <- antes de cada commit
 *     mvn test -DexcludedGroups=lento todos menos los lentos
 *     mvn test -Dgroups="rapido | acta"   expresiones con | & !
 *
 * Convencion sana: pocas etiquetas y con significado operativo
 * (rapido/lento, unitario/integracion). Diez etiquetas distintas no las
 * recuerda nadie y acaban sin usarse.
 */
class EtiquetasTest {

    @Test
    @Tag("rapido")
    @DisplayName("[rapido] Un curso nuevo tiene todos sus lugares")
    void cursoNuevo() {
        assertEquals(30, new Curso("JAVA-101", 30).lugaresDisponibles());
    }

    @Test
    @Tag("rapido")
    @DisplayName("[rapido] Inscribir descuenta un lugar")
    void inscribirDescuenta() {
        Curso c = new Curso("JAVA-101", 30);
        c.inscribir("A01");

        assertEquals(29, c.lugaresDisponibles());
    }

    /**
     * Este tarda ~400 ms: diez alumnos a 40 ms de acta cada uno.
     * Poca cosa suelto. Multiplicalo por doscientos tests parecidos y
     * ya tienes los once minutos.
     */
    @Test
    @Tag("lento")
    @Tag("acta")
    @DisplayName("[lento] El acta lista a los diez inscritos")
    void actaCompleta() {
        Curso c = new Curso("JAVA-101", 30);
        for (int i = 1; i <= 10; i++) {
            c.inscribir(String.format("A%02d", i));
        }

        String acta = c.actaDeInscripcion();

        assertNotNull(acta);
        assertEquals(11, acta.lines().count(), "cabecera + 10 alumnos");
        assertTrue(acta.contains("A10"));
    }

    /**
     * Las etiquetas tambien van en la CLASE entera, y entonces aplican a
     * todos sus tests. Es lo habitual: una clase de tests de integracion
     * completa marcada @Tag("integracion") de una sola vez.
     */
    @Test
    @Tag("lento")
    @Tag("acta")
    @DisplayName("[lento] El acta de un curso vacio solo trae la cabecera")
    void actaVacia() {
        assertEquals(1, new Curso("VACIO", 5).actaDeInscripcion().lines().count());
    }
}
