package coding.example;

import coding.example.config.ApplicationData;
import coding.example.controllers.AdminController;
import coding.example.controllers.PageController;
import coding.example.database.DBAccess;
import coding.example.database.DatabaseService;
import coding.example.database.JdbcDatabase;
import coding.example.database.JpaDatabase;
import coding.example.database.dto.user.UserMapperImpl;
import coding.example.database.entity.role.RoleRepository;
import coding.example.database.entity.user.UserDetail;
import coding.example.database.entity.user.UserRepository;
import coding.example.database.entity.vegetable.VegetableRepository;
import coding.example.security.CustomAuthenticationFailureHandler;
import coding.example.security.CustomAuthenticationProvider;
import coding.example.services.ImageService;
import coding.example.services.SpoonacularService;
import coding.example.services.UserService;
import coding.example.services.VegetableService;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.Arrays;

/**
 * Test are always on profile jdbc no need for profiling.
 */
@Configuration
@ComponentScan("coding.example.database.entity")
@EnableJpaRepositories(basePackageClasses = {UserRepository.class, VegetableRepository.class, RoleRepository.class})
@Profile("jpa")
public class JpaTestConfig {
    private static final Logger log = LogManager.getLogger();

    private final Environment environment;
    private final ResourceLoader resourceLoader;

    @Autowired
    public JpaTestConfig(Environment environment, ResourceLoader resourceLoader) {
        this.environment = environment;
        this.resourceLoader = resourceLoader;
    }

    @Value("${spring.datasource.url}")
    String datasourceUrl;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Bean
    ServletWebServerFactory servletWebServerFactory(){
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addAdditionalTomcatConnectors(createSslConnector());
        return factory;
    }

    private Connector createSslConnector() {
        Connector connector = new Connector(Http11NioProtocol.class.getName());
        connector.setPort(8443);
        connector.setSecure(true);
        connector.setScheme("https");
        connector.setProperty("keyAlias", "mycert");
        connector.setProperty("keystorePass", "sK0d4X33!");
        connector.setProperty("keystoreFile", "classpath:keystore.p12");
        return connector;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan("coding.example.database.entity");
        return entityManagerFactoryBean;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setGenerateDdl(false);
        jpaVendorAdapter.setShowSql(true);
        jpaVendorAdapter.setDatabase(Database.H2);
        return jpaVendorAdapter;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:app_example");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        return dataSource;
    }

    @Bean
    public PageController pageController(){
        return new PageController(createVegetableService(), createSpoonacularService(), createApplicationData());
    }

    @Bean
    public AdminController adminController(){
        return new AdminController(createUserService(), createVegetableService(), createImageService());
    }

    @Bean
    public JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource());
    }

    @Bean
    @Primary
    public DatabaseService databaseService(){
        return new DatabaseService(createDatabaseAccess(), passwordEncoder());
    }

    @Bean
    public CustomAuthenticationFailureHandler authenticationFailureHandler(){
        return new CustomAuthenticationFailureHandler();
    }

    @Bean
    public UserDetail userDetail() {
        return new UserDetail(createJdbcDatabase(),passwordEncoder());
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider(){
        return new CustomAuthenticationProvider(userDetail());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationData createApplicationData(){
        return new ApplicationData();
    }

    @Bean
    public ImageService createImageService(){
        return new ImageService(resourceLoader);
    }

    @Bean
    public UserMapperImpl UserMapperImpl(){
        return new UserMapperImpl();
    }

    @Bean
    public UserService createUserService(){
        return new UserService(databaseService(), UserMapperImpl());
    }

    @Bean
    public VegetableService createVegetableService(){
        return new VegetableService(databaseService(), createImageService());
    }

    @Bean
    public DBAccess createDatabaseAccess(){
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(s -> s.equalsIgnoreCase("jdbc"))) {
            return createJdbcDatabase();
        }
        return createJpaDatabase();
    }

    public JdbcDatabase createJdbcDatabase() {
        return new JdbcDatabase(jdbcTemplate());
    }

    public JpaDatabase createJpaDatabase(){
        return new JpaDatabase();
    }

    @Bean
    public DbSetup createDbSetup(){
        return new DbSetup();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SpoonacularService createSpoonacularService(){
        return new SpoonacularService();
    }
}
