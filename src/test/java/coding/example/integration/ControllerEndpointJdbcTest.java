package coding.example.integration;

import coding.example.TestConfig;
import coding.example.controllers.PageController;
import coding.example.security.CustomAuthenticationProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest(classes = {TestConfig.class})
@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(PageController.class)
@ActiveProfiles("jdbc")
public class ControllerEndpointJdbcTest {

    private final MockMvc mvc;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final SecurityContext context;

    @Autowired
    ControllerEndpointJdbcTest(MockMvc mvc, CustomAuthenticationProvider customAuthenticationProvider){
        this.mvc = mvc;
        this.customAuthenticationProvider = customAuthenticationProvider;
        context = SecurityContextHolder.getContext();
    }

    @Test
    @Transactional
    public void helloRequestTest() throws Exception {
        mvc.perform(get("/hello/dude").secure(true)).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Hello dude")));
    }
}
