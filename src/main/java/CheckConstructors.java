

import com.theokanning.openai.service.OpenAiService;

import java.lang.reflect.Constructor;

public class CheckConstructors {
    public static void main(String[] args) {
        Constructor<?>[] constructors = OpenAiService.class.getConstructors();
        for (Constructor<?> ctor : constructors) {
            System.out.println(ctor);
        }
    }
}
