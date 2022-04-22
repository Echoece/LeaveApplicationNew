package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.YearlyLeave;
import com.example.leaveapplicationnew.entity.dto.ApplicationUserDTO;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.LeaveTypeDTO;
import com.example.leaveapplicationnew.entity.dto.YearlyLeaveDTO;
import com.example.leaveapplicationnew.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // tested + done
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<LeaveApplicationDTO> viewAllLeave(){
        return adminService.viewAllLeave();
    }

    // done + tested
    @PostMapping("/addUser")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApplicationUser addUser(@RequestBody ApplicationUserDTO userDTO){
        return adminService.addUser(userDTO);
    }

    // done + tested
    @PostMapping("/addYearlyLeave")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public YearlyLeave allocateYearlyLeave(@RequestBody YearlyLeaveDTO yearlyLeaveDTO){
        return adminService.allocateYearlyLeave(yearlyLeaveDTO);
    }

    // done + tested
    @PostMapping("/addLeaveType")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public LeaveType createLeaveType(@RequestBody LeaveTypeDTO leavetypeDTO){
        return adminService.createLeaveType(leavetypeDTO);
    }

    @PostMapping("/setManager/{mId}/{eId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void setManagerToEmployee(@PathVariable("eId") long employeeId, @PathVariable("mId") long managerId){
        adminService.setManagerToEmployee(employeeId, managerId);
    }
}
