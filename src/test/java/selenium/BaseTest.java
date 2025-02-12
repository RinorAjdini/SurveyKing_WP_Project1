package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public abstract class BaseTest {
    protected WebDriver webDriver;

    @Before
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        webDriver = new ChromeDriver();
        webDriver.manage().window().maximize();
        webDriver.get("http://localhost:9091/login"); // Go directly to login page

        performLogin("rinor", "rinor"); // Change to valid credentials
    }

    @After
    public void tearDown() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
    protected void verifyPageTitle(String expectedTitle) {
        Assert.assertEquals(expectedTitle, webDriver.getTitle());
    }

    private void performLogin(String username, String password) {
        try {
            webDriver.findElement(By.name("username")).sendKeys(username);
            webDriver.findElement(By.name("password")).sendKeys(password);
            webDriver.findElement(By.xpath("//button[contains(text(), 'Login')]")).click();
        } catch (Exception e) {
            System.out.println("Already logged in or login elements not found.");
        }
    }
}
