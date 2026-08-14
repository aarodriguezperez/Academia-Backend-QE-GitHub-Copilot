package Proyecto;

public abstract class Vehiculo {

    private final int idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private double kilometraje;

    public Vehiculo(int idVehiculo, String placa, String marca,
                    String modelo, int anio, double kilometraje) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.kilometraje = kilometraje;
    }
}
