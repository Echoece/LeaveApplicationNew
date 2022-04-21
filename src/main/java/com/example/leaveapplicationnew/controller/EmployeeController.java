package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Year;
import java.util.List;

@RestController
@RequestMapping("api/v1/employee")
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/addApplication")
    public LeaveApplication createLeaveApplication(@RequestBody LeaveApplicationDTO applicationDTO){
        return employeeService.createApplication(applicationDTO);
    }

    @GetMapping("/viewApplication")
    public List<LeaveApplicationDTO> viewAllApplication(){
        long userId = 4;        // TODO: get the id from security context
        return employeeService.viewAllApplicationForUser(userId);
    }

    @PutMapping("/editApplication")
    public LeaveApplication editApplication(@RequestBody LeaveApplicationDTO leaveApplicationDTO){
        return employeeService.editApplication(leaveApplicationDTO);
    }

    @GetMapping("/submitApplication/{id}")
    public void submitApplication(@PathVariable("id") long id){
        employeeService.sendApplicationForApproval(id);
    }


    public List<TotalLeaveDTO>  showLeaveBalance(){
        int searchYear = Year.now().getValue();
        long userId = 4;  // TODO: get from security context

        return employeeService.showLeaveBalanceForEmployee(userId, searchYear);
    }


}
