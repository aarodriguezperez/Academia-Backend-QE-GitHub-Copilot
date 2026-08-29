package com.academymty.academia;

import java.util.Optional;

/**
 * MOTIVO PARA MOCKEARLO: montar el escenario de verdad es CARO.
 *
 * Para probar "que pasa cuando el curso esta lleno" con el repositorio real
 * habria que crear el curso, inscribir a treinta alumnos y despues probar.
 * Con un doble, el escenario se monta en una linea.
 */
public interface RepositorioCursos {

    Optional<Curso> buscar(String clave);
}
