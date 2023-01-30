package com.csye6225.Config;


import com.csye6225.POJO.User;
import com.csye6225.Service.UserService;
import com.csye6225.Util.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    @Autowired
    UserService userService;

    @Autowired
    BCryptPasswordEncoder bcryptPasswordEncoder;
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> web.ignoring().requestMatchers("/v1/user/","/v1/user","/healthz","/healthz/");
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        return new AuthenticationProvider(){

            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = authentication.getCredentials().toString();

                UserDetails user = userService.loadUserByUsername(username);
                if(bcryptPasswordEncoder.matches(password, user.getPassword())){
                    log.info("Access successful");
                    return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
                }else{
                    log.error("Access denied, The username of password is wrong");
                    throw new BadCredentialsException("The username of password is wrong");
                }

            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        };
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf().disable().authorizeHttpRequests((auth)->{
            auth.anyRequest().authenticated();
        }).httpBasic(withDefaults());
        return http.build();
    }
}

