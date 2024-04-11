package coding.example.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.mock;

public class PageContollerTestAbstractBase {
    protected PageController pageController;
    protected final HttpServletRequest request;
    protected final HttpSession session;

    protected final static String LOG_IN_TEMPLATE_NAME = "login";
    protected final static String INDEX_TEMPLATE_NAME = "index";
    protected final static String ERROR_WITH_MESSAGE_TEMPLATE_NAME = "error-with-message";
    protected final static String LOGIN_ERROR_TEMPLATE_NAME = "loginerror";
    protected final static String SELECTED_VEG_TEMPLATE_NAME = "selected-veg";
    protected final static String SUGGESTIONS_TEMPLATE_NAME = "suggestions";

    protected final VegDetails parsnipDets = new VegDetails("Parsnip","parsnip_lse.jpg");
    protected final VegDetails tomatoVegDets = new VegDetails("Tomato", "tomato", "tomato.jpg");
    protected final VegDetails redPepperVegDets = new VegDetails("Red pepper","red_pepper.jpg");
    protected final VegDetails carrotVegDets = new VegDetails("Carrot","carrors_lse.jpg");

    public PageContollerTestAbstractBase() {
        request = mock(HttpServletRequest.class);
        session = new MockHttpSession();
    }

    protected void setupSecurity(boolean addAdmin){
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority("ROLE_USER");
        List<SimpleGrantedAuthority> updatedAuthorities = new ArrayList<>();
        updatedAuthorities.add(userAuthority);
        if (addAdmin){
            updatedAuthorities.add(adminAuthority);
        }

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("Test", "p455word", updatedAuthorities)
        );
    }

}
