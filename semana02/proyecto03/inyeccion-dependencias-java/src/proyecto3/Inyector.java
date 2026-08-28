package proyecto3;

public class Inyector {

//    INYECCIÓN POR CONSTRUCTOR
//    Inyector crea la dependencia y se la pasa a Orden
//    al momento de crear el objeto.
//    static Orden getOrden(){
//       Vehiculo camioneta = new Camioneta("Ford", "Lobo", "negro", 2021);
//       Vehiculo automovil = new Automovil("Toyota", "Corolla", "blanco", 2026);
//       return new Orden(1,"Diego", "Torres", automovil);
//    }

//    INYECCIÓN POR SETTER
//    Primero se crea el objeto Orden y después se le asigna
//    el Vehiculo mediante un metodo setter.
//    Ejemplo:
//    static void inyectarVehiculo(Orden orden){
//       orden.setMyVehiculo(new Camioneta("Ford", "Lobo", "negro", 2021));
//       orden.setMyVehiculo(new Automovil("Toyota", "Corolla", "blanco", 2026));
//    }

//    INYECCIÓN POR ATRIBUTO
//    El Vehiculo se asigna directamente a la variable de la clase.
//    Para hacerlo, el atributo debe poder ser accedido desde afuera.
//    Ejemplo:
//    static void inyectarVehiculo(Orden orden){
//       orden.myVehiculo = new Camioneta("Ford", "Lobo", "negro", 2021);
//       orden.myVehiculo = new Automovil("Toyota", "Corolla", "blanco", 2026);
//    }
}