package com.luv2code.springboot.cruddemo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "vehiculos")
public class Vehiculo {

    // define campos
    //
    // El id es String, no int: MongoDB genera un ObjectId (hexadecimal de 24
    // caracteres). No lleva @GeneratedValue — el driver lo asigna al insertar.
    @Id
    private String id;

    // Sin @Column: MongoDB no tiene esquema, así que el campo del documento se
    // llama igual que la propiedad Java. No hay nada que mapear.
    private String marca;

    private String modelo;

    private String color;

    private int anio;


    // define constructor
    public Vehiculo() {

    }

    public Vehiculo(String marca, String modelo, String color, int anio) {
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.anio = anio;
    }

    // define getter/setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAnio(){return anio;}

    public void setAnio(int anio) { this.anio = anio;}

    // define toString
    @Override
    public String toString() {
        return "Vehiculo{" +
                "id='" + id + '\'' +
                ", marca='" + marca + '\'' +
                ", modelo='" + modelo + '\'' +
                ", color='" + color + '\'' +
                ", año='" + anio + '\'' +
                '}';
    }
}
