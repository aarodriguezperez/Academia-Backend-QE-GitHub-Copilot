package tests;

import base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.EmpresasMenuPage;
import pages.SantanderHomePage;

public class EmpresasMenuTest extends BaseTest {

    @Test
    public void shouldOpenEmpresasGobierno() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        EmpresasMenuPage empresas =
                new EmpresasMenuPage(driver);

        home.clickEmpresas();
        empresas.clickEmpresasGobierno();

        Assert.assertTrue(
                empresas.isUrlCorrect("bei/home.html")
        );
    }

    @Test
    public void shouldOpenMultinacionales() {

        SantanderHomePage home =
                new SantanderHomePage(driver);

        EmpresasMenuPage empresas =
                new EmpresasMenuPage(driver);

        home.clickEmpresas();
        empresas.clickMultinacionales();

        Assert.assertTrue(
                empresas.isUrlCorrect("multinacionales.html")
        );
    }
}
