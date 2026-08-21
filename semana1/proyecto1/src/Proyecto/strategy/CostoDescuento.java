package Proyecto.strategy;

public class CostoDescuento implements EstrategiaCosto {
    @Override
    public double calcularCosto(double subtotal){
        return subtotal*0.80;
    }
}
