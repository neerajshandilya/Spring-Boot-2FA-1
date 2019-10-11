package lol.gilliard.springboot2fa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    TwoFAAuthenticationDetailsSource authenticationDetailsSource;

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/user/registration").permitAll()
                .antMatchers("/user/2fa").permitAll()
                .antMatchers("/login*").permitAll()
                .antMatchers("/").permitAll()
                .anyRequest().authenticated()
            .and().headers().frameOptions().sameOrigin() // for h2-console
            .and()
                .formLogin()
                .authenticationDetailsSource(authenticationDetailsSource)
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
            .and()
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/");
    }

}
