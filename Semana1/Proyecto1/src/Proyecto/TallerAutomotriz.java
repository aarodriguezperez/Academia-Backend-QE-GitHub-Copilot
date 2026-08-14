package Proyecto;

public class TallerAutomotriz {
    public static void main(String[] args) {
        Vehiculo v1 = new Automovil(1,"aaa", "Toyota",
                "Corolla", 2002,40000, "Manual");
        Vehiculo v2 = new Camioneta(3, "ccc", "Ford",
                "Lobo",2020, 2000, 4500);
        Vehiculo v3 = new Motocicleta(2, "bbb", "Susuki",
                "Ninja", 2018, 5000, 65);


        // Caso de Polimorfismo
        System.out.println("\nCaso de Poliformismo");
        System.out.println("El costo de Inspeccion es de: " + v1.costoInspeccion());
        System.out.println("El costo de Inspeccion es de: " + v2.costoInspeccion());
        System.out.println("El costo de Inspeccion es de: " + v3.costoInspeccion());

        // Caso de casteo
        System.out.println("\nCaso de Casteo");
        if (v2 instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) v2;
            System.out.println(camioneta.getCapacidadCarga());
        }


        // Caso de exception
        try {
            v2.actualizarKilometraje(1000);
        } catch (IllegalArgumentException e) {
            System.out.println("\nCaso de Exception");
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println(v2);


    }


}
