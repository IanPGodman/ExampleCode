package coding.example;

import coding.example.config.ApplicationData;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.util.Arrays;


@Configuration
@EnableWebSecurity
@ComponentScan("coding.example.database")
@Profile("h2")
public class AppConfigH2 {
    private static final Logger log = LogManager.getLogger();

    private final DataSource dataSource;
    private final Environment environment;
    private final ResourceLoader resourceLoader;


    @Autowired
    public AppConfigH2(DataSource dataSource, Environment environment, ResourceLoader resourceLoader) {
        log.info("Initialising Test Config.");
        this.dataSource = dataSource;
        this.environment = environment;
        this.resourceLoader = resourceLoader;
    }

    @Bean
    JdbcTemplate jdbcTemplate(){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    DatabaseService databaseService(){
        return new DatabaseService(createDatabaseAccess(), passwordEncoder());
    }

    @Bean
    CustomAuthenticationFailureHandler authenticationFailureHandler(){
        return new CustomAuthenticationFailureHandler();
    }

    public JdbcDatabase createJdbcDatabase() {
        return new JdbcDatabase(jdbcTemplate());
    }

    @Bean
    UserDetail userDetail() {
        return new UserDetail(createJdbcDatabase(), passwordEncoder());
    }

    @Bean
    CustomAuthenticationProvider customAuthenticationProvider(){
        return new CustomAuthenticationProvider(userDetail());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .requiresChannel(chanel -> chanel
                .anyRequest()
                .requiresSecure()
            )
//            .authenticationProvider(customAuthenticationProvider())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/static/**", "/logout/**",
                    "/login/**","/performlogin/**","/health/**",
                    "/error/**", "/loginerror/**", "/hello/**") .permitAll()
                .requestMatchers("/admin/**", "/updateuser/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(exceptionHandling ->
                    exceptionHandling
                            .accessDeniedPage("/403")
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .loginProcessingUrl("/performlogin")
                .defaultSuccessUrl("/index", true)
                .failureUrl("/loginerror")
                .permitAll()
            )
                .logout((logout) -> logout.logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
            )
            .rememberMe((remember) -> remember
                    .rememberMeServices(rememberMeServices())
            );

        return http.build();
    }

    private static final String REMEMBER_ME_KEY = "app_example";
    @Bean
    RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices.RememberMeTokenAlgorithm encodingAlgorithm = TokenBasedRememberMeServices.RememberMeTokenAlgorithm.SHA256;
        return new TokenBasedRememberMeServices(REMEMBER_ME_KEY, userDetail(), encodingAlgorithm);
    }

//    @Bean
//    RememberMeAuthenticationFilter rememberMeFilter() {
//        RememberMeAuthenticationFilter rememberMeFilter = new RememberMeAuthenticationFilter();
//        rememberMeFilter.setRememberMeServices(rememberMeServices());
//        rememberMeFilter.setAuthenticationManager(theAuthenticationManager);
//        return rememberMeFilter;
//    }
//
//    @Bean
//    RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
//        RememberMeAuthenticationProvider rememberMeAuthenticationProvider = new RememberMeAuthenticationProvider();
//        rememberMeAuthenticationProvider.setKey(REMEMBER_ME_KEY);
//        return rememberMeAuthenticationProvider;
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ApplicationData createApplicationData(){
        return new ApplicationData();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public UserMapperImpl userMapperImpl(){
        return new UserMapperImpl();
    }

    @Bean
    public UserService createUserService(){
        return new UserService(databaseService(), userMapperImpl());
    }

    @Bean
    public ImageService createImageService(){
        return new ImageService(resourceLoader);
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

    @Autowired
    UserRepository userRepo;
    @Autowired
    RoleRepository roleRepo;
    @Autowired
    VegetableRepository vegRepo;

    public JpaDatabase createJpaDatabase(){
        JpaDatabase jpaDatabase = new JpaDatabase();
        jpaDatabase.jpaDatabaseSetup( userRepo,  roleRepo,  vegRepo);
        return jpaDatabase;
    }

    @Bean
    public SpoonacularService createSpoonacularService(){
        return new SpoonacularService();
    }
}
