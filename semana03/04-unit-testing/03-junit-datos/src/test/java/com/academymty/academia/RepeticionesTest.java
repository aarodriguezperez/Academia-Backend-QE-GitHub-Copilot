package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 05 de la guia 03 -- @RepeatedTest: el mismo test, N veces.
 *
 * EMPECEMOS POR LO QUE NO ES, porque es el mal uso mas frecuente:
 *
 *   @RepeatedTest NO ES PARA TESTS INESTABLES.
 *
 * Si un test falla una de cada diez veces, repetirlo diez veces no lo
 * arregla: lo esconde mejor. Un test inestable senala un bug real --
 * casi siempre una condicion de carrera o una dependencia del reloj o
 * del orden-- y taparlo con repeticiones es cambiar una senal por ruido.
 *
 * PARA LO QUE SI SIRVE:
 *   - Codigo con aleatoriedad dentro (aqui, generar datos al azar).
 *   - Comprobar que una operacion es idempotente.
 *   - Provocar a proposito problemas de concurrencia o de cache.
 */
class RepeticionesTest {

    private static final Random AZAR = new Random(20260826L);   // semilla fija: reproducible

    /**
     * Diez CURP generadas al azar, diez comprobaciones.
     *
     * El atributo 'name' usa dos marcadores propios de @RepeatedTest:
     * {currentRepetition} y {totalRepetitions}. Sin ellos, las diez
     * corridas se llaman igual y no sabes cual cayo.
     */
    @RepeatedTest(value = 10, name = "CURP al azar {currentRepetition} de {totalRepetitions}")
    void cualquierCurpGeneradaEsValida() {
        String curp = curpAlAzar();

        assertTrue(ValidadorCurp.esValida(curp), "Se genero una CURP invalida: " + curp);
        assertNotNull(ValidadorCurp.fechaNacimiento(curp));
    }

    /**
     * RepetitionInfo se inyecta igual que el TestInfo del proyecto 02:
     * lo pides por parametro y JUnit te lo da.
     *
     * Sirve para que cada repeticion haga algo LIGERAMENTE distinto --
     * aqui, probar el dia 1, el 2, ... el 28 del mes.
     */
    @RepeatedTest(value = 28, name = "dia {currentRepetition} del mes")
    @DisplayName("Los 28 dias que tienen todos los meses son validos")
    void losVeintiochoDias(RepetitionInfo info) {
        int dia = info.getCurrentRepetition();

        String curp = String.format("RUGM8001%02dHNLZRK09", dia);

        assertTrue(ValidadorCurp.esValida(curp), "El dia " + dia + " deberia valer");
        assertEquals(dia, ValidadorCurp.fechaNacimiento(curp).getDayOfMonth());
    }

    /**
     * Idempotencia: hacer lo mismo dos veces da lo mismo.
     *
     * Parece trivial y no lo es. Un validador que guardara estado interno
     * -- una cache mal hecha, un StringBuilder reutilizado -- daria
     * resultados distintos en la segunda llamada. Repetir lo caza.
     */
    @RepeatedTest(value = 5, name = "llamada {currentRepetition}: mismo resultado")
    void validarNoCambiaNada() {
        String curp = "RUGM800101HNLZRK09";

        assertTrue(ValidadorCurp.esValida(curp));
        assertEquals(ValidadorCurp.fechaNacimiento(curp), ValidadorCurp.fechaNacimiento(curp));
    }

    /**
     * Genera una CURP con estructura valida.
     *
     * La semilla del Random es FIJA a proposito. Un test con aleatoriedad
     * sin semilla es un test que no se puede reproducir: falla el martes,
     * lo corres el miercoles y pasa, y te quedas sin saber que dato lo
     * tumbo. Con semilla fija, la misma secuencia siempre.
     */
    private static String curpAlAzar() {
        String consonantes = "BCDFGHJKLMNPQRSTVWXYZ";
        String[] entidades = ValidadorCurp.entidadesValidas().toArray(new String[0]);

        StringBuilder sb = new StringBuilder();
        sb.append(letra("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));      // 1: cualquier letra
        sb.append(letra("AEIOUX"));                          // 2: vocal o X
        sb.append(letra("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));      // 3
        sb.append(letra("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));      // 4
        sb.append(String.format("%02d", AZAR.nextInt(100))); // anio
        sb.append(String.format("%02d", 1 + AZAR.nextInt(12)));   // mes 01-12
        sb.append(String.format("%02d", 1 + AZAR.nextInt(28)));   // dia 01-28: siempre existe
        sb.append(AZAR.nextBoolean() ? 'H' : 'M');
        sb.append(entidades[AZAR.nextInt(entidades.length)]);
        for (int i = 0; i < 3; i++) {
            sb.append(letra(consonantes));
        }
        sb.append(AZAR.nextInt(10));                         // homoclave: digito -> 1900s
        sb.append(AZAR.nextInt(10));                         // verificador
        return sb.toString();
    }

    private static char letra(String alfabeto) {
        return alfabeto.charAt(AZAR.nextInt(alfabeto.length()));
    }
}
