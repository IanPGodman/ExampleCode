package coding.example.integration;

import coding.example.DbSetup;
import coding.example.TestConfig;
import coding.example.database.DatabaseService;
import coding.example.database.entity.user.User;
import coding.example.database.entity.vegetable.Vegetable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles(profiles = "jdbc")
public class DatabaseJdbcTest {
    private static final Logger log = LogManager.getLogger();

    @Autowired
    DatabaseService database;

    @Autowired
    DbSetup dbSetup;

    private static final String TEST_USER_EMAIL = "test@user.net";
    private static final String TEST_USER_NAME = "Test";
    private static final String OTHER_USER_NAME = "Another User";

    @BeforeEach
    public void initDB(){
        assertTrue(dbSetup.setUp());
    }

    @Test
    @Transactional
    public void countUsersTest(){
        int allUsers = database.countUsers();
        List<User> users = database.getAllUser();
        for(User user : users){
            log.info("countUsersTest found user: {}", user);
        }
        assertEquals(3, allUsers);
        assertFalse(database.userExistsWithEmail("notanother@user.net"));
    }

    @Test
    @Transactional
    public void getUserIdTest(){
        Optional<Long> userIdByEmail = database.getUserIdByEmail(TEST_USER_EMAIL);
        assertTrue(userIdByEmail.isPresent());
        Optional<Long> userIdByName = database.getUserIdByName(TEST_USER_NAME);
        assertTrue(userIdByName.isPresent());
        assertEquals(userIdByEmail.get(), userIdByName.get());

        Optional<Long> otherUserIdByName = database.getUserIdByName(OTHER_USER_NAME);
        assertTrue(otherUserIdByName.isPresent());
        assertNotEquals(otherUserIdByName.get(),  userIdByName.get());
    }

    @Test
    @Transactional
    public void getVegListTest(){
        List<Vegetable> veglist = database.getAvalableVeg();
        assertNotNull(veglist);
        assertEquals(19, veglist.size());
    }
}
