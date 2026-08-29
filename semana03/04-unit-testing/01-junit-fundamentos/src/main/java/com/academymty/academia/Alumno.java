package com.academymty.academia;

/**
 * Un alumno de la academia. Es un record: inmutable y sin logica.
 *
 * Fijate en que no tiene NINGUN test propio. No hace falta:
 * un record no tiene comportamiento que se pueda romper.
 * Se prueba el codigo que DECIDE, no el que solo guarda.
 */
public record Alumno(String matricula, String nombre) {

    public Alumno {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("La matricula no puede venir vacia");
        }
    }
}
