package com.example.leaveapplicationnew.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;

@Data @Builder
public class LeaveApplicationDTO {
    private long id;

    private String remark;
    private String managerRemark;

    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "dd-MM-yyyy")
    private Date fromDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "dd-MM-yyyy")
    private Date toDate;

    private long leaveTypeID;
    private String leaveTypeName;

    private Long userId;
    private String userName;

    private String status;
}
