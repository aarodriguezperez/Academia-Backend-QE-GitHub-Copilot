package Proyecto;

public class Automovil extends Vehiculo{

    private String tipoTransmision;

    public Automovil(int idVehiculo, String placa, String marca, String modelo,
                     int anio, double kilometraje, String tipoTransmision) {
        super(idVehiculo, placa, marca, modelo, anio, kilometraje);
        this.tipoTransmision = tipoTransmision;
    }
}
