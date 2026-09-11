package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AcercaDelBancoMenuPage;
import pages.SantanderHomePage;

public class AcercaDelBancoMenuTest extends BaseTest {

    @Test
    public void shouldOpenFundacionSantander() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        AcercaDelBancoMenuPage acerca =
                new AcercaDelBancoMenuPage(driver);

        home.clickAcercaDelBanco();
        acerca.clickFundacionSantander();

        Assert.assertTrue(
                acerca.isUrlCorrect("fundacion-santander.html")
        );
    }

    @Test
    public void shouldOpenResponsabilidadSocial() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        AcercaDelBancoMenuPage acerca =
                new AcercaDelBancoMenuPage(driver);

        home.clickAcercaDelBanco();
        acerca.clickResponsabilidadSocial();

        Assert.assertTrue(
                acerca.isUrlCorrect("responsabilidad-social.html")
        );
    }

    @Test
    public void shouldOpenEducacionFinanciera() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        AcercaDelBancoMenuPage acerca =
                new AcercaDelBancoMenuPage(driver);

        home.clickAcercaDelBanco();
        acerca.clickEducacionFinanciera();

        Assert.assertTrue(
                acerca.isUrlCorrect("educacion-financiera")
        );
    }

    @Test
    public void shouldOpenInversionistas() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        AcercaDelBancoMenuPage acerca =
                new AcercaDelBancoMenuPage(driver);

        home.clickAcercaDelBanco();
        acerca.clickInversionistas();

        Assert.assertTrue(
                acerca.isUrlCorrect("/ir/home/")
        );
    }

    @Test
    public void shouldOpenSalaComunicacion() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        AcercaDelBancoMenuPage acerca =
                new AcercaDelBancoMenuPage(driver);

        home.clickAcercaDelBanco();
        acerca.clickSalaComunicacion();

        Assert.assertTrue(
                acerca.isUrlCorrect("sala_prensa_2026.html")
        );
    }

    @Test
    public void shouldOpenBolsaTrabajo() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        AcercaDelBancoMenuPage acerca =
                new AcercaDelBancoMenuPage(driver);

        home.clickAcercaDelBanco();
        acerca.clickBolsaTrabajo();

        Assert.assertTrue(
                acerca.isUrlCorrect("bolsa-de-trabajo.html")
        );
    }

    @Test
    public void shouldOpenBlog() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        AcercaDelBancoMenuPage acerca =
                new AcercaDelBancoMenuPage(driver);

        home.clickAcercaDelBanco();
        acerca.clickBlog();

        Assert.assertTrue(
                acerca.isUrlCorrect("blog.html")
        );
    }
}