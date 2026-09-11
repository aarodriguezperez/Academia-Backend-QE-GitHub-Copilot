package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.SantanderHomePage;

public class SantanderHomeTest extends BaseTest {

    @Test
    public void shouldOpenPersonasMenu() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        home.clickPersonas();

        Assert.assertTrue(
                home.isPersonasMenuExpanded()
        );
    }

    @Test
    public void shouldOpenEmpresasMenu() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        home.clickEmpresas();

        Assert.assertTrue(
                home.isEmpresasMenuExpanded()
        );
    }

    @Test
    public void shouldOpenPymesMenu() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        home.clickPymes();

        Assert.assertTrue(
                home.isPymesMenuExpanded()
        );
    }

    @Test
    public void shouldOpenBancaPrivada() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        home.clickBancaPrivada();

        Assert.assertTrue(
                driver.getCurrentUrl().contains("/bp/home/")
        );
    }

    @Test
    public void shouldOpenAcercaDelBancoMenu() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        home.clickAcercaDelBanco();

        Assert.assertTrue(
                home.isAcercaDelBancoMenuExpanded()
        );
    }
}
