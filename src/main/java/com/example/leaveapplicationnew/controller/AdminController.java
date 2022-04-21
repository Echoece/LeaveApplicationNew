package com.example.leaveapplicationnew.controller;

import com.example.leaveapplicationnew.auth.ApplicationUser;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.YearlyLeave;
import com.example.leaveapplicationnew.entity.dto.ApplicationUserDTO;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.LeaveTypeDTO;
import com.example.leaveapplicationnew.entity.dto.YearlyLeaveDTO;
import com.example.leaveapplicationnew.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin")
@AllArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // tested + done
    @GetMapping("/all")
    public List<LeaveApplicationDTO> viewAllLeave(){
        return adminService.viewAllLeave();
    }

    @PostMapping("/addUser")
    public ApplicationUser addUser(@RequestBody ApplicationUserDTO userDTO){
        return adminService.addUser(userDTO);
    }

    @PostMapping("/addYearlyLeave")
    public YearlyLeave allocateYearlyLeave(@RequestBody YearlyLeaveDTO yearlyLeaveDTO){
        return adminService.allocateYearlyLeave(yearlyLeaveDTO);
    }

    @PostMapping("/addLeaveType")
    public LeaveType createLeaveType(@RequestBody LeaveTypeDTO leavetypeDTO){
        return adminService.createLeaveType(leavetypeDTO);
    }
}
