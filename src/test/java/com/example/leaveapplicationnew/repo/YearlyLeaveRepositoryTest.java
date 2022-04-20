package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.YearlyLeave;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Year;

@SpringBootTest
class YearlyLeaveRepositoryTest {
    @Autowired
    private YearlyLeaveRepository yearlyLeaveRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Test
    public void addYearlyLeave(){
        LeaveType leaveType = leaveTypeRepository.findFirstByName("Sick").orElseThrow(RuntimeException::new);
        Year year = Year.of(2020);

        YearlyLeave yearlyLeave = YearlyLeave.builder()
                .leaveType(leaveType)
                .year(year)
                .maximumDay(22)
                .build();

        yearlyLeaveRepository.save(yearlyLeave);
    }

    @Test
    public void showYearlyLeave(){
        YearlyLeave yearlyLeave = yearlyLeaveRepository.findById(2L).orElseThrow(RuntimeException::new);

        System.out.println(yearlyLeave);
    }
}
