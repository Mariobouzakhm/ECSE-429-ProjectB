import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StepDefinitions {
    private int mostRecentTodoId;
    private int mostRecentCategoryId;

    private int mostRecentTodoId2;
    private int mostRecentCategoryId2;

    private int mostRecentStatusCode;
    private String mostRecentErrorMessage;

    private int mostRecentStatusCode2;
    private String mostRecentErrorMessage2;

    @Given("^The application is running$")
    public void test_application_is_running() {
        Utils.checkApplicationWorking();

        mostRecentStatusCode = 0;
        mostRecentStatusCode2 = 0;
        mostRecentCategoryId = 0;
        mostRecentCategoryId2 = 0;
        mostRecentTodoId = 0;
        mostRecentTodoId2 = 0;
        mostRecentErrorMessage = "";
        mostRecentErrorMessage2 = "";
    }

    @Given("^a todo with non-existing description \\\"([^\\\"]*)\\\"$")
    public void mock_create_todo_with_desc(String description) {
        mostRecentTodoId = 0;
    }

    @Given("^a todo with description \\\"([^\\\"]*)\\\"$")
    public void create_todo_with_desc(String description) {
        Utils.createTodo("Mock Title", false, description);

        mostRecentTodoId = Utils.mostRecentProcessedId;
    }
    @Given("^todo with title \\\"([^\\\"]*)\\\"$")
    public void create_todo_with_title(String title) {
        Utils.createTodo(title, false, "Mock Description");
        mostRecentTodoId = Utils.mostRecentProcessedId;
    }
    @Given("^category with title \\\"([^\\\"]*)\\\" and description \\\"([^\\\"]*)\\\" created")
    public void create_category(String title, String description) {
        Utils.createCategory(title, description);
        mostRecentTodoId = Utils.mostRecentProcessedId;
    }
    @Given("^category with id \\\"([^\\\"]*)\\\" is associated with todo with id \\\"([^\\\"]*)\\\"$")
    public void associate_category_with_todo(String categoryId, String id) {
        Utils.addCategoryToTodo(Integer.valueOf(categoryId), Integer.valueOf(id));

        mostRecentTodoId = Utils.mostRecentProcessedId;
        mostRecentCategoryId = Utils.mostRecentProcessedId2;
    }
    @Given("^todo with title \\\"([^\\\"]*)\\\" and status \\\"([^\\\"]*)\\\"$")
    public void create_todo_with_status(String title, String status) {
        Utils.createTodo(title, Boolean.valueOf(status), "Mock Description");

        mostRecentTodoId = Utils.mostRecentProcessedId;
        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }

    @When("^I create a new todo list item called \\\"([^\\\"]*)\\\", with completed status \\\"([^\\\"]*)\\\", and description \\\"([^\\\"]*)\\\"$")
    public void create_todo(String title, String status, String description) {
        Utils.createTodo(title, Boolean.valueOf(status), description);

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }
    @When("^I change the description of the todo to \\\"([^\\\"]*)\\\"$")
    public void change_todo_description(String new_description) {
        Utils.modifyTodoDescription(mostRecentTodoId, new_description);

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }
    @When("^I want to add a category with id \\\"([^\\\"]*)\\\" to the todo with id \\\"([^\\\"]*)\\\"$")
    public void add_category_to_given_todo(String categoryId, String id) {
        int catId = Integer.valueOf(categoryId);
        int todoId = Integer.valueOf(id);

        Utils.addCategoryToTodo(todoId, catId);

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }
    @When("^I want to remove a category with id \\\"([^\\\"]*)\\\" to the todo with id \\\"([^\\\"]*)\\\"$")
    public void remove_category_to_given_todo(String categoryId, String id) {
        int catId = Integer.valueOf(categoryId);
        int todoId = Integer.valueOf(id);

        Utils.removeCategoryTodo(todoId, catId);

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }
    @When("^I want to add a category with id \\\"([^\\\"]*)\\\" to the todo with non existing id \\\"([^\\\"]*)\\\"")
    public void add_category_to_missing_todo(String categoryId, String id) {
        int catId = Integer.valueOf(categoryId);
        int todoId = Integer.valueOf(id);

        Utils.addCategoryToTodo(todoId, catId);

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }
    @When("^I want to remove a category with id \\\"([^\\\"]*)\\\" to the todo with non existing id \\\"([^\\\"]*)\\\"")
    public void remove_category_to_missing_todo(String categoryId, String id) {
        int catId = Integer.valueOf(categoryId);
        int todoId = Integer.valueOf(id);

        Utils.removeCategoryTodo(todoId, catId);

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }
    @When("^I want to change the status of todo to \\\"([^\\\"]*)\\\"$")
    public void change_todo_status(String status) {
        Utils.modifyTodoStatus(mostRecentTodoId, Boolean.valueOf(status));

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }

    @When("^I want to wrongly change the status of todo to \\\"([^\\\"]*)\\\"$")
    public void wrongly_change_todo_status(String status) {
        Utils.modifyTodoStatus(mostRecentTodoId, status);

        mostRecentStatusCode = Utils.status_code;
        mostRecentErrorMessage = Utils.errorMessage;
    }

    @Then("^returned status code is \\\"([^\\\"]*)\\\"$")
    public void checkStatusCode(String statusCode) {
        Assert.assertEquals(mostRecentStatusCode, (int) Integer.valueOf(statusCode));
    }
    @Then("^todo with title \\\"([^\\\"]*)\\\" is created$")
    public void check_todo_with_title(String title) {
        assertTrue(Utils.hasTodoWithTitle(title));
    }
    @Then("^error message \\\"([^\\\"]*)\\\" is returned$")
    public void check_error_message(String error_message) {
        assertEquals(mostRecentErrorMessage, error_message);
    }
    @Then("^the new description of the todo is \\\"([^\\\"]*)\\\"$")
    public void check_new_todo_description(String final_description) {
        assertTrue(Utils.hasCorrectDescription(mostRecentTodoId, final_description));
    }
    @Then("^number of categories associated with todo is \\\"([^\\\"]*)\\\"$")
    public void check_numb_categories_todo(String number) {
        assertTrue(Utils.hasCorrectNumCategories(mostRecentTodoId, Integer.valueOf(number)));
    }
    @Then("^the new status of todo is \\\"([^\\\"]*)\\\"$")
    public void check_todo_status(String status) {
        assertTrue(Utils.hasCorrectStatus(mostRecentTodoId ,Boolean.valueOf(status)));
    }
}