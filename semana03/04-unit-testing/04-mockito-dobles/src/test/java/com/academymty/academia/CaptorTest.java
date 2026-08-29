package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SECCION 06 de la guia 04 -- ArgumentCaptor: que le pasaron, exactamente.
 *
 * verify() responde "se llamo, si o no". ArgumentCaptor responde algo mas
 * fino: "con QUE lo llamaron". Atrapa el argumento real y te lo entrega
 * para que le hagas las aserciones que quieras.
 *
 * Cuando usar cada cosa, que es la duda de siempre:
 *
 *   El argumento es simple y lo conoces  -> verify(mock).metodo(valorEsperado)
 *   El argumento lo CONSTRUYO tu codigo  -> ArgumentCaptor
 *
 * El segundo caso es el que importa. Si tu servicio arma un objeto por
 * dentro -- un correo, un evento, un registro de auditoria -- no tienes
 * una referencia con la que compararlo. El captor es la unica forma de
 * mirar lo que construyo.
 */
@ExtendWith(MockitoExtension.class)
class CaptorTest {

    private static final Alumno ANA = new Alumno("A01", "Ana Torres", "ana@academymty.mx");

    @Mock private RepositorioAlumnos repoAlumnos;
    @Mock private RepositorioCursos repoCursos;
    @Mock private Notificador notificador;

    @InjectMocks private ServicioInscripcion servicio;

    /** @Captor evita escribir ArgumentCaptor.forClass(...) y ademas conserva los genericos. */
    @Captor private ArgumentCaptor<Alumno> alumnoCapturado;
    @Captor private ArgumentCaptor<String> motivoCapturado;

    private Curso java101;

    @BeforeEach
    void curso() {
        java101 = new Curso("JAVA-101", 2);
    }

    @Test
    @DisplayName("A quien se le mando la confirmacion, exactamente")
    void aQuienSeLeMando() {
        when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

        servicio.inscribir("A01", "JAVA-101");

        // El captor se coloca DONDE iria el matcher, dentro del verify:
        verify(notificador).enviarConfirmacion(alumnoCapturado.capture(), any());

        Alumno recibido = alumnoCapturado.getValue();
        assertAll("el alumno que llego al notificador",
                () -> assertEquals("A01", recibido.matricula()),
                () -> assertEquals("Ana Torres", recibido.nombre()),
                () -> assertTrue(recibido.correo().endsWith("@academymty.mx"),
                        "El correo tiene que ser del dominio de la academia"));
    }

    /**
     * El caso donde el captor es imprescindible: el MOTIVO del rechazo lo
     * escribe el servicio por dentro. No tienes ninguna referencia previa
     * con la que compararlo -- solo puedes atraparlo al vuelo.
     */
    @Test
    @DisplayName("Con que motivo se rechazo")
    void conQueMotivo() {
        java101.inscribir("A08");
        java101.inscribir("A09");                     // lleno

        when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(java101));

        assertThrows(CupoLlenoException.class, () -> servicio.inscribir("A01", "JAVA-101"));

        verify(notificador).enviarRechazo(eq(ANA), eq(java101), motivoCapturado.capture());

        assertEquals("cupo lleno", motivoCapturado.getValue());
    }

    /**
     * getAllValues(): todas las llamadas, en orden.
     * Util para comprobar que se notifico a los tres alumnos correctos.
     */
    @Test
    @DisplayName("getAllValues: los tres alumnos notificados, en orden")
    void todosLosCapturados() {
        Curso amplio = new Curso("JAVA-101", 10);
        Alumno beto  = new Alumno("A02", "Beto Ruiz",  "beto@academymty.mx");
        Alumno carla = new Alumno("A03", "Carla Diaz", "carla@academymty.mx");

        when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repoAlumnos.buscar("A02")).thenReturn(Optional.of(beto));
        when(repoAlumnos.buscar("A03")).thenReturn(Optional.of(carla));
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(amplio));

        servicio.inscribir("A01", "JAVA-101");
        servicio.inscribir("A02", "JAVA-101");
        servicio.inscribir("A03", "JAVA-101");

        ArgumentCaptor<Alumno> todos = ArgumentCaptor.forClass(Alumno.class);
        verify(notificador, org.mockito.Mockito.times(3))
                .enviarConfirmacion(todos.capture(), any());

        List<Alumno> capturados = todos.getAllValues();
        assertEquals(3, capturados.size());
        assertEquals(List.of("A01", "A02", "A03"),
                capturados.stream().map(Alumno::matricula).toList());
    }

    /**
     * AVISO: el captor NO sustituye al matcher.
     *
     * Un captor puesto en un verify captura lo que HAYA llegado; no filtra
     * nada. Si hubo tres llamadas y solo esperabas una, getValue() te
     * devuelve la ULTIMA sin quejarse. Por eso conviene acompanarlo del
     * times(n) correspondiente, como en el test de arriba.
     */
    @Test
    @DisplayName("getValue() devuelve la ULTIMA llamada, sin avisar")
    void getValueEsLaUltima() {
        Curso amplio = new Curso("JAVA-101", 10);
        Alumno beto = new Alumno("A02", "Beto Ruiz", "beto@academymty.mx");

        when(repoAlumnos.buscar("A01")).thenReturn(Optional.of(ANA));
        when(repoAlumnos.buscar("A02")).thenReturn(Optional.of(beto));
        when(repoCursos.buscar("JAVA-101")).thenReturn(Optional.of(amplio));

        servicio.inscribir("A01", "JAVA-101");
        servicio.inscribir("A02", "JAVA-101");

        verify(notificador, org.mockito.Mockito.times(2))
                .enviarConfirmacion(alumnoCapturado.capture(), any());

        assertEquals("A02", alumnoCapturado.getValue().matricula(),
                "getValue() es la ultima, no la primera");
        assertEquals("A01", alumnoCapturado.getAllValues().get(0).matricula());
    }
}
