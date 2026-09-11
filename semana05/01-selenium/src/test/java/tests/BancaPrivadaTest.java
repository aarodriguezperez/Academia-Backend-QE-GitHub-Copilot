package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BancaPrivadaMenuPage;
import pages.SantanderHomePage;

public class BancaPrivadaTest extends BaseTest {

    @Test
    public void shouldOpenNuestrasCuentas() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        BancaPrivadaMenuPage bancaPrivada =
                new BancaPrivadaMenuPage(driver);

        home.clickBancaPrivada();

        Assert.assertTrue(
                bancaPrivada.isUrlCorrect("/bp/home/")
        );

        bancaPrivada.clickNuestrasCuentas();

        Assert.assertTrue(
                bancaPrivada.isNuestrasCuentasActive()
        );
    }

    @Test
    public void shouldOpenQuienesSomos() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        BancaPrivadaMenuPage bancaPrivada =
                new BancaPrivadaMenuPage(driver);

        home.clickBancaPrivada();

        Assert.assertTrue(
                bancaPrivada.isUrlCorrect("/bp/home/")
        );

        bancaPrivada.clickQuienesSomos();

        Assert.assertTrue(
                bancaPrivada.isUrlCorrect("/bp/quienes/index.html")
        );
    }

    @Test
    public void shouldOpenProductos() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        BancaPrivadaMenuPage bancaPrivada =
                new BancaPrivadaMenuPage(driver);

        home.clickBancaPrivada();

        Assert.assertTrue(
                bancaPrivada.isUrlCorrect("/bp/home/")
        );

        bancaPrivada.clickProductos();

        Assert.assertTrue(
                bancaPrivada.isUrlCorrect("/bp/productos/index.html")
        );
    }

    @Test
    public void shouldOpenPodcast() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        BancaPrivadaMenuPage bancaPrivada =
                new BancaPrivadaMenuPage(driver);

        home.clickBancaPrivada();

        Assert.assertTrue(
                bancaPrivada.isUrlCorrect("/bp/home/")
        );

        bancaPrivada.clickPodcast();

        Assert.assertTrue(
                bancaPrivada.isUrlCorrect("/bp/podcast/index.html")
        );
    }
}
