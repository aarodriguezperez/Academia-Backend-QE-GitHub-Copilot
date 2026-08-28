package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.dao.VehiculoRepository;
import com.luv2code.springboot.cruddemo.entity.Vehiculo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    private VehiculoRepository vehiculoRepository;

    @Autowired
    public VehiculoServiceImpl(VehiculoRepository theVehiculoRepository) {
        vehiculoRepository = theVehiculoRepository;
    }

    @Override
    public List<Vehiculo> findAll() {
        return vehiculoRepository.findAll();
    }

    @Override
    public Vehiculo findById(int theId) {
        Optional<Vehiculo> result = vehiculoRepository.findById(theId);

        Vehiculo theVehiculo = null;

        if (result.isPresent()) {
            theVehiculo = result.get();
        }
        else {
            // No se encuentra el vehículo
            throw new RuntimeException("No encontrado vehiculo id - " + theId);
        }

        return theVehiculo;
    }

    @Override
    public Vehiculo save(Vehiculo theVehiculo) {
        return vehiculoRepository.save(theVehiculo);
    }

    @Override
    public void deleteById(int theId) {
        vehiculoRepository.deleteById(theId);
    }
}






