package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * SECCION 06 de la guia 03 -- @TestFactory: tests que no existen hasta que corres.
 *
 * Un @Test lo descubre JUnit ANTES de ejecutar nada, leyendo las
 * anotaciones de la clase. Un @ParameterizedTest tambien: sus datos
 * estan en el codigo o en un archivo del classpath.
 *
 * Un @TestFactory es distinto: es un metodo que se EJECUTA y devuelve
 * tests. Los casos pueden salir de donde sea -- una consulta a la base
 * de datos, una llamada a un API, un archivo que no existia al compilar.
 *
 * LA LETRA PEQUENA, y es importante:
 *
 *   @BeforeEach y @AfterEach corren UNA VEZ por @TestFactory,
 *   NO una vez por cada test dinamico que devuelve.
 *
 * Es la fuente numero uno de sorpresas con esta anotacion. Si cada caso
 * necesita su escenario limpio, montalo dentro de la lambda de cada
 * dynamicTest -- no confies en el @BeforeEach.
 *
 * REGLA PRACTICA: si puedes con @ParameterizedTest, usa
 * @ParameterizedTest. @TestFactory es para cuando los casos no se
 * conocen hasta el momento de correr.
 */
class TestFactoryTest {

    private int vecesQueCorrioElBeforeEach = 0;

    @BeforeEach
    void contar() {
        vecesQueCorrioElBeforeEach++;
    }

    /**
     * Lo minimo: una lista de tests construida a mano.
     *
     * dynamicTest(nombre, lambda). El nombre es un String normal, asi que
     * puede llevar acentos, espacios y datos calculados -- ventaja real
     * sobre el nombre de un metodo.
     */
    @TestFactory
    @DisplayName("Tres casos construidos a mano")
    Stream<DynamicTest> tresCasosAMano() {
        return Stream.of(
                dynamicTest("una CURP de 1980 se lee en el siglo XX",
                        () -> assertEquals(1980,
                                ValidadorCurp.fechaNacimiento("RUGM800101HNLZRK09").getYear())),

                dynamicTest("una CURP con homoclave alfabetica se lee en el siglo XXI",
                        () -> assertEquals(2001,
                                ValidadorCurp.fechaNacimiento("TOLA010615MNLRPNA5").getYear())),

                dynamicTest("el 29 de febrero de un anio no bisiesto no existe",
                        () -> assertFalse(ValidadorCurp.esValida("RUGM990229HNLZRK09"))));
    }

    /**
     * EL CASO DE USO DE VERDAD: un test por cada fila de un archivo que
     * se lee EN TIEMPO DE EJECUCION.
     *
     * Con @CsvFileSource tambien se lee un archivo, si -- pero el nombre
     * tiene que estar escrito en la anotacion, es decir, decidido al
     * compilar. Aqui el archivo podria elegirse por una variable de
     * entorno, bajarse de un servidor o listarse de un directorio.
     *
     * Fijate en el nombre de cada test dinamico: lleva la CURP dentro.
     * En el reporte, cada fila del CSV aparece como su propio test con
     * su propio nombre.
     */
    @TestFactory
    @DisplayName("Un test por cada fila del CSV, generado al vuelo")
    Stream<DynamicTest> unTestPorFilaDelArchivo() throws IOException {
        return leerCsv("/curps-validas.csv").stream()
                .map(fila -> dynamicTest(
                        "fila: " + fila[0] + " (" + fila[1] + ")",
                        () -> {
                            String curp = fila[0];
                            assertTrue(ValidadorCurp.esValida(curp), curp + " deberia ser valida");
                            assertEquals(LocalDate.parse(fila[1]), ValidadorCurp.fechaNacimiento(curp));
                            assertEquals(Sexo.valueOf(fila[2]), ValidadorCurp.sexo(curp));
                            assertEquals(fila[3], ValidadorCurp.entidad(curp));
                        }));
    }

    /**
     * DynamicContainer: tests dinamicos agrupados en un arbol, igual que
     * hacia @Nested con los estaticos.
     *
     * Aqui se genera un grupo por cada entidad federativa, y dentro de
     * cada grupo, dos comprobaciones. Son 33 grupos x 2 = 66 tests que
     * nadie escribio a mano.
     */
    @TestFactory
    @DisplayName("Un grupo de comprobaciones por cada entidad federativa")
    Stream<DynamicNode> unGrupoPorEntidad() {
        return ValidadorCurp.entidadesValidas().stream()
                .sorted()
                .limit(5)                     // 5 y no 33: el reporte se lee mejor
                .map(entidad -> {
                    String curp = "RUGM800101H" + entidad + "ZRK09";
                    return DynamicContainer.dynamicContainer("entidad " + entidad, Stream.of(
                            dynamicTest("la CURP es valida",
                                    () -> assertTrue(ValidadorCurp.esValida(curp))),
                            dynamicTest("se lee de vuelta como " + entidad,
                                    () -> assertEquals(entidad, ValidadorCurp.entidad(curp)))));
                });
    }

    /**
     * LA DEMOSTRACION DE LA LETRA PEQUENA.
     *
     * Este factory devuelve tres tests dinamicos y los tres comprueban
     * el mismo contador. Si @BeforeEach corriera por cada uno, el
     * contador iria subiendo. No sube: se queda clavado.
     *
     * Es la prueba de que el ciclo de vida NO se aplica por test
     * dinamico. Recuerdalo el dia que un @TestFactory te de resultados
     * que "no tienen sentido".
     */
    @TestFactory
    @DisplayName("El @BeforeEach NO corre por cada test dinamico")
    Stream<DynamicTest> elCicloDeVidaNoAplica() {
        int alEmpezar = vecesQueCorrioElBeforeEach;

        return Stream.of(1, 2, 3).map(n -> dynamicTest(
                "test dinamico " + n + ": el contador sigue en " + alEmpezar,
                () -> assertEquals(alEmpezar, vecesQueCorrioElBeforeEach,
                        "Si esto subiera, @BeforeEach se estaria ejecutando por test dinamico")));
    }

    /** Lector de CSV minimo: solo para los datos de prueba de este proyecto. */
    private static List<String[]> leerCsv(String recurso) throws IOException {
        List<String[]> filas = new ArrayList<>();
        try (InputStream in = TestFactoryTest.class.getResourceAsStream(recurso)) {
            assertNotNull(in, "No se encontro el recurso " + recurso);
            try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                r.readLine();                                  // cabecera
                for (String linea = r.readLine(); linea != null; linea = r.readLine()) {
                    if (!linea.isBlank()) {
                        filas.add(linea.split(",", -1));
                    }
                }
            }
        }
        return filas;
    }
}
