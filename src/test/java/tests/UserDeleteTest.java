package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Delete user cases")
@Feature("Delete user")
public class UserDeleteTest extends BaseTestCase {

    ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String header, cookie;
    Map<String, String> authData = new HashMap<>();

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Positive case")
    @Description("This test check delete permanent user (delete prohibited)")
    @DisplayName("Test positive delete permanent user")
    @Issue("Ex19")
    public void testDeletePermanentUser() {
        //LOGIN
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        login(authData);

        //DELETE
        Response responseDelete = apiCoreRequests.makeDeleteRequest("https://playground.learnqa.ru/api/user/2",
                header, cookie);

        Assertions.assertResponseCodeEquals(responseDelete, 400);
        Assertions.assertResponseTextEquals(responseDelete, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //GET INFORMATION
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/2",
                header, cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 200);
    }

    @Test
    @Severity(SeverityLevel.CRITICAL)
    @Story("Positive case")
    @Description("This test check delete user")
    @DisplayName("Test positive delete user")
    @Issue("Ex19")
    public void testDeleteUser() {
        //CREATE
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreteAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);

        String userId = responseCreteAuth.jsonPath().getString("id");

        //LOGIN
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        login(authData);

        //DELETE
        Response responseDelete = apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseDelete, 200);

        //GET INFORMATION
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId, header, cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 404);
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Story("Negative case")
    @Description("This test check delete other user with authorization")
    @DisplayName("Test negative delete other user")
    @Issue("Ex19")
    public void testDeleteOtherUser() {
        //CREATE
        Map<String, String> userData1 = DataGenerator.getRegistrationData();

        Response responseCreteAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData1);
        String userId1 = responseCreteAuth.jsonPath().getString("id");

        Map<String, String> userData2 = DataGenerator.getRegistrationData();
        responseCreteAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData2);
        String userId2 = responseCreteAuth.jsonPath().getString("id");

        //LOGIN
        authData.put("email", userData1.get("email"));
        authData.put("password", userData1.get("password"));

        login(authData);

        //DELETE
        apiCoreRequests.makeDeleteRequest(
                "https://playground.learnqa.ru/api/user/" + userId2, header, cookie);

        //GET INFORMATION
        Response responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId1, header, cookie);

        // Тест упадет, т.к. будучи авторизованным под пользователем1 пытаемся elfkbnm пользователя2,
        // при этом удаляется сам пользователь1, что вызывает сомнения
        // Ожидается, что должна быть проверка на принадлежность userId тому пользователю, который удаляет
        Assertions.assertResponseCodeEquals(responseUserData, 200);

        responseUserData = apiCoreRequests.makeGetRequest(
                "https://playground.learnqa.ru/api/user/" + userId2, header, cookie);

        Assertions.assertResponseCodeEquals(responseUserData, 200);
    }

    @Step("Login user")
    private void login(Map<String, String> authData) {
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",
                authData);

        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
    }
}
