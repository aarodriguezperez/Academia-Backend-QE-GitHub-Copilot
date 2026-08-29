package com.academymty.academia;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 02 de la guia 03 -- Cuando cada caso necesita VARIOS datos.
 *
 * @ValueSource solo da un valor por corrida. En cuanto necesitas
 * "esta entrada produce ESTE resultado" hacen falta al menos dos
 * columnas, y ahi entra @CsvSource.
 *
 * Cada cadena es una fila. Las comas separan los parametros del metodo,
 * en orden.
 *
 * Y JUnit CONVIERTE los tipos solo: el String "80" llega como int 80 si
 * el parametro es int, "HOMBRE" llega como Sexo.HOMBRE si el parametro
 * es Sexo, "1980-01-01" llega como LocalDate. No hay que parsear nada.
 * A eso se le llama conversion implicita.
 */
class CsvSourceTest {

    /**
     * La tabla de limites del Calificador, completa, en nueve lineas.
     *
     * Esta es la forma que mas se usa en la vida real, y por un motivo:
     * la tabla se lee como una especificacion. Un analista que no
     * programa puede revisarla y decir "falta el caso del 100".
     *
     * Fijate en que estan los tres limites (90, 80, 70) y su vecino de
     * abajo (89, 79, 69). Ahi es donde vive el bug del >= mal escrito,
     * como viste en el proyecto 01.
     */
    @ParameterizedTest(name = "{0} -> {1}")
    @CsvSource({
            "100, EXCELENTE",
            " 90, EXCELENTE",     // limite exacto
            " 89, BUENO",         // uno menos
            " 80, BUENO",         // limite exacto
            " 79, SUFICIENTE",    // uno menos
            " 70, SUFICIENTE",    // limite exacto
            " 69, INSUFICIENTE",  // uno menos
            "  1, INSUFICIENTE",
            "  0, INSUFICIENTE"
    })
    void tablaDeNiveles(int calificacion, Nivel esperado) {
        assertEquals(esperado, Calificador.nivelDe(calificacion));
    }

    /**
     * EL CASO ESTRELLA DE ESTE PROYECTO. Leelo despacio.
     *
     * Las dos CURP dicen "29 de febrero". Se diferencian en UN caracter,
     * el 17 -- la homoclave -- y eso decide el siglo:
     *
     *   ...ZRK 0 9   homoclave DIGITO -> 1999 -> no fue bisiesto -> INVALIDA
     *   ...ZRK A 9   homoclave LETRA  -> 2000 -> SI fue bisiesto -> valida
     *
     * (2000 es bisiesto porque es divisible entre 400. 1900 no lo fue.)
     *
     * Ninguna expresion regular puede distinguirlas: para el patron las
     * dos son "dia 29 del mes 02". Hace falta un calendario de verdad.
     *
     * Por eso ValidadorCurp valida en dos capas -- formato y significado --
     * y por eso este test existe: es el unico que protege la segunda.
     */
    @ParameterizedTest(name = "{0} -> valida={1} ({2})")
    @CsvSource({
            "RUGM990229HNLZRK09, false, 1999 no fue bisiesto",
            "RUGM000229HNLZRKA9, true,  2000 si fue bisiesto",
            "RUGM040229HNLZRKA9, true,  2004 bisiesto normal",
            "RUGM010229HNLZRKA9, false, 2001 no fue bisiesto"
    })
    void elVeintinueveDeFebrero(String curp, boolean esperada, String porque) {
        assertEquals(esperada, ValidadorCurp.esValida(curp), porque);
    }

    /**
     * Comillas y delimitador propio.
     *
     * Si un dato lleva coma dentro, se encierra entre comillas simples.
     * Y si el texto lleva muchas comas, es mas limpio cambiar el
     * delimitador entero con delimiter.
     */
    @ParameterizedTest(name = "{1}")
    @CsvSource(delimiter = '|', textBlock = """
            RUGM800101HNLZRK09 | 1980-01-01 | HOMBRE | NL
            TOLA010615MNLRPNA5 | 2001-06-15 | MUJER  | NL
            MAAJ990228HDFRRS08 | 1999-02-28 | HOMBRE | DF
            """)
    void desglosarLaCurp(String curp, LocalDate fecha, Sexo sexo, String entidad) {
        assertAll("todo lo que se puede sacar de " + curp,
                () -> assertTrue(ValidadorCurp.esValida(curp)),
                () -> assertEquals(fecha, ValidadorCurp.fechaNacimiento(curp)),
                () -> assertEquals(sexo, ValidadorCurp.sexo(curp)),
                () -> assertEquals(entidad, ValidadorCurp.entidad(curp)));
    }

    /**
     * @CsvFileSource: los datos salen del codigo y se van a un archivo.
     *
     * src/test/resources/curps-validas.csv
     *
     * Cuando usarlo en vez de @CsvSource:
     *   - Muchas filas (a partir de diez o quince, la anotacion estorba).
     *   - El archivo lo mantiene alguien que no toca el codigo.
     *   - Los datos salen de una exportacion de la base de datos.
     *
     * numLinesToSkip = 1 se salta la cabecera. Si se te olvida, JUnit
     * intentara convertir la palabra "curp" en... una CURP, y el fallo
     * que veras no dira "te falto saltarte la cabecera": dira algo sobre
     * una conversion imposible. Vale la pena reconocerlo a la primera.
     *
     * La ultima columna viene VACIA en algunas filas. Sin nullValues,
     * JUnit la entrega como cadena vacia y la conversion a enum truena.
     * Con nullValues, esas celdas llegan como null.
     */
    @ParameterizedTest(name = "[{index}] {0} nacio el {1}")
    @CsvFileSource(resources = "/curps-validas.csv", numLinesToSkip = 1, nullValues = "")
    void desdeUnArchivo(String curp, LocalDate fecha, Sexo sexo, String entidad, Nivel beca) {
        assertAll(
                () -> assertTrue(ValidadorCurp.esValida(curp), curp + " deberia ser valida"),
                () -> assertEquals(fecha, ValidadorCurp.fechaNacimiento(curp)),
                () -> assertEquals(sexo, ValidadorCurp.sexo(curp)),
                () -> assertEquals(entidad, ValidadorCurp.entidad(curp)));

        // 'beca' es null en las filas donde la columna venia vacia.
        if (beca != null) {
            assertTrue(beca.esAprobatorio(), "Una beca no se da con nivel insuficiente");
        }
    }

    /**
     * Una fila puede describir tambien lo que NO debe pasar.
     * Aqui el segundo parametro es el MOTIVO del rechazo: no se afirma
     * sobre el, pero aparece en el nombre de la corrida y convierte el
     * reporte en documentacion legible.
     */
    @ParameterizedTest(name = "{0} se rechaza porque {1}")
    @CsvSource({
            "RUGM800101HNLZRK0,   le falta el digito verificador",
            "RBGM800101HNLZRK09,  la segunda letra no es vocal",
            "RUGM801301HNLZRK09,  el mes 13 no existe",
            "RUGM800101XNLZRK09,  el sexo solo puede ser H o M",
            "RUGM800101HZZZRK09,  ZZ no es una entidad federativa"
    })
    void motivosDeRechazo(String curp, String motivo) {
        assertFalse(ValidadorCurp.esValida(curp), motivo);
    }
}
