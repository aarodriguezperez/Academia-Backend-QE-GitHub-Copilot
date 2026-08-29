package com.academymty.academia;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Una implementacion real y rapida, con un detalle a proposito: cuenta
 * cuantas veces la han consultado.
 *
 * Ese contador es un EFECTO SECUNDARIO observable, y existe para demostrar,
 * en la seccion 07, el peligro de espiar con when(): el metodo real llega a
 * ejecutarse durante la propia programacion del doble.
 */
public class RepositorioCursosEnMemoria implements RepositorioCursos {

    private final Map<String, Curso> tabla = new LinkedHashMap<>();
    private int consultas = 0;

    public RepositorioCursosEnMemoria() {
        Curso java101 = new Curso("JAVA-101", 2);
        tabla.put(java101.clave(), java101);
    }

    @Override
    public Optional<Curso> buscar(String clave) {
        consultas++;                       // <- el efecto que delata a when()
        return Optional.ofNullable(tabla.get(clave));
    }

    public int consultas() {
        return consultas;
    }
}
