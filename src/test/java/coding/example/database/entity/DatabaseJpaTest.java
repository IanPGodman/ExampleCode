package coding.example.database.entity;

import coding.example.DbSetup;
import coding.example.JpaTestConfig;
import coding.example.database.DatabaseService;
import coding.example.database.JpaDatabase;
import coding.example.database.entity.role.RoleRepository;
import coding.example.database.entity.user.UserRepository;
import coding.example.database.entity.vegetable.Vegetable;
import coding.example.database.entity.vegetable.VegetableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {JpaTestConfig.class})
@ActiveProfiles(profiles = "jpa")
@ComponentScan("coding.example.database.entity")
public class DatabaseJpaTest {
    @Autowired
    DatabaseService database;

    @Autowired
    DbSetup dbSetup;


    @Autowired
    public DatabaseJpaTest(JpaDatabase jpaDatabaseBean, UserRepository userRepo, RoleRepository roleRepo, VegetableRepository vegRepo) {
        jpaDatabaseBean.jpaDatabaseSetup(userRepo, roleRepo, vegRepo);
    }

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
        assertEquals(database.getName(), "JpaDatabase");
        // verify we can count all the test users
        int allUsers = database.countUsers();
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
