package HomeWork;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex7Test {

    @Test
    public void test() {
        Response response;
        int statusCode;
        int countRedirect = 0;
        String location = "https://playground.learnqa.ru/api/long_redirect";
        while (true) {
            response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(location)
                    .andReturn();
            location = response.getHeader("location");
            statusCode = response.getStatusCode();
            if (statusCode == 200)
                break;
            countRedirect++;
            System.out.println("statusCode = " + statusCode + ", newURL = " + location);
        }
        System.out.println(countRedirect);
    }
}
