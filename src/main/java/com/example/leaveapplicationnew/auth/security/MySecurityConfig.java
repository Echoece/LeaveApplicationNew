package com.example.leaveapplicationnew.auth.security;


import com.example.leaveapplicationnew.auth.MyUserDetailsService;
import com.example.leaveapplicationnew.auth.jwt.JwtAuthenticationFilter;
import com.example.leaveapplicationnew.auth.jwt.JwtConfig;
import com.example.leaveapplicationnew.auth.jwt.JwtTokenVerifier;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.OPTIONS;

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
                .cors(cors-> cors.configurationSource(corsConfigurationSource()))
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

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        List<String> allowedOrigins = List.of("http://localhost:4200", "production api here");
        List<String> allowedHttpHeaders = List.of(HttpHeaders.AUTHORIZATION, HttpHeaders.CONTENT_TYPE);
        List<String> allowedHttpMethods = List.of(
                GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name()
        );
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedHttpMethods);
        configuration.setAllowedHeaders(allowedHttpHeaders);
//        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);

        return source;
    }
}
