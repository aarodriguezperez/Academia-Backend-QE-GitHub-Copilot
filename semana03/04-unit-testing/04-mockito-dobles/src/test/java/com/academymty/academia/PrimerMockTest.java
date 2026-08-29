package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * SECCION 02 de la guia 04 -- Que es un mock, exactamente.
 *
 * Un mock NO es una version falsa que alguien programo. Es un objeto que
 * Mockito FABRICA en tiempo de ejecucion implementando tu interfaz, con
 * todos los metodos vacios y una libreta donde apunta cada llamada que
 * recibe.
 *
 * De ahi salen sus dos poderes, y conviene tenerlos separados en la cabeza
 * desde el primer dia:
 *
 *   1. Puedes decirle QUE RESPONDER            -> when(...)   (seccion 03)
 *   2. Puedes preguntarle QUE LE LLAMARON      -> verify(...) (seccion 04)
 *
 * Casi todos los errores de principiante vienen de confundir esos dos.
 */
@ExtendWith(MockitoExtension.class)
class PrimerMockTest {

    /**
     * @Mock le pide a la extension que cree el doble y lo meta en este campo.
     *
     * Sin el @ExtendWith(MockitoExtension.class) de arriba, este campo se
     * queda en null y el primer test truena con NullPointerException. Es,
     * con diferencia, el fallo numero uno al empezar con Mockito.
     */
    @Mock
    private RepositorioAlumnos repo;

    @Test
    @DisplayName("Un mock existe y es del tipo que pediste")
    void esUnObjetoDeVerdad() {
        assertNotNull(repo, "Si esto es null, falta el @ExtendWith(MockitoExtension.class)");
        assertTrue(repo instanceof RepositorioAlumnos);
    }

    /**
     * LO QUE DEVUELVE UN MOCK SIN PROGRAMAR.
     *
     * No truena, no avisa: devuelve el "valor vacio" del tipo de retorno.
     * Y esa amabilidad es una trampa, porque un test puede pasar por
     * casualidad sobre un mock que nunca programaste.
     */
    @Test
    @DisplayName("Sin programar nada, un mock devuelve el vacio de cada tipo")
    void losValoresPorDefecto() {
        // Optional -> Optional.empty(), NO null. Mockito lo sabe.
        assertEquals(Optional.empty(), repo.buscar("A01"));

        // Y los metodos void, como los del notificador, simplemente no hacen nada.
        Notificador notificador = mock(Notificador.class);
        notificador.enviarConfirmacion(null, null);   // no truena
    }

    /**
     * mock() a mano, sin anotaciones. Hace exactamente lo mismo.
     *
     * Cuando usar cada forma:
     *   @Mock          los colaboradores fijos de la clase de test
     *   mock(X.class)  un doble suelto, dentro de un solo test
     *
     * Con @Mock, ademas, el mock lleva NOMBRE: si un verify falla, el
     * mensaje dice "repo.buscar(...)" en vez de "repositorioAlumnos$MockitoMock$123".
     * En una clase con cinco dobles, eso se agradece.
     */
    @Test
    @DisplayName("mock() a mano hace lo mismo que @Mock")
    void mockManual() {
        RepositorioCursos cursos = mock(RepositorioCursos.class);

        assertNotNull(cursos);
        assertEquals(Optional.empty(), cursos.buscar("JAVA-101"));
    }
}
