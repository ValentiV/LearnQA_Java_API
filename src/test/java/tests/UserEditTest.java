package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Edit user data cases")
@Feature("Edit user data")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    Map<String, String> userData;
    String header, cookie, userId;
    private final String newName = "Changed Name";
    Map<String, String> authData = new HashMap<>();
    Map<String, String> editData = new HashMap<>();

    @BeforeEach
    @Step("Create new user")
    public void createUser() {
        userData = DataGenerator.getRegistrationData();

        Response responseCreteAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);

        userId = responseCreteAuth.jsonPath().getString("id");
    }

    @Test
    @Story("Positive case")
    @Description("This test check edit firstName for user after authorization")
    @DisplayName("Test positive edit test")
    public void testEditJustCreated() {
        //LOGIN
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        login(authData);

        //EDIT
        editData.put("firstName", this.newName);
        apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId, header, cookie, editData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Story("Negative case")
    @Description("This test check edit firstName for user without authorization")
    @DisplayName("Test negative edit without authorization")
    @Issue("Ex17")
    public void testEditWithoutAuth() {
        //EDIT - header, cookie = null
        editData.put("firstName", this.newName);
        Response responseEdit = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.assertResponseTextEquals(responseEdit, "Auth token not supplied");

        //LOGIN
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        login(authData);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @Story("Negative case")
    @Description("This test check edit firstName for other user with authorization")
    @DisplayName("Test negative edit other user")
    @Issue("Ex17")
    public void testEditWithAuthOtherUser() {
        //CREATE new user
        Map<String, String> newUserData = DataGenerator.getRegistrationData();

        Response responseCreteAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                newUserData);

        String newUserId = responseCreteAuth.jsonPath().getString("id");

        //LOGIN new user
        authData.put("email", newUserData.get("email"));
        authData.put("password", newUserData.get("password"));
        login(authData);

        //EDIT other user
        editData.put("firstName", this.newName);
        apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId, header, cookie, editData);

        //GET new user data
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + newUserId,
                header, cookie);

        // Тест упадет, т.к. будучи авторизованным под пользователем1 пытаемся изменить данные пользователя2,
        // при этом меняются данные самого пользователя1, что вызывает сомнения
        // Ожидается, что должна быть проверка на принадлежность userId тому пользователю, который вносит изменения
        Assertions.assertJsonByName(responseUserData, "firstName", newUserData.get("firstName"));

        //LOGIN other user
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        login(authData);

        //GET other user data
        responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", userData.get("firstName"));
    }

    @Test
    @Story("Negative case")
    @Description("This test check edit email for user (email without @))")
    @DisplayName("Test negative edit email (without @)")
    @Issue("Ex17")
    public void testEditWithAuthIncorrectEmail() {
        //LOGIN
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        login(authData);

        //EDIT
        String email = RandomStringUtils.randomAlphabetic(10) + "example.com";
        editData.put("email", email);
        Response responseEdit = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.assertResponseTextEquals(responseEdit, "Invalid email format");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie);

        Assertions.assertJsonByName(responseUserData, "email", userData.get("email"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    @Story("Negative case")
    @Description("This test check edit name for user (short name = 1 character)")
    @DisplayName("Test negative edit name (short name)")
    @Issue("Ex17")
    public void testEditWithAuthShortName(String fieldName) {
        //LOGIN
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));
        login(authData);

        //EDIT
        String newShortName = "q";
        editData.put(fieldName, newShortName);
        Response responseEdit = apiCoreRequests.makePutRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie, editData);

        Assertions.assertResponseCodeEquals(responseEdit, 400);
        Assertions.assertJsonByName(responseEdit, "error", "Too short value for field " + fieldName);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequest("https://playground.learnqa.ru/api/user/" + userId,
                header, cookie);

        Assertions.assertJsonByName(responseUserData, fieldName, userData.get(fieldName));
    }

    @Step("Login user")
    private void login(Map<String, String> authData) {
        Response responseGetAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/login",
                authData);

        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
    }
}
