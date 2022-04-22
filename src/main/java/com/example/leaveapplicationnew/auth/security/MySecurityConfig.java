package com.example.leaveapplicationnew.auth.security;


import com.example.leaveapplicationnew.auth.MyUserDetailsService;
import com.example.leaveapplicationnew.auth.jwt.JwtAuthenticationFilter;
import com.example.leaveapplicationnew.auth.jwt.JwtConfig;
import com.example.leaveapplicationnew.auth.jwt.JwtTokenVerifier;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@AllArgsConstructor
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MySecurityConfig extends WebSecurityConfigurerAdapter {
    private final String[] WHITE_LIST_URL = {
            "/",
            "index",
            "/css/*",
            "/js/*",
            "/api/v1/auth/**",
            "/login",
            "/test/**"
    };

    private final PasswordEncoder passwordEncoder;
    private final MyUserDetailsService userDetailsService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()                           // making session stateless, No session will be created or used by spring security
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig, secretKey), JwtAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(WHITE_LIST_URL).permitAll()
                .anyRequest()
                .authenticated();
    }


    // Here we configure authentication provider for database connectivity, DaoAuthenticationProvider is one of the implementation of the AuthenticationManager.
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);

        //auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);

        auth.authenticationProvider(provider);
    }
}
