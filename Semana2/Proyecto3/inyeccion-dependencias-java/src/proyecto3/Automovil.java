package proyecto3;

public class Automovil implements Vehiculo {
    private String marca;
    private String modelo;
    private String color;
    private int anio;

    public Automovil(String marca, String modelo, String color, int anio) {
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.anio = anio;
    }

    @Override
    public void asignar() {
        System.out.println("Vehiculo: " + marca + " " + modelo +
                ", " + color + ", " + anio);
    }
}