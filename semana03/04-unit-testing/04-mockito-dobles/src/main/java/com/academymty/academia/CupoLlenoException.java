package com.academymty.academia;

public class CupoLlenoException extends RuntimeException {

    private final String claveCurso;

    public CupoLlenoException(String claveCurso, int cupo) {
        super("El curso " + claveCurso + " ya tiene sus " + cupo + " lugares ocupados");
        this.claveCurso = claveCurso;
    }

    public String claveCurso() {
        return claveCurso;
    }
}
