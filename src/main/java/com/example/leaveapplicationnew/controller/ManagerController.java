package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.ManagerRemarkDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.service.ManagerService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/manager")
@AllArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    // tested + need to fix the manager id from spring security
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<LeaveApplicationDTO> showPendingApplication(){
        return managerService.showPendingApplication();
    }

    // tested + done
    @PostMapping("/approve/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public LeaveApplication approveApplication(@PathVariable("id") long id){
        return managerService.approveOrRejectApplication(id, Status.APPROVED);
    }

    // tested + done
    @PostMapping("/reject/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    public LeaveApplication rejectApplication(@PathVariable("id") long id){
        return managerService.approveOrRejectApplication(id, Status.REJECTED);
    }

    // not fully tested
    @GetMapping("/leaveBalance")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<TotalLeaveDTO> showLeaveBalance(){
        return managerService.showLeaveBalance();
    }

    // tested + done
    @PostMapping("/addRemark/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public LeaveApplication addRemark(@PathVariable("id") long applicationId, @RequestBody ManagerRemarkDTO managerRemark){
        return managerService.putManagerRemark(applicationId, managerRemark.getValue());
    }


}
