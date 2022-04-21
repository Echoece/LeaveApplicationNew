package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.auth.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    @Query(
            value = "SELECT id FROM user WHERE manager_id = :managerId "
            ,nativeQuery = true
    )
    public List<Integer> userIdUnderManager(@Param("managerId") int managerId);
}
