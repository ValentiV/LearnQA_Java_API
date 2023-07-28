package HomeWork;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Ex10Test {
    String text = "Тест на длину строки.";

    @Test
    public void test() {
        Assertions.assertTrue(text.length() > 15, "Text length less than 15 characters");
    }
}