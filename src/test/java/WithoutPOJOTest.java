import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class WithoutPOJOTest {

    private String userId;
    private final String API_KEY = "reqres-free-v1"; // No newline
    private final String BASE_PATH = "/users";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "https://reqres.in/api";
    }

    // Helper method to create headers
    private RequestSpecification commonRequest() {
        return given()
                .contentType(ContentType.JSON)
                .header("x-api-key", API_KEY);
    }

    private String createUserPayload(String name, String job) {
        return String.format("{ \"name\": \"%s\", \"job\": \"%s\" }", name, job);
    }

    @Test
    public void getListUsers() {
        System.out.println("GET - List Users:-");
        given()
                .queryParam("page", 2)
                .when()
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .body("data", notNullValue());
    }

    @Test(dependsOnMethods = "getListUsers")
    public void createNewUser() {
        System.out.println("POST - Create User:-");

        String newUser = createUserPayload("John", "developer");

        Response response = commonRequest()
                .body(newUser)
                .when()
                .post(BASE_PATH);

        response.then()
                .statusCode(201)
                .body("id", notNullValue()); // Remove 'name' and 'job' assertions — not returned

        userId = response.jsonPath().getString("id");
    }

    @Test(dependsOnMethods = "createNewUser")
    public void updateUser() {
        System.out.println("PUT - Update User:-");

        String updatedUser = createUserPayload("John", "senior developer");

        commonRequest()
                .body(updatedUser)
                .when()
                .put(BASE_PATH + "/" + userId)
                .then()
                .statusCode(200)
                .body("job", equalTo("senior developer"));
    }

    @Test(dependsOnMethods = "updateUser")
    public void deleteUser() {
        System.out.println("DELETE - Delete User:-");

        commonRequest()
                .when()
                .delete(BASE_PATH + "/" + userId)
                .then()
                .statusCode(204);

        System.out.println("User deleted successfully.");
    }
}