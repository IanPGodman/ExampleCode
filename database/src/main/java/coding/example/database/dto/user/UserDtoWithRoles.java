package coding.example.database.dto.user;

import coding.example.database.entity.role.Role;
import coding.example.database.entity.user.User;

import java.io.Serializable;
import java.util.Set;

/**
 * DTO for {@link User}
 */
public record UserDtoWithRoles(int userId, String userName, String email, String enabled, Set<Role> roles) implements Serializable {
    public boolean isAdmin(){
        return roles.stream().anyMatch( s -> s.getName().equals("ROLE_ADMIN"));
    }

    public boolean isEnabled(){
        return enabled.equals("Y");
    }
}