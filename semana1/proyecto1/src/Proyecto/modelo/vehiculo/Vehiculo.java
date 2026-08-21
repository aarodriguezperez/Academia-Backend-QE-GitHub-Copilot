package Proyecto.modelo.vehiculo;

// Uso de clase abstracta, con 3 clases hijas = diferentes tipos de vehiculos
public abstract class Vehiculo {

//    Uso de final, para que el idVehiculo no se pueda modificar
    private final int idVehiculo;
    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private double kilometraje;


//    Uso de Constructores, dan valor inicial a los atributos
    public Vehiculo(int idVehiculo, String placa, String marca,
                    String modelo, int anio, double kilometraje) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.kilometraje = kilometraje;
    }

//    Encapsulamiento getters y setters

//    Para IdVehiculo solo getter porque el id no debe modificarse
    public int getIdVehiculo() {
        return idVehiculo;
    }

//    Para placa si podria cambiar, por eso se agrega el setter y getter
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

//    Marca, modelo, año: son valores que no cambian en un vehiculo
    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnio() {
        return anio;
    }

//    Para el kilometraje se hace uso del get, pero el set se sustituye
//    por un metodo que permita unicamente actualizar el valor por
//    un número mayor, permitiendonos implementar un Exception
    public double getKilometraje() {
        return kilometraje;
    }

    public void actualizarKilometraje(double nuevoKilometraje){
        if(nuevoKilometraje > kilometraje){
            kilometraje = nuevoKilometraje;
        } else {
            throw new IllegalArgumentException(
                    "El nuevo kilometraje: " + nuevoKilometraje + " debe ser mayor al registrado"
            );
        }
    }

//    Metodo para implementar polimorfismo, este metodo se utiliza en
//    las clases hijas y en cada una tiene una implementación disntinta
    public abstract double costoInspeccion();


//    Metodo para ver los datos en consola, tambien es polimorfismo
    public String toString() {
        return getMarca() + " " + getModelo();
    }


}
