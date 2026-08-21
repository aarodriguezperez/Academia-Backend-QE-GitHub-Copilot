package Proyecto.modelo.persona;

public class Mecanico extends Persona {

    private String especialidad;

    public Mecanico(int idPersona, String nombre, String apellido, String telefono, String correo, String especialidad) {
        super(idPersona, nombre, apellido, telefono, correo);
        this.especialidad = especialidad;
    }

//    Ambos atributos pueden cambiar, tanto la especialidad
//    como la disponibilidad

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    @Override
    public String toString() {
        return getNombre() + " " + getApellido();
    }
}
