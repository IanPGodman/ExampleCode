package coding.example.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.codeborne.selenide.Selenide.*;

@Testcontainers
public class LoginTest {
    private static final Logger logger = LoggerFactory.getLogger(LoginTest.class);

    static WebDriver driver;
    static int port = 8443;


    @Container
    static GenericContainer<?> appContainer = new GenericContainer<>(DockerImageName.parse("coding.example:latest"))
            .withExposedPorts(8443)
            .waitingFor(Wait.forLogMessage(".*Started Application.*", 1))
            // Uncomment if you wat to see the container startup log
//            .withLogConsumer(outputFrame -> {
//                logger.info(outputFrame.getUtf8String().trim());
//            })
            ;

    @BeforeAll
    static void setupClass() {
        port = appContainer.getMappedPort(8443);
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @AfterAll
    static void windupClass() {
        driver.close();
    }

    @Test
    public void adminUserCanLoginByUsernameTest() {
        open("https://localhost.local:" + port + "/login");
        $(By.name("username")).setValue("Admin");
        $(By.name("password")).setValue("p455word");
        $("#submit").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/index", webdriver().object().getCurrentUrl());
        Assertions.assertTrue($("#admin-menu-item").isEnabled());
    }

    @Test
    public void testUserCanLoginByUsernameTest() {
        open("https://localhost.local:" + port + "/login");
        $(By.name("username")).setValue("Test");
        $(By.name("password")).setValue("p455word");
        $("#submit").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/index", webdriver().object().getCurrentUrl());
        Assertions.assertFalse($("#admin-menu-item").isEnabled());
    }

    @Test
    public void unregisteredUserCanNotLogin() {
        open("https://localhost.local:" + port + "/login");
        $(By.name("username")).setValue("unreistered");
        $(By.name("password")).setValue("notset");
        $("#submit").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/loginerror", webdriver().object().getCurrentUrl());
    }

    @Test
    public void adminUserCanLoginAndGotoAdminPageTest() {
        open("https://localhost.local:" + port + "/login");
        $(By.name("username")).setValue("Admin");
        $(By.name("password")).setValue("p455word");
        $("#submit").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/index", webdriver().object().getCurrentUrl());
        Assertions.assertTrue($("#admin-menu-item").isEnabled());
        $("#admin-menu-item").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/admin", webdriver().object().getCurrentUrl());
    }
}
