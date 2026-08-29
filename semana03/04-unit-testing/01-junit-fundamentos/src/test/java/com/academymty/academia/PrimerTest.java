package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 01 de la guia -- La anatomia de un test.
 *
 * Un test es un metodo normal de Java con tres caracteristicas y ni una mas:
 *
 *   1. Lleva @Test encima.
 *   2. No devuelve nada (void) y no recibe parametros.  (*)
 *   3. Termina en una asercion: una frase que dice que ESPERABAS.
 *
 * (*) Lo de "no recibe parametros" es mentira a medias, y en el proyecto 02
 *     vas a ver por que: JUnit sabe inyectarte cosas. Pero para empezar,
 *     piensalo asi.
 *
 * Fijate en algo que sorprende a todo el mundo: la clase NO es public,
 * los metodos NO son public. JUnit 5 en adelante no lo necesita
 * (JUnit 4 si lo exigia). Package-private basta y es lo idiomatico.
 */
class PrimerTest {

    /**
     * El nombre del metodo es documentacion. Este dice, leido en voz alta:
     * "una boleta recien creada promedia cero".
     *
     * Cuando este test falle -- y algun dia va a fallar -- lo primero que
     * vas a leer en la consola es ese nombre. Escribelo pensando en ese dia.
     */
    @Test
    void unaBoletaRecienCreadaPromediaCero() {

        // ---- 1. PREPARAR: monta el escenario ------------------------------
        Alumno ana = new Alumno("A01", "Ana Torres");
        Boleta boleta = new Boleta(ana);

        // ---- 2. ACTUAR: ejecuta lo que quieres probar ----------------------
        double promedio = boleta.promedio();

        // ---- 3. COMPROBAR: una sola pregunta, con respuesta si o no --------
        assertEquals(0.0, promedio);
    }

    /**
     * @DisplayName sirve para lo que el nombre del metodo no puede: espacios,
     * acentos, dos puntos, numeros al inicio. Es lo que veras en el IDE y en
     * el reporte de Surefire, en lugar del nombre del metodo.
     *
     * Regla practica: el nombre del metodo para quien lee el codigo,
     * el @DisplayName para quien lee el reporte.
     */
    @Test
    @DisplayName("Registrar una materia deja el promedio en esa calificacion")
    void unaSolaMateria() {
        Boleta boleta = new Boleta(new Alumno("A01", "Ana Torres"));

        boleta.registrar("Java", 90);

        assertEquals(90.0, boleta.promedio());
        assertEquals(1, boleta.totalMaterias());
    }

    /**
     * El tercer parametro de una asercion es el MENSAJE DE FALLO.
     * No describe lo que pasa cuando el test va bien: describe lo que
     * quieres leer el dia que se ponga en rojo.
     *
     * Compara los dos mensajes que veria alguien a las 11 de la noche:
     *
     *   sin mensaje ->  expected: <true> but was: <false>
     *   con mensaje ->  Con 90 de promedio deberia aprobar
     *
     * El segundo te dice QUE regla se rompio. El primero no dice nada.
     */
    @Test
    @DisplayName("Con 90 de promedio, el alumno aprueba")
    void aprobarConNoventa() {
        Boleta boleta = new Boleta(new Alumno("A01", "Ana Torres"));
        boleta.registrar("Java", 90);

        assertTrue(boleta.aprobado(), "Con 90 de promedio deberia aprobar");
    }
}
