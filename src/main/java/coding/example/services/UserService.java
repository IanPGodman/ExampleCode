package coding.example.services;

import coding.example.database.DatabaseService;
import coding.example.database.dto.user.UserDtoWithRoles;
import coding.example.database.dto.user.UserMapperImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


public class UserService {
    private final static Logger log = LogManager.getLogger();

    DatabaseService database;
    ObjectMapper objectMapper;
    UserMapperImpl userMapper;

    public UserService(DatabaseService database, UserMapperImpl userMapper){
        this.database = database;
        this.userMapper = userMapper;
        objectMapper = new ObjectMapper();
    }

    public List<UserDtoWithRoles> getAllUserDto() {
        return database.getAllUser().stream().map(userMapper::toDto).toList();
    }

    @Transactional
    public String updateUser(String jsonString){
        try {
            Map<String, Object> map = objectMapper.readValue(jsonString, Map.class);
            log.info("Map: {}", map);
            UserDtoWithRoles user = userMapper.toDto(database.getById(((Integer) map.get("userid")).longValue()));
            String userName = user.userName();
            boolean enabled = (Boolean)map.get("enable");
            if (map.get("prop").equals("enabled")) {
                log.info("enabled: '{}'", enabled);
                return database.updateUserEnabled(user.userId(), enabled) ? String.format("User '%s' enabled status updated successfully.", userName) :
                        String.format("Failed to update enabled status for user '%s'!", userName);
            }
            else if (map.get("prop").equals("admin")) {
                log.info("Admin processing to be added here");
                return database.updateUserAdmin(user.userId(), enabled) ? String.format("User '%s' roles updated successfully.", userName) :
                        String.format("Failed to update roles for user '%s'!", userName);
            }
            else{
                return "Nothing to do for: " + map.get("prop");
            }
        } catch (JsonProcessingException e) {
            return "Failed to read data";
        }
    }
}
