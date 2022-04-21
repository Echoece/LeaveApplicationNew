package com.example.leaveapplicationnew.entity.rowmapper;

import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LeaveApplicationRowMapper implements RowMapper<LeaveApplicationDTO> {
    @Override
    public LeaveApplicationDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        LeaveApplicationDTO leaveApplicationDTO = LeaveApplicationDTO.builder()
                .id(rs.getLong("application_id"))
                .fromDate(
                        Date.valueOf(
                                rs.getString("from_date")
                        )
                )
                .toDate(
                        Date.valueOf(
                                rs.getString("to_date")
                        )
                )
                .remark(rs.getString("remark"))
                .status(rs.getString("status"))
                .managerRemark(rs.getString("manager_remark"))
                .leaveTypeID(rs.getLong("leave_type_id"))
                .leaveTypeName(rs.getString("leave_type_name"))
                .userId(rs.getLong("user_id"))
                .userName(rs.getString("user_name"))
                .build();
        return leaveApplicationDTO;
    }
}
