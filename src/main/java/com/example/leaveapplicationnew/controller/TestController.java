package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.repo.ApplicationUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("test")
@AllArgsConstructor
public class TestController {
    private final ApplicationUserRepository userRepository;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void testing(Principal principal){
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
        System.out.println(principal);
        System.out.println("hello world");
        ApplicationUser user = userRepository.getApplicationUserByName(principal.getName()).orElseThrow(()->new UsernameNotFoundException("User not found"));
        System.out.println(user);
    }
}
