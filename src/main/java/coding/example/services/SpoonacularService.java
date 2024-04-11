package coding.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public class SpoonacularService {
    private final static Logger log = LogManager.getLogger();

    @Value("${SpoonacularApiKey}")
    private String spoonacularApiKey;

    @Value("${spoonacularResults}")
    private String spoonacularResults;

    private static final String BASE_URL = "https://api.spoonacular.com/";

    private static final ObjectMapper mapper = new ObjectMapper();

    private static List<Map<String, String>> processResponse(String response) {
        try {
            return mapper.readValue(response, ArrayList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Future<List<Map<String, String>>> getRecipies(List<String>ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new RuntimeException("List of ingredients not supplied!");
        }
        String requestStr = String.format("%srecipes/findByIngredients?number=%s&apiKey=%s&ingredients=%s", BASE_URL, spoonacularResults, spoonacularApiKey, String.join(",", ingredients));

        log.info("getRecipies url: '{}'", requestStr);

        WebClient producerClient = WebClient.create(requestStr);

        // Process the response asynchronously
        return producerClient.get()
                    .retrieve()
                    .bodyToMono(String.class)
                        .toFuture()
                        .thenApply(SpoonacularService::processResponse);
    }
}
