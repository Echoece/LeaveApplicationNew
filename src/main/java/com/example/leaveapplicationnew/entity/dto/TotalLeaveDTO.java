package com.example.leaveapplicationnew.entity.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class TotalLeaveDTO {
    private long leaveTypeId;
    private long userId;
    private int totalLeave;

    private String userName;
    private String leaveTypeName;
    private int maxAllowedLeave;
}
