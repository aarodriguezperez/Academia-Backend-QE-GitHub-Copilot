package com.academymty.academia;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * SECCION 04 de la guia 04 -- La otra mitad: verify(...).
 *
 * when() sirve cuando te importa lo que el colaborador DEVUELVE.
 * verify() sirve cuando te importa que se le LLAMO.
 *
 * Y hay una familia entera de casos donde solo existe la segunda: los
 * metodos void. Un notificador de correo no devuelve nada. La unica
 * pregunta que se le puede hacer es "se mando el correo, si o no".
 *
 * De hecho el verify mas valioso de todos es el que comprueba que algo
 * NO paso -- never(). Cubre los bugs que nadie ve venir: el correo que se
 * manda dos veces, el cargo que se cobra aunque la compra fallara.
 */
@ExtendWith(MockitoExtension.class)
class VerificarTest {

    private static final Alumno ANA = new Alumno("A01", "Ana Torres", "ana@academymty.mx");

    @Mock
    private Notificador notificador;

    private Curso java101;

    @BeforeEach
    void curso() {
        java101 = new Curso("JAVA-101", 30);
    }

    @Test
    @DisplayName("verify: se llamo, exactamente una vez")
    void seLlamo() {
        notificador.enviarConfirmacion(ANA, java101);

        verify(notificador).enviarConfirmacion(ANA, java101);
        // 'verify(x)' sin mas equivale a 'verify(x, times(1))'
    }

    @Test
    @DisplayName("times(n): cuantas veces exactamente")
    void cuantasVeces() {
        notificador.enviarConfirmacion(ANA, java101);
        notificador.enviarConfirmacion(ANA, java101);

        verify(notificador, times(2)).enviarConfirmacion(ANA, java101);
        verify(notificador, atLeast(1)).enviarConfirmacion(ANA, java101);
        verify(notificador, atMost(5)).enviarConfirmacion(ANA, java101);
    }

    /**
     * EL MAS VALIOSO DE TODOS.
     *
     * "Cuando la inscripcion es rechazada, NO se manda la confirmacion."
     *
     * Ese test no existe en la mayoria de las suites, y es justo el que
     * evita el correo que dice "felicidades, ya estas inscrito" a alguien
     * que se quedo fuera. Piensa en never() cada vez que escribas un if.
     */
    @Test
    @DisplayName("never(): la confirmacion NO se manda")
    void loQueNoDebePasar() {
        notificador.enviarRechazo(ANA, java101, "cupo lleno");

        verify(notificador).enviarRechazo(ANA, java101, "cupo lleno");
        verify(notificador, never()).enviarConfirmacion(any(), any());
    }

    /**
     * verifyNoInteractions: a este doble no lo tocaron para NADA.
     *
     * Mas fuerte que never() sobre un metodo: cubre todos los metodos,
     * incluidos los que anadan manana. Util para "si el alumno no existe,
     * ni siquiera se consulta el repositorio de cursos".
     */
    @Test
    @DisplayName("verifyNoInteractions: ni se le rozo")
    void niSeLeToco() {
        verifyNoInteractions(notificador);
    }

    /**
     * verifyNoMoreInteractions: y ADEMAS no paso nada mas.
     *
     * Usalo con moderacion. Es un test fragil por naturaleza: cualquier
     * llamada nueva y legitima que anadas al codigo lo rompe, aunque el
     * comportamiento siga siendo correcto. Reservalo para los colaboradores
     * donde "una llamada de mas" es un bug de verdad: cobros, correos,
     * borrados.
     */
    @Test
    @DisplayName("verifyNoMoreInteractions: nada mas ocurrio")
    void nadaMas() {
        notificador.enviarConfirmacion(ANA, java101);

        verify(notificador).enviarConfirmacion(ANA, java101);
        verifyNoMoreInteractions(notificador);
    }

    /**
     * InOrder: cuando el ORDEN es la regla.
     *
     * Aqui importa de verdad: primero se avisa del rechazo y despues se
     * lanza la excepcion. Al reves, el alumno nunca se entera.
     */
    @Test
    @DisplayName("InOrder: primero el rechazo, despues la confirmacion")
    void elOrdenImporta() {
        notificador.enviarRechazo(ANA, java101, "cupo lleno");
        notificador.enviarConfirmacion(ANA, java101);

        InOrder orden = inOrder(notificador);
        orden.verify(notificador).enviarRechazo(ANA, java101, "cupo lleno");
        orden.verify(notificador).enviarConfirmacion(ANA, java101);
    }

    /**
     * Los matchers tambien valen en verify. Y la misma regla de siempre:
     * si uno es matcher, todos son matchers. De ahi el eq(java101).
     */
    @Test
    @DisplayName("Matchers en verify, con la misma regla de todo-o-nada")
    void matchersEnVerify() {
        notificador.enviarRechazo(ANA, java101, "cupo lleno");

        verify(notificador).enviarRechazo(any(Alumno.class), eq(java101), anyString());
    }
}
