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

@Epic("Get user data cases")
@Feature("Get user data")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String cookie, headers;

    @Test
    @Story("Negative case")
    @Description("This test check get user data without authorization")
    @DisplayName("Test negative get user data without authorization")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserDateNotAuth() {
        //GET user data
        Response responseUserDate = apiCoreRequests.makeGetRequestNotAuth("https://playground.learnqa.ru/api/user/2");

        Assertions.assertJsonHasField(responseUserDate, "username");
        Assertions.assertJsonHasNotField(responseUserDate, "firstName");
        Assertions.assertJsonHasNotField(responseUserDate, "lastName");
        Assertions.assertJsonHasNotField(responseUserDate, "email");
    }

    @Test
    @Story("Positive case")
    @Description("This test check get user data with authorization")
    @DisplayName("Test positive get user data with authorization")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetUserDetailsAuthAsSameUser() {
        //AUTHORIZATION for permanent user with email = vinkotov@example.com
        Response responseGetAuth = authConstantUser();
        cookie = this.getCookie(responseGetAuth, "auth_sid");
        headers = this.getHeader(responseGetAuth, "x-csrf-token");

        //GET user data
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/2",
                headers, cookie);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Test
    @Story("Negative case")
    @Description("This test check get user data after authorization but use other user id")
    @DisplayName("Test negative get user data")
    @Issue("Ex16")
    @Severity(SeverityLevel.MINOR)
    public void testGetUserDateForOtherUserId() {
        //CREATE NEW USER for getting an existing userId
        Map<String, String> userData = DataGenerator.getRegistrationData();
        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);
        String userId = responseCreateAuth.jsonPath().getString("id");

        //AUTHORIZATION for permanent user with email = vinkotov@example.com
        authConstantUser();

        //GET user data
        Response responseUserData = apiCoreRequests.makeGetRequestNotAuth(
                "https://playground.learnqa.ru/api/user/" + userId);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Step("Login permanent user")
    private Response authConstantUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        return apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",
                authData);
    }
}
