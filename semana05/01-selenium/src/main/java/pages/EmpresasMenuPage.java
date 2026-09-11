package pages;

import base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EmpresasMenuPage extends BasePage {

    private static final By EMPRESAS_GOBIERNO =
            By.cssSelector("a[href*='/bei/home.html']");

    private static final By MULTINACIONALES =
            By.cssSelector("a[href*='/personas/acerca-del-banco/multinacionales.html']");

    public EmpresasMenuPage(WebDriver driver) {
        super(driver);
    }

    public void clickEmpresasGobierno() {
        click(EMPRESAS_GOBIERNO);
    }

    public void clickMultinacionales() {
        click(MULTINACIONALES);
    }

    public boolean isUrlCorrect(String value) {
        return waitForUrl(value);
    }
}
