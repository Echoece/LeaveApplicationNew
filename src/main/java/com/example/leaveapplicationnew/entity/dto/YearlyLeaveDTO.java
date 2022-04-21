package com.example.leaveapplicationnew.entity.dto;


import lombok.Data;

@Data
public class YearlyLeaveDTO {
    private long leaveTypeId;
    private int year;
    private int maximumDay;
}
