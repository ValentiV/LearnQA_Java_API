package HomeWork;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Ex12Test {

    @Test
    public void test() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();
        String headers = response.getHeaders().toString();
        Assertions.assertTrue(headers.contains("x-secret-homework-header"),
                "Response does not contains header 'x-secret-homework-header'!");
        String valueHeadersSecret = response.getHeader("x-secret-homework-header");
        Assertions.assertEquals("Some secret value", valueHeadersSecret, "Value is incorrect!");
    }
}