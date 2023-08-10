package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Story("Negative case")
    @Description("This test check registration with existing email")
    @DisplayName("Test negative registration with existing email")
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "Users with email '" + email + "' already exists");
    }

    @Test
    @Story("Positive case")
    @Description("This test check successfully registration")
    @DisplayName("Test positive registration")
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Test
    @Story("Negative case")
    @Description("This test check registration with incorrect email (without @)")
    @DisplayName("Test negative registration with incorrect email")
    @Issue("Ex15")
    public void testCreateUserWithIncorrectEmail() {
        String email = RandomStringUtils.randomAlphabetic(10) + "example.com";
        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    @Story("Negative case")
    @Description("This test check registration without one mandatory field")
    @DisplayName("Test negative registration without one mandatory field")
    @Issue("Ex15")
    public void testCreateUserWithoutOneMandatoryField(String fieldName) {
        Map<String, String> userData = DataGenerator.getIncorrectRegistrationData(fieldName);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "The following required params are missed: " + fieldName);
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    @Story("Negative case")
    @Description("This test check registration with short name (1 character)")
    @DisplayName("Test negative registration with short name")
    @Issue("Ex15")
    public void testCreateUserWithShortName(String fieldName) {
        String name = "q";
        Map<String, String> userData = new HashMap<>();
        userData.put(fieldName, name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "The value of '" + fieldName + "' field is too short");
    }

    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName"})
    @Story("Negative case")
    @Description("This test check registration with long name (251 character)")
    @DisplayName("Test negative registration with long name")
    @Issue("Ex15")
    public void testCreateUserWithLongName(String fieldName) {
        String name = RandomStringUtils.randomAlphabetic(251);
        Map<String, String> userData = new HashMap<>();
        userData.put(fieldName, name);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequest("https://playground.learnqa.ru/api/user/",
                userData);
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth,
                "The value of '" + fieldName + "' field is too long");
    }
}
