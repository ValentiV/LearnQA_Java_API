package HomeWork;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class Ex5Test {

    @Test
    public void test() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        Map <String, List<Map <String, String>>> messages = response.get();
        String message2 = messages.get("messages").get(1).get("message");
        System.out.println(message2);
    }
}
