package Proyecto.strategy;

public class CostoNormal implements EstrategiaCosto {
    @Override
    public double calcularCosto(double subtotal){
        return subtotal;
    }
}
