package coding.example.database;

import coding.example.database.dto.vegetable.VegetableDto;
import coding.example.database.entity.user.User;
import coding.example.database.entity.vegetable.Vegetable;

import java.util.List;
import java.util.Optional;

public interface DBAccess {
    String getName();

    int countUsers();
    boolean userExistsWithEmail(String email);

    boolean registerUser(String user_name,String email,String password);
    Optional<Long> getUserIdByEmail(String email);
    Optional<Long> getUserIdByName(String user_name);
    Optional<User> findByUserName(String user_name);
    User getById(long userId);

    List<Vegetable> getAvailableVeg();
    List<User> getAllUser();

    boolean updateUserEnabled(Long userId, boolean enabled);
    boolean updateUserAdmin(Long userId, boolean admin);

    boolean addVegitable(VegetableDto dto);

    boolean deleteVegetable(String vegetableName);
}
