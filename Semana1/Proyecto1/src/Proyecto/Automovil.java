package Proyecto;

public class Automovil extends Vehiculo{

    private String tipoTransmision;

    // Uso de Constructores
    public Automovil(int idVehiculo, String placa, String marca, String modelo,
                     int anio, double kilometraje, String tipoTransmision) {
        super(idVehiculo, placa, marca, modelo, anio, kilometraje);
        this.tipoTransmision = tipoTransmision;
    }

    public String getTipoTransmision() {
        return tipoTransmision;
    }

    //Polimorfismo
    @Override
    public double costoInspeccion() {
        if(getKilometraje()>30000){
            return 2000;
        } else {
            return 1000;
        }
    }

    @Override
    public String toString() {
        return super.toString() +
                ", tipoTransmision=" + tipoTransmision + '}';
    }
}
