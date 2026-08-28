package proyecto.modelo.servicio;

public class Mantenimiento extends Servicio {
    private double intervaloKilometraje;

    public Mantenimiento(int idServicio, String nombre, String descripcion, double precioBase, double intervaloKilometraje) {
        super(idServicio, nombre, descripcion, precioBase);
        this.intervaloKilometraje = intervaloKilometraje;
    }

    public double getIntervaloKilometraje() {
        return intervaloKilometraje;
    }

    public void setIntervaloKilometraje(double intervaloKilometraje) {
        this.intervaloKilometraje = intervaloKilometraje;
    }

}
