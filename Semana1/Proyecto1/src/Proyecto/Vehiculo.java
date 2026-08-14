package Proyecto;

// Uso de clase abstracta, 3 clases hijas = diferentes tipos de vehiculos
public abstract class Vehiculo {

    // Uso de final, para que el idVehiculo no se pueda modificar
    private final int idVehiculo;

    private String placa;
    private String marca;
    private String modelo;
    private int anio;
    private double kilometraje;


    // Uso de Constructores, dar valor inicial a los atributos
    public Vehiculo(int idVehiculo, String placa, String marca,
                    String modelo, int anio, double kilometraje) {
        this.idVehiculo = idVehiculo;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.kilometraje = kilometraje;
    }

    // Encapsulamiento getters y setters

    // Solo getter porque el id no debe modificarse
    public int getIdVehiculo() {
        return idVehiculo;
    }

    // Una placa si podria cambiar por eso se le agregan ambos
    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    // Marca, modelo, año: son valores que no cambian en un vehiculo
    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getAnio() {
        return anio;
    }

   /* Para el kilometraje se hace uso del get, pero no del set ya que se utiliza
    un metodo para solo permitir actualizar el valor por un número mayor */

    public double getKilometraje() {
        return kilometraje;
    }

    public void actualizarKilometraje(double nuevoKilometraje){
        if(nuevoKilometraje > kilometraje){
            kilometraje = nuevoKilometraje;
        } else {
            throw new IllegalArgumentException(
                    "El nuevo kilometraje debe ser mayor al registrado"
            );
        }
    }

    //Metodo para implementar polimorfismo
    public abstract double costoInspeccion();


    // Metodo para ver los datos en consola, aunque tambien es polimorfismo
    public String toString() {
        return "Vehículo {" +
                "id=" + idVehiculo +
                ", placa='" + placa + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", anio=" + anio +
                ", kilometraje=" + kilometraje;
    }


}
