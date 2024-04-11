package coding.example.e2e.steps;

import com.codeborne.selenide.SelenideElement;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.concurrent.*;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.selected;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
public class AdminSteps {
    private static final Logger logger = LoggerFactory.getLogger(AdminSteps.class);

    static int port = 8443;
    static final int TEST_USER_ID = 2;

    @Container
    static GenericContainer<?> appContainer = new GenericContainer<>(DockerImageName.parse("coding.example:latest"))
                    .withExposedPorts(8443)
                    .waitingFor(Wait.forLogMessage(".*Started Application.*", 1))
                    .withLogConsumer(outputFrame -> logger.info(outputFrame.getUtf8String().trim()));


    @Given("^that the test container is running$")
    public void startTestContainer(){
        Callable<GenericContainer<?>> startContainerTask = () -> {
            appContainer.start();
            return appContainer;
        };

        Future<GenericContainer<?>> containerFuture;
        try (ExecutorService executor = newSingleThreadExecutor()) {
            containerFuture = executor.submit(startContainerTask);

            try {
                GenericContainer<?> startedContainer = containerFuture.get(1, TimeUnit.MINUTES);
                port = startedContainer.getMappedPort(8443);
            } catch (TimeoutException e) {
                // Handle timeout (container didn't start within 1 minute)
                // Optionally cancel the container if needed: container.stop();
            } catch (Exception e) {
                // Handle other exceptions
            }
        }
    }

    @Then ("^the user Test can not access the admin page$")
    public void theUserTestLogsIn() {
        open("https://localhost.local:" + port + "/login");
        $(By.name("username")).setValue("Test");
        $(By.name("password")).setValue("p455word");
        $("#submit").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/index", webdriver().object().getCurrentUrl());
        Assertions.assertFalse($("#admin-menu-item").isEnabled());
    }

    @Then ("^the Amin user logs in")
    public void theUserAdminLogsIn() {
        open("https://localhost.local:" + port + "/login");
        $(By.name("username")).setValue("Admin");
        $(By.name("password")).setValue("p455word");
        $("#submit").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/index", webdriver().object().getCurrentUrl());
        Assertions.assertTrue($("#admin-menu-item").isEnabled());
    }

    @And("^can access the admin page$")
    public void canAccessTheAdminPage(){
        Assertions.assertTrue($("#admin-menu-item").isEnabled());
        $("#admin-menu-item").click();
        $("meta[name='description']").shouldHave(attribute("content", "admin page"));
    }

    @And("^can give the user Test admin rights$")
    public void canGiveUserTestAdminRights(){
        SelenideElement element = $("#admin-" + TEST_USER_ID);
        // test user should not be admin
        assertFalse(element.is(selected));
        $("#admin-" + TEST_USER_ID).click();
    }

    @Then("^the Admin user logs out$")
    public void logOutFromAdminPage() {
        $(byText("Back to home page")).click();
        logOut();
    }

    @And("^logs out$")
    public void logOut() {
        // should be back on the index page
        Assertions.assertEquals("https://localhost.local:" + port + "/index", webdriver().object().getCurrentUrl());
        $("#logout-menu-item").click();
        // Should now be back to lon in page.
        Assertions.assertEquals("https://localhost.local:" + port + "/login?logout", webdriver().object().getCurrentUrl());
    }

    @Then("^the user Test logs in$")
    public void logInAsTestUser() {
        open("https://localhost.local:" + port + "/login");
        $(By.name("username")).setValue("Test");
        $(By.name("password")).setValue("p455word");
        $("#submit").click();
        Assertions.assertEquals("https://localhost.local:" + port + "/index", webdriver().object().getCurrentUrl());
    }
}
