package coding.example.profile.jdbc;

import coding.example.TestConfig;
import coding.example.config.ApplicationData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {TestConfig.class})
@ActiveProfiles(profiles = "jdbc")
public class AppDataJdbcTest {
    @Autowired
    ApplicationData appData;

    @Test
    @Transactional
    public void JdbcAppDataConfigTest(){
        assertEquals("Coding Example UNDER_TEST", appData.getAppName());
    }
}
