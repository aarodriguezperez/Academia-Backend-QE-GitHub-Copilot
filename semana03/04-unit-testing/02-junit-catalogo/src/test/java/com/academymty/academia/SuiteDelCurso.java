package com.academymty.academia;

import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * SECCION 08 de la guia 02 -- @Suite: una seleccion con nombre.
 *
 * Una suite es una clase que no tiene ni un solo @Test: solo dice QUE
 * ejecutar. Sirve para darle nombre a una seleccion que se repite --
 * "los tests de humo", "lo que corre antes de un release".
 *
 * Necesita su propia dependencia (ya esta en el pom):
 *
 *     org.junit.platform:junit-platform-suite
 *
 * Y ojo con Maven: el pom EXCLUYE esta clase de `mvn test`. Si no, los
 * tests que selecciona correrian dos veces -- una por si mismos y otra
 * a traves de la suite -- y el conteo del reporte saldria inflado.
 * En Eclipse o IntelliJ, en cambio, se ejecuta con click derecho > Run.
 *
 * Selectores disponibles: @SelectClasses, @SelectPackages,
 * @IncludeTags / @ExcludeTags, @IncludeClassNamePatterns.
 *
 * Nota honesta: en un proyecto con Maven las suites se usan poco, porque
 * -Dgroups= sobre @Tag hace lo mismo sin clase extra ni exclusiones que
 * mantener. Vale la pena conocerlas porque salen en codigo heredado.
 */
@Suite
@SuiteDisplayName("Humo: lo minimo que debe pasar antes de subir codigo")
@SelectClasses({
        ExcepcionesTest.class,
        CursoNestedTest.class,
        EtiquetasTest.class
})
@ExcludeTags("lento")
public class SuiteDelCurso {
    // Sin cuerpo. La configuracion ES la clase.
}
