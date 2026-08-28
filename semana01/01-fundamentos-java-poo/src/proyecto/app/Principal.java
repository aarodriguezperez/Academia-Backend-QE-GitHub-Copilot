package proyecto.app;

import proyecto.comparator.ComparatorPorCosto; import proyecto.modelo.orden.Orden; import proyecto.modelo.persona.Cliente; import proyecto.modelo.persona.Mecanico;
import proyecto.modelo.refaccion.Refaccion; import proyecto.modelo.servicio.Diagnostico; import proyecto.modelo.servicio.Mantenimiento; import proyecto.modelo.servicio.Reparacion;
import proyecto.modelo.servicio.Servicio; import proyecto.modelo.vehiculo.Automovil; import proyecto.modelo.vehiculo.Camioneta; import proyecto.modelo.vehiculo.Motocicleta;
import proyecto.modelo.vehiculo.Vehiculo; import proyecto.singleton.Taller; import proyecto.strategy.CostoDescuento; import proyecto.strategy.CostoNormal;
import java.util.*;

public class Principal {
    public static void main(String[] args) {

//        Creacion de vehiculos - Caso de Upcasting
//        Se crean objetos de clases hijas utilizando una referencia
//        de la clase padre Vehiculo.
        Vehiculo v1 = new Automovil(1, "aaa", "Toyota",
                "Corolla", 2002, 40000, "Manual");
        Vehiculo v2 = new Camioneta(2, "ccc", "Ford",
                "Lobo", 2020, 2000, 4500);
        Vehiculo v3 = new Motocicleta(3, "bbb", "Suzuki",
                "Ninja", 2018, 5000, 65);

//        Creación de clientes
        Cliente c1 = new Cliente(1, "Juan", "Perez",
                "8121245367", "juanperez@email.com");
        Cliente c2 = new Cliente(2, "Diego", "Alonso",
                "8124538279", "diegoalonso@email.com");
        Cliente c3 = new Cliente(3, "Bruno", "Hernandez",
                "812367495", "brunohernandez@email.com");

//        Creacion de mecanicos
        Mecanico m1 = new Mecanico(4, "Miguel", "Torres", "8187654321",
                "miguel.torres@email.com", "Carroceria");
        Mecanico m2 = new Mecanico(5, "Roberto", "Garcia", "8123456789",
                "roberto.garcia@email.com", "Diagnostico electrónico");
        Mecanico m3 = new Mecanico(6, "Luis", "Martinez", "8198765432",
                "luis.martinez@email.com", "Mantenimiento general");

//        Creacion de servicios
        Servicio s1 = new Reparacion(1, "Abolladura",
                "Arreglar abolladura en facia", 5000, "Media");
        Servicio s2 = new Diagnostico(2, "Escaneo electrónico",
                "Revision completa de la computadora del vehiculo", 3000, "Electronico");
        Servicio s3 = new Mantenimiento(3, "Cambio de Aceite",
                "Se reemplaza el aceite quemada por uno nuevo", 1500, 1000);

//        Creacion de refacciones
        Refaccion r1 = new Refaccion(1, "Facia delantera",
                "Facia completa delantera de protección ", 1200, 6);
        Refaccion r2 = new Refaccion(2, "Batería",
                "Batería automotriz de 12V", 2800, 4);
        Refaccion r3 = new Refaccion(3, "Filtro de aceite",
                "Filtro para sistema de lubricación del motor", 2500, 10);

//        Creacion de Orden
        Orden o1 = new Orden(1, c1, m1, v1);
        o1.agregarServicio(s1);
        o1.agregarRefaccion(r1);

        Orden o2 = new Orden(2, c2, m2, v2);
        o2.agregarServicio(s2);
        o2.agregarServicio(s3);
        o2.agregarRefaccion(r2);

        Orden o3 = new Orden(3, c3, m3, v3);
        o3.agregarServicio(s3);
        o3.agregarRefaccion(r2);
        o3.agregarRefaccion(r3);

        System.out.println("\nProyecto 01 Semana 01 - Taller Automotriz");

//        Caso de Strategy
        System.out.println("\nCaso de Strategy");
        System.out.println(o1);
        o1.asignarEstrategiaCosto(new CostoNormal());
        System.out.println("Total sin descuento orden 1: $" + o1.calcularTotal());
        o1.asignarEstrategiaCosto(new CostoDescuento());
        System.out.println("Total con 20% de descuento orden 1: $" + o1.calcularTotal());
        o2.asignarEstrategiaCosto(new CostoNormal());
        o3.asignarEstrategiaCosto(new CostoNormal());

        List<Orden> listaOrden = new ArrayList<>();
        listaOrden.add(o3);
        listaOrden.add(o2);
        listaOrden.add(o1);

//        Caso de Lambdas
//        Recorre la lista en el orden que quedo anteriormente
        System.out.println("\nCaso de Lambdas");
        listaOrden.forEach(orden ->
                System.out.println("Orden: " + orden.getIdOrden()));

//        Caso de Comparable
//        Se ordenan los objetos de la lista a traves de la interfaz Comparable
//        y el metodo compareTo()
        System.out.println("\nCaso de Comparable");
        System.out.println("Antes de ordenar:");
        for (Orden orden : listaOrden) {
            System.out.println("Orden: " + orden.getIdOrden());
        }
        Collections.sort(listaOrden);
        System.out.println("\nDespués de ordenar:");
        for (Orden orden : listaOrden) {
            System.out.println("Orden: " + orden.getIdOrden());
        }

//        Caso de Comparator
        o1.asignarEstrategiaCosto(new CostoNormal());
        System.out.println("\nCaso de Comparator");
        System.out.println("Antes de ordenar por costo:");
        for (Orden orden : listaOrden) {
            System.out.println("Orden: " + orden.getIdOrden()
                            + " - Total: $" + orden.calcularTotal());
        }
        Collections.sort(listaOrden, new ComparatorPorCosto());
        System.out.println("\nDespués de ordenar por costo:");
        for (Orden orden : listaOrden) {
            System.out.println("Orden: " + orden.getIdOrden()
                            + " - Total: $" + orden.calcularTotal());
        }

//        Caso de Singleton
//        Al ser el resutado true indica que ambas variables de
//        referencia apuntan al mismo punto
        System.out.println("\nCaso de Singleton");
        Taller taller1 = Taller.getInstancia();
        Taller taller2 = Taller.getInstancia();
        System.out.println("¿Taller 1 y Taller 2 son la misma instancia?: "
                        + (taller1 == taller2));

//        Caso de HAS-A
//        Cliente tiene un vehiculo o una lista de, lo que significa que una clase
//        tiene una referencia a otra clase como atributo
        c1.agregarVehiculo(v1);
        c2.agregarVehiculo(v2);
        c3.agregarVehiculo(v3);
        System.out.println("\nCaso de HAS-A");
        System.out.println("Vehiculos de "+ c1.getNombre() + ": " + c1.getVehiculos());
        System.out.println("Vehiculos de "+ c2.getNombre() + ": " + c2.getVehiculos());
        System.out.println("Vehiculos de "+ c3.getNombre() + ": " + c3.getVehiculos());
//
//
//        Caso de Polimorfismo
//        El metodo costoInspeccion funciona de manera diferente dependiendo
//        de la clase y objeto que lo llame
        System.out.println("\nCaso de Polimorfismo");
        System.out.println("El costo de Inspeccion de " + v1.getClass().getSimpleName()
                + " es: " + v1.costoInspeccion());
        System.out.println("El costo de Inspeccion de " + v2.getClass().getSimpleName()
                + " es: " + v2.costoInspeccion());
        System.out.println("El costo de Inspeccion de " + v3.getClass().getSimpleName()
                + " es: " + v3.costoInspeccion());
//
//        Caso de casteo
//        En este caso se aplica un downcasting, convirtiendo una referencia
//        de la clase padre al tipo específico de la clase hija y accediendo a un
//        atributo del hijo
        System.out.println("\nCaso de Casteo");
        if (v2 instanceof Camioneta) {
            Camioneta camioneta = (Camioneta) v2;
            System.out.println("Capacidad de carga: "
                    + camioneta.getCapacidadCarga() + " kg");
        }
//
//        Caso de exception
//        La exception se lanza en la clase vehiculo y se captura mediante
//        un try-catch para que el programa no pare y el codigo siga ejeceutandose
        System.out.println("\nCaso de Exception");
        System.out.println("Kilometraje a intentar cambiar: " + v2.getKilometraje());
        try {
            v2.actualizarKilometraje(1000);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
        System.out.println("No se cambia el valor sigue en: " + v2.getKilometraje());
    }
}
