package server;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServerTest {
    private Server server;
    private WebDriver driver;
    private WebElement todoInput;

    // You may not have created a list before the way this code does.
    // This is a shortcut to creating a list and calling add() multiple times to add the items.
    private final List<String> todoTestList = Arrays.asList(
            "Learn Selenium",
            "Understanding and modifying an existing codebase",
            "Static analysis with IntelliJ Static Analyzer",
            "Code profiling with YourKit",
            "Dependency Management Tools: Maven and Gradle",
            "Unit Testing with JUnit");

    @BeforeEach
    void setUp() throws IOException {
        server = new Server();
        server.start();
        System.setProperty("webdriver.chrome.driver","/Users/ethan.kerr/Downloads/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:" + server.getPort());

        todoInput = driver.findElement(By.tagName("input"));

        for (String todo : todoTestList) {
            submitTodo(todo);
        }
    }

    @AfterEach
    void teardown() {

        try{
            Thread.sleep(3000);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        driver.quit();
        server.stop();
    }

    private void submitTodo(String todoText) {
        todoInput.sendKeys(todoText);
        todoInput.submit();

        try{
            Thread.sleep(10);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        todoInput.clear();
    }

    @Test
    void testAddTodos() {
        List<WebElement> foundTodoElements = driver.findElements(By.className("todo-text"));
        Assertions.assertEquals(todoTestList.size(), foundTodoElements.size());

        List<String> foundTodoStrings = getTextForElements(foundTodoElements);
    }

    @Test
    void testMarkAndShowCompleted() {
        List<String> expectedCompletedItems = Arrays.asList("Learn Selenium", "Code profiling with YourKit");

        for(String completedItem : expectedCompletedItems){
            WebElement completedItemCheckbox = findCheckboxForTodoItemByText(completedItem);
            Assertions.assertNotNull(completedItemCheckbox);
            completedItemCheckbox.click();
        }

        WebElement showCompletedButton = driver.findElement(By.id("show-completed"));
        Assertions.assertNotNull(showCompletedButton);
        showCompletedButton.click();

        List<WebElement> displayedTodoElements = driver.findElements(By.className("todo-text"));
        Assertions.assertEquals(expectedCompletedItems.size(), displayedTodoElements.size());

        List<String> displayedElementText = getTextForElements(displayedTodoElements);
        for(String expectedCompletedItem : expectedCompletedItems){
            Assertions.assertTrue(displayedElementText.contains(expectedCompletedItem),
                    "Expected item " + expectedCompletedItem + " not displayed");
        }
    }

    @Test
    void testDeleteCompleted() {
        List<String> expectedCompletedItems = todoTestList;

        for(String completedItem : expectedCompletedItems){
            WebElement completedItemCheckbox = findCheckboxForTodoItemByText(completedItem);
            Assertions.assertNotNull(completedItemCheckbox);
            completedItemCheckbox.click();
        }

        WebElement deleteButton = driver.findElement(By.id("delete-completed"));
        Assertions.assertNotNull(deleteButton);
        deleteButton.click();

        // List shouldn't exist
        List<WebElement> displayedTodos = driver.findElements(By.className("todo-text"));
        Assertions.assertEquals(displayedTodos.size(), 0);


    }

    private WebElement findCheckboxForTodoItemByText(String todoItemText){
        String xpathExpression = String.format("//*[contains(text(),'%s')]/parent::li",
                todoItemText);
        WebElement liElement = driver.findElement(By.xpath(xpathExpression));
        return liElement.findElement(By.className("completed-checkbox"));
    }


    private List<String> getTextForElements(List<WebElement> webElements){
        List<String> strings = new ArrayList<>(webElements.size());

        for(WebElement webElement : webElements){
            strings.add(webElement.getText());
        }

        return strings;
    }
}