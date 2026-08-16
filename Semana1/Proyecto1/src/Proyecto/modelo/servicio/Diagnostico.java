package Proyecto.modelo.servicio;

public class Diagnostico extends Servicio {
    private String tipoDiagnostico;

    public Diagnostico(int idServicio, String nombre, String descripcion, double precioBase, String tipoDiagnostico) {
        super(idServicio, nombre, descripcion, precioBase);
        this.tipoDiagnostico = tipoDiagnostico;
    }

    public String getTipoDiagnostico() {
        return tipoDiagnostico;
    }

    public void setTipoDiagnostico(String tipoDiagnostico) {
        this.tipoDiagnostico = tipoDiagnostico;
    }

}
