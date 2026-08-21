package Proyecto.modelo.vehiculo;

public class Motocicleta extends Vehiculo {

    private int cilindrada;

//    Uso de Constructores
    public Motocicleta(int idVehiculo, String placa, String marca, String modelo,
                       int anio, double kilometraje, int cilindrada) {
        super(idVehiculo, placa, marca, modelo, anio, kilometraje);
        this.cilindrada = cilindrada;
    }

    public int getCilindrada() {
        return cilindrada;
    }

//    Polimorfismo
    @Override
    public double costoInspeccion() {
        if(getCilindrada()>60){
            return 3000;
        } else {
            return 2000;
        }
    }
    @Override
    public String toString() {
        return super.toString();
    }
}
