package proyecto3;

public class Principal {

    public static void main(String[] args) {

        System.out.println("\nProyecto Inyección de dependencias");

//    INYECCIÓN POR CONSTRUCTOR
//    Se crea la dependencia fuera de la clase Orden.
//    Podemos cambiar Automovil por Camioneta sin modificar Orden.
        Vehiculo vehiculo = new Automovil("Toyota", "Corolla",
                "blanco", 2026);

//    Para usar otra implementación solo cambiamos la línea anterior por:
//    Vehiculo vehiculo = new Camioneta(
//                      "Ford", "Lobo", "negro", 2021);
//
//    La dependencia Vehiculo se pasa a Orden por medio del constructor.
        Orden o1 = new Orden(1, "Diego", "Torres", vehiculo);
        o1.asignarVehiculo();

//    NOTA: Al depender de la interfaz Vehiculo también es más fácil hacer pruebas,
//    porque podemos pasar otra implementación sin modificar la clase Orden.

//    OTROS EJEMPLOS DE INYECCIÓN
//    Se encuentran comentados porque actualmente
//    usamos la inyección por constructor.

//    INYECCIÓN POR CONSTRUCTOR USANDO INYECTOR
//    Ejemplo:
//    Orden o1 = Inyector.getOrden();
//    o1.asignarVehiculo();

//    INYECCIÓN POR SETTER
//    Primero se crea Orden y después se le asigna el Vehiculo.
//    Ejemplo:
//    Orden o1 = new Orden(1, "Juan", "Perez");
//    Inyector.inyectarVehiculo(o1);
//    o1.asignarVehiculo();

//    INYECCIÓN POR ATRIBUTO
//    El Vehiculo se asigna directamente a la variable de Orden.
//    Ejemplo:
//    Orden o1 = new Orden(1, "Juan", "Perez");
//    Inyector.inyectarVehiculo(o1);
//    o1.asignarVehiculo();

//    SIN INYECCIÓN DE DEPENDENCIAS
//    En este caso, la clase Orden crea por sí misma el Vehiculo
//    que necesita usando "new".
//    Esto hace que Orden quede ligada a una implementación
//    específica, por ejemplo Automovil o Camioneta.
//    Para cambiar el tipo de Vehiculo sería necesario modificar
//    directamente la clase Orden.
//    Ejemplo:
//    Orden o1 = new Orden(1, "Diego", "Torres");
//    o1.asignarVehiculoSinInyeccion();
    }
}