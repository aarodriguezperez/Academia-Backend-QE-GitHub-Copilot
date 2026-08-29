package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 03 de la guia -- El catalogo de aserciones.
 *
 * Todas viven en la MISMA clase: org.junit.jupiter.api.Assertions.
 * Y todas son static, por eso el import lleva "static".
 *
 * El orden de los argumentos es SIEMPRE el mismo y hay que memorizarlo:
 *
 *      assertEquals(loQueESPERAS, loQueOBTUVISTE)
 *                   ^ esperado    ^ real
 *
 * Al reves compila igual y el test pasa igual. Solo se nota el dia que falla,
 * y entonces el mensaje te miente al derecho: te dice "expected: <90>" cuando
 * 90 era en realidad el resultado. Es un error caro y silencioso.
 */
class AsercionesTest {

    private Boleta boleta;

    @BeforeEach
    void prepararBoleta() {
        boleta = new Boleta(new Alumno("A01", "Ana Torres"));
        boleta.registrar("Java", 90);
        boleta.registrar("SQL", 70);
    }

    @Test
    @DisplayName("Igualdad, negacion y nulos")
    void lasBasicas() {
        assertEquals(80.0, boleta.promedio());       // (90 + 70) / 2
        assertEquals(2, boleta.totalMaterias());

        assertTrue(boleta.aprobado());
        assertFalse(boleta.calificaciones().isEmpty());

        assertNotNull(boleta.alumno());
        assertNull(boleta.calificaciones().get("Kotlin"),
                "Kotlin no se registro: el mapa devuelve null, no lanza excepcion");
    }

    /**
     * assertEquals compara con equals(). assertSame compara con ==.
     *
     * Alumno es un record, y los records traen equals() de fabrica comparando
     * campo por campo. Por eso dos Alumno distintos con los mismos datos son
     * EQUALS pero no son SAME.
     *
     * Esta distincion es la misma que viste en el proyecto stringStringBuilder.
     */
    @Test
    @DisplayName("assertEquals usa equals(); assertSame usa ==")
    void igualdadContraIdentidad() {
        Alumno unaAna   = new Alumno("A01", "Ana Torres");
        Alumno otraAna  = new Alumno("A01", "Ana Torres");

        assertEquals(unaAna, otraAna, "Mismos datos -> equals() dice que si");
        assertNotSame(unaAna, otraAna, "Pero son DOS objetos distintos en memoria");

        assertSame(unaAna, unaAna);
    }

    @Test
    @DisplayName("Arreglos y colecciones tienen su propia asercion")
    void coleccionesYArreglos() {
        // assertEquals sobre arreglos compararia REFERENCIAS y siempre fallaria.
        // Para eso existe assertArrayEquals: compara contenido, posicion a posicion.
        int[] esperadas = { 90, 70 };
        int[] reales    = boleta.calificaciones().values().stream().mapToInt(Integer::intValue).toArray();
        assertArrayEquals(esperadas, reales);

        // assertIterableEquals: mismo elemento, mismo orden. El tipo de coleccion
        // no importa -- una List puede ser "igual" a un Set ordenado.
        assertIterableEquals(List.of("Java", "SQL"), boleta.calificaciones().keySet());

        // assertLinesMatch entiende expresiones regulares linea por linea.
        // Util para comparar salidas de texto donde una parte es variable.
        assertLinesMatch(
                List.of("Alumno: A01", ">> promedio: \\d+\\.\\d"),
                List.of("Alumno: A01", ">> promedio: 80.0"));
    }

    /**
     * LA MAS IMPORTANTE DE ESTA CLASE.
     *
     * Cuatro aserciones seguidas se comportan como un cortocircuito: en cuanto
     * la primera falla, las demas NI SE EJECUTAN. Arreglas esa, corres otra vez,
     * y descubres que la segunda tambien fallaba. Tres iteraciones despues sigues
     * ahi.
     *
     * assertAll las ejecuta TODAS y te reporta juntas las que fallaron. Una sola
     * corrida, la foto completa del dano.
     *
     * Regla: si las aserciones describen el mismo objeto desde varios angulos,
     * van dentro de un assertAll.
     */
    @Test
    @DisplayName("assertAll: no te enteras de un fallo a la vez")
    void todasLasAsercionesDeUnGolpe() {
        assertAll("estado completo de la boleta",
                () -> assertEquals("A01", boleta.alumno().matricula()),
                () -> assertEquals("Ana Torres", boleta.alumno().nombre()),
                () -> assertEquals(2, boleta.totalMaterias()),
                () -> assertEquals(80.0, boleta.promedio()),
                () -> assertTrue(boleta.aprobado()));
    }

    /**
     * El mensaje de fallo puede venir como String o como Supplier<String>.
     *
     * Con String, el texto se construye SIEMPRE -- aunque el test pase.
     * Con Supplier, solo se construye si el test falla.
     *
     * En un test suelto da igual. En una suite de 4,000 tests que concatenan
     * cadenas en cada asercion, deja de dar igual.
     */
    @Test
    @DisplayName("El mensaje perezoso: () -> en vez de String")
    void mensajePerezoso() {
        assertTrue(boleta.aprobado(),
                () -> "Fallo con promedio " + boleta.promedio()
                      + " y materias " + boleta.calificaciones());
    }
}
