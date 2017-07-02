package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.springframework.security.core.GrantedAuthority;
import restvotes.RestVotes;
import restvotes.domain.base.LongId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.ZonedDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;
import static org.springframework.util.StringUtils.isEmpty;
import static restvotes.config.SecurityConfig.PASSWORD_ENCODER;
import static restvotes.domain.entity.User.Role.ROLE_USER;

/**
 * Defines User entity
 * <p>Email is unique field
 * @author Cepro, 2016-11-25
 */
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends LongId {
    
    public static final int NAME_MIN_LEN = 3;
    public static final int PASSWORD_MIN_LEN = 6;
    public static final String NAME_PATTERN = "[A-zА-Яа-я .]{" + NAME_MIN_LEN + ",}";
    public static final String PASSWORD_PATTERN = ".{" + PASSWORD_MIN_LEN + ",}";
    public static final String EMAIL_PATTERN = "[A-z0-9._%+-]+@[A-z0-9.-]+\\.[A-z]{2,6}$";
    
    @NotNull(message = "valid.field")
    @Pattern(regexp = NAME_PATTERN, message = "valid.username")
    @Column(nullable = false)
    private String name;
    
    @Email(regexp = EMAIL_PATTERN, message = "valid.email")
    @NotNull(message = "valid.field_must_not_be_null")
    @Column(unique = true)
    private String email;
    
    @JsonProperty(access = WRITE_ONLY)
    @NotNull(message = "valid.field")
    @Pattern(regexp = PASSWORD_PATTERN, message = "valid.password")
    @Column(nullable = false)
    private String password;
    
    @NotNull(message = "valid.field")
    @Column(columnDefinition = "boolean default true", nullable = false)
    private boolean enabled = true;
    
    @Column(columnDefinition = "integer default '0'", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "valid.field")
    private Role role = ROLE_USER;
    
    @NotNull(message = "valid.field")
    @Column(columnDefinition = "timestamp default now()", updatable = false, nullable = false)
    @JsonFormat(timezone = RestVotes.TIME_ZONE, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime registered;
    
    public User(String name, String email, String password, boolean enabled, Role role, ZonedDateTime registered) {
        this.name = name;
        this.email = email;
        setPassword(password);
        this.enabled = enabled;
        this.role = role;
        this.registered = registered;
    }
    
    public User() {
        this(null, null, null, true, ROLE_USER, ZonedDateTime.now());
    }
    
    public User(String name, String email, String password, Role role) {
        this(name, email, password, true, role, ZonedDateTime.now());
    }

    public User(String name, String email, String password) {
        this(name, email, password, true, ROLE_USER, ZonedDateTime.now());
    }
    
    public User update(String name, String email, String password) {
        if (!isEmpty(name)) setName(name);
        if (!isEmpty(email)) setEmail(email);
        if (!isEmpty(password)) setPassword(password);
        return this;
    }
    
    public User update(User user) {
        String name = user.getName();
        String email = user.getEmail();
        String password = user.getPassword();
        boolean enabled = user.isEnabled();
        Role role = user.getRole();
        ZonedDateTime registered = user.getRegistered();
    
        if (!isEmpty(name)) setName(name);
        if (!isEmpty(email)) setEmail(email);
        if (!isEmpty(password)) setPassword(password);
        if (!isEmpty(enabled)) setEnabled(enabled);
        if (!isEmpty(role)) setRole(role);
        if (!isEmpty(registered)) setRegistered(registered);

        return this;
    }
    
    public User setPassword(String password) {
        this.password = password;// != null ? PASSWORD_ENCODER.encode(password) : null;
        return this;
    }
    
    @PrePersist
    private void encodePassword() {
        password = (password != null) ? PASSWORD_ENCODER.encode(password) : null;
    }
    
    // TODO Add projection for UserProfile?
    /**
     * Defines user roles
     */
    public enum Role implements GrantedAuthority {

        ROLE_USER("User"), ROLE_ADMIN("Admin");
    
        private String title;
        
        Role(String title) {
            this.title = title;
        }
            
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
