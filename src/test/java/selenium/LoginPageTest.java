package selenium;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPageTest extends BaseTest {

    @Test
    public void testLoginPageTitle() {
        webDriver.get("http://localhost:9091/login");
        verifyPageTitle("Login");
    }

    @Test
    public void testLoginFormSubmission() {
        webDriver.get("http://localhost:9091/login");

        WebElement usernameField = webDriver.findElement(By.id("username"));
        WebElement passwordField = webDriver.findElement(By.id("password"));
        WebElement loginButton = webDriver.findElement(By.cssSelector("button[type='submit']"));


        usernameField.sendKeys("rinor");
        passwordField.sendKeys("rinor");
        loginButton.click();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe("http://localhost:9091/"));

        Assert.assertEquals("http://localhost:9091/", webDriver.getCurrentUrl());
    }
}
