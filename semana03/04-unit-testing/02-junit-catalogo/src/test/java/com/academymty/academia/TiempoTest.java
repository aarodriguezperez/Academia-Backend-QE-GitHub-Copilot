package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 02 de la guia 02 -- Probar el tiempo.
 *
 * Curso.actaDeInscripcion() tarda 40 ms por alumno inscrito. Es una
 * simulacion, pero el problema que representa es real: un reporte que
 * crece con los datos y un dia deja de caber en la ventana de respuesta.
 *
 * Hay DOS familias de timeout y la diferencia entre ellas es la que
 * confunde a todo el mundo.
 */
class TiempoTest {

    private Curso java101;

    @BeforeEach
    void tresInscritos() {
        java101 = new Curso("JAVA-101", 10);
        java101.inscribir("A01");
        java101.inscribir("A02");
        java101.inscribir("A03");        // 3 x 40 ms = ~120 ms de acta
    }

    /**
     * assertTimeout ESPERA A QUE TERMINE y despues compara.
     *
     * Si el acta tardara 10 minutos, este test tardaria 10 minutos y
     * DESPUES se pondria en rojo. No corta nada. Lo que te da es el
     * diagnostico, no la proteccion.
     *
     * A cambio, tu codigo corre en el hilo del test, que es lo normal
     * y lo seguro. Esta es la que debes usar por defecto.
     */
    @Test
    @DisplayName("assertTimeout: mide, pero no interrumpe")
    void midePeroNoInterrumpe() {
        String acta = assertTimeout(Duration.ofSeconds(2),
                () -> java101.actaDeInscripcion());

        // assertTimeout devuelve lo que devolvio tu codigo: se puede seguir afirmando.
        assertNotNull(acta);
        assertTrue(acta.contains("A02"));
        assertEquals(4, acta.lines().count(), "cabecera + 3 alumnos");
    }

    /**
     * assertTimeoutPreemptively SI CORTA. Ejecuta tu codigo en OTRO HILO y
     * lo aborta al cumplirse el plazo.
     *
     * Y ese "otro hilo" es la letra pequena, la que cuesta una tarde de
     * depuracion: todo lo que viva en un ThreadLocal deja de estar ahi.
     * Transacciones de Spring, SecurityContext, MDC de logs, EntityManager.
     * Codigo que funciona perfectamente puede fallar solo por estar dentro
     * de un assertTimeoutPreemptively.
     *
     * Regla: uselo unicamente cuando el riesgo real sea que el codigo se
     * cuelgue PARA SIEMPRE y te bloquee el build. Para todo lo demas,
     * assertTimeout.
     */
    @Test
    @DisplayName("assertTimeoutPreemptively: corta, pero cambia de hilo")
    void cortaPeroCambiaDeHilo() {
        String hiloDelTest = Thread.currentThread().getName();

        String hiloDeDentro = assertTimeoutPreemptively(Duration.ofSeconds(2),
                () -> Thread.currentThread().getName());

        assertTrue(!hiloDelTest.equals(hiloDeDentro),
                "Corrio en otro hilo (" + hiloDeDentro + "), no en el del test ("
                + hiloDelTest + "). Ahi se pierden los ThreadLocal.");
    }

    /**
     * >>> DESCOMENTA la linea de abajo y corre `mvn test`.
     *
     * El mensaje es una de las mejores cosas que da JUnit:
     *
     *     execution exceeded timeout of 50 ms by 73 ms
     *
     * Te dice el plazo Y por cuanto te pasaste. Con eso ya sabes si te
     * falta afinar el codigo o mover la expectativa.
     */
    @Test
    @DisplayName("Un plazo imposible: 120 ms de trabajo en 50 ms")
    void plazoImposible() {
        // assertTimeout(Duration.ofMillis(50), () -> java101.actaDeInscripcion());  // <-- DESCOMENTA

        assertTimeout(Duration.ofSeconds(2), () -> java101.actaDeInscripcion());
    }

    /**
     * @Timeout hace lo mismo sin ensuciar el cuerpo del test.
     *
     * Se puede poner en un metodo, en la clase entera (aplica a todos sus
     * tests) o en un @BeforeEach. La diferencia practica con assertTimeout:
     * @Timeout cubre el metodo COMPLETO, no un bloque concreto.
     *
     * En una suite de CI, un @Timeout a nivel de clase es la red de
     * seguridad barata contra el test que se cuelga y para el build.
     */
    @Test
    @Timeout(value = 3, unit = TimeUnit.SECONDS)
    @DisplayName("@Timeout: el plazo va en la anotacion, no en el cuerpo")
    void plazoEnLaAnotacion() {
        assertNotNull(java101.actaDeInscripcion());
    }
}
