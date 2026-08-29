package com.academymty.academia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 03 de la guia 03 -- Cuando los datos no caben en una anotacion.
 *
 * @ValueSource y @CsvSource solo aceptan CONSTANTES: cadenas, numeros,
 * clases. Es una limitacion de Java, no de JUnit -- una anotacion no
 * puede contener un objeto construido en tiempo de ejecucion.
 *
 * En cuanto necesitas un LocalDate, una lista, un objeto de tu dominio o
 * datos CALCULADOS, hace falta @MethodSource: un metodo que devuelve un
 * Stream de casos.
 *
 * Reglas del metodo proveedor:
 *   - static (salvo con @TestInstance(PER_CLASS), como viste en el 02)
 *   - sin parametros
 *   - devuelve Stream, Collection, Iterator o un arreglo
 *   - un solo parametro -> Stream<String>, Stream<Integer>...
 *     varios parametros -> Stream<Arguments>
 */
class MethodSourceTest {

    /**
     * Lo mas simple: un solo parametro, sin Arguments.
     *
     * Si el metodo proveedor se llama IGUAL que el test, ni siquiera hace
     * falta nombrarlo: @MethodSource a secas lo encuentra. Aqui se nombra
     * por claridad, que es lo recomendable cuando un proveedor lo comparten
     * varios tests.
     */
    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("curpsDeLaGeneracion")
    void todasLasCurpsDeLaGeneracionSonValidas(String curp) {
        assertTrue(ValidadorCurp.esValida(curp));
    }

    static Stream<String> curpsDeLaGeneracion() {
        return Stream.of(
                "RUGM800101HNLZRK09",
                "TOLA010615MNLRPNA5",
                "MAAJ990228HDFRRS08",
                "LOMS951231MJCPRL03");
    }

    /**
     * Varios parametros: Stream<Arguments>.
     *
     * Arguments.of(...) empaqueta una fila. Y aqui esta la ventaja sobre
     * @CsvSource: los valores son OBJETOS DE VERDAD -- un LocalDate, un
     * enum -- construidos en Java, con el compilador vigilando. Si te
     * equivocas de tipo, no compila. En un CSV te enterarias en ejecucion.
     */
    @ParameterizedTest(name = "{0} -> {1}, {2}, {3}")
    @MethodSource("curpsConSuContenido")
    void desglosarConObjetosReales(String curp, LocalDate fecha, Sexo sexo, String entidad) {
        assertEquals(fecha, ValidadorCurp.fechaNacimiento(curp));
        assertEquals(sexo, ValidadorCurp.sexo(curp));
        assertEquals(entidad, ValidadorCurp.entidad(curp));
    }

    static Stream<Arguments> curpsConSuContenido() {
        return Stream.of(
                Arguments.of("RUGM800101HNLZRK09", LocalDate.of(1980, 1, 1), Sexo.HOMBRE, "NL"),
                Arguments.of("TOLA010615MNLRPNA5", LocalDate.of(2001, 6, 15), Sexo.MUJER, "NL"),
                Arguments.of("MAAJ990228HDFRRS08", LocalDate.of(1999, 2, 28), Sexo.HOMBRE, "DF"),
                Arguments.of("RUGM000229HNLZRKA9", LocalDate.of(2000, 2, 29), Sexo.HOMBRE, "NL"));
    }

    /**
     * Datos CALCULADOS. Esto es lo que ninguna anotacion puede hacer.
     *
     * En vez de escribir a mano las 33 entidades federativas, se piden a
     * la propia clase. Ventaja real: el dia que se anada una entidad, el
     * test la cubre solo. Un @ValueSource habria que actualizarlo -- y
     * nadie se acuerda.
     *
     * (Se ordena para que el reporte salga estable entre corridas: el Set
     * de origen no garantiza orden.)
     */
    @ParameterizedTest(name = "entidad {0}")
    @MethodSource("todasLasEntidades")
    void cualquierEntidadValidaSeAcepta(String entidad) {
        String curp = "RUGM800101H" + entidad + "ZRK09";

        assertTrue(ValidadorCurp.esValida(curp), "Deberia aceptar la entidad " + entidad);
        assertEquals(entidad, ValidadorCurp.entidad(curp));
    }

    static Stream<String> todasLasEntidades() {
        return ValidadorCurp.entidadesValidas().stream().sorted();
    }

    /**
     * Generar casos con un bucle: los 12 meses, los 31 dias.
     *
     * 12 + 31 = 43 casos escritos en seis lineas. A mano serian 43
     * metodos, o un @ValueSource de 43 cadenas que nadie querria revisar.
     */
    @ParameterizedTest(name = "mes {0}")
    @MethodSource("mesesDelAnio")
    void losDoceMesesSonValidos(int mes) {
        String curp = String.format("RUGM80%02d01HNLZRK09", mes);

        assertTrue(ValidadorCurp.esValida(curp), "El mes " + mes + " deberia valer");
        assertEquals(mes, ValidadorCurp.fechaNacimiento(curp).getMonthValue());
    }

    static IntStream mesesDelAnio() {
        return IntStream.rangeClosed(1, 12);
    }

    /**
     * Y el reverso generado: los meses que NO existen.
     * 00 y del 13 al 19 -- el regex solo admite dos digitos.
     */
    @ParameterizedTest(name = "mes {0} no existe")
    @MethodSource("mesesImposibles")
    void losMesesImposiblesSeRechazan(int mes) {
        assertFalse(ValidadorCurp.esValida(String.format("RUGM80%02d01HNLZRK09", mes)));
    }

    static List<Integer> mesesImposibles() {
        return List.of(0, 13, 14, 15, 16, 17, 18, 19);
    }

    /**
     * Un proveedor puede vivir en OTRA clase. Se nombra con la ruta
     * completa y un # antes del metodo.
     *
     * Asi se comparte un catalogo de datos entre varias clases de test
     * sin duplicarlo. Es lo que se hace en proyectos grandes: una clase
     * DatosDePrueba con los casos canonicos.
     */
    @ParameterizedTest(name = "[{index}] {0} (compartida)")
    @MethodSource("com.academymty.academia.CatalogoDeCurps#lasValidas")
    void proveedorEnOtraClase(String curp) {
        assertTrue(ValidadorCurp.esValida(curp));
    }
}
