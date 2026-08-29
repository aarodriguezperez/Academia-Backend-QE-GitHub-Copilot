package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * SECCION 03 de la guia 04 -- Programar la respuesta: when(...).
 *
 * Se lee tal cual: "cuando te llamen asi, devuelve esto".
 *
 *     when(repo.buscar("A01")).thenReturn(Optional.of(ana));
 *          ^^^^^^^^^^^^^^^^^^  ^^^^^^^^^^
 *          la llamada           la respuesta
 *
 * Y una advertencia que ahorra media hora de desconcierto: eso NO es una
 * llamada de verdad. Cuando escribes repo.buscar("A01") dentro de when(),
 * Mockito intercepta la invocacion y la usa como PATRON, no la ejecuta.
 * Por eso el resultado de esa linea no significa nada.
 */
@ExtendWith(MockitoExtension.class)
class RespuestasTest {

    private static final Alumno ANA = new Alumno("A01", "Ana Torres", "ana@academymty.mx");

    @Mock
    private RepositorioAlumnos repo;

    @Test
    @DisplayName("thenReturn: la respuesta fija")
    void respuestaFija() {
        when(repo.buscar("A01")).thenReturn(Optional.of(ANA));

        assertEquals(Optional.of(ANA), repo.buscar("A01"));
        assertEquals("Ana Torres", repo.buscar("A01").orElseThrow().nombre());
    }

    /**
     * TRAMPA CLASICA: el mock solo responde al argumento EXACTO que programaste.
     *
     * Programaste "A01". Preguntas por "A99". No truena, no avisa: devuelve
     * el valor por defecto (Optional.empty()). Es coherente y sorprende a todo
     * el mundo la primera vez.
     */
    @Test
    @DisplayName("Un argumento distinto del programado devuelve el valor por defecto")
    void soloRespondeAlArgumentoExacto() {
        when(repo.buscar("A01")).thenReturn(Optional.of(ANA));

        assertEquals(Optional.of(ANA), repo.buscar("A01"));
        assertEquals(Optional.empty(), repo.buscar("A99"),
                "No lo programaste para A99: devuelve el vacio, no falla");
    }

    /**
     * Los MATCHERS: cuando el argumento concreto da igual.
     *
     * anyString(), any(), anyInt(), eq(...), argThat(...)
     *
     * REGLA QUE HAY QUE MEMORIZAR: si usas un matcher en un argumento,
     * TODOS los argumentos de esa llamada tienen que ser matchers. Un
     * valor literal mezclado con matchers lanza InvalidUseOfMatchersException.
     * Para eso existe eq(): "este argumento concreto, pero en forma de matcher".
     */
    @Test
    @DisplayName("anyString(): responde a cualquier matricula")
    void conMatchers() {
        when(repo.buscar(anyString())).thenReturn(Optional.of(ANA));

        assertEquals(Optional.of(ANA), repo.buscar("A01"));
        assertEquals(Optional.of(ANA), repo.buscar("A99"));
        assertEquals(Optional.of(ANA), repo.buscar("lo-que-sea"));
    }

    /**
     * argThat(): un matcher con tu propia condicion.
     * Util cuando "cualquiera" es demasiado y "este exacto" es demasiado poco.
     */
    @Test
    @DisplayName("argThat(): solo las matriculas que empiezan por A")
    void matcherPropio() {
        when(repo.buscar(argThat(m -> m != null && m.startsWith("A"))))
                .thenReturn(Optional.of(ANA));

        assertTrue(repo.buscar("A07").isPresent());
        assertTrue(repo.buscar("B07").isEmpty(), "La B no casa con el matcher");
    }

    /**
     * thenThrow: programar el camino triste.
     *
     * Es la unica forma razonable de probar "que hace mi codigo cuando la
     * base de datos se cae". Con la base de datos real habria que apagarla.
     */
    @Test
    @DisplayName("thenThrow: simular que la base de datos truena")
    void programarUnFallo() {
        when(repo.buscar("A01"))
                .thenThrow(new IllegalStateException("conexion perdida"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> repo.buscar("A01"));
        assertEquals("conexion perdida", ex.getMessage());
    }

    /**
     * Respuestas ENCADENADAS: una distinta por llamada.
     *
     * Sirve para probar reintentos: "la primera vez falla, la segunda funciona".
     * Y para probar paginacion, cursores, o cualquier cosa con estado.
     * A partir de la ultima, se repite la ultima.
     */
    @Test
    @DisplayName("Respuestas distintas en llamadas sucesivas")
    void respuestasEncadenadas() {
        when(repo.buscar("A01"))
                .thenReturn(Optional.empty())        // 1a llamada
                .thenReturn(Optional.of(ANA));       // 2a y siguientes

        assertTrue(repo.buscar("A01").isEmpty(),   "primera");
        assertTrue(repo.buscar("A01").isPresent(), "segunda");
        assertTrue(repo.buscar("A01").isPresent(), "tercera: se repite la ultima");
    }

    /**
     * thenAnswer: la respuesta se CALCULA a partir del argumento.
     *
     * Es el ultimo recurso, y conviene usarlo poco: un mock con logica dentro
     * empieza a ser un segundo programa que tambien puede tener bugs, y que
     * nadie prueba. Si necesitas mucho thenAnswer, casi siempre lo que
     * querias era un fake escrito a mano.
     */
    @Test
    @DisplayName("thenAnswer: construir la respuesta con el argumento recibido")
    void respuestaCalculada() {
        when(repo.buscar(anyString())).thenAnswer(invocacion -> {
            String matricula = invocacion.getArgument(0);
            return Optional.of(new Alumno(matricula, "Generico " + matricula,
                    matricula.toLowerCase() + "@academymty.mx"));
        });

        assertEquals("Generico A42", repo.buscar("A42").orElseThrow().nombre());
        assertEquals("a42@academymty.mx", repo.buscar("A42").orElseThrow().correo());
    }

    /**
     * La mezcla PROHIBIDA: un matcher y un literal en la misma llamada.
     *
     * Aqui no aplica porque buscar() solo tiene un argumento, asi que se
     * ensena con el notificador, que tiene dos. Descomenta y observa el error:
     *
     *   when(notificador...)  con (any(), curso)  -> InvalidUseOfMatchersException
     *
     * La forma correcta es envolver el literal en eq(), como abajo.
     */
    @Test
    @DisplayName("eq(): como mezclar un valor concreto con matchers")
    void mezclarMatchersConValores() {
        Curso java101 = new Curso("JAVA-101", 30);

        // Con dos argumentos, o los dos son matchers o ninguno:
        when(repo.buscar(eq("A01"))).thenReturn(Optional.of(ANA));

        assertEquals(Optional.of(ANA), repo.buscar("A01"));
        assertEquals("JAVA-101", java101.clave());
    }

    /**
     * Y el matcher generico any(), util cuando el tipo no es String.
     */
    @Test
    @DisplayName("any(): cualquier objeto del tipo que sea")
    void matcherGenerico() {
        RepositorioCursos cursos = org.mockito.Mockito.mock(RepositorioCursos.class);
        when(cursos.buscar(any())).thenReturn(Optional.of(new Curso("X", 1)));

        assertTrue(cursos.buscar("cualquier-cosa").isPresent());
    }
}
