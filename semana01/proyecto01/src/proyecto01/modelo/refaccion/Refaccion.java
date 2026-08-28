package proyecto01.modelo.refaccion;

public class Refaccion {
    private final int idRefaccion;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;

    public Refaccion(int idRefaccion, String nombre, String descripcion, double precio, int stock) {
        this.idRefaccion = idRefaccion;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public int getIdRefaccion() {
        return idRefaccion;
    }

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

    public double getPrecio() {
        return precio;
    }

    public void actualizarPrecio(double nuevoPrecio){
        if(nuevoPrecio > 0){
            precio = nuevoPrecio;
        } else {
            throw new IllegalArgumentException(
                    "El nuevo precio debe ser mayor que 0"
            );
        }
    }

    public int getStock() {
        return stock;
    }

    public void actualizarStock(int nuevoStock){
        if(nuevoStock >= 0){
            stock = nuevoStock;
        } else {
            throw new IllegalArgumentException(
                    "El nuevo stock debe ser mayor que 0"
            );
        }
    }

    @Override
    public String toString() {
        return getNombre();
    }
}


