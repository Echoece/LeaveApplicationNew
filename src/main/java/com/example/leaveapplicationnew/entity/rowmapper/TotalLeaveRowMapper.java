package com.example.leaveapplicationnew.entity.rowmapper;

import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TotalLeaveRowMapper implements RowMapper<TotalLeaveDTO> {
    @Override
    public TotalLeaveDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        TotalLeaveDTO leaveApplicationDTO = TotalLeaveDTO.builder()
                .status(rs.getString("status"))
                .leaveTypeId(rs.getLong("leave_type_id"))
                .leaveTypeName(rs.getString("leave_type_name"))
                .userId(rs.getLong("user_id"))
                .userName(rs.getString("user_name"))
                .totalLeave(rs.getInt("total_leave"))
                .maxAllowedLeave(rs.getInt("max_allowed_leave"))
                .build();
        return leaveApplicationDTO;
    }
}
