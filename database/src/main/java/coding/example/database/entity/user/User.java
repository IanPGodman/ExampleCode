package coding.example.database.entity.user;

import coding.example.database.entity.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "app_user")
public class User implements UserInterface {

    @Id
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;


    private String email;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "app_user_roles",
            joinColumns = @JoinColumn(name = "user_userId"),
            inverseJoinColumns = @JoinColumn(name = "roles_id"))

    private Set<Role> roles = new LinkedHashSet<>();


    private String password;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @Setter
    private String enabled;

    public Set<SimpleGrantedAuthority>getGrantedAuthority() {
        return roles.stream().map((a -> new SimpleGrantedAuthority(a.getName()))).collect(Collectors.toSet());
    }
}
