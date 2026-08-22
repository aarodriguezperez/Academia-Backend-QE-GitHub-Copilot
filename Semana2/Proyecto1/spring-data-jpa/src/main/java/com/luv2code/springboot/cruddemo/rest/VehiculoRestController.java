package com.luv2code.springboot.cruddemo.rest;

import tools.jackson.databind.json.JsonMapper;
import com.luv2code.springboot.cruddemo.entity.Vehiculo;
import com.luv2code.springboot.cruddemo.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class VehiculoRestController {

    private VehiculoService vehiculoService;

    private JsonMapper jsonMapper;

    @Autowired
    public VehiculoRestController(VehiculoService theVehiculoService, JsonMapper theJsonMapper) {
        vehiculoService = theVehiculoService;
        jsonMapper = theJsonMapper;
    }

    // expone "/vehiculos" y regresa la lista de vehiculos
    @GetMapping("/vehiculos")
    public List<Vehiculo> findAll() {
        return vehiculoService.findAll();
    }

    // agrega mapeo para GET por Id /vehiculos/{vehiculoId}

    @GetMapping("/vehiculos/{vehiculoId}")
    public Vehiculo getVehiculo(@PathVariable int vehiculoId) {

        Vehiculo theVehiculo = vehiculoService.findById(vehiculoId);

        if (theVehiculo == null) {
            throw new RuntimeException("No encontrado vehiculo id - " + vehiculoId);
        }

        return theVehiculo;
    }

    // agrega mapeo para POST /vehiculos - agregar nuevo vehiculo

    @PostMapping("/vehiculos")
    public Vehiculo addVehiculo(@RequestBody Vehiculo theVehiculo) {

        // en caso de que se pase un id en JSON ... se define el id en 0
        // esto para forzar a que se guarde como un nuevo item ...
        // en lugar de una actualización

        theVehiculo.setId(0);

        Vehiculo dbVehiculo = vehiculoService.save(theVehiculo);

        return dbVehiculo;
    }

    // agrega mapeo para PUT /vehiculos - actualizar vehiculo existente

    @PutMapping("/vehiculos")
    public Vehiculo updateVehiculo(@RequestBody Vehiculo theVehiculo) {

        Vehiculo dbVehiculo = vehiculoService.save(theVehiculo);

        return dbVehiculo;
    }

    // agregar mapeo para PATCH /vehiculos/{vehiculoId} - patch vehiculo ...
    // actualización parcial

    @PatchMapping("/vehiculos/{vehiculoId}")
    public Vehiculo patchVehiculo(@PathVariable int vehiculoId,
            @RequestBody Map<String, Object> patchPayload) {

        // Paso 1: Devolver el vehiculo existente de la base de datos
        Vehiculo tempVehiculo = vehiculoService.findById(vehiculoId);

        if (tempVehiculo == null) {
            throw new RuntimeException("No encontrado vehiculo id - " + vehiculoId);
        }

        // Paso 2: Prueba de seguridad - Prevenir modificaciones de iD
        // El ID no debe cambiar NUNCA, por eso se rechazan los intentos por modificarlo
        if (patchPayload.containsKey("id")) {
            throw new RuntimeException(
                    "Vehiculo id no se puede modificar. " +
                            "Remueve 'id' de la solicitud.");
        }

        // Paso 3: Aplicar la actualización parcial
        // Esto crea un NUEVO vehículo objeto con las actualizaciones aplicadas
        Vehiculo patchedVehiculo = jsonMapper.updateValue(tempVehiculo, patchPayload);

        // Paso 4: Guarda el vehiculo actualizado en la base de datos y lo devuelve
        Vehiculo dbVehiculo = vehiculoService.save(patchedVehiculo);

        return dbVehiculo;
    }

    // agregar mapeo para DELETE /vehiculos/{vehiculoId} - borrar vehiculos

    @DeleteMapping("/vehiculos/{vehiculoId}")
    public String deleteVehiculo(@PathVariable int vehiculoId) {

        Vehiculo tempVehiculo = vehiculoService.findById(vehiculoId);

        // lanza excepción en caso de que sea null el vehiculo

        if (tempVehiculo == null) {
            throw new RuntimeException("No encontrado vehiculo id - " + vehiculoId);
        }

        vehiculoService.deleteById(vehiculoId);

        return "Se ha borrado vehiculo id - " + vehiculoId;
    }

}
