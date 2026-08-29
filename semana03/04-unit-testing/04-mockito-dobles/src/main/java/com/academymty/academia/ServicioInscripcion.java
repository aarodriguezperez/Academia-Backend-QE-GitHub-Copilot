package com.academymty.academia;

/**
 * El codigo bajo prueba del proyecto 04.
 *
 * No hace nada por si solo: coordina a tres colaboradores. Y eso es
 * exactamente lo que lo convierte en el ejemplo perfecto para Mockito --
 * probarlo sin dobles significa arrastrar una base de datos lenta y un
 * servidor de correo.
 *
 * Fijate en el constructor: los tres colaboradores ENTRAN POR AHI. No se
 * construyen dentro con new. Si lo hicieran, no habria forma de sustituirlos
 * en un test, y ninguna cantidad de Mockito lo arreglaria.
 *
 *   La testeabilidad se decide en el DISENO, no en la libreria de tests.
 */
public class ServicioInscripcion {

    private final RepositorioAlumnos repoAlumnos;
    private final RepositorioCursos repoCursos;
    private final Notificador notificador;

    public ServicioInscripcion(RepositorioAlumnos repoAlumnos,
                               RepositorioCursos repoCursos,
                               Notificador notificador) {
        this.repoAlumnos = repoAlumnos;
        this.repoCursos = repoCursos;
        this.notificador = notificador;
    }

    /**
     * Inscribe a un alumno en un curso.
     *
     * El orden de estos pasos IMPORTA, y es lo que prueban los tests:
     *   1. Buscar al alumno. Si no existe, se acabo.
     *   2. Buscar el curso. Si no existe, se acabo.
     *   3. Si el curso esta lleno: avisar del RECHAZO y lanzar. Nunca confirmar.
     *   4. Inscribir y confirmar por correo.
     *
     * @throws AlumnoNoEncontradoException si la matricula no existe
     * @throws CursoNoEncontradoException  si la clave del curso no existe
     * @throws CupoLlenoException          si ya no quedan lugares
     */
    public Inscripcion inscribir(String matricula, String claveCurso) {
        Alumno alumno = repoAlumnos.buscar(matricula)
                .orElseThrow(() -> new AlumnoNoEncontradoException(matricula));

        Curso curso = repoCursos.buscar(claveCurso)
                .orElseThrow(() -> new CursoNoEncontradoException(claveCurso));

        if (curso.estaLleno()) {
            notificador.enviarRechazo(alumno, curso, "cupo lleno");
            throw new CupoLlenoException(claveCurso, curso.cupo());
        }

        curso.inscribir(matricula);
        notificador.enviarConfirmacion(alumno, curso);

        return new Inscripcion(matricula, claveCurso, curso.lugaresDisponibles());
    }
}
