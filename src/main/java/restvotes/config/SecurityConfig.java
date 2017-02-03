package restvotes.config;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import restvotes.service.UserService;

import static org.springframework.http.HttpMethod.*;

/**
 * @author Cepro, 2017-01-22
 */
@RequiredArgsConstructor
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    private final @NonNull UserService userService;
    
    private final @NonNull RepositoryRestConfiguration configuration;
    
    // https://spring.io/guides/tutorials/react-and-spring-data-rest/#react-and-spring-data-rest-part-5
    public static final PasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(PASSWORD_ENCODER);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        
        String BASE = configuration.getBasePath().getPath();
        
        String[] ROOT_BASE_AND_PROFILE = {"/*", BASE, BASE + "/", BASE + "/profile/**"};
        String[] USER_PROFILE = {BASE + "/userProfile", BASE + "/userProfile/"};
        String[] COMMON_ELEMENTS = {BASE + "/polls/**", BASE + "/menus/**", BASE + "/restaurants/**", BASE + "/userProfile/**"};
        String VOTE = BASE + "/menus/*/vote";
    
        http.httpBasic().realmName("restvotes").and().csrf().disable().authorizeRequests()
            .antMatchers(GET, ROOT_BASE_AND_PROFILE).permitAll()
            .antMatchers(POST, USER_PROFILE).permitAll()
            .antMatchers(GET, COMMON_ELEMENTS).authenticated()
            .antMatchers(PUT, VOTE).authenticated()
            .anyRequest().hasRole("ADMIN");
        
        // http://stackoverflow.com/a/30819556/5380322
        // http://stackoverflow.com/a/22636142/5380322
    }
}
