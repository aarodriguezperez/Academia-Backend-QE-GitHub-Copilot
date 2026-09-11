package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SantanderHomePage extends BasePage {

    private static final By PERSONAS =
            By.cssSelector("button[aria-label='personas']");

    private static final By EMPRESAS =
            By.cssSelector("button[aria-label='empresas']");

    private static final By PYMES =
            By.cssSelector("button[aria-label='pymes']");

    private static final By BANCA_PRIVADA =
            By.cssSelector("a[href*='/bp/home/']");

    private static final By ACERCA_DEL_BANCO =
            By.cssSelector("button[aria-label='acerca del banco']");

    public SantanderHomePage(WebDriver driver) {
        super(driver);
    }

    public void clickPersonas() {
        click(PERSONAS);
    }

    public boolean isPersonasMenuExpanded() {
        return find(PERSONAS)
                .getAttribute("aria-expanded")
                .equals("true");
    }

    public void clickEmpresas() {
        click(EMPRESAS);
    }

    public boolean isEmpresasMenuExpanded() {
        return find(EMPRESAS)
                .getAttribute("aria-expanded")
                .equals("true");
    }

    public void clickPymes() {
        click(PYMES);
    }

    public boolean isPymesMenuExpanded() {
        return find(PYMES)
                .getAttribute("aria-expanded")
                .equals("true");
    }

    public void clickBancaPrivada() {
        click(BANCA_PRIVADA);
    }

    public void clickAcercaDelBanco() {
        click(ACERCA_DEL_BANCO);
    }

    public boolean isAcercaDelBancoMenuExpanded() {
        return find(ACERCA_DEL_BANCO)
                .getAttribute("aria-expanded")
                .equals("true");
    }
}