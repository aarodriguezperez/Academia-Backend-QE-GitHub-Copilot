package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestReporter;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SECCION 06 de la guia 02 -- JUnit te inyecta cosas.
 *
 * En la guia 01 dijimos que un test "no recibe parametros". Era una
 * mentira didactica. La verdad es esta: JUnit resuelve los parametros
 * del metodo por TIPO, igual que un contenedor de inyeccion de
 * dependencias. Si pides un TestInfo, te lo da.
 *
 * Y esto no es un truco cerrado: es el mismo mecanismo (ParameterResolver)
 * con el que Spring te inyecta el ApplicationContext en un @SpringBootTest.
 * Entender esto aqui, con tres clases, hace que aquello se entienda solo.
 *
 * Funciona en @Test, en @BeforeEach, en @AfterEach y en el constructor.
 */
class InyeccionTest {

    private Curso curso;

    /**
     * TestInfo tambien se puede pedir en el @BeforeEach: util para logs
     * que dicen que test esta a punto de correr.
     */
    @BeforeEach
    void preparar(TestInfo info) {
        curso = new Curso("JAVA-101", 3);
        System.out.println("   -> arrancando: " + info.getDisplayName());
    }

    /**
     * TestInfo: metadatos del test que esta corriendo AHORA.
     * Nombre del metodo, @DisplayName, clase y etiquetas.
     */
    @Test
    @Tag("rapido")
    @DisplayName("TestInfo sabe quien es y como se llama")
    void metadatosDelTest(TestInfo info) {
        assertEquals("TestInfo sabe quien es y como se llama", info.getDisplayName());
        assertEquals("metadatosDelTest", info.getTestMethod().orElseThrow().getName());
        assertEquals(InyeccionTest.class, info.getTestClass().orElseThrow());
        assertTrue(info.getTags().contains("rapido"));
    }

    /**
     * TestReporter en lugar de System.out.println().
     *
     * La diferencia: lo que publicas aqui entra en el REPORTE (el XML de
     * Surefire, el arbol del IDE), atado al test que lo produjo. Un
     * println se pierde en un muro de texto compartido por toda la suite.
     */
    @Test
    @DisplayName("TestReporter: dejar rastro en el reporte, no en la consola")
    void publicarEnElReporte(TestReporter reporter) {
        curso.inscribir("A01");
        curso.inscribir("A02");

        reporter.publishEntry("curso", curso.clave());
        reporter.publishEntry("lugares_restantes", String.valueOf(curso.lugaresDisponibles()));

        assertEquals(1, curso.lugaresDisponibles());
    }

    /**
     * @TempDir: un directorio de verdad, vacio, que JUnit crea antes del
     * test y BORRA ENTERO al terminar -- pase o falle.
     *
     * Es la respuesta a un problema viejo y feo: el test que escribe en
     * /tmp/salida.txt, pasa la primera vez, y a la segunda pasa porque el
     * archivo de la primera seguia ahi. Basura que sobrevive entre
     * corridas y hace que la suite mienta.
     *
     * Con @TempDir eso no puede pasar: cada test empieza con un directorio
     * nuevo. Pidelo por parametro (uno por test) o como campo.
     */
    @Test
    @Tag("lento")
    @DisplayName("@TempDir: escribir el acta en disco sin dejar basura")
    void escribirElActa(@TempDir Path carpeta) throws IOException {
        curso.inscribir("A01");
        curso.inscribir("A02");

        Path archivo = carpeta.resolve("acta-" + curso.clave() + ".txt");
        Files.writeString(archivo, curso.actaDeInscripcion());

        assertTrue(Files.exists(archivo));

        List<String> lineas = Files.readAllLines(archivo);
        assertEquals(3, lineas.size(), "cabecera + 2 alumnos");
        assertTrue(lineas.get(0).startsWith("ACTA JAVA-101"));
        assertTrue(lineas.contains(" - A01"));

        // Al salir de este metodo, JUnit borra 'carpeta' completa.
        // No hay @AfterEach que limpiar ni archivo que se quede colgado.
    }

    /**
     * Se pueden combinar. Los parametros van en cualquier orden: JUnit
     * los resuelve por tipo, no por posicion.
     */
    @Test
    @DisplayName("Varias inyecciones a la vez")
    void variasALaVez(TestInfo info, TestReporter reporter, @TempDir Path carpeta) throws IOException {
        Path archivo = carpeta.resolve("evidencia.txt");
        Files.writeString(archivo, "test: " + info.getDisplayName());

        reporter.publishEntry("evidencia", archivo.getFileName().toString());

        assertTrue(Files.readString(archivo).contains("Varias inyecciones"));
    }
}
