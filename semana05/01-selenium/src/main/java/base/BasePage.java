package base;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BasePage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final Actions actions;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        this.actions = new Actions(driver);
    }

    protected WebElement find(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

//    protected void click(By locator) {
//        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
//        ((JavascriptExecutor) driver)
//                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
//        element.click();
//    }

    protected void click(By locator) {
        WebElement element =
                wait.until(ExpectedConditions.elementToBeClickable(locator));

        element.click();
    }

    protected String getText(By locator) {
        return find(locator).getText();
    }

    protected boolean waitForUrl(String value) {
        return wait.until(
                ExpectedConditions.urlContains(value)
        );
    }

}
