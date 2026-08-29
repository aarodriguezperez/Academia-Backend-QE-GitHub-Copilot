package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

/**
 * SECCION 08 de la guia 04 -- La estrictez, y por que te esta ayudando.
 *
 * Con MockitoExtension, la estrictez por defecto es STRICT_STUBS. Significa
 * que un when(...) que ningun codigo llega a usar NO se ignora: rompe el
 * test con UnnecessaryStubbingException.
 *
 * A todo el mundo le parece una molestia el primer dia. No lo es: es de las
 * mejores decisiones de diseno de Mockito, y por dos motivos.
 *
 *   1. Un stub sin usar suele significar que el test NO recorre el camino
 *      que su autor creia. El aviso te esta diciendo "esto no pasa por ahi".
 *   2. Los stubs sobrantes se acumulan. En una suite vieja acabas con
 *      metodos de veinte lineas de when() de los que solo tres importan,
 *      y nadie se atreve a borrar ninguno.
 *
 * Es el mismo principio que ya viste en la guia 01 con los tests que no
 * atrapan nada: el problema no es lo que falla, es lo que sobra en verde.
 */
@ExtendWith(MockitoExtension.class)
class EstrictezTest {

    private static final Alumno ANA = new Alumno("A01", "Ana Torres", "ana@academymty.mx");

    @Mock
    private RepositorioAlumnos repo;

    /**
     * Un stub que SI se usa. Normal y correcto.
     *
     * >>> Anade debajo una linea que programe algo que no vas a llamar:
     *
     *       when(repo.buscar("A99")).thenReturn(Optional.empty());
     *
     *     y corre `./mvnw test -Dtest=EstrictezTest`. Vas a ver:
     *
     *       UnnecessaryStubbingException:
     *       Unnecessary stubbings detected.
     *       Clean & maintainable test code requires zero unnecessary code.
     *
     *     Ese es el ejercicio de esta clase.
     */
    @Test
    @DisplayName("Un stub que se usa: todo en orden")
    void stubUsado() {
        when(repo.buscar("A01")).thenReturn(Optional.of(ANA));

        assertEquals("Ana Torres", repo.buscar("A01").orElseThrow().nombre());
    }

    /**
     * lenient(): la valvula de escape, para UN stub concreto.
     *
     * Usala cuando el stub es un montaje comun que solo algunos tests de la
     * clase llegan a usar -- tipicamente algo que vive en un @BeforeEach.
     *
     * Y usala poco. Cada lenient() es un trocito de la red de seguridad que
     * apagas. Si te encuentras poniendolos por todas partes, el problema no
     * es la estrictez: es que la clase de test esta montando un escenario
     * demasiado grande para lo que cada test necesita.
     */
    @Test
    @DisplayName("lenient(): este stub puede quedarse sin usar")
    void stubTolerado() {
        lenient().when(repo.buscar("A99")).thenReturn(Optional.empty());

        // Nunca llamamos a buscar("A99") y aun asi el test pasa.
        assertTrue(true);
    }

    /**
     * @MockitoSettings apaga la estrictez de la CLASE entera.
     *
     * Practicamente nunca es lo correcto en codigo nuevo. Su sitio es una
     * migracion: heredas doscientas clases de test escritas con la
     * estrictez vieja, se ponen todas en rojo al actualizar, y esto te deja
     * avanzar mientras las arreglas por partes.
     *
     * Si lo pones en un test que escribes hoy, lo que estas haciendo es
     * silenciar al unico que te iba a avisar.
     */
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("@MockitoSettings LENIENT: apaga el aviso, con su precio")
    void toleranteEnTodoElMetodo() {
        when(repo.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repo.buscar("A99")).thenReturn(Optional.empty());   // este no se usa

        assertEquals("Ana Torres", repo.buscar("A01").orElseThrow().nombre());
    }

    /**
     * La otra cara de la estrictez, que se agradece mucho mas: el
     * PotentialStubbingProblem.
     *
     * Si programas buscar("A01") y tu codigo llama a buscar("A99"), la
     * estrictez estricta te lo dice a la cara, con los dos valores. Sin
     * ella, el mock devolveria Optional.empty() en silencio y te pasarias
     * media hora buscando por que tu servicio "no encuentra al alumno".
     */
    @Test
    @DisplayName("La estrictez tambien caza el argumento equivocado")
    void argumentoQueNoCuadra() {
        when(repo.buscar("A01")).thenReturn(Optional.of(ANA));

        // Llamar con "A99" aqui lanzaria PotentialStubbingProblem y diria
        // exactamente que esperabas "A01". Descomenta para verlo:
        //     repo.buscar("A99");

        assertTrue(repo.buscar("A01").isPresent());
    }
}
