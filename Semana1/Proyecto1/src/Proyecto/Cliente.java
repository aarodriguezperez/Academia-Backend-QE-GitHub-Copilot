package Proyecto;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Persona{

    public Cliente(int idPersona, String nombre, String apellido, String telefono, String correo) {
        super(idPersona, nombre, apellido, telefono, correo);
        vehiculos = new ArrayList<>();
    }

    //    Lista para asignar vehiculos a las Personas
    private List<Vehiculo> vehiculos;

    //    Metodo para agregar vehículos
    public void agregarVehiculo(Vehiculo vehiculo) {
        vehiculos.add(vehiculo);
    }

    //    Metodo para consultar vehiculos
    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }
}
