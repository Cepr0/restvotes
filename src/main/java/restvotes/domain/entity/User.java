package restvotes.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import restvotes.domain.base.LongId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

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
    
    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private Role role = Role.ROLE_USER;
    
    @NotNull
    @Column(columnDefinition = "timestamp default now()", updatable = false, nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime registered;
    
    public User() {
        this(null, null, null, false, null, LocalDateTime.now());
    }
    
    public User(String name, String email, String password, Role role) {
        this(name, email, password, true, role, LocalDateTime.now());
    }
    
    /**
     * Defines user roles
     */
    public static enum Role {
        ROLE_USER("User"), ROLE_ADMIN("Admin");
        
        Role(String title) {
            this.title = title;
        }
        
        private String title;
            
        @Override
        public String toString() {
            return title;
        }
    }
}
