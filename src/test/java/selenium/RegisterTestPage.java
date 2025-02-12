package selenium;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class RegisterTestPage extends BaseTest {

    @Test
    public void testRegistrationAndLogin() {
        webDriver.get("http://localhost:9091/register");

        WebElement usernameField = webDriver.findElement(By.id("username"));
        WebElement nameField = webDriver.findElement(By.id("name"));
        WebElement surnameField = webDriver.findElement(By.id("surname"));
        WebElement passwordField = webDriver.findElement(By.id("password"));
        WebElement confirmPasswordField = webDriver.findElement(By.id("confirmPassword"));
        WebElement registerButton = webDriver.findElement(By.cssSelector("button[type='submit']"));

        String username = "testuser_" + System.currentTimeMillis(); 
        String name = "Test";
        String surname = "User";
        String password = "testpassword";

        usernameField.sendKeys(username);
        nameField.sendKeys(name);
        surnameField.sendKeys(surname);
        passwordField.sendKeys(password);
        confirmPasswordField.sendKeys(password); 
        registerButton.click();
        
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlToBe("http://localhost:9091/login"));
        
        Assert.assertEquals("http://localhost:9091/login", webDriver.getCurrentUrl());
        
        WebElement loginUsernameField = webDriver.findElement(By.id("username"));
        WebElement loginPasswordField = webDriver.findElement(By.id("password"));
        WebElement loginButton = webDriver.findElement(By.cssSelector("button[type='submit']"));

        loginUsernameField.sendKeys(username);
        loginPasswordField.sendKeys(password);
        loginButton.click();
        
        wait.until(ExpectedConditions.urlToBe("http://localhost:9091/"));
        
        Assert.assertEquals("http://localhost:9091/", webDriver.getCurrentUrl());
        
        WebElement logoutLink = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(text(), 'Logout')]")));
        Assert.assertTrue(logoutLink.isDisplayed());
        
        logoutLink.click();
        wait.until(ExpectedConditions.urlToBe("http://localhost:9091/login?logout"));
        Assert.assertEquals("http://localhost:9091/login?logout", webDriver.getCurrentUrl());
    }
}