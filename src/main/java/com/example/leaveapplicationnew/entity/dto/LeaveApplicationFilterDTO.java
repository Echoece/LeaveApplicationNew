package com.example.leaveapplicationnew.entity.dto;

import com.example.leaveapplicationnew.entity.Status;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.sql.Date;

public class LeaveApplicationFilterDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date endDate;

    private long leaveTypeId;
    private Status status;

}
