package com.academymty.academia;

public class CursoNoEncontradoException extends RuntimeException {

    public CursoNoEncontradoException(String clave) {
        super("No existe el curso " + clave);
    }
}
