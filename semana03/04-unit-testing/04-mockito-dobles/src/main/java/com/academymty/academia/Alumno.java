package com.academymty.academia;

/** Un alumno de la academia. Sin comportamiento: no se prueba, no se mockea. */
public record Alumno(String matricula, String nombre, String correo) {

    public Alumno {
        if (matricula == null || matricula.isBlank()) {
            throw new IllegalArgumentException("La matricula no puede venir vacia");
        }
    }
}
