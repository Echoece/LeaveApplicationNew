package com.example.leaveapplicationnew.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TotalLeaveDTO {
    private String status;
    private long leaveTypeId;
    private int maxAllowedLeave;
    private String leaveTypeName;
    private long userId;
    private String userName;
    private int totalLeave;
}
