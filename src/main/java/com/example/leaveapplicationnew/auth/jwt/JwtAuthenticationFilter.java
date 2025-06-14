package com.example.leaveapplicationnew.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    // Here we attempt to authenticate the user authentication request
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException
    {
        try{
            // Deserializing the authentication Request and map it to UsernameAndPasswordAuthenticationRequest using jackson
            UsernameAndPasswordAuthenticationRequest authenticationRequest = new ObjectMapper()
                    .readValue(
                            request.getInputStream(),
                            UsernameAndPasswordAuthenticationRequest.class
                    );

            //  Authentication interface has few implementations. Here using the UsernamePasswordAuthenticationToken class implementation.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()
            );

            return authenticationManager.authenticate(authentication);
        }catch (IOException e){
            throw new RuntimeException("Fail to authenticate" + e);
        }
    }

    // this method will run only when authentication is successful, here we will build the jwt token and add it to response header
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException
    {

        String token = Jwts.builder()
                .setSubject(authResult.getName())                                           // setting the username
                .claim(JwtConfig.AUTHORITIES, authResult.getAuthorities())                  // setting the authority
                .setIssuedAt(new Date())                                                    // issued at current time
                .setExpiration(java.sql.Date.valueOf(
                        LocalDate.now().plusDays(jwtConfig.getTokenExpirationAfterDays())
                ))                                                                          // setting the expiration date to  2 weeks
                .signWith(secretKey)                                                        // signing withe the secret key
                .compact();                                                                 // builds the jwt

        response.addHeader(jwtConfig.getAuthorizationHeader(), jwtConfig.getTokenPrefix() + token);    // finally we add the token to the response
    }
}
