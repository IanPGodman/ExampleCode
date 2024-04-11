package coding.example;

import coding.example.database.DatabaseService;
import coding.example.database.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * testing functionality on hsqldb.
 * for tests we use the jdbc profile
 */
@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles(profiles = {"jdbc"})
public class DatabaseDefaultAccessTests {

    @Autowired
    DatabaseService database;

    @Autowired
    DbSetup dbSetup;

    @BeforeEach
    public void initDB(){
        assertTrue(dbSetup.setUp());
    }

    @Test
    @Transactional
    public void TestDbTableCreated(){
        boolean dbIsJdbc = "JdbcDatabase".equals(database.getName());
        assumeTrue(dbIsJdbc);
        int currentUsers = database.countUsers();
        assertTrue( currentUsers > 0);
        assertTrue(database.registerUser("Fred Bloggs", "fred@bloggs.com", "ABC"));
        assertEquals( currentUsers+1, database.countUsers());
        Optional<Long> fredsIdByEmail = database.getUserIdByEmail("fred@bloggs.com");
        assumeTrue(fredsIdByEmail.isPresent());
        Optional<Long> fredsIdByName = database.getUserIdByName("Fred Bloggs");
        assumeTrue(fredsIdByName.isPresent());
        assertEquals(fredsIdByEmail.get(), fredsIdByName.get());
        Optional<User> user = database.findByUserName("Fred Bloggs");
        assertTrue(user.isPresent());
        assertEquals("Fred Bloggs", user.get().getUserName());
    }
}
