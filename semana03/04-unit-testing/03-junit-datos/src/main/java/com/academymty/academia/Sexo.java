package com.academymty.academia;

/** La letra 11 de la CURP. Un enum, porque los valores posibles son dos y no cambian. */
public enum Sexo {
    HOMBRE('H'),
    MUJER('M');

    private final char letra;

    Sexo(char letra) {
        this.letra = letra;
    }

    public char letra() {
        return letra;
    }

    public static Sexo desdeLetra(char letra) {
        for (Sexo s : values()) {
            if (s.letra == letra) {
                return s;
            }
        }
        throw new IllegalArgumentException("Letra de sexo no valida: " + letra);
    }
}
