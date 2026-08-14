package Proyecto;

public class TallerAutomotriz {
    public static void main(String[] args) {

//        Se definen los objetos haciendo un cast, tipo upcasting convirtiendo
//        un objeto de la clase hijo a la clase padre
        Vehiculo v1 = new Automovil(1,"aaa", "Toyota",
                "Corolla", 2002,40000, "Manual");
        Vehiculo v2 = new Camioneta(3, "ccc", "Ford",
                "Lobo",2020, 2000, 4500);
        Vehiculo v3 = new Motocicleta(2, "bbb", "Suzuki",
                "Ninja", 2018, 5000, 65);


//        Caso de Polimorfismo
//        El metodo costoInsepeccion funciona de manera diferente dependiendo
//        de la clase y objeto que lo llame
        System.out.println("\nCaso de Poliformismo");
        System.out.println("El costo de Inspeccion es de: " + v1.costoInspeccion());
        System.out.println("El costo de Inspeccion es de: " + v2.costoInspeccion());
        System.out.println("El costo de Inspeccion es de: " + v3.costoInspeccion());

//        Caso de casteo
//        En este caso se aplica un downcasting, convirtiendo un objeto de la clase hijo a la clase padre
        System.out.println("\nCaso de Casteo");
        if (v2 instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) v2;
            System.out.println(camioneta.getCapacidadCarga());
        }


//        Caso de exception
//        El exception se define en la clase vehiculo() y se utiliza un try-catch
//        para que el programa no pare y el codigo siga ejeceutandose
        try {
            v2.actualizarKilometraje(1000);
        } catch (IllegalArgumentException e) {
            System.out.println("\nCaso de Exception");
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println(v2);


    }


}
