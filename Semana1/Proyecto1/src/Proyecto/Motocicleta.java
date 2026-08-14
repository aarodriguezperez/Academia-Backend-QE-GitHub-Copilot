package Proyecto;

public class Motocicleta extends Vehiculo{

    private int cilindrada;

    // Uso de Constructores
    public Motocicleta(int idVehiculo, String placa, String marca, String modelo,
                       int anio, double kilometraje, int cilindrada) {
        super(idVehiculo, placa, marca, modelo, anio, kilometraje);
        this.cilindrada = cilindrada;
    }
}
