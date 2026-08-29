package com.academymty.academia;

import java.util.Optional;

/**
 * MOTIVO PARA MOCKEARLO: es LENTO.
 *
 * La implementacion real simula una base de datos: 300 ms por consulta.
 * Multiplica eso por los tests de una suite y ya tienes minutos de espera.
 *
 * Fijate en que es una INTERFAZ. Mockito 5 puede mockear clases concretas
 * sin problema, pero una interfaz declara el contrato de forma explicita y
 * hace evidente que el servicio no depende de "la base de datos" sino de
 * "algo que sepa buscar alumnos". Esa distincion es la que hace testeable
 * el codigo, y es anterior a Mockito.
 */
public interface RepositorioAlumnos {

    Optional<Alumno> buscar(String matricula);

    void guardar(Alumno alumno);
}
