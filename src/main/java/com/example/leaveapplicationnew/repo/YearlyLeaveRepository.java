package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.YearlyLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YearlyLeaveRepository extends JpaRepository<YearlyLeave, Long> {
}
