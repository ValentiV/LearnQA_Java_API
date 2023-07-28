package HomeWork;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class Ex11Test {

    @Test
    public void test() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();
        Map<String, String> cookies = response.getCookies();
        Assertions.assertTrue(cookies.containsKey("HomeWork"), "Response does not contains cookie 'HomeWork'!");
        String valueCookieHomeWork =  response.getCookie("HomeWork");
        Assertions.assertEquals("hw_value", valueCookieHomeWork, "Value is incorrect!");
    }
}