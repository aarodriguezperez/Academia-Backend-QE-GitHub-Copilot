package com.academymty.academia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 04 de la guia 03 -- @EnumSource: la fuente que se actualiza sola.
 *
 * Tiene una propiedad que ninguna otra fuente tiene, y es la razon de
 * usarla siempre que haya un enum de por medio:
 *
 *   EL DIA QUE ALGUIEN ANADA UNA CONSTANTE AL ENUM,
 *   ESTE TEST LA PRUEBA SIN QUE NADIE LO TOQUE.
 *
 * Con @ValueSource escribirias los cuatro niveles a mano, llegaria un
 * quinto, y el test seguiria en verde sin haberlo probado nunca. Ese es
 * el fallo silencioso mas comun al probar enums.
 */
class EnumSourceTest {

    /**
     * Sin atributos: TODAS las constantes del tipo del parametro.
     * JUnit deduce el enum de la firma del metodo.
     */
    @ParameterizedTest(name = "{0}")
    @EnumSource
    void todoNivelSabeSiEsAprobatorio(Nivel nivel) {
        assertNotNull(nivel.name());

        // La regla, expresada de forma que aguante niveles nuevos:
        assertEquals(nivel != Nivel.INSUFICIENTE, nivel.esAprobatorio());
    }

    /**
     * names: solo estas constantes.
     */
    @ParameterizedTest(name = "{0} permite beca")
    @EnumSource(names = { "EXCELENTE", "BUENO" })
    void losNivelesConBeca(Nivel nivel) {
        assertTrue(nivel.esAprobatorio());
    }

    /**
     * mode = EXCLUDE: todas MENOS estas.
     *
     * Preferible a listar las incluidas cuando la excepcion es corta:
     * si manana se anade un nivel nuevo, entra aqui automaticamente.
     * "Todas menos X" envejece bien; "estas tres" no.
     */
    @ParameterizedTest(name = "{0} no es INSUFICIENTE, luego aprueba")
    @EnumSource(mode = EnumSource.Mode.EXCLUDE, names = "INSUFICIENTE")
    void todosMenosInsuficiente(Nivel nivel) {
        assertTrue(nivel.esAprobatorio());
    }

    /**
     * mode = MATCH_ALL con expresiones regulares sobre el nombre.
     */
    @ParameterizedTest(name = "{0} termina en E")
    @EnumSource(mode = EnumSource.Mode.MATCH_ALL, names = ".*E$")
    void losQueTerminanEnE(Nivel nivel) {
        assertTrue(nivel.name().endsWith("E"));
    }

    /**
     * Otro enum, otra propiedad. Aqui se prueba el viaje de ida y vuelta:
     * de la constante a su letra y de la letra a la constante.
     *
     * Los tests de ida y vuelta son de los mas rentables que existen:
     * una sola linea cubre dos metodos y garantiza que no se
     * desincronicen.
     */
    @ParameterizedTest(name = "{0} <-> ''{0}''")
    @EnumSource
    void idaYVueltaDelSexo(Sexo sexo) {
        assertEquals(sexo, Sexo.desdeLetra(sexo.letra()));
    }

    /**
     * Y cerrando el circulo con la CURP: para cada Sexo, se construye una
     * CURP con su letra y se comprueba que el validador la lee igual.
     *
     * Si manana el enum creciera, este test cubriria el caso nuevo solo.
     */
    @ParameterizedTest(name = "una CURP de {0} se lee como {0}")
    @EnumSource
    void laCurpConcuerdaConElEnum(Sexo sexo) {
        String curp = "RUGM800101" + sexo.letra() + "NLZRK09";

        assertTrue(ValidadorCurp.esValida(curp));
        assertEquals(sexo, ValidadorCurp.sexo(curp));
    }

    /**
     * La otra mitad: cualquier letra que NO este en el enum debe rechazarse.
     */
    @ParameterizedTest(name = "la letra de sexo ''{0}'' no vale")
    @org.junit.jupiter.params.provider.ValueSource(chars = { 'X', 'A', 'F', '1' })
    void letrasDeSexoInvalidas(char letra) {
        assertFalse(ValidadorCurp.esValida("RUGM800101" + letra + "NLZRK09"));
    }
}
