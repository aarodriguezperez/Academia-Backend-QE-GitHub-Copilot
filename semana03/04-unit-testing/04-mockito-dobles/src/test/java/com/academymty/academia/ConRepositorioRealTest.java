package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SECCION 01 de la guia 04 -- Por que existe Mockito, medido.
 *
 * Esta clase prueba lo MISMO que ServicioInscripcionTest, con una
 * diferencia: usa el RepositorioAlumnosLento de verdad, con sus 300 ms
 * de latencia simulada por consulta.
 *
 * Corre `./scripts/por-que-mockear.sh` y compara los dos numeros. No es
 * una diferencia de estilo: es la diferencia entre una suite que se corre
 * antes de cada commit y una que no se corre nunca.
 *
 * Va etiquetada @Tag("lento") por el mismo motivo que en la guia 02:
 *
 *     ./mvnw test -DexcludedGroups=lento
 *
 * Y ojo con una cosa: esta clase NO sobra. Los tests contra la pieza real
 * tienen su sitio -- son los unicos que comprueban que el repositorio de
 * verdad funciona. Lo que no puede pasar es que TODA la suite sea asi.
 */
@ExtendWith(MockitoExtension.class)
@Tag("lento")
@DisplayName("[lento] El servicio contra el repositorio REAL")
class ConRepositorioRealTest {

    /** El de verdad. 300 ms cada consulta. */
    private final RepositorioAlumnos repoReal = new RepositorioAlumnosLento();

    @Mock private RepositorioCursos repoCursos;
    @Mock private Notificador notificador;

    private ServicioInscripcion servicioConRepoReal() {
        return new ServicioInscripcion(repoReal, repoCursos, notificador);
    }

    @Test
    @DisplayName("inscribe a Ana, tras esperar a la base")
    void inscribeAAna() {
        Curso java101 = new Curso("JAVA-101", 2);
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

        Inscripcion resultado = servicioConRepoReal().inscribir("A01", "JAVA-101");

        assertEquals("A01", resultado.matricula());
        assertEquals(1, resultado.lugaresRestantes());
        verify(notificador).enviarConfirmacion(
                new Alumno("A01", "Ana Torres", "ana@academymty.mx"), java101);
    }

    @Test
    @DisplayName("inscribe a Beto, tras esperar otra vez")
    void inscribeABeto() {
        Curso java101 = new Curso("JAVA-101", 2);
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

        assertEquals("A02", servicioConRepoReal().inscribir("A02", "JAVA-101").matricula());
    }

    @Test
    @DisplayName("inscribe a Carla, y van tres esperas")
    void inscribeACarla() {
        Curso java101 = new Curso("JAVA-101", 2);
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

        assertEquals("A03", servicioConRepoReal().inscribir("A03", "JAVA-101").matricula());
    }

    @Test
    @DisplayName("un alumno que no esta en la base")
    void alumnoInexistente() {
        assertThrows(AlumnoNoEncontradoException.class,
                () -> servicioConRepoReal().inscribir("A99", "JAVA-101"));
    }

    @Test
    @DisplayName("el curso lleno se rechaza igual, pero pagando la espera")
    void cursoLleno() {
        Curso lleno = new Curso("JAVA-101", 1);
        lleno.inscribir("A08");
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(lleno));

        assertThrows(CupoLlenoException.class,
                () -> servicioConRepoReal().inscribir("A01", "JAVA-101"));

        assertTrue(lleno.estaLleno());
    }
}
