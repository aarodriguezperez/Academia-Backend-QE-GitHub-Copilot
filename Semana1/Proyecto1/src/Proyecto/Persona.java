package Proyecto;

//Segundo uso de clase abstracta, 2 clases hijas: Cliente y Mecanico
public abstract class Persona {

    private final int idPersona;
    private String nombre;
    private String apellido;
    private String telefono;
    private String correo;

    public Persona(int idPersona, String nombre, String apellido, String telefono, String correo) {
        this.idPersona = idPersona;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
    }

//    Para IdPersona solo getter porque el id no debe modificarse
    public int getIdPersona() {
        return idPersona;
    }

//    Tanto nombre como apellido, telefono y correo deben poder
//    sufrir cambios por correcciones
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}


