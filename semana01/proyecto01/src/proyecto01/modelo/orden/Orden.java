package proyecto01.modelo.orden;

import proyecto01.strategy.EstrategiaCosto;
import proyecto01.modelo.refaccion.Refaccion;
import proyecto01.modelo.persona.Cliente;
import proyecto01.modelo.persona.Mecanico;
import proyecto01.modelo.servicio.Servicio;
import proyecto01.modelo.vehiculo.Vehiculo;

import java.util.*;

public class Orden implements Comparable<Orden>{
    private final int idOrden;
    private Cliente cliente;
    private Mecanico mecanico;
    private Vehiculo vehiculo;
    private List<Servicio> servicios;
    private List<Refaccion> refacciones;
    private EstrategiaCosto estrategiaCosto;

    public Orden(int idOrden, Cliente cliente, Mecanico mecanico,
                         Vehiculo vehiculo) {
        this.idOrden = idOrden;
        this.cliente = cliente;
        this.mecanico = mecanico;
        this.vehiculo = vehiculo;
        this.servicios = new ArrayList<>();
        this.refacciones = new ArrayList<>();
    }

    public int getIdOrden() {
        return idOrden;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Mecanico getMecanico() {
        return mecanico;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void agregarServicio(Servicio servicio){
        servicios.add(servicio);
    }

    public void agregarRefaccion(Refaccion refaccion){
        refacciones.add(refaccion);
    }

    public List<Servicio> getServicios(){
        return servicios;
    }

    public List<Refaccion> getRefacciones(){
        return refacciones;
    }

//    @Override
//    public String toString() {
//        return "\nOrden " + getIdOrden() +
//                "\nidOrden:" + idOrden + "\n" +
//                "\nCliente:" + cliente + "\n" +
//                "\nMecanico:" + mecanico + "\n" +
//                "\nVehiculo:" + vehiculo + "\n" +
//                "\nServicios:" + servicios + "\n" +
//                "\nRefacciones:" + refacciones + "\n" +
//                '}';
//    }

    @Override
    public String toString() {
        return "Orden " + getIdOrden() +
                "\nidOrden: " + idOrden +
                "\nCliente: " + cliente +
                "\nMecanico:  " + mecanico +
                "\nVehiculo: " + vehiculo +
                "\nServicios: " + servicios +
                "\nRefacciones: " + refacciones;
    }

    public void asignarEstrategiaCosto(EstrategiaCosto estrategiaCosto){
        this.estrategiaCosto = estrategiaCosto;
    }

    // Metodo default, solo puede ser llamado por clases del mismo paquete
     double calcularSubtotal() {
        double total = 0;

        for (Servicio servicio : servicios) {
            total += servicio.getPrecioBase();
        }

        for (Refaccion refaccion : refacciones) {
            total += refaccion.getPrecio();
        }

        return total;
    }

    public double calcularTotal() {
        double subtotal = calcularSubtotal();
        return estrategiaCosto.calcularCosto(subtotal);
    }

    @Override
    public int compareTo(Orden otraOrden) {
        return Integer.compare(this.idOrden, otraOrden.idOrden);
    }
}
