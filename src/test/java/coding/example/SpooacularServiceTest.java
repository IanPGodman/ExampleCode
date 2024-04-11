package coding.example;

import coding.example.services.SpoonacularService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles("jdbc")
public class SpooacularServiceTest {

    @Autowired
    SpoonacularService service;

    @Test
    @Transactional
    public void serviceWithoutAnyIngredientsTest() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            Future<List<Map<String, String>>> f = service.getRecipies(null);
        });

        assertEquals("List of ingredients not supplied!", exception.getMessage());
    }

    @Test
    @Transactional
    public void serviceIncludingIngredientsTest(){
        Future<List<Map<String, String>>> f = service.getRecipies(Arrays.asList("tomato", "carrot", "cheese"));
        List<Map<String, String>> l = null;
        try {
            l = f.get();
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
        assertNotNull(l);
        assertFalse(l.isEmpty());
    }
}
