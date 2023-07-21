import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class Ex8Test {

    @Test
    public void test() throws InterruptedException {
        JsonPath response;
        JsonPath responseCreateTask = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String token = responseCreateTask.get("token");
        int timeout = responseCreateTask.get("seconds");

        response = getRequest(token);

        HashMap<String, String> message = response.get();
        if (message.containsKey("error")) {
            Assertions.assertEquals("No job linked to this token", message.get("error"));
            Assertions.fail("Token is invalid!");
        }
        if (message.containsKey("status")) {
            Assertions.assertEquals("Job is NOT ready", message.get("status"));

            Thread.sleep(timeout * 1000);

            response = getRequest(token);
            message = response.get();
            Assertions.assertEquals("Job is ready", message.get("status"));
            Assertions.assertTrue(message.containsKey("result"));
        } else {
            Assertions.fail("Answer is invalid!");
        }
    }

    public JsonPath getRequest (String token) {
        return RestAssured
                .given()
                .queryParam("token", token)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
    }
}
