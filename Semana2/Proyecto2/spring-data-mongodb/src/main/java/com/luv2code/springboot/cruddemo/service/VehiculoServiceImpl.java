package com.luv2code.springboot.cruddemo.service;

import com.luv2code.springboot.cruddemo.entity.Vehiculo;
import com.luv2code.springboot.cruddemo.repository.VehiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoServiceImpl implements VehiculoService {

    // Antes se inyectaba VehiculoDAO. Ahora es el repositorio de Spring Data,
    // que no tiene implementación escrita a mano.
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
    public Vehiculo findById(String theId) {

        // findById() devuelve Optional<Vehiculo>. Lo convertimos a null para
        // conservar el mismo contrato que tenía la versión con JPA: el
        // controlador sigue comprobando "if (tempVehiculo == null)".
        return vehiculoRepository.findById(theId).orElse(null);
    }

    // Ojo: aquí ya no hay @Transactional.
    //
    // MongoDB en modo standalone (un contenedor suelto, sin replica set) no
    // soporta transacciones multi-documento. Y no hacen falta: cada operación
    // toca un solo documento, y MongoDB garantiza atomicidad por documento.
    @Override
    public Vehiculo save(Vehiculo theVehiculo) {
        return vehiculoRepository.save(theVehiculo);
    }

    @Override
    public void deleteById(String theId) {
        vehiculoRepository.deleteById(theId);
    }
}
