package HomeWork;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

public class Ex13Test {
    static Map<String, Map<String, String>> expectedResult = new HashMap<>();
    private static final String userAgent1 = "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
    private static final String userAgent2 = "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1";
    private static final String userAgent3 = "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)";
    private static final String userAgent4 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0";
    private static final String userAgent5 = "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1";

    public static void createTestData(String userAgent) {
        Map<String, String> s = new HashMap<>();
        switch (userAgent) {
            case userAgent1:
                s.put("platform", "Mobile");
                s.put("browser", "No");
                s.put("device", "Android");
                break;
            case userAgent2:
                s.put("platform", "Mobile");
                s.put("browser", "Chrome");
                s.put("device", "iOS");
                break;
            case userAgent3:
                s.put("platform", "Googlebot");
                s.put("browser", "Unknown");
                s.put("device", "Unknown");
                break;
            case userAgent4:
                s.put("platform", "Web");
                s.put("browser", "Chrome");
                s.put("device", "No");
                break;
            case userAgent5:
                s.put("platform", "Mobile");
                s.put("browser", "No");
                s.put("device", "iPhone");
                break;
            default:
                break;
        }
        expectedResult.put(userAgent, s);
    }

    @ParameterizedTest
    @ValueSource(strings = {userAgent1, userAgent2, userAgent3, userAgent4, userAgent5})
    public void test(String userAgent) {
        createTestData(userAgent);
        JsonPath response = RestAssured
                .given()
                .header("User-Agent", userAgent)
                .get("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .jsonPath();
        Map<String, String> result = response.get();
        String platform = result.get("platform");
        String browser = result.get("browser");
        String device = result.get("device");
        Assertions.assertEquals(expectedResult.get(userAgent).get("platform"), platform);
        Assertions.assertEquals(expectedResult.get(userAgent).get("browser"), browser);
        Assertions.assertEquals(expectedResult.get(userAgent).get("device"), device);
    }
}
