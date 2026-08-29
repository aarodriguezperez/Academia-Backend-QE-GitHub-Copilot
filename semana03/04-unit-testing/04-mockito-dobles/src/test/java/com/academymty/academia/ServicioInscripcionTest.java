package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * SECCION 05 de la guia 04 -- El test de verdad, con @InjectMocks.
 *
 * Esta es la clase que justifica todo el proyecto. Y fijate en la decision
 * mas importante que toma, que no es de Mockito sino de criterio:
 *
 *   SE MOCKEAN los dos repositorios y el notificador -- lentos y externos.
 *   NO SE MOCKEA el Curso                            -- es rapido y DECIDE.
 *
 * El Curso se construye de verdad, con new. Cuesta microsegundos y, sobre
 * todo, hace que la regla del cupo se ejecute de verdad en cada test. En
 * cuanto lo mockeas, dejas de probar esa regla -- y eso es exactamente lo
 * que demuestra SobreMockeoTest en la seccion 09.
 *
 *   Mockea lo que DUELE (lento, externo, no repetible).
 *   No mockees lo que DECIDE.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("El servicio de inscripcion")
class ServicioInscripcionTest {

    private static final Alumno ANA = new Alumno("A01", "Ana Torres", "ana@academymty.mx");

    @Mock private RepositorioAlumnos repoAlumnos;
    @Mock private RepositorioCursos repoCursos;
    @Mock private Notificador notificador;

    /**
     * @InjectMocks construye el objeto de verdad y le pasa los tres @Mock
     * de arriba por el constructor.
     *
     * Los empareja por TIPO. Si tu constructor tuviera dos parametros del
     * mismo tipo, Mockito los emparejaria por nombre de campo -- y si
     * tampoco cuadran, te deja un null dentro sin decir nada. Por eso, en
     * un servicio con muchas dependencias, a veces es mas honesto construirlo
     * a mano con new: el compilador te obliga a no olvidarte de ninguna.
     */
    @InjectMocks
    private ServicioInscripcion servicio;

    private Curso java101;

    @BeforeEach
    void cursoRealConDosLugares() {
        java101 = new Curso("JAVA-101", 2);      // real, no mockeado
    }

    @Nested
    @DisplayName("cuando todo esta en orden")
    class CaminoFeliz {

        @Test
        @DisplayName("inscribe, descuenta el lugar y manda la confirmacion")
        void inscribeYConfirma() {
            when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
            when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

            Inscripcion resultado = servicio.inscribir("A01", "JAVA-101");

            assertAll("el comprobante",
                    () -> assertEquals("A01", resultado.matricula()),
                    () -> assertEquals("JAVA-101", resultado.claveCurso()),
                    () -> assertEquals(1, resultado.lugaresRestantes()));

            // El curso REAL cambio de estado. Con un mock esto no se veria.
            assertTrue(java101.inscritos().contains("A01"));
            assertEquals(1, java101.lugaresDisponibles());

            verify(notificador).enviarConfirmacion(ANA, java101);
            verify(notificador, never()).enviarRechazo(any(), any(), anyString());
        }
    }

    @Nested
    @DisplayName("cuando el curso ya esta lleno")
    class CursoLleno {

        @BeforeEach
        void llenarlo() {
            java101.inscribir("A08");
            java101.inscribir("A09");            // cupo 2: lleno
        }

        @Test
        @DisplayName("rechaza, avisa del rechazo y NO manda confirmacion")
        void rechazaSinConfirmar() {
            when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
            when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

            assertThrows(CupoLlenoException.class,
                    () -> servicio.inscribir("A01", "JAVA-101"));

            verify(notificador).enviarRechazo(ANA, java101, "cupo lleno");

            // EL TEST QUE MAS VALE DE TODA LA CLASE.
            // Sin el, un bug podria mandar "felicidades, ya estas inscrito"
            // a alguien que se quedo fuera -- y todo lo demas seguiria en verde.
            verify(notificador, never()).enviarConfirmacion(any(), any());
        }

        @Test
        @DisplayName("y el curso no se toca: sigue con sus dos de siempre")
        void noSeCuela() {
            when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
            when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

            assertThrows(CupoLlenoException.class,
                    () -> servicio.inscribir("A01", "JAVA-101"));

            assertEquals(2, java101.inscritos().size());
            assertTrue(!java101.inscritos().contains("A01"));
        }
    }

    @Nested
    @DisplayName("cuando algo no existe")
    class NoExiste {

        /**
         * Fijate en lo que NO hay aqui: ningun when() sobre repoCursos.
         *
         * No es un olvido. El servicio no llega a consultarlo, y con la
         * estrictez por defecto de MockitoExtension, programar algo que no
         * se usa ROMPE el test con UnnecessaryStubbingException. Esa
         * severidad es una funcion, no una molestia: te obliga a que el
         * test describa exactamente el camino que recorre. Seccion 08.
         */
        @Test
        @DisplayName("alumno inexistente: ni se consulta el curso, ni se avisa a nadie")
        void alumnoInexistente() {
            when(repoAlumnos.buscar("A99")).thenReturn(Optional.empty());

            assertThrows(AlumnoNoEncontradoException.class,
                    () -> servicio.inscribir("A99", "JAVA-101"));

            verifyNoInteractions(repoCursos);
            verifyNoInteractions(notificador);
        }

        @Test
        @DisplayName("curso inexistente: no se avisa a nadie")
        void cursoInexistente() {
            when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
            when(repoCursos.buscar("NO-EXISTE")).thenReturn(Optional.empty());

            assertThrows(CursoNoEncontradoException.class,
                    () -> servicio.inscribir("A01", "NO-EXISTE"));

            verifyNoInteractions(notificador);
        }
    }

    @Nested
    @DisplayName("cuando la base de datos falla")
    class BaseCaida {

        /**
         * Esto es lo que un doble te deja hacer y la base real no: provocar
         * el fallo. Sin Mockito, para probar este camino habria que apagar
         * la base de datos a mitad de la suite.
         */
        @Test
        @DisplayName("el fallo sube y no se manda ningun correo")
        void laBaseTruena() {
            when(repoAlumnos.buscar("A01"))
                    .thenThrow(new IllegalStateException("conexion perdida"));

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> servicio.inscribir("A01", "JAVA-101"));

            assertEquals("conexion perdida", ex.getMessage());
            verifyNoInteractions(notificador);
        }
    }
}
