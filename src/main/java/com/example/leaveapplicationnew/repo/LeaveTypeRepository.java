package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    Optional<LeaveType> findFirstByName(String name);
}
