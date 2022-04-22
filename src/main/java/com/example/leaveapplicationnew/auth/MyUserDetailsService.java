package com.example.leaveapplicationnew.auth;

import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.repo.ApplicationUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final ApplicationUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return userRepository.getApplicationUserByEmail(username)
                .map(this::createUser)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User name %s not found",username)));

    }

    // ROLE_ prefix is added to each role
    private MyUserDetails createUser(ApplicationUser user){
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getName()))
                .collect(Collectors.toSet());

        return MyUserDetails.builder()
                .username(user.getName())
                .password(user.getPassword())
                .authorities(authorities)
                .accountNonLocked(true)
                .accountNonExpired(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();
    }
}
