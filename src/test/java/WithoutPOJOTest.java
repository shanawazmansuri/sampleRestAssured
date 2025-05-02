import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class WithoutPOJOTest {

    public static void main(String[] args) {
        RestAssured.baseURI = "https://reqres.in/api";

        // 1. GET request to list users
        System.out.println("GET - List Users:");
        given()
                .queryParam("page", 2)
                .when()
                .get("/users")
                .then()
                .statusCode(200)
                .body("data", notNullValue());

        // 2. POST request to create a new user
        System.out.println("POST - Create User:");
        String newUser = "{ \"name\": \"John\", \"job\": \"developer\" }";

        Response postResponse = given()
                .contentType(ContentType.JSON)
                .body(newUser)
                .when()
                .post("/users");

        postResponse.then()
                .statusCode(201)
                .body("name", equalTo("John"))
                .body("job", equalTo("developer"));

        String userId = postResponse.jsonPath().getString("id");

        // 3. PUT request to update the user
        System.out.println("PUT - Update User:");
        String updatedUser = "{ \"name\": \"John\", \"job\": \"senior developer\" }";

        given()
                .contentType(ContentType.JSON)
                .body(updatedUser)
                .when()
                .put("/users/" + userId)
                .then()
                .statusCode(200)
                .body("job", equalTo("senior developer"));

        // 4. DELETE request to delete the user
        System.out.println("DELETE - Delete User:");
        when()
                .delete("/users/" + userId)
                .then()
                .statusCode(204);

        System.out.println("Test completed.");
    }
}
