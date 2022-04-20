package com.example.leaveapplicationnew.service;

import com.example.leaveapplicationnew.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.repo.LeaveApplicationRepository;
import com.example.leaveapplicationnew.repo.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {
    private LeaveApplicationRepository leaveApplicationRepository;
    private LeaveTypeRepository leaveTypeRepository;

    // need to add user Id
    public LeaveApplication createApplication(LeaveApplicationDTO applicationDTO){
        if(applicationDTO==null){
            return null;
        }
        LeaveType leaveType =
                leaveTypeRepository.findById(applicationDTO.getLeaveTypeID())
                .orElseThrow(RuntimeException::new);

        // TODO: get the current logged in user and add the userId in the application object

        LeaveApplication application = LeaveApplication.builder()
                .fromDate(applicationDTO.getFromDate())
                .toDate(applicationDTO.getToDate())
                .remark(applicationDTO.getRemark())
                .leaveType(leaveType)
                .build();

        return leaveApplicationRepository.save(application);
    }

    // need to add user id
    public LeaveApplication editApplication(LeaveApplicationDTO applicationDTO){
        if(applicationDTO==null){
            return null;
        }
        LeaveType leaveType = leaveTypeRepository.findById(applicationDTO.getLeaveTypeID())
                        .orElseThrow(RuntimeException::new);

        LeaveApplication application = leaveApplicationRepository.findById(applicationDTO.getId())
                .orElseThrow(RuntimeException::new);

        // TODO: get the current logged in user and add the userId in the application object

        application.setRemark(applicationDTO.getRemark());
        application.setFromDate(applicationDTO.getFromDate());
        application.setToDate(applicationDTO.getToDate());
        application.setLeaveType(leaveType);

        return leaveApplicationRepository.save(application);
    }

    // done
    public List<LeaveApplication> showAllApplication(){
        return leaveApplicationRepository.findAll();
    }
    // done
    public LeaveApplication submitApplication(long applicationId){
        LeaveApplication application = leaveApplicationRepository.findById(applicationId)
                .orElseThrow(RuntimeException::new);

        application.setStatus(Status.PENDING);

        return leaveApplicationRepository.save(application);
    }
}
