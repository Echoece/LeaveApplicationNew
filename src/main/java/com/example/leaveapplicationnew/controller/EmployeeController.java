package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/employee")
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    // tested + done, todo- need to add user id from context
    @PostMapping("/addApplication")
    public LeaveApplication createLeaveApplication(@RequestBody LeaveApplicationDTO applicationDTO){
        return employeeService.createApplication(applicationDTO);
    }

    // done + tested
    // TODO: get the id from security context
    @GetMapping("/viewApplication")
    public List<LeaveApplicationDTO> viewAllApplication(){
        long userId = 4;
        return employeeService.viewAllApplicationForUser(userId);
    }

    // TODO: get the id from security context
    // tested + done otherwise
    @PutMapping("/editApplication")
    public LeaveApplication editApplication(@RequestBody LeaveApplicationDTO leaveApplicationDTO){
        return employeeService.editApplication(leaveApplicationDTO);
    }



    // utility method year is hardcoded atm, need fix
    @GetMapping("/submitApplication/{id}")
    public void submitApplication(@PathVariable("id") long id){
        employeeService.sendApplicationForApproval(id);
    }


    // done + tested // current year is hardcoded atm
    @GetMapping("/leaveBalance")
    public List<TotalLeaveDTO>  showLeaveBalance(){
        //int searchYear = Year.now().getValue();
        int searchYear = 2020;
        long userId = 4;  // TODO: get from security context

        return employeeService.showLeaveBalanceForEmployee(userId, searchYear);
    }


}
