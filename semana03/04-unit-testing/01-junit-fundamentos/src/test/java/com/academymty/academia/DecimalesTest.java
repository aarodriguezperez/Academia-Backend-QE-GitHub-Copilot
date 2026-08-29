package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 04 de la guia -- La trampa de los decimales.
 *
 * Aqui hay LINEAS COMENTADAS A PROPOSITO. Descomentalas y observa el fallo:
 * ese es el ejercicio de esta clase.
 *
 * El promedio de 70, 80 y 85 es 235/3 = 78.33333333333333, y un double no
 * puede guardar ese numero. Guarda el mas cercano que puede representar.
 * No es un bug de Java ni de JUnit: es como funciona el punto flotante
 * (IEEE 754) en cualquier lenguaje.
 */
class DecimalesTest {

    private Boleta boleta;

    @BeforeEach
    void tresMateriasQueNoDividenExacto() {
        boleta = new Boleta(new Alumno("A01", "Ana Torres"));
        boleta.registrar("Java", 70);
        boleta.registrar("SQL", 80);
        boleta.registrar("Redes", 85);
    }

    /**
     * PRIMERO: comprueba con tus ojos que el problema existe.
     * 0.1 + 0.2 no da 0.3. En ningun lenguaje que use double.
     */
    @Test
    @DisplayName("La suma de decimales no da lo que dice la primaria")
    void elProblemaDeRaiz() {
        double suma = 0.1 + 0.2;

        assertNotEquals(0.3, suma, "0.1 + 0.2 vale " + suma + ", no 0.3");
        assertEquals(0.3, suma, 0.000001, "Con tolerancia, si son 'iguales'");
    }

    /**
     * AHORA el mismo problema, pero en el codigo de la academia.
     *
     * >>> DESCOMENTA la linea de abajo y corre `./mvnw test`.
     *
     * El mensaje que veras es este, y merece la pena leerlo entero:
     *
     *     expected: <78.33> but was: <78.33333333333333>
     *
     * El test no esta mal escrito. El codigo no esta roto. Simplemente
     * pediste una igualdad EXACTA sobre un numero que no es exacto.
     */
    @Test
    @DisplayName("Sin tolerancia, un promedio con decimales periodicos falla")
    void laTrampa() {
        double promedio = boleta.promedio();

        // assertEquals(78.33, promedio);   // <-- DESCOMENTA ESTO

        assertTrue(promedio > 78.33 && promedio < 78.34,
                "El valor real anda por 78.3333..., no clavado en 78.33");
    }

    /**
     * LA SOLUCION: el tercer parametro de assertEquals para doubles es la
     * TOLERANCIA (delta). Se lee asi:
     *
     *     "esperaba 78.33, y acepto cualquier valor que no se aleje mas de 0.01"
     *
     * No es hacer trampa. Es declarar con cuanta precision te importa el
     * resultado -- que es una decision de negocio, no de programacion.
     * Para calificaciones, dos decimales sobran.
     */
    @Test
    @DisplayName("Con delta, el mismo promedio pasa")
    void laSolucion() {
        assertEquals(78.33, boleta.promedio(), 0.01);
    }

    /**
     * Regla para recordar cual usar:
     *
     *   int, long, String, boolean, enum, record  ->  assertEquals SIN delta
     *   double, float                             ->  assertEquals CON delta
     *   dinero                                    ->  ni double ni float: BigDecimal
     *
     * Esa ultima linea no es un detalle de tests. Es la razon por la que
     * ningun sistema bancario guarda pesos en un double.
     */
    @Test
    @DisplayName("Los enteros no tienen este problema: cuentan, no miden")
    void losEnterosNoSufren() {
        assertEquals(3, boleta.totalMaterias());
    }
}
