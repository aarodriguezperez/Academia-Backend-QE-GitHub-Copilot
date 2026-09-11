package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.PymesMenuPage;
import pages.SantanderHomePage;

public class PymesMenuTest extends BaseTest {

    @Test
    public void shouldOpenSantanderPyme() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PymesMenuPage pymes =
                new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickSantanderPyme();

        Assert.assertTrue(
                pymes.isUrlCorrect("www.santander.com.mx/pyme.html")
        );
    }

    @Test
    public void shouldOpenCuentas() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PymesMenuPage pymes =
                new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickCuentas();

        Assert.assertTrue(
                pymes.isUrlCorrect("cuentas.html")
        );
    }

    @Test
    public void shouldOpenPaquetesPymes() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        PymesMenuPage pymes =
                new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickPaquetesPymes();

        Assert.assertTrue(
                pymes.isUrlCorrect("paquetes-pymes.html")
        );
    }

    @Test
    public void shouldOpenSeguros() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickSeguros();

        Assert.assertTrue(
                pymes.isUrlCorrect("seguros.html")
        );
    }

    @Test
    public void shouldOpenNegocioTransaccional() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickNegocioTransaccional();

        Assert.assertTrue(
                pymes.isUrlCorrect("negocio-transaccional.html")
        );
    }

    @Test
    public void shouldOpenInversiones() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickInversiones();

        Assert.assertTrue(
                pymes.isUrlCorrect("inversiones.html")
        );
    }

    @Test
    public void shouldOpenDivisasCoberturas() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickDivisasCoberturas();

        Assert.assertTrue(
                pymes.isUrlCorrect("coberturas-y-cambios.html")
        );
    }

    @Test
    public void shouldOpenNegocioInternacional() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickNegocioInternacional();

        Assert.assertTrue(
                pymes.isUrlCorrect("negocio-internacional.html")
        );
    }

    @Test
    public void shouldOpenCreditos() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickCreditos();

        Assert.assertTrue(
                pymes.isUrlCorrect("creditos.html")
        );
    }

    @Test
    public void shouldOpenAlianzas() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickAlianzas();

        Assert.assertTrue(
                pymes.isUrlCorrect("alianzas.html")
        );
    }

    @Test
    public void shouldOpenEcosistemaNoFinanciero() {

        SantanderHomePage home = new SantanderHomePage(driver);
        PymesMenuPage pymes = new PymesMenuPage(driver);

        home.clickPymes();
        pymes.clickEcosistemaNoFinanciero();

        Assert.assertTrue(
                pymes.isUrlCorrect("ecosistemas-pyme.html")
        );
    }
}