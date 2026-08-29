package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 01 de la guia 03 -- Un test, muchos datos.
 *
 * Empieza por el problema. Asi se prueban cuatro CURP validas sin
 * @ParameterizedTest:
 *
 *     @Test void valida1() { assertTrue(ValidadorCurp.esValida("RUGM...")); }
 *     @Test void valida2() { assertTrue(ValidadorCurp.esValida("TOLA...")); }
 *     @Test void valida3() { ... }
 *     @Test void valida4() { ... }
 *
 * Cuatro metodos identicos salvo por una cadena. Y con dos consecuencias
 * malas: anadir el quinto caso cuesta copiar y pegar (asi que nadie lo
 * hace), y si cambia la regla hay que tocar cuatro sitios.
 *
 * Un @ParameterizedTest es UN metodo que corre N veces, una por dato.
 * En el reporte siguen apareciendo como N tests independientes: si el
 * tercero falla, los otros tres siguen en verde y sabes exactamente
 * cual cayo.
 *
 * Dos cambios respecto a un @Test normal, y son los dos que se olvidan:
 *   1. @ParameterizedTest EN LUGAR DE @Test, no ademas.
 *   2. El metodo ahora SI recibe parametros.
 */
class ValueSourceTest {

    /**
     * @ValueSource: la forma mas simple. Un arreglo literal de un solo tipo.
     *
     * El atributo 'name' controla como se lee cada corrida en el reporte.
     * Sin el veras "[1]", "[2]", "[3]" -- inutil cuando algo falla.
     * Con el veras la CURP que fallo. Vale los diez segundos que cuesta.
     *
     * Marcadores disponibles: {index}, {0} {1}... (por posicion),
     * {argumentsWithNames}.
     */
    @ParameterizedTest(name = "[{index}] {0} es una CURP con estructura valida")
    @ValueSource(strings = {
            "RUGM800101HNLZRK09",     // 1980-01-01, hombre, Nuevo Leon
            "TOLA010615MNLRPNA5",     // 2001-06-15, mujer, Nuevo Leon
            "MAAJ990228HDFRRS08",     // 1999-02-28, hombre, CDMX
            "XEXX010101HNEXXXA4"      // la CURP generica de extranjero
    })
    void curpsValidas(String curp) {
        assertTrue(ValidadorCurp.esValida(curp), "Deberia ser valida: " + curp);
    }

    /**
     * La otra mitad, y la que de verdad prueba el validador: las que NO pasan.
     *
     * Fijate en que cada una rompe UNA sola regla. Si un dato rompiera dos,
     * el test seguiria en verde aunque el validador solo detectara una, y no
     * te enterarias. Un caso invalido por cada motivo.
     */
    @ParameterizedTest(name = "[{index}] {0} NO es valida")
    @ValueSource(strings = {
            "RUGM800101HNLZRK0",      // 17 caracteres: falta el verificador
            "RUGM800101HNLZRK099",    // 19: sobra uno
            "RBGM800101HNLZRK09",     // la 2a posicion debe ser vocal (o X); B no
            "RUGM801301HNLZRK09",     // mes 13
            "RUGM800132HNLZRK09",     // dia 32
            "RUGM800000HNLZRK09",     // mes 00 y dia 00
            "RUGM800101XNLZRK09",     // sexo X: solo H o M
            "RUGM800101HXXZRK09",     // XX no es una entidad federativa
            "RUGM800101HNLAEK09",     // A y E son vocales en el bloque de consonantes
            "rugm800101hnlzrk09"      // minusculas
    })
    void curpsInvalidas(String curp) {
        assertFalse(ValidadorCurp.esValida(curp), "No deberia ser valida: " + curp);
    }

    /**
     * @NullSource y @EmptySource: el null y la cadena vacia.
     *
     * @ValueSource NO puede producir null -- las anotaciones de Java no
     * admiten null en sus arreglos. Por eso existen aparte.
     *
     * Y merecen su propio test siempre. El null y el vacio son, por mucho,
     * los dos valores que mas veces tumban codigo en produccion.
     */
    @ParameterizedTest(name = "[{index}] entrada vacia o nula -> invalida")
    @NullAndEmptySource                          // equivale a @NullSource + @EmptySource
    @ValueSource(strings = { " ", "   ", "\t" }) // y de paso, solo espacios
    void nadaEsValido(String curp) {
        assertFalse(ValidadorCurp.esValida(curp));
    }

    /**
     * Las anotaciones de fuente se APILAN: los datos se suman.
     * Este test corre 1 (null) + 1 (vacio) + 2 (@ValueSource) = 4 veces.
     */
    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = { "ABC", "12345678901234567890" })
    void variasFuentesSeSuman(String curp) {
        assertFalse(ValidadorCurp.esValida(curp));
    }

    /**
     * @ValueSource tambien va con numeros. Aqui, los limites del Calificador.
     *
     * Compara este metodo con lo que serian nueve @Test sueltos. La tabla
     * de limites se lee de un vistazo, y anadir un caso es anadir un numero.
     */
    @ParameterizedTest(name = "{0} es aprobatorio")
    @ValueSource(ints = { 70, 71, 79, 80, 89, 90, 100 })
    void calificacionesAprobatorias(int calificacion) {
        assertTrue(Calificador.nivelDe(calificacion).esAprobatorio());
    }

    @ParameterizedTest(name = "{0} NO es aprobatorio")
    @ValueSource(ints = { 0, 1, 68, 69 })
    void calificacionesReprobatorias(int calificacion) {
        assertFalse(Calificador.nivelDe(calificacion).esAprobatorio());
        assertEquals(Nivel.INSUFICIENTE, Calificador.nivelDe(calificacion));
    }

    /**
     * CUANDO NO USAR @ParameterizedTest.
     *
     * Si cada caso necesita una afirmacion distinta, no es el mismo test
     * con otros datos: son tests distintos. Forzarlos a caber en uno solo
     * produce un metodo lleno de if, que es peor que dos metodos.
     *
     * La pregunta que decide: si le cambio el dato, la afirmacion es la
     * misma? Si es que si -> parametrizado. Si no -> dos @Test.
     */
    @Test
    @DisplayName("Este NO se parametriza: cada caso afirma algo distinto")
    void cuandoNoParametrizar() {
        assertEquals(java.time.LocalDate.of(1980, 1, 1),
                ValidadorCurp.fechaNacimiento("RUGM800101HNLZRK09"));
        assertEquals(Sexo.MUJER, ValidadorCurp.sexo("TOLA010615MNLRPNA5"));
        assertEquals("DF", ValidadorCurp.entidad("MAAJ990228HDFRRS08"));
    }
}
