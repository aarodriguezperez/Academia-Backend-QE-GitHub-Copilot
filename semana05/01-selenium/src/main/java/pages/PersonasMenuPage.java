package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PersonasMenuPage extends BasePage {

    // CREDITO Y FINANCIAMIENTO

    private static final By TARJETAS_CREDITO =
            By.cssSelector("a[href*='/personas/credito-y-financiamiento/tarjetas-de-credito/']");

    private static final By CREDITO_PERSONAL =
            By.cssSelector("a[href*='/personas/credito-y-financiamiento/creditos-personales/']");

    private static final By CREDITO_HIPOTECARIO =
            By.cssSelector("a[href*='/personas/credito-y-financiamiento/creditos-hipotecarios/']");

    private static final By SIMULADOR_HIPOTECA =
            By.cssSelector("a[href*='/personas/credito-y-financiamiento/creditos-hipotecarios/simulador-hipotecario/']");

    private static final By CREDITO_AUTOMOTRIZ =
            By.cssSelector("a[href*='/personas/credito-y-financiamiento/credito-automotriz/']");

    private static final By BURO_DE_CREDITO =
            By.cssSelector("a[href*='/personas/credito-y-financiamiento/buro-de-credito/']");


    //CANALES DIGITALES

    private static final By SANTANDER_DIGITAL =
            By.cssSelector("a[href*='/personas/santander-digital/']");

    private static final By APP_SANTANDER =
            By.cssSelector("a[href*='/personas/santander-digital/app-santander/']");

    private static final By SANTANDER_WEB =
            By.cssSelector("a[href*='/personas/santander-digital/santander-web/']");

    private static final By LIMITE_TRANSACCION =
            By.cssSelector("a[href*='/personas/santander-digital/limite-por-transaccion/']");


    // TIPO DE CUENTAS

    private static final By CUENTAS =
            By.cssSelector("a[href='https://www.santander.com.mx/personas/cuentas/']");

    private static final By CUENTA_BASICA =
            By.cssSelector("a[href*='/personas/cuentas/basica/']");

    private static final By CUENTA_NOMINA =
            By.cssSelector("a[href*='/personas/cuentas/basica-nomina/']");

    private static final By CHEQUES =
            By.cssSelector("a[href*='/personas/cuentas/cheque-saldo-promedio/']");

    private static final By PORTABILIDAD_NOMINA =
            By.cssSelector("a[href*='/personas/cuentas/portabilidad-de-nomina/']");


    // AHORRO E INVERSION

    private static final By FONDOS_INVERSION =
            By.cssSelector("a[href*='/personas/inversiones/#fondos-de-inversion']");

    private static final By INVERSIONES_PLAZO =
            By.cssSelector("a[href*='/personas/inversiones/#inversiones-a-plazo']");

    private static final By NOTAS_ESTRUCTURADAS =
            By.cssSelector("a[href*='/personas/inversiones/#notas-estructuradas']");


    // SEGUROS

    private static final By SEGURO_AUTO =
            By.cssSelector("a[href*='/personas/seguros.html#auto']");

    private static final By SEGURO_VIDA =
            By.cssSelector("a[href*='/personas/seguros.html#vida']");

    private static final By SEGURO_HOGAR =
            By.cssSelector("a[href*='/personas/seguros.html#hogar']");

    private static final By SEGURO_AHORRO =
            By.cssSelector("a[href*='/personas/seguros.html#ahorro']");

    private static final By GASTOS_MEDICOS =
            By.cssSelector("a[href*='/personas/seguros.html#gastos-medicos']");

    private static final By PERTENENCIAS =
            By.cssSelector("a[href*='/personas/seguros.html#pertenencias']");


    // INFORMACION Y AYUDA

    private static final By SUPERLINEA =
            By.cssSelector("a[href*='/personas/superlinea.html']");

    private static final By SUCURSALES =
            By.cssSelector("a[href*='/personas/sucursales.html']");

    private static final By CAJEROS_AUTOMATICOS =
            By.cssSelector("a[href*='/personas/cajeros-automaticos.html']");

    private static final By CANALES_ALTERNOS =
            By.cssSelector("a[href*='/personas/operaciones-canales-alternos/index.html']");

    private static final By CENTRO_AYUDA =
            By.cssSelector("a[href*='/personas/centro-de-ayuda.html']");

    private static final By CENTRO_SEGURIDAD =
            By.cssSelector("a[href*='/personas/informacion-y-ayuda/centro-de-seguridad']");

    private static final By TUTORIALES =
            By.cssSelector("a[href*='/personas/tutoriales.html']");

    private static final By TERMINOS_CONDICIONES =
            By.cssSelector("a[href*='/promociones/terminos-y-condiciones.html']");

    private static final By REGULACION =
            By.cssSelector("a[href*='/personas/regulacion/tramite-por-defuncion.html']");


    // BENFEFICIOS

    private static final By SELECT =
            By.cssSelector("a[href*='/personas/select.html']");

    private static final By PROMOCIONES_SANTANDER =
            By.cssSelector("a[href='https://www.santander.com.mx/promociones/']");

    private static final By UNIQUE_REWARDS =
            By.cssSelector("a[href*='uniquerewards.santander.com.mx/web/home']");

    private static final By SOLUCIONES_COLECTIVOS =
            By.cssSelector("a[href*='/personas/colectivos.html']");

    private static final By MUNDO_HOGAR =
            By.cssSelector("a[href*='mundohogar.com.mx']");

    private static final By CASHBACK =
            By.cssSelector("a[href*='/cashback.html']");


    // CONSTRUCTOR
    public PersonasMenuPage(WebDriver driver) {
        super(driver);
    }


    // CREDITO Y FINANCIAMIENTO

    public void clickTarjetasCredito() {
        click(TARJETAS_CREDITO);
    }

    public void clickCreditoPersonal() {
        click(CREDITO_PERSONAL);
    }

    public void clickCreditoHipotecario() {
        click(CREDITO_HIPOTECARIO);
    }

    public void clickSimuladorHipoteca() {
        click(SIMULADOR_HIPOTECA);
    }

    public void clickCreditoAutomotriz() {
        click(CREDITO_AUTOMOTRIZ);
    }

    public void clickBuroCredito() {
        click(BURO_DE_CREDITO);
    }


    // CANALES DIGITALES

    public void clickSantanderDigital() {
        click(SANTANDER_DIGITAL);
    }

    public void clickAppSantander() {
        click(APP_SANTANDER);
    }

    public void clickSantanderWeb() {
        click(SANTANDER_WEB);
    }

    public void clickLimiteTransaccion() {
        click(LIMITE_TRANSACCION);
    }


    // TIPOS DE CUENTAS

    public void clickCuentas() {
        click(CUENTAS);
    }

    public void clickCuentaBasica() {
        click(CUENTA_BASICA);
    }

    public void clickCuentaNomina() {
        click(CUENTA_NOMINA);
    }

    public void clickCheques() {
        click(CHEQUES);
    }

    public void clickPortabilidadNomina() {
        click(PORTABILIDAD_NOMINA);
    }


    // AHORROS E INVERSION

    public void clickFondosInversion() {
        click(FONDOS_INVERSION);
    }

    public void clickInversionesPlazo() {
        click(INVERSIONES_PLAZO);
    }

    public void clickNotasEstructuradas() {
        click(NOTAS_ESTRUCTURADAS);
    }


    // SEGUROS

    public void clickSeguroAuto() {
        click(SEGURO_AUTO);
    }

    public void clickSeguroVida() {
        click(SEGURO_VIDA);
    }

    public void clickSeguroHogar() {
        click(SEGURO_HOGAR);
    }

    public void clickSeguroAhorro() {
        click(SEGURO_AHORRO);
    }

    public void clickGastosMedicos() {
        click(GASTOS_MEDICOS);
    }

    public void clickPertenencias(){
        click(PERTENENCIAS);
    }


    // INFORMACION Y AYUDA

    public void clickSuperlinea() {
        click(SUPERLINEA);
    }

    public void clickSucursales() {
        click(SUCURSALES);
    }

    public void clickCajerosAutomaticos() {
        click(CAJEROS_AUTOMATICOS);
    }

    public void clickCanalesAlternos() {
        click(CANALES_ALTERNOS);
    }

    public void clickCentroAyuda() {
        click(CENTRO_AYUDA);
    }

    public void clickCentroSeguridad() {
        click(CENTRO_SEGURIDAD);
    }

    public void clickTutoriales() {
        click(TUTORIALES);
    }

    public void clickTerminosCondiciones() {
        click(TERMINOS_CONDICIONES);
    }

    public void clickRegulacion() {
        click(REGULACION);
    }


    // BENEFICIOS

    public void clickSelect() {
        click(SELECT);
    }

    public void clickPromocionesSantander() {
        click(PROMOCIONES_SANTANDER);
    }

    public void clickUniqueRewards() {
        click(UNIQUE_REWARDS);
    }

    public void clickSolucionesColectivos() {
        click(SOLUCIONES_COLECTIVOS);
    }

    public void clickMundoHogar() {
        click(MUNDO_HOGAR);
    }

    public void clickCashback() {
        click(CASHBACK);
    }

    // METODO PARA ESPERAR LA URL

    public boolean isUrlCorrect(String value) {
        return waitForUrl(value);
    }

}