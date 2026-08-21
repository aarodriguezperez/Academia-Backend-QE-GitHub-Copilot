package Proyecto.modelo.servicio;

public abstract class Servicio {

    //    Uso de final, para que el idServicio no se pueda modificar
    private final int idServicio;
    private String nombre;
    private String descripcion;
    private double precioBase;

    public Servicio(int idServicio, String nombre, String descripcion, double precioBase) {
        this.idServicio = idServicio;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioBase = precioBase;
    }

    //    Para IdServicio solo getter porque el id no debe modificarse
    public int getIdServicio() {
        return idServicio;
    }

//    Tanto nombre como descripcion deben poder sufrir cambios por correcciones
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

//    Para precioBase se hace uso del get, pero el set se sustituye por
//    un metodo que permita unicamente actualizar el valor por un número
//    positivo, permitiendonos implementar un Exception
    public double getPrecioBase() {
        return precioBase;
    }

    public void actualizarPrecioBase(double nuevoPrecioBase){
        if(nuevoPrecioBase > 0){
            precioBase = nuevoPrecioBase;
        } else {
            throw new IllegalArgumentException(
                    "El nuevo precio base debe ser mayor que 0"
            );
        }
    }

    public String toString() {
        return getNombre();
    }
}
