package coding.example.database;

import coding.example.database.dto.vegetable.VegetableDto;
import coding.example.database.entity.role.Role;
import coding.example.database.entity.role.RoleRepository;
import coding.example.database.entity.user.User;
import coding.example.database.entity.user.UserRepository;
import coding.example.database.entity.vegetable.Vegetable;
import coding.example.database.entity.vegetable.VegetableRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


public class JpaDatabase implements DBAccess{
    private static final Logger log = LogManager.getLogger();

    UserRepository userRepo;
    RoleRepository roleRepo;
    VegetableRepository vegRepo;


    public void jpaDatabaseSetup(UserRepository userRepo, RoleRepository roleRepo, VegetableRepository vegRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.vegRepo = vegRepo;
    }

    @Override
    public String getName() {
        return "JpaDatabase";
    }

    @Override
    public int countUsers() {
        return userRepo.findAll().size();
    }

    @Override
    public boolean userExistsWithEmail(String email) {
        User user = userRepo.findByEmail(email);
        return user != null;
    }

    @Override
    @Transactional
    public boolean registerUser(String user_name, String email, String password) {
        return false;
    }

    @Override
    public Optional<Long> getUserIdByEmail(String email) {
        User user = userRepo.findByEmail(email);
        if (user != null) {
            return Optional.of(user.getUserId());
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> getUserIdByName(String user_name) {
        User user = userRepo.findByUserName(user_name);
        if (user != null) {
            return Optional.of(user.getUserId());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUserName(String username) {
        return Optional.ofNullable(userRepo.findByUserName(username));
    }

    @Override
    public User getById(long userId) {
        return userRepo.getReferenceById(userId);
    }

    @Override
    public List<Vegetable> getAvailableVeg() {
        return vegRepo.findAll();
    }

    @Override
    public List<User> getAllUser() {
        return userRepo.findAll();
    }

    @Override
    @Transactional
    public boolean updateUserEnabled(Long userId, boolean enabled) {
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            user.setEnabled(enabled ? "Y": "N");
            userRepo.save(user);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean updateUserAdmin(Long userId, boolean admin) {
        User user = userRepo.findById(userId).orElse(null);
        boolean updated = false;
        if (user == null) {
            return updated;
        }
        Role adminRole = roleRepo.findByName("ROLE_ADMIN");

        if (admin) {
            if (!user.getRoles().contains(adminRole)) {
                user.getRoles().add(adminRole);
                updated = true;
            }
        } else {
            if (user.getRoles().contains(adminRole)) {
                user.getRoles().remove(adminRole);
                updated = true;
            }
        }
        if (updated) {
            userRepo.save(user);
        }
        return updated;
    }

    @Override
    public boolean addVegitable(VegetableDto dto) {
        Vegetable newVeg = new Vegetable();
        newVeg.setName(dto.name());
        newVeg.setImage_name(dto.image());
        Vegetable result = vegRepo.save(newVeg);
        log.info("New veg saved: {}", result);
        return result.getVeg_id() > 0;
    }

    @Override
    @Transactional
    public boolean deleteVegetable(String vegetableName) {
        vegRepo.deleteByName(vegetableName);
        return true;
    }
}
