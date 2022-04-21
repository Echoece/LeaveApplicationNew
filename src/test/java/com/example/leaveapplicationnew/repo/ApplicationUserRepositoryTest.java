package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.auth.ApplicationUser;
import com.example.leaveapplicationnew.auth.ApplicationUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@SpringBootTest
class ApplicationUserRepositoryTest {

    @Autowired
    private ApplicationUserRepository userRepository;
    @Autowired
    private ApplicationUserRoleRepository roleRepository;

    @Test
    public void createUser(){
        ApplicationUserRole employeeRole = roleRepository.findFirstByName("EMPLOYEE");
        ApplicationUserRole managerRole = roleRepository.findFirstByName("MANAGER");
        ApplicationUserRole adminRole = roleRepository.findFirstByName("ADMIN");

        Date tokenExpireDate = Date.valueOf("2022-02-14");

        ApplicationUser employee = ApplicationUser.builder()
                .email("employee@gmail.com")
                .password("1234")
                .name("employee")
                .tokenExpireTime(tokenExpireDate)
                .token("JWT token here")
                .roles(Set.of(employeeRole))
                .build();

        ApplicationUser manager = ApplicationUser.builder()
                .email("manager@gmail.com")
                .password("1234")
                .name("manager")
                .tokenExpireTime(tokenExpireDate)
                .token("JWT token here")
                .roles(Set.of(managerRole))
                .build();

        ApplicationUser admin = ApplicationUser.builder()
                .email("admin@gmail.com")
                .password("1234")
                .name("admin")
                .tokenExpireTime(tokenExpireDate)
                .token("JWT token here")
                .roles(Set.of(adminRole))
                .build();

        userRepository.saveAll(List.of(employee,manager,admin));
    }

    @Test
    public void changePassword(){
        ApplicationUser user = userRepository.findById(4L).get();

        user.setPassword("1234");
        userRepository.save(user);
    }

    @Test
    public void getUser(){
        long userId = 4;
        System.out.println(userRepository.findById(userId));
    }
}
