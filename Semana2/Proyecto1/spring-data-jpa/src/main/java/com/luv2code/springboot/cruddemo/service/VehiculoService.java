package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.entity.Vehiculo;

import java.util.List;

public interface VehiculoService {

    List<Vehiculo> findAll();

    Vehiculo findById(int theId);

    Vehiculo save(Vehiculo theVehiculo);

    void deleteById(int theId);

}
