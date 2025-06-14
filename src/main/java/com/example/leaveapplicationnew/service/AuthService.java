package com.example.leaveapplicationnew.service;


import com.example.leaveapplicationnew.auth.jwt.JwtUtils;
import com.example.leaveapplicationnew.auth.security.SecurityUtils;
import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.entity.ApplicationUserRole;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.repo.ApplicationUserRepository;
import com.example.leaveapplicationnew.repo.ApplicationUserRoleRepository;
import com.example.leaveapplicationnew.repo.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthService {
    private final ApplicationUserRepository userRepository;
    private final ApplicationUserRoleRepository roleRepository;
    private final JwtUtils jwtUtils;
    private final LeaveTypeRepository leaveTypeRepository;

    public void resetPassword(String newPassword){
        String username = SecurityUtils.getCurrentLoggedInUserName().orElseThrow(RuntimeException::new);

        ApplicationUser user = userRepository.findApplicationUsersByEmail(username).orElseThrow(RuntimeException::new);
        user.setPassword(newPassword);

        userRepository.save(user);
    }

    public Map<String, Object> login(Map<String, String> loginRequest) {
        ApplicationUser user = userRepository
                .findApplicationUsersByEmail(loginRequest.get("email"))
                .orElseThrow(()->new RuntimeException("Incorrect email"));

        if (!user.getPassword().equals(loginRequest.get("password"))){
            throw  new RuntimeException("Incorrect password");
        }

        user.setToken(jwtUtils.generateToken(user));
        return Map.of("data", user);
    }

    public Map<String, Object> registerUser(Map<String, Object> loginRequest) {
        ApplicationUser user = new ApplicationUser();
        user.setEmail(loginRequest.get("email").toString());
        user.setPassword(loginRequest.get("password").toString());

        List<Map<String, Object>> roleList = (List<Map<String, Object>>) loginRequest.get("roles");
        if (Objects.nonNull(roleList)){
            Set<ApplicationUserRole> roles = roleList.stream()
                    .map(roleMap -> {
                        Long id = Long.valueOf(roleMap.get("id").toString());
                        ApplicationUserRole role = new ApplicationUserRole();
                        role.setRoleId(id);
                        return role;
                    })
                    .collect(Collectors.toSet());

            user.setRoles(roles);
        }

        ApplicationUser savedUser = userRepository.save(user);
        savedUser.setToken(jwtUtils.generateToken(savedUser));

        return Map.of("data", savedUser);
    }

    public Map<String, Object> addRole(Map<String, String> request) {
        ApplicationUserRole role = new ApplicationUserRole();
        role.setLabel(request.get("label"));
        role.setName(request.get("name"));

        return Map.of("data", roleRepository.save(role));
    }

    public Map<String, Object> addUserRole(Map<String, Long> request) {
        Long roleId = request.get("roleId");
        Long userId = request.get("userId");

        ApplicationUser user = userRepository
                .findById(userId)
                .orElseThrow(RuntimeException::new);

        ApplicationUserRole role = roleRepository
                .findById(roleId)
                .orElseThrow(RuntimeException::new);

        role.getUsers().add(user);
        roleRepository.save(role);

        return Map.of("data", "success");
    }

    public List<LeaveType> getLeaveTypes() {
        return leaveTypeRepository.findAll();
    }
}
