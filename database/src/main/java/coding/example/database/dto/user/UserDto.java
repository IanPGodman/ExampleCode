package coding.example.database.dto.user;

import coding.example.database.entity.user.User;

import java.io.Serializable;

/**
 * DTO for {@link User}
 */
public record UserDto(String userName, String password) implements Serializable {
}