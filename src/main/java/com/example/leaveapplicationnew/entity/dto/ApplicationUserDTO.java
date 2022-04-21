package com.example.leaveapplicationnew.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;

@Data
public class ApplicationUserDTO {
    private String email;
    private String name;
    private String password;
    private String accessToken;

    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd")
    private Date tokenExpireTime;

    private long managerId;
    private String role;
}
