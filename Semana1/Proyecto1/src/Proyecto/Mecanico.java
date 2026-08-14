package Proyecto;

public class Mecanico extends Persona{

    private String especialidad;
    private boolean disponibilidad;

    public Mecanico(int idPersona, String nombre, String apellido, String telefono, String correo, String especialidad, boolean disponibilidad) {
        super(idPersona, nombre, apellido, telefono, correo);
        this.especialidad = especialidad;
        this.disponibilidad = disponibilidad;
    }

//    Ambos atributos pueden cambiar, tanto la especialidad
//    como la disponibilidad
    
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public boolean isDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(boolean disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
