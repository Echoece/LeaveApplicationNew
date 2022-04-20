package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

}
