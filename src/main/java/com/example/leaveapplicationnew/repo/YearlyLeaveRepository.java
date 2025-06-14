package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.YearlyLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.List;

@Repository
public interface YearlyLeaveRepository extends JpaRepository<YearlyLeave, Long> {

    @Query(
            value = "SELECT maximum_day FROM yearly_leave WHERE year =:year AND leave_type_id = :leaveTypeId"
            ,nativeQuery = true
    )
    int findMaximumDayByYearAndLeaveTypeId(@Param("year")int year, @Param("leaveTypeId")long leaveTypeId);

    List<YearlyLeave> findAllByYear(Year year);
}
