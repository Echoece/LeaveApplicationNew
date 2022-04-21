package com.example.leaveapplicationnew.service;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.entity.rowmapper.TotalLeaveRowMapper;
import com.example.leaveapplicationnew.repo.ApplicationUserRepository;
import com.example.leaveapplicationnew.repo.LeaveApplicationRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ManagerService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TotalLeaveRowMapper totalLeaveRowMapper;

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final ApplicationUserRepository userRepository;


    public List<LeaveApplicationDTO> showPendingApplication(){
        // find user id if this manager
        int managerId = 4; // TODO: change this to get the user from security context

        String query = "SELECT l.*, lt.name AS leave_type_name, u.name as user_name  "
                + " FROM leave_application l"
                + " JOIN user u ON l.user_id = u.id"
                + " JOIN leave_type lt ON lt.id = l.leave_type_id"
                + " WHERE user_id IN (SELECT id FROM user WHERE manager_id = :managerID) AND status = 'PENDING'";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("managerID",managerId);

        return jdbcTemplate.query(
                query ,
                parameterSource,
                ((rs, rowNum) -> LeaveApplicationDTO.builder()
                        .id(rs.getLong("id"))
                        .fromDate(rs.getDate("from_date"))
                        .toDate(rs.getDate("to_date"))
                        .managerRemark(rs.getString("manager_remark"))
                        .status(rs.getString("status"))
                        .remark(rs.getString("remark"))
                        .userName(rs.getString("user_name"))
                        .leaveTypeName(rs.getString("leave_type_name"))
                        .leaveTypeID(rs.getLong("leave_type_id"))
                        .userId(rs.getLong("user_id"))
                        .build()
                )
        );
    }

    public LeaveApplication approveOrRejectApplication(long applicationId, Status status){
        LeaveApplication application = leaveApplicationRepository.findById(applicationId).orElseThrow(RuntimeException::new);
        application.setStatus(status);
        return leaveApplicationRepository.save(application);
    }

    public List<TotalLeaveDTO> showLeaveBalance(int managerId, int searchYear){
        String status = Status.APPROVED.name();

        // SQL query to find status, leave Type id + name , user id + name,
        // maximum allowed leave for the year, total leave taken by employee that year.
        String SQL = "SELECT l.status,  l.leave_type_id, yl.maximum_day AS max_allowed_leave, "
                + " lt.name AS leave_type_name, u.id AS user_id, u.name AS user_name, "
                + " SUM(DATEDIFF(l.to_date,l.from_date)) AS total_leave "
                + " FROM leave_application_new.leave_application l "
                + " JOIN leave_application_new.user u "
                + " ON l.user_id = u.id "
                + " JOIN leave_application_new.leave_type lt "
                + " ON l.leave_type_id = lt.id "
                + " JOIN yearly_leave yl "
                + " ON lt.id = yl.leave_type_id "
                + " WHERE "
                + " l.user_id IN(SELECT id FROM user WHERE manager_id=:managerId) AND status = :status AND  YEAR(l.from_date) =:searchYear "
                + " GROUP BY l.leave_type_id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("searchYear", searchYear)
                .addValue("status", status)
                .addValue("managerId", managerId);

        return jdbcTemplate.query(SQL, sqlParameterSource, totalLeaveRowMapper);
    }

    public LeaveApplication putManagerRemark(long applicationId, String remark){
        LeaveApplication application = leaveApplicationRepository.findById(applicationId).orElseThrow(RuntimeException::new);

        application.setManagerRemark(remark);

        return leaveApplicationRepository.save(application);
    }
}
