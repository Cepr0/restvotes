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

import static org.springframework.http.HttpMethod.POST;

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
        http.httpBasic().and()
            .anonymous().and()
            .authorizeRequests().antMatchers(POST,"/api/userProfile", "/api/userProfile/").anonymous().and()
            .authorizeRequests().antMatchers("/api/**").authenticated().and()
            .csrf().disable();
    }
}
