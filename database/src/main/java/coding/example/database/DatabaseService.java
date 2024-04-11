package coding.example.database;

import coding.example.database.dto.vegetable.VegetableDto;
import coding.example.database.entity.user.User;
import coding.example.database.entity.vegetable.Vegetable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class DatabaseService {

    DBAccess dbAccess;
    PasswordEncoder encoder;

    public DatabaseService(DBAccess dbAccess, PasswordEncoder encoder){
        this.dbAccess = dbAccess;
        this.encoder = encoder;
    }

    public String getName() {
        return dbAccess.getName();
    }

    public int countUsers() {
        return dbAccess.countUsers();
    }
    
    public boolean userExistsWithEmail(String email) {
        return dbAccess.userExistsWithEmail(email) ;
    }
    
    public boolean registerUser(String user_name, String email, String password) {
        String encodedPassword = encoder.encode(password);
        return dbAccess.registerUser(user_name, email, encodedPassword);
    }

    public Optional<Long> getUserIdByEmail(String email) {
        return dbAccess.getUserIdByEmail(email);
    }

    public Optional<Long> getUserIdByName(String user_name) {
        return dbAccess.getUserIdByName(user_name);
    }

    public Optional<User> findByUserName(String user_name) {
        return dbAccess.findByUserName(user_name) ;
    }

    public List<Vegetable> getAvalableVeg() {
        return dbAccess.getAvailableVeg();
    }

    public List<User> getAllUser() {
        return dbAccess.getAllUser();
    }

    public User getById(long userId) {
        return dbAccess.getById(userId);
    }

    public boolean updateUserEnabled(long userId, boolean enabled){
        return dbAccess.updateUserEnabled(userId, enabled);
    }

    public boolean updateUserAdmin(long userId, boolean enabled){
        return dbAccess.updateUserAdmin(userId, enabled);
    }

    @Transactional
    public boolean addVegitable(VegetableDto dto) {
        return dbAccess.addVegitable(dto);
    }

    public boolean deleteVegetable(String vegetableName){
        return dbAccess.deleteVegetable(vegetableName);
    }
}
