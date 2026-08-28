package proyecto03;

public class Orden {
    private int idOrden;
    private String nombreCliente;
    private String apellidoCliente;
    //    Si usamos directamente Camioneta o Automovil,
//    Orden quedaría ligada a una implementación específica.
//
//    Camioneta myVehiculo;
//    Automovil myVehiculo;
//
//    Al usar la interfaz Vehiculo podemos recibir cualquiera
//    de las dos implementaciones.
    private Vehiculo myVehiculo;

    //    Orden no crea el Vehiculo.
//    Lo recibe desde afuera por medio del constructor.
    public Orden(int idOrden, String nombreCliente, String apellidoCliente, Vehiculo myVehiculo) {
        this.idOrden = idOrden;
        this.nombreCliente = nombreCliente;
        this.apellidoCliente = apellidoCliente;
        this.myVehiculo = myVehiculo;
    }

//    Este constructor era necesario para utilizar los tipos de inyeccion por Setter y Atributo
//    public Orden(int idOrden, String nombreCliente, String apellidoCliente) {
//        this.idOrden = idOrden;
//        this.nombreCliente = nombreCliente;
//        this.apellidoCliente = apellidoCliente;
//    }
//
//    Los metodos getter y setter eran necesarios para el tipo de inyeccion por Setter
//    public Vehiculo getMyVehiculo() {return myVehiculo;}
//
//    public void setMyVehiculo(Vehiculo myVehiculo) {this.myVehiculo = myVehiculo;}

    void asignarVehiculo() {
        System.out.println("\nOrden " + idOrden + "\nNombre del cliente: "
                + nombreCliente + " " + apellidoCliente);
        myVehiculo.asignar();
    }

//    VERSIÓN SIN INYECCIÓN DE DEPENDENCIAS - ALTO ACOPLAMIENTO
//    Orden crea directamente el Vehiculo que necesita.
//    Para cambiar de Camioneta a Automovil habría que modificar
//    esta misma clase.
//
//    void asignarVehiculoSinInyeccion() {
//        System.out.println("\nOrden " + idOrden + "\nNombre del cliente: "
//                + nombreCliente + " " + apellidoCliente);
//
//        myVehiculo = new Camioneta("Ford", "Lobo", "negro", 2021);
//        myVehiculo.asignar();
//    }
}