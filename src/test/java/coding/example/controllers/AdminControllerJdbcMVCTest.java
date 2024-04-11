package coding.example.controllers;

import coding.example.DbSetup;
import coding.example.TestConfig;
import coding.example.database.dto.user.UserMapperImpl;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {AdminController.class, PageController.class})
@ActiveProfiles(profiles = "jdbc")
@ContextConfiguration(classes={TestConfig.class, UserMapperImpl.class})
@ExtendWith(SpringExtension.class)
public class AdminControllerJdbcMVCTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AdminControllerJdbcMVCTest.class);

    @Autowired
    DbSetup dbSetup;

    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain springSecurityFilterChain;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    AdminController adminController;

    HttpSessionCsrfTokenRepository httpSessionCsrfTokenRepository;
    CsrfToken csrfToken;
    MockHttpSession session;

    @BeforeEach
    public void setup() {
        assertTrue(dbSetup.setUp());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .addFilters(springSecurityFilterChain.getFilters().toArray(new Filter[0]))
                .build();
        httpSessionCsrfTokenRepository = new HttpSessionCsrfTokenRepository();
        csrfToken = httpSessionCsrfTokenRepository.generateToken(new MockHttpServletRequest());
        session = new MockHttpSession();
    }


    @Test
    @Transactional
    public void adminUserCanRetrieveAdminPageTest() throws Exception {
        UserDetails customUserDetails = userDetailsService.loadUserByUsername("Admin");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword(), customUserDetails.getAuthorities());

        // Perform your test using this authentication
        MvcResult result = mockMvc.perform(get("/admin").secure(true)
                        .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();
        assertTrue(responseBody.contains("<meta name=\"description\" content=\"admin page\"/>"));
    }

    @Test
    @Transactional
    public void normalUserCanNotRetrieveAdminPageTest() throws Exception {
        UserDetails customUserDetails = userDetailsService.loadUserByUsername("Test");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword(), customUserDetails.getAuthorities());
        mockMvc.perform(get("/admin").secure(true)
                        .with(authentication(authentication)))
                .andExpect(status().is4xxClientError());
    }


    @Test
    @Transactional
    public void retrieveHomePageTest() throws Exception {
        assertNotNull(mockMvc);

        UserDetails customUserDetails = userDetailsService.loadUserByUsername("Test");
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUserDetails, customUserDetails.getPassword(), customUserDetails.getAuthorities());
        mockMvc.perform(get("/index").secure(true)
                .with(authentication(authentication)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<meta name=\"description\" content=\"index page\"/>")));
    }
}
