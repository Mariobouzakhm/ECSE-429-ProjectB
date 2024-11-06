import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class Utils {
    public static Process p = null;
    public static int status_code = 0;
    public static String errorMessage = null;
    public static Response responseValue = null;

    public static int mostRecentProcessedId = 0;
    public static int mostRecentProcessedId2 = 0;

    public static void main(String[] args) {
        startApplication();

        createTodo( "Mock Title", false, "Mock Description");

        int length = getLengthOfTodos();
        boolean isTrue = hasTodoWithTitle("Mock Title");

        stopApplication();
    }

    public static void startApplication() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 4567;

        Runtime runtime = Runtime.getRuntime();
        try {
            p = runtime.exec("java -jar runTodoManagerRestAPI-1.5.5.jar");
            Thread.sleep(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        given().when().get("/docs").then().assertThat().statusCode(200);
    }

    public static void checkApplicationWorking() {
        given().when().get("/docs").then().assertThat().statusCode(200);
    }

    public static void stopApplication() {
        try {
            p.destroy();
            Thread.sleep(500);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void createTodo(String title, boolean status, String description) {
        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");

        JSONObject paramsMap =  new JSONObject();
        paramsMap.put("title", title);
        paramsMap.put("description", description);
        paramsMap.put("doneStatus", status);

        request.body(paramsMap.toJSONString());

        Response response = request.post("/todos");
        Map<String, Object> responseMap = response.then().extract().jsonPath().getMap("$");

        status_code = response.statusCode();
        responseValue = response;

        System.out.println("Status Code: " + status_code);

        if(status_code == 400 || status_code == 404) {
            errorMessage = ((ArrayList<String>) responseMap.get("errorMessages")).get(0);
        }
        else {
            mostRecentProcessedId2 = mostRecentProcessedId;
            mostRecentProcessedId = Integer.valueOf((String) responseMap.get("id"));
        }
    }

    public static void modifyTodoDescription(int todoId, String new_description) {
        JSONObject todoMap = new JSONObject();
        todoMap.put("description", new_description);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(todoMap.toJSONString());

        Response response = request.post("/todos/"+String.valueOf(todoId));
        Map<String, Object> responseMap = response.then().extract().jsonPath().getMap("$");

        status_code = response.statusCode();
        responseValue = response;

        mostRecentProcessedId = todoId;

        System.out.println("Status Code: " + status_code);

        if(status_code == 404 || status_code == 400) {
            errorMessage = ((ArrayList<String>) responseMap.get("errorMessages")).get(0);
        }
    }

    public static void modifyTodoStatus(int todoId, boolean status) {
        JSONObject todoMap = new JSONObject();
        todoMap.put("doneStatus", status);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(todoMap.toJSONString());

        Response response = request.post("/todos/"+String.valueOf(todoId));
        Map<String, Object> responseMap = response.then().extract().jsonPath().getMap("$");

        status_code = response.statusCode();
        responseValue = response;

        mostRecentProcessedId = todoId;

        System.out.println("Status Code: " + status_code);
    }

    public static void modifyTodoStatus(int todoId, String status) {
        JSONObject todoMap = new JSONObject();
        todoMap.put("doneStatus", status);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(todoMap.toJSONString());

        Response response = request.post("/todos/"+String.valueOf(todoId));

        Map<String, Object> responseMap = response.then().extract().jsonPath().getMap("$");

        status_code = response.statusCode();
        responseValue = response;

        mostRecentProcessedId = todoId;
    }

    public static void addCategoryToTodo(int todoId, int categoryId) {
        JSONObject paramsMap = new JSONObject();
        paramsMap.put("id", String.valueOf(categoryId));

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(paramsMap.toJSONString());

        Response response = request.post("/todos/"+todoId+"/categories");

        status_code = response.statusCode();
        responseValue = response;

        mostRecentProcessedId = todoId;
        mostRecentProcessedId2 = categoryId;

        System.out.println("Status Code: " + status_code);
    }

    public static void removeCategoryTodo(int todoId, int categoryId) {
        Response response = given().when().delete("/todos/"+String.valueOf(todoId)+"/categories/"+String.valueOf(categoryId));
        status_code = response.statusCode();
        responseValue = response;

        mostRecentProcessedId = todoId;
        mostRecentProcessedId2 = categoryId;

        System.out.println("Status Code: " + status_code);
    }

    public static boolean hasTodoWithTitle(String title) {
        Map<String, Object> responseMap = given().when()
                .get("/todos").then().assertThat().statusCode(200)
                .extract().jsonPath().getMap("$");

        ArrayList<Object> list = (ArrayList) responseMap.get("todos");
        for(int i = 0; i < list.size(); i++) {
            LinkedHashMap<String, String> hashMap = (LinkedHashMap<String, String>) list.get(i);
            if(hashMap.get("title").equals(title)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hasCorrectNumCategories(int id, int expectedNumber) {
        Map<String, Object> responseMap = given().when().get("/todos/"+id+"/categories").then().assertThat()
                .statusCode(200).extract().jsonPath().getMap("$");

        ArrayList<LinkedHashMap<String, Object>> list = (ArrayList) responseMap.get("categories");

        mostRecentProcessedId = id;

        return list.size() == expectedNumber;
    }

    public static boolean hasCorrectStatus(int id, boolean status) {
        Map<String, Object> responseMap = given().when().get("/todos/"+String.valueOf(id)).then()
                .extract().jsonPath().getMap("$");
        ArrayList<Object> list = (ArrayList) responseMap.get("todos");
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) list.get(0);

        mostRecentProcessedId = id;

        return map.get("doneStatus").equals(String.valueOf(status));
    }

    public static boolean hasCorrectDescription(int id, String description) {
        Map<String, Object> responseMap = given().when().get("/todos/"+String.valueOf(id)).then()
                .extract().jsonPath().getMap("$");
        ArrayList<Object> list = (ArrayList) responseMap.get("todos");
        LinkedHashMap<String, String> map = (LinkedHashMap<String, String>) list.get(0);
        mostRecentProcessedId = id;

        return map.get("description").equals(description);
    }

    public static int getLengthOfTodos() {
        Map<String, Object> responseMap = given().when()
                .get("/todos").then().assertThat().statusCode(200)
                .extract().jsonPath().getMap("$");

        ArrayList<Object> list = (ArrayList) responseMap.get("todos");

        return list.size();
    }

    public static void createCategory(String title, String description) {
        JSONObject categoriesMap = new JSONObject();
        categoriesMap.put("title", title);
        categoriesMap.put("description", description);

        RequestSpecification request = RestAssured.given();
        request.header("Content-Type", "application/json");
        request.header("Accept", "application/json");
        request.body(categoriesMap.toJSONString());

        Response response = request.post("/categories");
        Map<String, Object> responseMap = response
                .then()
                .assertThat()
                .statusCode(201)
                .extract().jsonPath().getMap("$");

        status_code = response.statusCode();
        responseValue = response;

        System.out.println("Status Code: " + status_code);
    }
}
