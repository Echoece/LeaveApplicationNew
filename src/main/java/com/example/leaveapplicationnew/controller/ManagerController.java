package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
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

    @GetMapping("/all")
    public List<LeaveApplicationDTO> showPendingApplication(){
        return managerService.showPendingApplication();
    }

    @PostMapping("/approve/{id}")
    public LeaveApplication approveApplication(@PathVariable("id") long id){
        return managerService.approveOrRejectApplication(id, Status.APPROVED);
    }

    @PostMapping("/reject/{id}")
    public LeaveApplication rejectApplication(@PathVariable("id") long id){
        return managerService.approveOrRejectApplication(id, Status.REJECTED);
    }

    @GetMapping("/leaveBalance")
    public List<TotalLeaveDTO> showLeaveBalance(){
        int searchYear = Year.now().getValue();
        int managerId = 2; // TODO: get this id from security context

        return managerService.showLeaveBalance(managerId, searchYear);
    }

    @PostMapping("/addRemark/{id}")
    public LeaveApplication addRemark(@PathVariable("id") long id, @RequestBody String managerRemark){
        return managerService.putManagerRemark(id, managerRemark);
    }


}
