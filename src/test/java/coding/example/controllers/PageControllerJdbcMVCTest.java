package coding.example.controllers;

import coding.example.DbSetup;
import coding.example.TestConfig;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(PageController.class)
@ActiveProfiles(profiles = "jdbc")
@ContextConfiguration(classes={TestConfig.class})
public class PageControllerJdbcMVCTest {

    @Autowired
    DbSetup dbSetup;

    @Autowired
    private WebApplicationContext context;

    MockMvc mockMvc;

    @Autowired
    private Filter springSecurityFilterChain;

    @BeforeEach
    public void setup() {
        assertTrue(dbSetup.setUp());
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .addFilters(springSecurityFilterChain)
                .build();
    }


    @Test
    @Transactional
    public void retrieveLoginPageTest() throws Exception {
        assertNotNull(mockMvc);
        mockMvc.perform(get("/login").secure(true))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<meta name=\"description\" content=\"login page\"/>")));
    }

}
