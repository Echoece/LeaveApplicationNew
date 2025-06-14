package com.example.leaveapplicationnew.service;

import com.example.leaveapplicationnew.auth.security.SecurityUtils;
import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.entity.rowmapper.TotalLeaveRowMapper;
import com.example.leaveapplicationnew.repo.ApplicationUserRepository;
import com.example.leaveapplicationnew.repo.LeaveApplicationRepository;
import com.example.leaveapplicationnew.repo.LeaveTypeRepository;
import com.example.leaveapplicationnew.repo.YearlyLeaveRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;

@Service
@AllArgsConstructor
public class ManagerService {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TotalLeaveRowMapper totalLeaveRowMapper;
    private final LeaveTypeRepository leaveTypeRepository;
    private final YearlyLeaveRepository yearlyLeaveRepository;

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final ApplicationUserRepository userRepository;


    public List<LeaveApplicationDTO> showPendingApplication(){
        // find user id of this manager
        long managerId = getApplicationUser().getUserId();

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

    public List<TotalLeaveDTO> showLeaveBalance( ){
        long managerId = getApplicationUser().getUserId();
        int searchYear = Year.now().getValue();
        String status = Status.APPROVED.name();

        // SQL query to find status, leave Type id + name , user id + name,
        // maximum allowed leave for the year, total leave taken by employee that year.

        String SQL = "SELECT SUM(DATEDIFF(l.to_date, l.from_date)) AS total_leave , l.leave_type_id, lt.name , l.user_id, u.email AS email "
                + " FROM leave_application l"
                + " LEFT JOIN user u ON u.id = l.user_id "
                + " LEFT JOIN leave_type lt ON lt.id = l.leave_type_id"
                + " LEFT JOIN yearly_leave yl ON yl.leave_type_id = lt.id"
                + " WHERE YEAR(l.from_date) = :searchYear AND l.status = :status AND l.user_id IN(SELECT id FROM user WHERE manager_id=:managerId)  "
                + " GROUP BY l.leave_type_id, l.user_id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("searchYear", searchYear)
                .addValue("status", status)
                .addValue("managerId", managerId);

        List<TotalLeaveDTO> totalLeaveDTO =  jdbcTemplate.query(
                SQL,
                sqlParameterSource,
                (rs, rowNum) -> TotalLeaveDTO.builder()
                                .leaveTypeId(rs.getLong("leave_type_id"))
                                .userId(rs.getLong("user_id"))
                                .totalLeave(rs.getInt("total_leave"))
                                .userName(rs.getString("email"))
                                .leaveTypeName(rs.getString("lt.name"))
                                .build()
        );

        totalLeaveDTO.forEach(element -> {
            element.setMaxAllowedLeave(yearlyLeaveRepository.findMaximumDayByYearAndLeaveTypeId(Year.now().getValue(), element.getLeaveTypeId()));
        });

        return totalLeaveDTO;
    }

    public LeaveApplication putManagerRemark(long applicationId, String remark){
        LeaveApplication application = leaveApplicationRepository.findById(applicationId).orElseThrow(RuntimeException::new);

        application.setManagerRemark(remark);

        return leaveApplicationRepository.save(application);
    }

    // utility method to get current logged in user
    private ApplicationUser getApplicationUser() {
        String username = SecurityUtils
                .getCurrentLoggedInUserName()
                .orElseThrow(RuntimeException::new);

        ApplicationUser user = userRepository
                .findApplicationUsersByEmail(username)
                .orElseThrow(RuntimeException::new);
        return user;
    }
}
