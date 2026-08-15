package Proyecto;

import java.util.*;

public class TallerAutomotriz {
    public static void main(String[] args) {

//        Caso de Upcasting
//        Se definen los objetos haciendo un cast, tratando un objeto
//        de una clase hija como una referencia de la clase padre.
        Vehiculo v1 = new Automovil(1,"aaa", "Toyota",
                "Corolla", 2002,40000, "Manual");
        Vehiculo v2 = new Camioneta(3, "ccc", "Ford",
                "Lobo",2020, 2000, 4500);
        Vehiculo v3 = new Motocicleta(2, "bbb", "Suzuki",
                "Ninja", 2018, 5000, 65);
        Cliente c1 = new Cliente(1, "Juan", "Perez",
                "8121245367", "juanperez@email.com");
        Cliente c2 = new Cliente(2, "Diego", "Alonso",
                "8124538279", "diegoalonso@email.com");


//        Caso de HAS-A
//        Cliente tiene un vehiculo o una lista de, lo que significa que una clase
//        tiene una referencia a otra clase como atributo
        c1.agregarVehiculo(v1);
        c1.agregarVehiculo(v2);
        c2.agregarVehiculo(v3);
        System.out.println("Vehiculos de "+ c1.getNombre() + ": " + c1.getVehiculos());
        System.out.println("Vehiculos de "+ c2.getNombre() + ": " + c2.getVehiculos());


//        Caso de Polimorfismo
//        El metodo costoInspeccion funciona de manera diferente dependiendo
//        de la clase y objeto que lo llame
        System.out.println("\nCaso de Polimorfismo");
        System.out.println("El costo de Inspeccion es de: " + v1.costoInspeccion());
        System.out.println("El costo de Inspeccion es de: " + v2.costoInspeccion());
        System.out.println("El costo de Inspeccion es de: " + v3.costoInspeccion());

//        Caso de casteo
//        En este caso se aplica un downcasting, convirtiendo una referencia
//        de la clase padre al tipo específico de la clase hija.
        System.out.println("\nCaso de Casteo");
        if (v2 instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) v2;
            System.out.println(camioneta.getCapacidadCarga());
        }

//        Caso de exception
//        La exception se lanza en la clase vehiculo() y se captura mediante
//        un try-catch para que el programa no pare y el codigo siga ejeceutandose
        try {
            v2.actualizarKilometraje(1000);
        } catch (IllegalArgumentException e) {
            System.out.println("\nCaso de Exception");
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println(v2);


    }


}
