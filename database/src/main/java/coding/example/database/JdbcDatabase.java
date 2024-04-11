package coding.example.database;

import coding.example.database.dto.vegetable.VegetableDto;
import coding.example.database.entity.role.Role;
import coding.example.database.entity.user.User;
import coding.example.database.entity.vegetable.Vegetable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class JdbcDatabase implements DBAccess{
    private static final Logger log = LogManager.getLogger();

    private final JdbcTemplate jdbcTemplate;

    public JdbcDatabase(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getName() {
        return "JdbcDatabase";
    }

    @Override
    public int countUsers() {
        return jdbcTemplate.query(
                "SELECT COUNT(*) FROM APP_USER",
                (rs, rowNum) -> rs.getInt(1)).getFirst();
    }

    @Override
    public boolean userExistsWithEmail(String email) {
        return getUserIdByEmail(email).isPresent();
    }

    @Override
    public boolean registerUser(String user_name, String email, String password) {
        SimpleJdbcInsert insertIntoUser = new SimpleJdbcInsert(jdbcTemplate).withTableName("APP_USER").usingGeneratedKeyColumns("user_id");
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("user_name", user_name);
        parameters.put("email", email);
        parameters.put("password", password);
        parameters.put("enabled", 'Y');
        parameters.put("created_date", LocalDate.now());
        return insertIntoUser.executeAndReturnKey(parameters).intValue() > 0;
    }

    @Override
    public Optional<Long> getUserIdByEmail(String email) {
        return jdbcTemplate.query(
                "SELECT user_id FROM APP_USER WHERE email = ?",
                (rs, rowNum) -> rs.getLong(1),
                email).stream().findFirst();
    }

    @Override
    public Optional<Long> getUserIdByName(String user_name) {
        return jdbcTemplate.query(
                "SELECT user_id FROM APP_USER WHERE user_name = ?",
                (rs, rowNum) -> rs.getLong(1),
                user_name).stream().findFirst();
    }

    private Set<Role> getUserRoles(long userId ){
        return new LinkedHashSet<>(jdbcTemplate.query(
                "SELECT id, role_name FROM role WHERE id IN (SELECT roles_id FROM app_user_roles WHERE user_user_id = ?)",
                (rs, rowNum) ->
                        new Role(
                                rs.getLong("id"),
                                rs.getString("role_name")
                        ),
                userId
        ));
    }

    @Override
    public Optional<User> findByUserName(String user_name) {
        return jdbcTemplate.query(
                "SELECT * FROM APP_USER WHERE user_name = ?",
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("user_name"),
                                rs.getString("email"),
                                getUserRoles(rs.getLong("user_id")),
                                rs.getString("password"),
                                rs.getDate("created_date").toLocalDate(),
                                rs.getString("enabled")
                        ),
                user_name
        ).stream().findFirst();
    }

    @Override
    public User getById(long userId) {
        return jdbcTemplate.query(
                "SELECT * FROM APP_USER WHERE user_id = ?",
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("user_name"),
                                rs.getString("email"),
                                getUserRoles(rs.getLong("user_id")),
                                rs.getString("password"),
                                rs.getDate("created_date").toLocalDate(),
                                rs.getString("enabled")
                        ),
                userId
        ).stream().findFirst().orElse(null);
    }

    @Override
    public List<Vegetable> getAvailableVeg() {
        return jdbcTemplate.query("SELECT * FROM vegetable", this::rowToVegetable);
    }

    private Vegetable rowToVegetable(ResultSet rs, int rowNum) {
        try {
            return new Vegetable(rs.getInt("veg_id"),
                    rs.getString("display_name"),
                    rs.getString("image_name"));
        } catch (SQLException e) {
            log.error("Error mapping row to Vegetable.");
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<User> getAllUser() {
        return jdbcTemplate.query(
                "SELECT * FROM APP_USER",
                (rs, rowNum) ->
                        new User(
                                rs.getLong("user_id"),
                                rs.getString("user_name"),
                                rs.getString("email"),
                                getUserRoles(rs.getLong("user_id")),
                                rs.getString("password"),
                                rs.getDate("created_date").toLocalDate(),
                                rs.getString("enabled")
                        )
        );
    }

    @Override
    public boolean updateUserEnabled(Long userId, boolean enabled) {
        return jdbcTemplate.update(
                "UPDATE APP_USER SET enabled = ? WHERE user_id = ?",
                (enabled ? "Y" : "N"), userId
        ) > 0;
    }

    private Role getRoleByName(String name){
        return jdbcTemplate.query(
                "SELECT * FROM ROLE WHERE role_name = ?",
                (rs, rowNum) ->
                        new Role(
                                rs.getLong("id"),
                                rs.getString("role_name")
                        ),
                name
        ).stream().findFirst().orElse(null);
    }

    @Override
    public boolean updateUserAdmin(Long userId, boolean admin) {
        User user = getById(userId);
        if (user == null){
            return false;
        }
        Role adminRole = getRoleByName("ROLE_ADMIN");
        boolean isAdmin = user.getRoles().contains(adminRole);
        if (admin){ // add if not present
            if (isAdmin){
                // already admin
                return false;
            }
            else{
                jdbcTemplate.update(
                        "INSERT INTO app_user_roles (user_user_id, roles_id) VALUES (?, ?)",
                        userId, adminRole.getId()
                );
                return true;
            }
        }
        else{ //remove if present
            if (!isAdmin){
                // already not admin
                return false;
            }
            else{
                jdbcTemplate.update(
                        "DELETE FROM app_user_roles WHERE user_user_id = ? AND roles_id = ?",
                        userId, adminRole.getId()
                );
                return true;
            }
        }
    }

    @Override
    public boolean addVegitable(VegetableDto dto) {
        return jdbcTemplate.update(
                "INSERT INTO vegetable (display_name, image_name) VALUES (?, ?)",
                dto.name(), dto.image()
        ) > 0;
    }

    @Override
    public boolean deleteVegetable(String vegetableName) {
        return jdbcTemplate.update(
                "DELETE FROM vegetable WHERE display_name = ?",
                vegetableName
        ) > 0;
    }

}
