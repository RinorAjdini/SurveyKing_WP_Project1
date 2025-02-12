package selenium;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.*;

public class CreatePollPageTest extends BaseTest {
        private final String  surveyName = "testPoll";

        @Test
        public void testCreatePoll() {
            webDriver.get("http://localhost:9091/poll/create");

            WebElement surveyNameInput = webDriver.findElement(By.id("name"));
            surveyNameInput.sendKeys(surveyName);

            WebElement firstQuestionInput = webDriver.findElement(By.name("questions[0].text"));
            firstQuestionInput.sendKeys("test?");

            WebElement firstOption = webDriver.findElement(By.name("questions[0].options[0].description"));
            firstOption.sendKeys("test1");

            WebElement secondOption = webDriver.findElement(By.name("questions[0].options[1].description"));
            secondOption.sendKeys("test2");

            WebElement submitButton = webDriver.findElement(By.cssSelector("button.btn-success"));
            submitButton.click();

            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("table-responsive")));

            WebElement table = webDriver.findElement(By.className("table-responsive"));
            List<WebElement> rows = table.findElements(By.tagName("tr"));

            boolean surveyExists = rows.stream()
                    .anyMatch(row -> row.getText().contains(surveyName));

            assertTrue("Survey should appear in the table", surveyExists);
        }

    @Test
    public void testDeletePoll() {
        // Navigate to the page with the list of polls
        webDriver.get("http://localhost:9091/");

        // Wait until the table is present
        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("table-responsive")));

        // Find the table containing the polls
        WebElement table = webDriver.findElement(By.className("table-responsive"));
        List<WebElement> rows = table.findElements(By.tagName("tr"));

        // Find the row corresponding to the survey you created
        WebElement surveyRow = rows.stream()
                .filter(row -> row.getText().contains(surveyName))  // Look for the row with the specific survey name
                .findFirst()
                .orElse(null);

        // Assert that the survey is present in the table
        assertNotNull("Survey should be in the table before deletion", surveyRow);

        // Find the delete button associated with this poll
        WebElement deleteButton = surveyRow.findElement(By.cssSelector("form button.btn-danger"));
        wait.until(ExpectedConditions.elementToBeClickable(deleteButton));

        // Click the delete button to trigger the deletion
        deleteButton.click();

        // Wait for the confirmation alert and accept it
        wait.until(ExpectedConditions.alertIsPresent());
        webDriver.switchTo().alert().accept();

        // Wait until the page reloads and the table is refreshed
        wait.until(ExpectedConditions.stalenessOf(surveyRow));

        // Refresh the table reference after deletion
        table = webDriver.findElement(By.className("table-responsive"));
        rows = table.findElements(By.tagName("tr"));

        // After deletion, verify the survey row is no longer present in the table
        boolean surveyDeleted = rows.stream()
                .noneMatch(row -> row.getText().contains(surveyName)); // Ensure the survey is not present

        assertTrue("Survey should be deleted", surveyDeleted);
    }

}



