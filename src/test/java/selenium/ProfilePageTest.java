package selenium;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class ProfilePageTest extends BaseTest {

    @Test
    public void testProfilePage() {
        webDriver.get("http://localhost:9091/profile");

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

        verifyPageTitle("Profile");

        WebElement userName = webDriver.findElement(By.xpath("//h3"));
        assert userName.getText().contains("Hello, ");

        WebElement profilePic = webDriver.findElement(By.tagName("img"));
        String imgSrc = profilePic.getAttribute("src");
        System.out.println("Profile Picture Source: " + imgSrc);
        assert imgSrc.contains("/images/admin.jpg") || imgSrc.contains("/images/default_user.jpg");

        WebElement nameInput = webDriver.findElement(By.id("name"));
        nameInput.clear();
        nameInput.sendKeys("Marija");

        WebElement surnameInput = webDriver.findElement(By.id("surname"));
        surnameInput.clear();
        surnameInput.sendKeys("Vrzhovska");

        webDriver.findElement(By.xpath("//button[contains(text(), 'Update Profile')]")).click();

        System.out.println("Profile updated successfully!");

        WebElement newPassword = webDriver.findElement(By.id("newPassword"));
        WebElement confirmPassword = webDriver.findElement(By.id("confirmPassword"));
        newPassword.sendKeys("rinor");
        confirmPassword.sendKeys("rinor");

        webDriver.findElement(By.xpath("//button[contains(text(), 'Change Password')]")).click();

        System.out.println("Password changed successfully!");
    }
}
