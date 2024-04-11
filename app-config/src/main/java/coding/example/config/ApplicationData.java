package coding.example.config;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
public class ApplicationData {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApplicationData.class);

    @Value("${spring.application.name}")
    private String appName;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.statement-cache-size}")
    private String statementCacheSize;

    @Value("${spring.datasource.statement-cache-sql-limit}")
    private String statementCacheSqlLimit;

    public String getAppName() {
        logger.warn("Application name: {}", appName);

        return appName;
    }
}
