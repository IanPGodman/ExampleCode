package coding.example.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;


public class CustomAuthenticationFailureHandler  extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger log = LogManager.getLogger();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        String errorMessage = exception.getMessage();
        log.debug("Authentication failed for '{}' with exception: '{}'", username, errorMessage);

        super.onAuthenticationFailure(request, response, exception);
    }
}
