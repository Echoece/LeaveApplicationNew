package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.ManagerRemarkDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.service.ManagerService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("api/v1/manager")
@AllArgsConstructor
public class ManagerController {
    private final ManagerService managerService;

    // tested + need to fix the manager id from spring security
    @GetMapping("/all")
    public List<LeaveApplicationDTO> showPendingApplication(){
        return managerService.showPendingApplication();
    }

    // tested + done
    @PostMapping("/approve/{id}")
    public LeaveApplication approveApplication(@PathVariable("id") long id){
        return managerService.approveOrRejectApplication(id, Status.APPROVED);
    }

    // tested + done
    @PostMapping("/reject/{id}")
    public LeaveApplication rejectApplication(@PathVariable("id") long id){
        return managerService.approveOrRejectApplication(id, Status.REJECTED);
    }

    // not fully tested
    @GetMapping("/leaveBalance")
    public List<TotalLeaveDTO> showLeaveBalance(){
        int searchYear = Year.now().getValue();
        int managerId = 2; // TODO: get this id from security context

        return managerService.showLeaveBalance(managerId, searchYear);
    }

    // tested + done
    @PostMapping("/addRemark/{id}")
    public LeaveApplication addRemark(@PathVariable("id") long applicationId, @RequestBody ManagerRemarkDTO managerRemark){
        return managerService.putManagerRemark(applicationId, managerRemark.getValue());
    }


}
