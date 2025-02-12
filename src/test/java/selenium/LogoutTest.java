package selenium;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.junit.Test;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LogoutTest extends BaseTest {

    @Test
    public void testLogoutFunctionality() {
        WebElement logoutButton = webDriver.findElement(By.linkText("Logout"));
        logoutButton.click();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe("http://localhost:9091/login?logout"));

        Assert.assertEquals("http://localhost:9091/login?logout", webDriver.getCurrentUrl());
    }


}
