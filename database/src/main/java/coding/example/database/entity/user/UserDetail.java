package coding.example.database.entity.user;

import coding.example.database.DBAccess;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Transactional
public class UserDetail implements UserDetailsService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserDetail.class);


    private final DBAccess database;
    private final PasswordEncoder passwordEncoder;

    public UserDetail(DBAccess database, PasswordEncoder passwordEncoder) {
        this.database = database;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean verifyPassword(String password, String encodedPassword){
        return passwordEncoder.matches(password, encodedPassword);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = database.findByUserName(username);
        logger.info("loadUserByUsername retrieved: {}", user.isPresent() ? user.get() : "NOTHING");
        if(user.isEmpty() || !user.get().getEnabled().equals("Y")){
            throw new UsernameNotFoundException("User not found or disabled: " + username);
        }
        return new org.springframework.security.core.userdetails.User(username, user.get().getPassword(), user.get().getGrantedAuthority());
    }
}
