package proyecto01.modelo.vehiculo;

public class Camioneta extends Vehiculo {

    private double capacidadCarga;

//    Uso de Constructores
    public Camioneta(int idVehiculo, String placa, String marca, String modelo,
                     int anio, double kilometraje, double capacidadCarga) {
        super(idVehiculo, placa, marca, modelo, anio, kilometraje);
        this.capacidadCarga = capacidadCarga;
    }

//    Polimorfismo
    @Override
    public double costoInspeccion() {
        if(getAnio()>2020){
            return 4000;
        } else {
            return 2500;
        }
    }

    public double getCapacidadCarga() {
        return capacidadCarga;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
