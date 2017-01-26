package restvotes;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
    
    private @NonNull UserService userService;
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and().csrf().disable()
                .authorizeRequests()
                    .antMatchers(GET, "/api?", "/api/profile/**").permitAll()
                    //.antMatchers(POST,"/api/userProfile?").permitAll()
                    .antMatchers(GET, "/api/**").authenticated()
                    //.antMatchers(GET, "/api/polls/**", "/api/menus/**", "/api/restaurants/**", "/api/userProfile?").authenticated()
                    .antMatchers(PUT, "/api/menus/*/vote").authenticated()
                    .antMatchers(POST, "/api/*").hasRole("ADMIN")
                    .antMatchers(PUT, "/api/*/*").hasRole("ADMIN")
                    .antMatchers(PATCH, "/api/*/*").hasRole("ADMIN")
                    .antMatchers(DELETE, "/api/*/*").hasRole("ADMIN");

        // http://stackoverflow.com/a/30819556/5380322
    }
}
