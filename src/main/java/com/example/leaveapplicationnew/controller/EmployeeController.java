package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employee")
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    // tested + done,
    @PostMapping("/addApplication")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public LeaveApplication createLeaveApplication(@RequestBody LeaveApplicationDTO applicationDTO){
        return employeeService.createApplication(applicationDTO);
    }

    // done + tested
    @GetMapping("/viewApplication")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public List<LeaveApplicationDTO> viewAllApplication(){
        return employeeService.viewAllApplicationForUser();
    }

    // tested + done
    @PutMapping("/editApplication")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public LeaveApplication editApplication(@RequestBody LeaveApplicationDTO leaveApplicationDTO){
        return employeeService.editApplication(leaveApplicationDTO);
    }

    // done + tested
    @GetMapping("/submitApplication/{id}")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public void submitApplication(@PathVariable("id") long id){
        employeeService.sendApplicationForApproval(id);
    }


    // done + tested
    @GetMapping("/leaveBalance")
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    public List<TotalLeaveDTO>  showLeaveBalance(){
        return employeeService.showLeaveBalanceForEmployee();
    }


}
