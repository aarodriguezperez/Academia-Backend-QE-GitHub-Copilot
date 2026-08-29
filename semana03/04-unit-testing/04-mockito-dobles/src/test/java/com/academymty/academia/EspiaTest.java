package com.academymty.academia;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * SECCION 07 de la guia 04 -- El espia, y la trampa del orden.
 *
 *   mock  = un objeto VACIO. Todo devuelve el valor por defecto.
 *   spy   = un objeto REAL envuelto. Todo funciona de verdad, salvo lo
 *           que decidas sustituir.
 *
 * Un spy sirve para "quiero el comportamiento real, menos este metodo".
 * Suena util y hay que usarlo poco: si necesitas espiar tu propia clase,
 * casi siempre es la senal de que esa clase hace dos cosas y deberia ser
 * dos clases. El spy es un parche; la mayoria de las veces el arreglo
 * de verdad es partir el codigo.
 *
 * Donde SI vale la pena: codigo heredado que todavia no puedes tocar.
 */
@ExtendWith(MockitoExtension.class)
class EspiaTest {

    @Test
    @DisplayName("Un spy ejecuta el codigo real")
    void elEspiaEsReal() {
        RepositorioCursosEnMemoria espia = spy(new RepositorioCursosEnMemoria());

        Optional<Curso> curso = espia.buscar("JAVA-101");

        assertTrue(curso.isPresent(), "El metodo real corrio de verdad");
        assertEquals("JAVA-101", curso.orElseThrow().clave());
        assertEquals(1, espia.consultas(), "y el contador real subio");

        // Y aun asi se le puede preguntar como a un mock:
        verify(espia).buscar("JAVA-101");
    }

    /**
     * LA TRAMPA. Es la razon por la que esta seccion existe.
     *
     * when(espia.buscar("X")).thenReturn(...) se lee como si programara el
     * doble. Pero para llegar a when(), Java tiene que EVALUAR primero el
     * argumento -- y eso significa LLAMAR AL METODO REAL.
     *
     * En un repositorio que solo cuenta consultas, el dano es un numero mal.
     * En uno que borra registros, manda correos o cobra una tarjeta, el dano
     * es que tu test hizo eso de verdad mientras "solo lo estaba programando".
     */
    @Test
    @DisplayName("TRAMPA: when() sobre un spy EJECUTA el metodo real")
    void whenEjecutaElMetodoReal() {
        RepositorioCursosEnMemoria espia = spy(new RepositorioCursosEnMemoria());

        assertEquals(0, espia.consultas(), "todavia nadie ha consultado");

        when(espia.buscar("JAVA-101")).thenReturn(Optional.empty());
        //   ^^^^^^^^^^^^^^^^^^^^^^^ esto corrio de verdad

        assertEquals(1, espia.consultas(),
                "El contador subio a 1 SOLO por programar el doble. Nadie llamo al metodo.");

        // Y ahora si usamos el stub. Dos motivos, y los dos son leccion:
        //
        //   1. Si no lo usaramos, la estrictez de la seccion 08 tumbaria este
        //      test con UnnecessaryStubbingException. (Paso de verdad mientras
        //      se escribia esta clase.)
        //   2. Fijate en que ESTA llamada ya NO sube el contador: el metodo
        //      quedo sustituido. La unica ejecucion real fue la del when().
        assertTrue(espia.buscar("JAVA-101").isEmpty());
        assertEquals(1, espia.consultas(),
                "Sigue en 1: la llamada real fue la de dentro del when(), no esta.");
    }

    /**
     * LA FORMA CORRECTA: doReturn(...).when(espia).metodo(...)
     *
     * Fijate en que el metodo se nombra DESPUES del .when(espia), y para
     * entonces Mockito ya intercepto la llamada. Nunca se ejecuta el real.
     *
     * Regla practica:
     *     sobre un MOCK  -> when(...).thenReturn(...)   se lee mejor
     *     sobre un SPY   -> doReturn(...).when(...)     SIEMPRE
     *
     * Y lo mismo con doThrow(...) y doNothing(...), que ademas son la unica
     * opcion para metodos void -- ahi no hay nada que meter dentro de when().
     */
    @Test
    @DisplayName("doReturn().when(): la forma segura, no ejecuta nada")
    void doReturnNoEjecutaNada() {
        RepositorioCursosEnMemoria espia = spy(new RepositorioCursosEnMemoria());

        doReturn(Optional.empty()).when(espia).buscar("JAVA-101");

        assertEquals(0, espia.consultas(),
                "El contador sigue en 0: el metodo real nunca corrio");

        assertTrue(espia.buscar("JAVA-101").isEmpty(), "y ahora responde lo programado");
        assertEquals(0, espia.consultas(), "ni siquiera al usarlo, porque esta sustituido");
    }

    /**
     * Lo mejor de un spy: sustituyes UN metodo y el resto sigue siendo real.
     */
    @Test
    @DisplayName("Solo se sustituye lo que pides; el resto sigue real")
    void sustitucionParcial() {
        RepositorioCursosEnMemoria espia = spy(new RepositorioCursosEnMemoria());

        doReturn(Optional.empty()).when(espia).buscar("JAVA-101");

        assertTrue(espia.buscar("JAVA-101").isEmpty(), "sustituido");
        assertTrue(espia.buscar("NO-EXISTE").isEmpty(), "real, y tampoco existe");
        assertEquals(1, espia.consultas(), "solo la segunda llamada fue real");
    }
}
