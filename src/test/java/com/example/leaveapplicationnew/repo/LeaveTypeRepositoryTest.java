package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.entity.LeaveType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LeaveTypeRepositoryTest {
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;


    @Test
    public void createLeaveType(){
        LeaveType leaveType = LeaveType.builder()
                .name("Sick")
                .remark("Sick leave for employee")
                .build();

        leaveTypeRepository.save(leaveType);
    }
}
