package Proyecto.modelo.servicio;

public class Reparacion extends Servicio {
    private String nivelComplejidad;

    public Reparacion(int idServicio, String nombre, String descripcion, double precioBase, String nivelComplejidad) {
        super(idServicio, nombre, descripcion, precioBase);
        this.nivelComplejidad = nivelComplejidad;
    }

    public String getNivelComplejidad() {
        return nivelComplejidad;
    }

    public void setNivelComplejidad(String nivelComplejidad) {
        this.nivelComplejidad = nivelComplejidad;
    }

}
