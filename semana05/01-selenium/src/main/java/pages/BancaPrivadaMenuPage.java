package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class BancaPrivadaMenuPage extends BasePage {

    private static final By NUESTRAS_CUENTAS =
            By.cssSelector("button[data-event-label='nuestras_cuentas']");

    private static final By QUIENES_SOMOS =
            By.cssSelector("a[href*='/bp/quienes/index.html']");

    private static final By PRODUCTOS =
            By.cssSelector("a[href*='/bp/productos/index.html']");

    private static final By PODCAST =
            By.cssSelector("a[href*='/bp/podcast/index.html']");

    public BancaPrivadaMenuPage(WebDriver driver) {
        super(driver);
    }

    public void clickNuestrasCuentas() {
        click(NUESTRAS_CUENTAS);
    }

    public void clickQuienesSomos() {
        click(QUIENES_SOMOS);
    }

    public void clickProductos() {
        click(PRODUCTOS);
    }

    public void clickPodcast() {
        click(PODCAST);
    }

    public boolean isUrlCorrect(String value) {
        return waitForUrl(value);
    }

    public boolean isNuestrasCuentasActive() {
        return find(NUESTRAS_CUENTAS)
                .getAttribute("class")
                .contains("active");
    }
}