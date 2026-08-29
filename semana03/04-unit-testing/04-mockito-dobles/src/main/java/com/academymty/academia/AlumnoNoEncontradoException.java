package com.academymty.academia;

public class AlumnoNoEncontradoException extends RuntimeException {

    public AlumnoNoEncontradoException(String matricula) {
        super("No existe el alumno con matricula " + matricula);
    }
}
