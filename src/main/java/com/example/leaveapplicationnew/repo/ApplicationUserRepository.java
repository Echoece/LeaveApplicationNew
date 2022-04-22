package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> getApplicationUserByEmail(String email);
    Optional<ApplicationUser> getApplicationUserByName(String name);
}
