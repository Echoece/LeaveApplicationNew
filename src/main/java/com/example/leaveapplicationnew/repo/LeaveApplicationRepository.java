package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {

    @Query(
          value = "SELECT * FROM leave_application WHERE user_id=:userId",
          nativeQuery = true
    )
    List<LeaveApplication> findAllByUser(@Param("userId") long userId);


    @Query(
            value = "SELECT * FROM leave_application WHERE user_id IN (:userIds) AND status = 'PENDING'"
            ,nativeQuery = true
    )
    List<LeaveApplication> findListOfPendingApplicationForManager(@Param("userIds") List<Integer> userId);

}
