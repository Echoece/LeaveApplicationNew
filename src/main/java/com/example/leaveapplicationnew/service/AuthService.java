package com.example.leaveapplicationnew.service;


import com.example.leaveapplicationnew.auth.security.SecurityUtils;
import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.repo.ApplicationUserRepository;
import com.example.leaveapplicationnew.repo.ApplicationUserRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final ApplicationUserRepository userRepository;
    private final ApplicationUserRoleRepository roleRepository;

    public void resetPassword(String newPassword){
        String username = SecurityUtils.getCurrentLoggedInUserName().orElseThrow(RuntimeException::new);

        ApplicationUser user = userRepository.getApplicationUserByName(username).orElseThrow(RuntimeException::new);
        user.setPassword(newPassword);

        userRepository.save(user);
    }
}
