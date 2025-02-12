package selenium;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.Assert.*;

public class SurveyIndexPageTest extends BaseTest {

    @Test
    public void testPageTitle() {
        verifyPageTitle("SurveyKing");
    }

    @Test
    public void testSurveyTableDisplayed() {
        WebElement table = webDriver.findElement(By.className("table-responsive"));
        assertTrue("Survey table should be visible", table.isDisplayed());

        List<WebElement> rows = webDriver.findElements(By.xpath("//table/tbody/tr"));
        assertFalse("Survey table should have at least one row", rows.isEmpty());
    }

    @Test
    public void testSearchFunctionality() {
        WebElement searchInput = webDriver.findElement(By.name("query"));
        WebElement searchButton = webDriver.findElement(By.xpath("//button[contains(text(), 'Search')]"));

        searchInput.sendKeys("Test Survey");
        searchButton.click();

        List<WebElement> rows = webDriver.findElements(By.xpath("//table/tbody/tr"));
        assertFalse("Search should return at least one result", rows.isEmpty());
    }

    @Test
    public void testSortFunctionality() {
        WebElement sortDropdown = webDriver.findElement(By.id("sort"));
        Select select = new Select(sortDropdown);
        select.selectByValue("likesDesc");
        List<WebElement> firstRowLikes = webDriver.findElements(By.xpath("//table/tbody/tr[1]/td[5]"));
        assertFalse("Sorting should update the survey list", firstRowLikes.isEmpty());
    }

    @Test
    public void testLikeAndDislikeToggle() {
        webDriver.get("http://localhost:9091/");

        WebElement table = webDriver.findElement(By.className("table-responsive"));
        assertTrue("Survey table should be visible", table.isDisplayed());

        WebElement likeButton = webDriver.findElement(By.xpath("//table/tbody/tr[1]//form[contains(@action, '/like')]/button"));

        WebElement likesCountElement = webDriver.findElement(By.xpath("//table/tbody/tr[1]/td[5]"));
        int initialLikesCount = Integer.parseInt(likesCountElement.getText());

        String initialLikeButtonText = likeButton.getText();
        assertTrue("The initial button text should be 'Like' or 'Dislike'",
                initialLikeButtonText.equals("Like") || initialLikeButtonText.equals("Dislike"));

        likeButton.click();

        WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(By.xpath("//table/tbody/tr[1]/td[5]"), String.valueOf(initialLikesCount))));

        WebElement updatedLikesCountElement = webDriver.findElement(By.xpath("//table/tbody/tr[1]/td[5]"));
        int updatedLikesCount = Integer.parseInt(updatedLikesCountElement.getText());

        if (initialLikeButtonText.equals("Like")) {
            assertEquals("Likes count should have increased by 1", initialLikesCount + 1, updatedLikesCount);
        } else {
            assertEquals("Likes count should have decreased by 1", initialLikesCount - 1, updatedLikesCount);
        }

        WebElement updatedLikeButton = webDriver.findElement(By.xpath("//table/tbody/tr[1]//form[contains(@action, '/like')]/button"));

        updatedLikeButton.click();

        wait.until(ExpectedConditions.textToBe(By.xpath("//table/tbody/tr[1]/td[5]"), String.valueOf(initialLikesCount)));

        WebElement finalLikesCountElement = webDriver.findElement(By.xpath("//table/tbody/tr[1]/td[5]"));
        int finalLikesCount = Integer.parseInt(finalLikesCountElement.getText());

        assertEquals("Likes count should have changed back to the initial state", initialLikesCount, finalLikesCount);
    }
}