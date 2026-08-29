package com.academymty.academia;

/**
 * Excepcion propia del dominio.
 *
 * Existe por un motivo que se ve en los tests: una excepcion con datos
 * dentro (aqui, el cupo) se puede INTERROGAR desde el test. Con un
 * IllegalStateException generico solo te queda leer el mensaje en texto,
 * que es fragil -- cambia una palabra y el test se cae sin que nada
 * este roto de verdad.
 */
public class CupoLlenoException extends RuntimeException {

    private final String claveCurso;
    private final int cupo;

    public CupoLlenoException(String claveCurso, int cupo) {
        super("El curso " + claveCurso + " ya tiene sus " + cupo + " lugares ocupados");
        this.claveCurso = claveCurso;
        this.cupo = cupo;
    }

    public String claveCurso() {
        return claveCurso;
    }

    public int cupo() {
        return cupo;
    }
}
