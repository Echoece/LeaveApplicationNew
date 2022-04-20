package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.auth.ApplicationUser;
import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.concurrent.TimeUnit;


@SpringBootTest
//@DataJpaTest
class LeaveApplicationRepositoryTest {
    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Test
    public void createApplication(){
        Date fromDate = Date.valueOf("2020-7-10");
        Date toDate = Date.valueOf("2020-7-20");

        LeaveType leaveType = leaveTypeRepository.findById(2L).get();
        ApplicationUser user = userRepository.findById(4L).get();

        LeaveApplication leaveApplication = LeaveApplication.builder()
                .remark("Sick application 2")
                .leaveType(leaveType)
                .toDate(toDate)
                .fromDate(fromDate)
                .status(Status.APPROVED)
                .user(user)
                .build();

        leaveApplicationRepository.save(leaveApplication);

    }

    @Test
    public void daysLeaveTaken(){
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(1L).orElseThrow(RuntimeException::new);

        Date fromDate = leaveApplication.getFromDate();
        Date toDate = leaveApplication.getToDate();

        // TimeUnit is an enum, DAYS enum has constructor value of microsecond converted to 24hour.
        long days = TimeUnit.DAYS.convert(
                Math.abs(
                        toDate.getTime() - fromDate.getTime()
                ),
                TimeUnit.MILLISECONDS
        );
        System.out.println(days);
    }
}
