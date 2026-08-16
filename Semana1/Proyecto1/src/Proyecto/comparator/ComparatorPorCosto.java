package Proyecto.comparator;

import Proyecto.modelo.orden.Orden;

import java.util.*;

public class ComparatorPorCosto implements Comparator<Orden>{

    @Override
    public int compare(Orden o1, Orden o2) {
        return (int) (o1.calcularTotal() - o2.calcularTotal());
    }
}
