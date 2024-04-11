package coding.example.controllers;

import org.springframework.lang.NonNull;
import org.springframework.ui.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;



public class MockModel implements Model {
    private final Map<String, Object> attributes = new HashMap<>();

    @Override
    @NonNull
    public Model addAttribute(@NonNull String attributeName, Object attributeValue) {
        attributes.put(attributeName, attributeValue);
        return this;
    }

    @Override
    @NonNull
    public Model addAttribute(@NonNull Object attributeValue) {
        return this;
    }

    @Override
    @NonNull
    public Model addAllAttributes(@NonNull Collection<?> attributeValues) {
        return this;
    }

    @Override
    @NonNull
    public Model addAllAttributes(@NonNull Map<String, ?> attributes) {
        return this;
    }

    @Override
    @NonNull
    public Model mergeAttributes(@NonNull Map<String, ?> attributes) {
        return this;
    }

    @Override
    @NonNull
    public boolean containsAttribute(@NonNull String attributeName) {
        return attributes.containsKey(attributeName);
    }

    @Override
    @NonNull
    public Object getAttribute(@NonNull String attributeName) {
        return attributes.get(attributeName);
    }

    @Override
    @NonNull
    public Map<String, Object> asMap() {
        return attributes;
    }

    public int getNumberOfAttributes(){
        return attributes.size();
    }
}