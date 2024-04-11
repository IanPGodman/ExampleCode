package coding.example.database.dto.vegetable;

import coding.example.database.entity.vegetable.Vegetable;

import java.io.Serializable;

/**
 * DTO for {@link Vegetable}
 */
public record VegetableDto(String name, String image) implements Serializable {
}