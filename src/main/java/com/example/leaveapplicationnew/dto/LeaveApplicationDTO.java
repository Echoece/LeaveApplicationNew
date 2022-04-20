package com.example.leaveapplicationnew.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;

@Data
public class LeaveApplicationDTO {
    private long id;
    private String remark;
    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "dd-MM-yyyy")
    private Date fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "dd-MM-yyyy")
    private Date toDate;
    private long leaveTypeID;
}
