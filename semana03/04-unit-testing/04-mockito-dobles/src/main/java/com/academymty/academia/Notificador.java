package com.academymty.academia;

/**
 * MOTIVO PARA MOCKEARLO: tiene EFECTO EXTERNO.
 *
 * La implementacion real manda correos. Un test que use la real manda
 * correos de verdad, a personas de verdad, cada vez que alguien corre
 * `mvn test`. No es que sea lento: es que NO SE PUEDE DESHACER.
 *
 * Este es el motivo mas fuerte de los tres para usar un doble, y ademas
 * el que introduce la otra mitad de Mockito: aqui no te interesa que
 * DEVUELVE (no devuelve nada), te interesa SI SE LLAMO. Eso es verify().
 */
public interface Notificador {

    void enviarConfirmacion(Alumno alumno, Curso curso);

    void enviarRechazo(Alumno alumno, Curso curso, String motivo);
}
