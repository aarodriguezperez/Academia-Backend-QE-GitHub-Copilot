package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ###################################################################
 * #                                                                 #
 * #   ESTA CLASE ES UN MAL EJEMPLO. NO LA COPIES.                   #
 * #                                                                 #
 * #   Esta aqui a proposito, y es el corazon de la seccion 09.       #
 * #                                                                 #
 * ###################################################################
 *
 * Leela y compara con ServicioInscripcionTest. Prueban EXACTAMENTE lo
 * mismo, las dos estan en verde, y las dos parecen razonables.
 *
 * La unica diferencia es una linea:
 *
 *     ServicioInscripcionTest:   Curso java101 = new Curso("JAVA-101", 2);
 *     esta clase:                @Mock Curso curso;
 *
 * Es decir: aqui se mockea la clase que CONTIENE LA REGLA DE NEGOCIO que
 * el servicio esta consultando. Y en el momento en que la mockeas, la
 * regla deja de ejecutarse. El test ya no comprueba "el curso lleno se
 * rechaza": comprueba "si yo digo que esta lleno, se rechaza".
 *
 * Que es una tautologia. El test aprueba su propia respuesta.
 *
 * Corre `./scripts/la-mentira.sh` para verlo: rompe la regla del cupo
 * dentro de Curso y esta clase SIGUE EN VERDE, tan tranquila, mientras
 * ServicioInscripcionTest se pone en rojo.
 *
 * La regla que sale de aqui:
 *
 *     MOCKEA lo que DUELE   -- lento, externo, no repetible, no determinista.
 *     NO MOCKEES lo que DECIDE -- si tiene la regla de negocio dentro, va real.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MAL EJEMPLO: sobre-mockeo")
class SobreMockeoTest {

    private static final Alumno ANA = new Alumno("A01", "Ana Torres", "ana@academymty.mx");

    @Mock private RepositorioAlumnos repoAlumnos;   // correcto: es lento
    @Mock private RepositorioCursos repoCursos;     // correcto: es caro de montar
    @Mock private Notificador notificador;          // correcto: manda correos

    /** AQUI ESTA EL ERROR. Curso es rapido, local y tiene la regla dentro. */
    @Mock private Curso curso;

    @InjectMocks private ServicioInscripcion servicio;

    @Test
    @DisplayName("inscribe cuando hay lugar (o eso cree)")
    void inscribeCuandoHayLugar() {
        when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(curso));

        // Estas dos lineas son el problema: la respuesta la escribe el test.
        when(curso.estaLleno()).thenReturn(false);
        when(curso.lugaresDisponibles()).thenReturn(1);

        Inscripcion resultado = servicio.inscribir("A01", "JAVA-101");

        assertEquals("A01", resultado.matricula());
        assertEquals(1, resultado.lugaresRestantes());
        verify(notificador).enviarConfirmacion(ANA, curso);
    }

    @Test
    @DisplayName("rechaza cuando esta lleno (o eso cree)")
    void rechazaCuandoEstaLleno() {
        when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(curso));

        // Idem: "esta lleno porque yo lo digo", no porque lo este.
        when(curso.estaLleno()).thenReturn(true);
        when(curso.cupo()).thenReturn(2);

        assertThrows(CupoLlenoException.class,
                () -> servicio.inscribir("A01", "JAVA-101"));

        verify(notificador).enviarRechazo(eq(ANA), eq(curso), anyString());
        verify(notificador, never()).enviarConfirmacion(any(), any());
    }

    /**
     * Y este es el remate. El test comprueba que el curso recibio la orden
     * de inscribir... a un mock que no hace nada con ella.
     *
     * Es verde, es inutil, y ademas ACOPLA el test a la implementacion: si
     * manana el servicio inscribe de otra forma igual de correcta, este
     * test se rompe sin que nada este mal. Lo peor de los dos mundos.
     */
    @Test
    @DisplayName("verifica que se llamo a inscribir... a un objeto vacio")
    void verificaLaLlamadaAUnObjetoVacio() {
        when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(curso));
        when(curso.estaLleno()).thenReturn(false);

        servicio.inscribir("A01", "JAVA-101");

        verify(curso).inscribir("A01");
    }
}
