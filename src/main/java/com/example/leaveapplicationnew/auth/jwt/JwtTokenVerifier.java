package com.example.leaveapplicationnew.auth.jwt;


import com.google.common.base.Strings;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public class JwtTokenVerifier extends OncePerRequestFilter {
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //get the token from header
        String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

        // if authorization header is null or empty, or it doesnt start with Bearer prefix, the request will be rejected in
        // next filter-chain as we didnt set the authentication here.
        if( Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())){
            filterChain.doFilter(request,response);
            return;
        }

        // extract the JWT from header
        String token = authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");

        try{
            // getting the claims (key value pair) from the token, Jws stands for signed JWT
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            // getting the body claims of the JWT
            Claims body = claimsJws.getBody();

            // extracting username and authorities
            String username = body.getSubject();
            List<Map<String, String>> authorities = (List<Map<String, String>>) body.get(JwtConfig.AUTHORITIES);

            // As our UsernamePasswordAuthenticationToken expects a collection<? extends GrantedAuthority>, we map the authorities as Set
            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = authorities.stream()
                    .map(element -> new SimpleGrantedAuthority( element.get("authority")))
                    .collect(Collectors.toSet());

            // creating the authentication object
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorities);

            // setting the authentication to the security context, essentially authenticating the user
            SecurityContextHolder.getContext().setAuthentication(authentication);

            //SecurityContextHolder.getContext().getAuthentication().getAuthorities().forEach(System.out::println);

        }catch (JwtException ex) {
            //this is very important, since it guarantees the user is not authenticated at all
            SecurityContextHolder.clearContext();
            throw new IllegalStateException(String.format("Token %s cant be trusted", token ));
        }
    }
}
