package coding.example.security;

import coding.example.config.ApplicationData;
import coding.example.database.entity.user.UserDetail;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;


public class CustomAuthenticationProvider implements AuthenticationProvider {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ApplicationData.class);

    private final UserDetail userDetailsService;


    public CustomAuthenticationProvider(UserDetail userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (userDetails != null) {
            // Validate credentials (e.g., compare password)
            if (userDetails.getPassword() != null){
                logger.debug("Passsword: '{}'", userDetails.getPassword());
            }
            if (!userDetailsService.verifyPassword(password, userDetails.getPassword())){
                throw new AuthenticationException("bad password") {
                };
            }
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
