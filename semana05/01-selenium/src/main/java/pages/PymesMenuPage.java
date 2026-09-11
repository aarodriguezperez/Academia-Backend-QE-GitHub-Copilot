package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PymesMenuPage extends BasePage {

    private static final By SANTANDER_PYME =
            By.cssSelector("a[href*='/pyme/']");

    private static final By CUENTAS =
            By.cssSelector("a[href*='/pyme/cuentas.html']");

    private static final By PAQUETES_PYMES =
            By.cssSelector("a[href*='/pyme/paquetes-pymes.html']");

    private static final By SEGUROS =
            By.cssSelector("a[href*='/pyme/seguros.html']");

    private static final By NEGOCIO_TRANSACCIONAL =
            By.cssSelector("a[href*='/pyme/negocio-transaccional.html']");

    private static final By INVERSIONES =
            By.cssSelector("a[href*='/pyme/inversiones.html']");

    private static final By DIVISAS_COBERTURAS =
            By.cssSelector("a[href*='/pyme/coberturas-y-cambios.html']");

    private static final By NEGOCIO_INTERNACIONAL =
            By.cssSelector("a[href*='/pyme/negocio-internacional.html']");

    private static final By CREDITOS =
            By.cssSelector("a[href*='/pyme/creditos.html']");

    private static final By ALIANZAS =
            By.cssSelector("a[href*='/pyme/alianzas.html']");

    private static final By ECOSISTEMA_NO_FINANCIERO =
            By.cssSelector("a[href*='/pyme/ecosistemas-pyme.html']");

    public PymesMenuPage(WebDriver driver) {
        super(driver);
    }

    public void clickSantanderPyme() {
        click(SANTANDER_PYME);
    }

    public void clickCuentas() {
        click(CUENTAS);
    }

    public void clickPaquetesPymes() {
        click(PAQUETES_PYMES);
    }

    public void clickSeguros() {
        click(SEGUROS);
    }

    public void clickNegocioTransaccional() {
        click(NEGOCIO_TRANSACCIONAL);
    }

    public void clickInversiones() {
        click(INVERSIONES);
    }

    public void clickDivisasCoberturas() {
        click(DIVISAS_COBERTURAS);
    }

    public void clickNegocioInternacional() {
        click(NEGOCIO_INTERNACIONAL);
    }

    public void clickCreditos() {
        click(CREDITOS);
    }

    public void clickAlianzas() {
        click(ALIANZAS);
    }

    public void clickEcosistemaNoFinanciero() {
        click(ECOSISTEMA_NO_FINANCIERO);
    }

    public boolean isUrlCorrect(String value) {
        return waitForUrl(value);
    }
}