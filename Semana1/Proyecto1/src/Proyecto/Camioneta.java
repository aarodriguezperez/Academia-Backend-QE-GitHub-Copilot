package Proyecto;

public class Camioneta extends Vehiculo{

    private double capacidadCarga;

    public Camioneta(int idVehiculo, String placa, String marca, String modelo,
                     int anio, double kilometraje, int capacidadCarga) {
        super(idVehiculo, placa, marca, modelo, anio, kilometraje);
        this.capacidadCarga = capacidadCarga;
    }
}
