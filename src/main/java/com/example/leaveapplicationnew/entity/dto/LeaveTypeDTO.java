package com.example.leaveapplicationnew.entity.dto;

import lombok.Data;

import java.time.Year;

@Data
public class LeaveTypeDTO {
    private String name;
    private String remark;
    private Year year;
}
