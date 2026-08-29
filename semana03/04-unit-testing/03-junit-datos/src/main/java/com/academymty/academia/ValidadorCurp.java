package com.academymty.academia;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Valida la ESTRUCTURA de una CURP y extrae lo que trae dentro.
 *
 * Se eligio esta clase para el proyecto 03 por un motivo muy concreto:
 * tiene decenas de casos que probar y todos se prueban IGUAL, cambiando
 * solo el dato. Escribir un @Test por cada uno serian 40 metodos
 * copiados y pegados. Con @ParameterizedTest son cuatro metodos.
 *
 * Las 18 posiciones:
 *
 *   RUGM 800101 H NL ZRK 0 9
 *   ^^^^ ^^^^^^ ^ ^^ ^^^ ^ ^
 *   |    |      | |  |   | +-- 18: digito verificador
 *   |    |      | |  |   +---- 17: homoclave. DIGITO -> nacio en 1900s
 *   |    |      | |  |                        LETRA  -> nacio en 2000s
 *   |    |      | |  +-------- 14-16: tres consonantes internas
 *   |    |      | +----------- 12-13: entidad federativa
 *   |    |      +------------- 11: H o M
 *   |    +------------------ 5-10: fecha AAMMDD
 *   +----------------------- 1-4: iniciales (la 2a es vocal, o X)
 *
 * FUERA DE ALCANCE, y a proposito: el digito verificador (posicion 18)
 * se calcula con una tabla oficial de RENAPO. Aqui NO se comprueba.
 * Se dice en voz alta porque un material didactico que finge validar
 * algo que no valida es peor que uno que no lo intenta.
 */
public final class ValidadorCurp {

    /** Las 32 entidades mas NE, "nacido en el extranjero". */
    private static final Set<String> ENTIDADES = Set.of(
            "AS", "BC", "BS", "CC", "CL", "CM", "CS", "CH", "DF", "DG",
            "GT", "GR", "HG", "JC", "MC", "MN", "MS", "NT", "NL", "OC",
            "PL", "QT", "QR", "SP", "SL", "SR", "TC", "TS", "TL", "VZ",
            "YN", "ZS", "NE");

    /**
     * El patron. Leelo por bloques, en el mismo orden del dibujo de arriba.
     *
     * Detalles que sorprenden y que los tests comprueban uno por uno:
     *   [AEIOUX]         la X es valida: hay apellidos sin vocal interna
     *   (0[1-9]|1[0-2])  el mes 00 y el 13 NO pasan
     *   [B-DF-HJ-NP-TV-Z] es "consonantes": el alfabeto entero menos
     *                     A, E, I, O, U. Los cinco huecos del rango son
     *                     justo las cinco vocales.
     */
    private static final Pattern PATRON = Pattern.compile(
            "^[A-Z][AEIOUX][A-Z]{2}"                          // iniciales
            + "\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])"   // AAMMDD
            + "[HM]"                                          // sexo
            + "(AS|BC|BS|CC|CL|CM|CS|CH|DF|DG|GT|GR|HG|JC|MC|MN|MS|NT|NL|OC"
            + "|PL|QT|QR|SP|SL|SR|TC|TS|TL|VZ|YN|ZS|NE)"      // entidad
            + "[B-DF-HJ-NP-TV-Z]{3}"                          // consonantes
            + "[A-Z\\d]"                                      // homoclave
            + "\\d$");                                        // verificador

    private ValidadorCurp() {
    }

    /**
     * Dos capas de validacion, y la segunda es la interesante.
     *
     * El regex acepta 990229 porque "29" es un dia posible en el mes 02.
     * Pero 1999 no fue bisiesto: ese dia NO EXISTIO. Ninguna expresion
     * regular razonable sabe eso -- hace falta un calendario.
     *
     * Es el ejemplo mas limpio de una regla general: el formato y el
     * significado son dos validaciones distintas, y confundirlas deja
     * pasar datos imposibles.
     */
    public static boolean esValida(String curp) {
        if (curp == null || !PATRON.matcher(curp).matches()) {
            return false;
        }
        return fechaReal(curp) != null;
    }

    public static LocalDate fechaNacimiento(String curp) {
        exigirValida(curp);
        return fechaReal(curp);
    }

    public static Sexo sexo(String curp) {
        exigirValida(curp);
        return Sexo.desdeLetra(curp.charAt(10));
    }

    public static String entidad(String curp) {
        exigirValida(curp);
        return curp.substring(11, 13);
    }

    /** La regla del siglo: homoclave numerica -> 1900s, alfabetica -> 2000s. */
    private static LocalDate fechaReal(String curp) {
        int anio = Integer.parseInt(curp.substring(4, 6));
        int mes = Integer.parseInt(curp.substring(6, 8));
        int dia = Integer.parseInt(curp.substring(8, 10));

        char homoclave = curp.charAt(16);
        int siglo = Character.isDigit(homoclave) ? 1900 : 2000;

        try {
            return LocalDate.of(siglo + anio, mes, dia);
        } catch (DateTimeException e) {
            return null;    // 29 de febrero de un anio no bisiesto, por ejemplo
        }
    }

    private static void exigirValida(String curp) {
        if (!esValida(curp)) {
            throw new IllegalArgumentException("CURP no valida: " + curp);
        }
    }

    /** Expuesto solo para los tests: la lista contra la que se comprueba la entidad. */
    public static Set<String> entidadesValidas() {
        return ENTIDADES;
    }
}
