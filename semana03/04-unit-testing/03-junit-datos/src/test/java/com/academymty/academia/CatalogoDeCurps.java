package com.academymty.academia;

import java.util.stream.Stream;

/**
 * Catalogo compartido de datos de prueba.
 *
 * No tiene ni un @Test: es solo el almacen. Surefire no la ejecuta porque
 * su nombre no termina en Test, que es como decide que clases correr.
 *
 * Es el patron que evita que la misma lista de CURP este copiada en
 * cinco clases de test -- y que la sexta se olvide de actualizarla.
 */
final class CatalogoDeCurps {

    private CatalogoDeCurps() {
    }

    static Stream<String> lasValidas() {
        return Stream.of(
                "RUGM800101HNLZRK09",
                "TOLA010615MNLRPNA5",
                "XEXX010101HNEXXXA4");
    }

    static Stream<String> lasInvalidas() {
        return Stream.of(
                "RUGM800101HNLZRK0",
                "RUGM801301HNLZRK09",
                "RUGM990229HNLZRK09");
    }
}
