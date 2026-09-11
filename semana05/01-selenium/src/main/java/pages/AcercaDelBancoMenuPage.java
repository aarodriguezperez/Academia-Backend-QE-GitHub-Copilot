package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AcercaDelBancoMenuPage extends BasePage {

    private static final By FUNDACION_SANTANDER =
            By.cssSelector("a[href*='/personas/acerca-del-banco/fundacion-santander.html']");

    private static final By RESPONSABILIDAD_SOCIAL =
            By.cssSelector("a[href*='/personas/acerca-del-banco/responsabilidad-social.html']");

    private static final By EDUCACION_FINANCIERA =
            By.cssSelector("a[href*='/educacion-financiera/']");

    private static final By INVERSIONISTAS =
            By.cssSelector("a[href*='/ir/home/']");

    private static final By SALA_COMUNICACION =
            By.cssSelector("a[href*='/ceb/sala_prensa_2026.html']");

    private static final By BOLSA_TRABAJO =
            By.cssSelector("a[href*='/personas/bolsa-de-trabajo.html']");

    private static final By BLOG =
            By.cssSelector("a[href*='/personas/blog.html']");

    public AcercaDelBancoMenuPage(WebDriver driver) {
        super(driver);
    }

    public void clickFundacionSantander() {
        click(FUNDACION_SANTANDER);
    }

    public void clickResponsabilidadSocial() {
        click(RESPONSABILIDAD_SOCIAL);
    }

    public void clickEducacionFinanciera() {
        click(EDUCACION_FINANCIERA);
    }

    public void clickInversionistas() {
        click(INVERSIONISTAS);
    }

    public void clickSalaComunicacion() {
        click(SALA_COMUNICACION);
    }

    public void clickBolsaTrabajo() {
        click(BOLSA_TRABAJO);
    }

    public void clickBlog() {
        click(BLOG);
    }

    public boolean isUrlCorrect(String value) {
        return waitForUrl(value);
    }
}