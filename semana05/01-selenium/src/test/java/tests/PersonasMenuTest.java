package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.PersonasMenuPage;
import pages.SantanderHomePage;

public class PersonasMenuTest extends BaseTest {

    //CREDITO Y FINANCIAMIENTO

    @Test
    public void shouldOpenTarjetasCredito() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();

        personas.clickTarjetasCredito();

        Assert.assertTrue(
                personas.isUrlCorrect("tarjetas-de-credito")
        );
    }

    @Test
    public void shouldOpenCreditoPersonal() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();

        personas.clickCreditoPersonal();

        Assert.assertTrue(
                personas.isUrlCorrect("creditos-personales")
        );
    }

    @Test
    public void shouldOpenCreditoHipotecario() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();

        personas.clickCreditoHipotecario();

        Assert.assertTrue(
                personas.isUrlCorrect("creditos-hipotecarios")
        );
    }

    @Test
    public void shouldOpenSimuladorHipotecario() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();

        personas.clickSimuladorHipoteca();

        Assert.assertTrue(
                personas.isUrlCorrect("simulador-hipotecario")
        );
    }

    @Test
    public void shouldOpenCreditoAutomotriz() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();

        personas.clickCreditoAutomotriz();

        Assert.assertTrue(
                personas.isUrlCorrect("credito-automotriz")
        );
    }

    @Test
    public void shouldOpenBuroDeCredito() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();

        personas.clickBuroCredito();

        Assert.assertTrue(
                personas.isUrlCorrect("buro-de-credito")
        );
    }


    //CANALES DIGITALES

    @Test
    public void shouldOpenSantanderDigital() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSantanderDigital();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/santander-digital/")
        );
    }

    @Test
    public void shouldOpenAppSantander() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickAppSantander();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/santander-digital/app-santander/")
        );
    }

    @Test
    public void shouldOpenSantanderWeb() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSantanderWeb();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/santander-digital/santander-web/")
        );
    }

    @Test
    public void shouldOpenLimiteTransaccion() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickLimiteTransaccion();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/santander-digital/limite-por-transaccion/")
        );
    }


    // TIPO DE CUENTAS

    @Test
    public void shouldOpenCuentas() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCuentas();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/cuentas/")
        );
    }

    @Test
    public void shouldOpenCuentaBasica() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCuentaBasica();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/cuentas/basica/")
        );
    }

    @Test
    public void shouldOpenCuentaNomina() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCuentaNomina();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/cuentas/basica-nomina/")
        );
    }

    @Test
    public void shouldOpenCheques() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCheques();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/cuentas/cheque-saldo-promedio/")
        );
    }

    @Test
    public void shouldOpenPortabilidadNomina() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickPortabilidadNomina();

        Assert.assertTrue(
                personas.isUrlCorrect("/personas/cuentas/portabilidad-de-nomina/")
        );
    }

    // AHORRO E INVERSION

    @Test
    public void shouldOpenFondosInversion() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickFondosInversion();

        Assert.assertTrue(
                personas.isUrlCorrect("fondos-de-inversion")
        );
    }

    @Test
    public void shouldOpenInversionesPlazo() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickInversionesPlazo();

        Assert.assertTrue(
                personas.isUrlCorrect("inversiones-a-plazo")
        );
    }

    @Test
    public void shouldOpenNotasEstructuradas() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickNotasEstructuradas();

        Assert.assertTrue(
                personas.isUrlCorrect("notas-estructuradas")
        );
    }


    // SEGUROS

    @Test
    public void shouldOpenSeguroAuto() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSeguroAuto();

        Assert.assertTrue(
                personas.isUrlCorrect("seguros.html#auto")
        );
    }

    @Test
    public void shouldOpenSeguroVida() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSeguroVida();

        Assert.assertTrue(
                personas.isUrlCorrect("seguros.html#vida")
        );
    }

    @Test
    public void shouldOpenSeguroHogar() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSeguroHogar();

        Assert.assertTrue(
                personas.isUrlCorrect("seguros.html#hogar")
        );
    }

    @Test
    public void shouldOpenSeguroAhorro() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSeguroAhorro();

        Assert.assertTrue(
                personas.isUrlCorrect("seguros.html#ahorro")
        );
    }

    @Test
    public void shouldOpenGastosMedicos() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickGastosMedicos();

        Assert.assertTrue(
                personas.isUrlCorrect("seguros.html#gastos-medicos")
        );
    }

    @Test
    public void shouldOpenPertenencias() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PersonasMenuPage personas =
                new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickPertenencias();

        Assert.assertTrue(
                personas.isUrlCorrect("seguros.html#pertenencias")
        );
    }


    // INFORMACION Y AYUDA

    @Test
    public void shouldOpenSuperlinea() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSuperlinea();

        Assert.assertTrue(
                personas.isUrlCorrect("superlinea.html")
        );
    }

    @Test
    public void shouldOpenSucursales() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSucursales();

        Assert.assertTrue(
                personas.isUrlCorrect("sucursales.html")
        );
    }

    @Test
    public void shouldOpenCajerosAutomaticos() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCajerosAutomaticos();

        Assert.assertTrue(
                personas.isUrlCorrect("cajeros-automaticos.html")
        );
    }

    @Test
    public void shouldOpenCanalesAlternos() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCanalesAlternos();

        Assert.assertTrue(
                personas.isUrlCorrect("operaciones-canales-alternos")
        );
    }

    @Test
    public void shouldOpenCentroAyuda() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCentroAyuda();

        Assert.assertTrue(
                personas.isUrlCorrect("centro-de-ayuda.html")
        );
    }

    @Test
    public void shouldOpenCentroSeguridad() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCentroSeguridad();

        Assert.assertTrue(
                personas.isUrlCorrect("centro-de-seguridad")
        );
    }

    @Test
    public void shouldOpenTutoriales() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickTutoriales();

        Assert.assertTrue(
                personas.isUrlCorrect("tutoriales.html")
        );
    }

    @Test
    public void shouldOpenTerminosCondiciones() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickTerminosCondiciones();

        Assert.assertTrue(
                personas.isUrlCorrect("tyc")
        );
    }

    @Test
    public void shouldOpenRegulacion() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickRegulacion();

        Assert.assertTrue(
                personas.isUrlCorrect("tramite-por-defuncion.html")
        );
    }


    // BENEFICIOS

    @Test
    public void shouldOpenSelect() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSelect();

        Assert.assertTrue(
                personas.isUrlCorrect("select.html")
        );
    }

    @Test
    public void shouldOpenPromocionesSantander() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickPromocionesSantander();

        Assert.assertTrue(
                personas.isUrlCorrect("santander.com.mx/promociones/")
        );
    }

    @Test
    public void shouldOpenUniqueRewards() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickUniqueRewards();

        Assert.assertTrue(
                personas.isUrlCorrect("uniquerewards.santander.com.mx")
        );
    }

    @Test
    public void shouldOpenSolucionesColectivos() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickSolucionesColectivos();

        Assert.assertTrue(
                personas.isUrlCorrect("colectivos.html")
        );
    }

    @Test
    public void shouldOpenMundoHogar() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickMundoHogar();

        Assert.assertTrue(
                personas.isUrlCorrect("mundohogar.com.mx")
        );
    }

    @Test
    public void shouldOpenCashback() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PersonasMenuPage personas = new PersonasMenuPage(driver);

        home.clickPersonas();
        personas.clickCashback();

        Assert.assertTrue(
                personas.isUrlCorrect("cashback.html")
        );
    }

}