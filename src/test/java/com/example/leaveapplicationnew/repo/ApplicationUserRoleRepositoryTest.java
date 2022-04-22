package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.ApplicationUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class ApplicationUserRoleRepositoryTest {
    @Autowired
    private ApplicationUserRoleRepository roleRepository;

    @Test
    public void createRole(){
        ApplicationUserRole role = ApplicationUserRole.builder()
                .label("administrator")
                .name("ADMIN")
                .build();

        ApplicationUserRole role1 = ApplicationUserRole.builder()
                .label("manager")
                .name("MANAGER")
                .build();

        ApplicationUserRole role2 = ApplicationUserRole.builder()
                .label("employee")
                .name("EMPLOYEE")
                .build();

        roleRepository.save(role2);
        roleRepository.save(role1);

    }
}
