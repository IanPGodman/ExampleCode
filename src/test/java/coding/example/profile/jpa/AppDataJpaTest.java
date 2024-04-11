package coding.example.profile.jpa;

import coding.example.JpaTestConfig;
import coding.example.config.ApplicationData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {JpaTestConfig.class})
@ActiveProfiles(profiles = "jpa")
public class AppDataJpaTest {
    @Autowired
    ApplicationData appData;

    @Test
    @Transactional
    public void JdbcAppDataConfigTest(){
        assertEquals("Coding Example UNDER_TEST", appData.getAppName());
    }
}
