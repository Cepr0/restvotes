package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.security.core.GrantedAuthority;
import restvotes.domain.base.LongId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static org.springframework.util.StringUtils.isEmpty;
import static restvotes.domain.entity.User.Role.ROLE_USER;

/**
 * Defines User entity
 * <p>Email is unique field
 * @author Cepro, 2016-11-25
 */
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends LongId {
    
    @NotEmpty
    @Column(nullable = false)
    @Length(min = 3)
    private String name;
    
    @Email
    @NotEmpty
    @Column(unique = true)
    private String email;
    
    @JsonProperty(access = WRITE_ONLY)
    @NotEmpty
    @Column(nullable = false)
    @Length(min = 6)
    private String password;
    
    @NotNull
    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean enabled = true;
    
    @Column(columnDefinition = "integer default '0'", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private Role role = ROLE_USER;
    
    @NotNull
    @Column(columnDefinition = "timestamp default now()", updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime registered;
    
    public User() {
        this(null, null, null, true, ROLE_USER, LocalDateTime.now());
    }
    
    public User(String name, String email, String password, Role role) {
        this(name, email, password, true, role, LocalDateTime.now());
    }

    public User(String name, String email, String password) {
        this(name, email, password, true, ROLE_USER, LocalDateTime.now());
    }
    
    public User update(String name, String email, String password) {
        if (!isEmpty(name)) this.name = name;
        if (!isEmpty(email)) this.email = email;
        if (!isEmpty(password)) this.password = password;
        return this;
    }
    
    /**
     * Defines user roles
     */
    public enum Role implements GrantedAuthority {
        ROLE_USER("User"), ROLE_ADMIN("Admin");
        
        Role(String title) {
            this.title = title;
        }
        
        private String title;
            
        @Override
        public String toString() {
            return title;
        }
    
        @Override
        public String getAuthority() {
            return name();
        }
    }
}
