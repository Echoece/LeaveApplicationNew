package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.auth.ApplicationUserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationUserRoleRepository extends JpaRepository<ApplicationUserRole, Long> {
    ApplicationUserRole findFirstByName(String name);
}
