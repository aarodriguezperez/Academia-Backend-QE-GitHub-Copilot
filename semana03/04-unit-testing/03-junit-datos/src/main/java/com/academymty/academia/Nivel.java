package com.academymty.academia;

/**
 * El nivel de desempeno que corresponde a una calificacion.
 *
 * Fijate en los limites: 90, 80 y 70. Tres fronteras, y en cada una hay
 * un >= que se puede escribir mal. Este enum existe para que en el
 * proyecto 03 pruebes las tres de un golpe, con una tabla de datos, en
 * vez de escribir nueve tests casi identicos a mano.
 */
public enum Nivel {
    EXCELENTE,
    BUENO,
    SUFICIENTE,
    INSUFICIENTE;

    public boolean esAprobatorio() {
        return this != INSUFICIENTE;
    }
}
