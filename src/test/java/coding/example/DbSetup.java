package coding.example;

import liquibase.command.CommandScope;
import liquibase.exception.CommandExecutionException;
import org.springframework.stereotype.Component;

@Component
public class DbSetup {

    private static final String CHANGELOG_PATH = "db/changelog/db.changelog-test.yaml";

    // As tests are Transactional and we call this in the test it gets rolled back at the end of each test.
    // Not efficient but safe
    public boolean setUp() {
        try {
            new CommandScope("update")
                    .addArgumentValue("changeLogFile", CHANGELOG_PATH)
                    .addArgumentValue("url", "jdbc:h2:mem:app_example")
                    .addArgumentValue("username", "sa")
                    .addArgumentValue("password", "")
                    .execute();
            return true;
        } catch (CommandExecutionException e) {
            return false;
        }
    }
}
