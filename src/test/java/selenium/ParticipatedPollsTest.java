//package selenium;
//
//import org.junit.Test;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.openqa.selenium.support.ui.WebDriverWait;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import static org.springframework.test.util.AssertionErrors.assertTrue;
//
//public class ParticipatedPollsTest extends BaseTest {
//    @Test
//    public void testParticipatedPoll() {
//        // Step 1: Create a new poll (similar to your existing create poll test)
//        webDriver.get("http://localhost:9091/poll/create");
//
//        // Fill out the poll form
//        WebElement surveyNameInput = webDriver.findElement(By.id("name"));
//        surveyNameInput.sendKeys("testPoll");
//
//        WebElement firstQuestionInput = webDriver.findElement(By.name("questions[0].text"));
//        firstQuestionInput.sendKeys("What is your favorite color?");
//
//        WebElement firstOption = webDriver.findElement(By.name("questions[0].options[0].description"));
//        firstOption.sendKeys("Red");
//
//        WebElement secondOption = webDriver.findElement(By.name("questions[0].options[1].description"));
//        secondOption.sendKeys("Blue");
//
//        WebElement submitButton = webDriver.findElement(By.cssSelector("button.btn-success"));
//        submitButton.click();
//
//        // Step 2: Participate in the poll
//        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("poll-list")));
//
//        // Assuming there's a link or button to participate in the poll
//        WebElement participateButton = webDriver.findElement(By.xpath("//button[contains(text(),'Participate')]"));
//        participateButton.click();
//
//        // Select an option (e.g., "Red" in the poll)
//        WebElement option = webDriver.findElement(By.xpath("//label[contains(text(),'Red')]"));
//        option.click();
//
//        // Submit the answer
//        WebElement submitPollButton = webDriver.findElement(By.cssSelector("button.btn-submit"));
//        submitPollButton.click();
//
//        // Step 3: Navigate to the participated polls section
//        webDriver.get("http://localhost:9091/poll/participated");
//
//        // Step 4: Verify that the poll appears in the participated polls list
//        wait.until(ExpectedConditions.presenceOfElementLocated(By.className("table-responsive")));
//
//        WebElement table = webDriver.findElement(By.className("table-responsive"));
//        List<WebElement> rows = table.findElements(By.tagName("tr"));
//
//        boolean participatedPollExists = rows.stream()
//                .anyMatch(row -> row.getText().contains("testPoll"));
//
//        assertTrue("Participated poll should appear in the table", participatedPollExists);
//    }
//
//
//    @Test
//    public void testViewEditAndDeleteSubmission() {
//        // Navigate to the participated polls page
//        webDriver.get("http://localhost:9091/participated-polls");
//
//        // Verify page title
//        verifyPageTitle("Participated Polls");
//
//        // Find all participated polls
//        List<WebElement> polls = webDriver.findElements(By.className("list-group-item"));
//        if (polls.isEmpty()) {
//            System.out.println("No participated surveys found.");
//            return;
//        }
//
//        // Select the first poll
//        WebElement firstPoll = polls.get(0);
//
//        // 1️⃣ View Submission
//        WebElement viewButton = firstPoll.findElement(By.xpath(".//a[contains(@class, 'btn-warning')]"));
//        viewButton.click();
//        verifyPageTitle("My Submission");  // Adjust if the page title differs
//        System.out.println("✅ Viewed submission successfully.");
//        webDriver.navigate().back();
//
//        // Wait to prevent stale element issue
//        webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//
//        // 2️⃣ Edit Submission
//        firstPoll = webDriver.findElements(By.className("list-group-item")).get(0); // Re-fetch element
//        WebElement editButton = firstPoll.findElement(By.xpath(".//a[contains(@class, 'btn-info')]"));
//        editButton.click();
//        verifyPageTitle("Edit Poll"); // Adjust if necessary
//
//        // Example: Modify a text field (adjust selector as needed)
//        WebElement textField = webDriver.findElement(By.name("question_text"));
//        textField.clear();
//        textField.sendKeys("Updated Question Text");
//
//        WebElement saveButton = webDriver.findElement(By.xpath("//button[contains(text(), 'Save')]"));
//        saveButton.click();
//
//        System.out.println("✅ Edited submission successfully.");
//        webDriver.navigate().back();
//
//        // Wait before deleting
//        webDriver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//
//        // 3️⃣ Delete Submission
//        firstPoll = webDriver.findElements(By.className("list-group-item")).get(0); // Re-fetch again
//        WebElement deleteButton = firstPoll.findElement(By.xpath(".//button[contains(@class, 'btn-danger')]"));
//        deleteButton.click();
//
//        // Confirm deletion
//        webDriver.switchTo().alert().accept();
//
//        System.out.println("✅ Deleted submission successfully.");
//    }
//
//}