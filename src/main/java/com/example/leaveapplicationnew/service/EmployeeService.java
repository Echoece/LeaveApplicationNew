package com.example.leaveapplicationnew.service;

import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.entity.rowmapper.LeaveApplicationRowMapper;
import com.example.leaveapplicationnew.entity.rowmapper.TotalLeaveRowMapper;
import com.example.leaveapplicationnew.repo.LeaveApplicationRepository;
import com.example.leaveapplicationnew.repo.LeaveTypeRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final LeaveApplicationRowMapper leaveApplicationRowMapper;
    private final TotalLeaveRowMapper totalLeaveRowMapper;

    // need to add user Id
    public LeaveApplication createApplication(LeaveApplicationDTO applicationDTO){
        if(applicationDTO==null){
            return null;
        }
        LeaveType leaveType =
                leaveTypeRepository.findById(applicationDTO.getLeaveTypeID())
                .orElseThrow(RuntimeException::new);

        // TODO: get the current logged in user and add the userId in the application object

        LeaveApplication application = LeaveApplication.builder()
                .fromDate(applicationDTO.getFromDate())
                .toDate(applicationDTO.getToDate())
                .remark(applicationDTO.getRemark())
                .leaveType(leaveType)
                .build();

        return leaveApplicationRepository.save(application);
    }

    // need to add user id
    public LeaveApplication editApplication(LeaveApplicationDTO applicationDTO){
        if(applicationDTO==null){
            return null;
        }
        LeaveType leaveType = leaveTypeRepository.findById(applicationDTO.getLeaveTypeID())
                        .orElseThrow(RuntimeException::new);

        LeaveApplication application = leaveApplicationRepository.findById(applicationDTO.getId())
                .orElseThrow(RuntimeException::new);

        // TODO: get the current logged in user and add the userId in the application object

        application.setRemark(applicationDTO.getRemark());
        application.setFromDate(applicationDTO.getFromDate());
        application.setToDate(applicationDTO.getToDate());
        application.setLeaveType(leaveType);

        return leaveApplicationRepository.save(application);
    }

    // done, all application for a user id
    public List<LeaveApplicationDTO> viewAllApplicationForUser(long userId){
        String SQL = "SELECT l.id AS application_id, l.from_date, l.to_date, l.remark, l.status, l.manager_remark, l.leave_type_id, "
                + " lt.name AS leave_type_name, u.id AS user_id, u.name AS user_name "
                + " FROM leave_application_new.leave_application l "
                + " JOIN leave_application_new.user u ON l.user_id = u.id "
                + " JOIN leave_application_new.leave_type lt ON l.leave_type_id = lt.id "
                + " WHERE l.user_id =:userId";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource().addValue("userId",userId);

        return jdbcTemplate.query(SQL, sqlParameterSource, leaveApplicationRowMapper);
    }

    // done
    public List<TotalLeaveDTO>  showLeaveBalanceForEmployee(long userId, int searchYear){
        String status = Status.APPROVED.name();
        // SQL query to find status, leave Type id + name , user id + name, maximum allowed leave for the year, total leave taken by employee that year.

        String SQL = "SELECT    l.leave_type_id, yl.maximum_day AS max_allowed_leave, "
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
                            + "l.user_id =:userId AND status = :status AND  YEAR(l.from_date) =:searchYear "
                + " GROUP BY l.leave_type_id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("searchYear", searchYear)
                .addValue("status", status)
                .addValue("userId", userId);

        return jdbcTemplate.query(SQL, sqlParameterSource, totalLeaveRowMapper);
    }

    // done
    public boolean sendApplicationForApproval(long applicationId){
        LeaveApplication application = leaveApplicationRepository.findById(applicationId).orElseThrow(RuntimeException::new);

        if (checkEligibilityForLeave(application)){
            application.setStatus(Status.PENDING);
            leaveApplicationRepository.save(application);
            return true;
        }
        return false;
    }

    // utility method to check if user is eligible to apply for leave
    private boolean checkEligibilityForLeave(LeaveApplication application){
        // find the total leave balance for the user
        List<TotalLeaveDTO> totalLeaveDTOS = showLeaveBalanceForEmployee(
                application.getUser().getUserId(),
                //Year.now().getValue()
                2020
        );

        // find the available leave balance from the totalLeaveDTO
        long leaveTypeId = application.getLeaveType().getLeaveTypeId();

        int maxAllowedLeave= totalLeaveDTOS.stream()
                .filter(element -> element.getLeaveTypeId() == leaveTypeId)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getMaxAllowedLeave();

        int totalTakenLeave = totalLeaveDTOS.stream()
                .filter(element -> element.getLeaveTypeId() == leaveTypeId)
                .findFirst()
                .orElseThrow(RuntimeException::new)
                .getTotalLeave();

        int availableLeaveBalance = maxAllowedLeave - totalTakenLeave;

        // find number of days employee applied in the current application
        Date fromDate = application.getFromDate();
        Date toDate = application.getToDate();

        long totalDaysApplied = TimeUnit.DAYS.convert(
                Math.abs(
                        toDate.getTime() - fromDate.getTime()
                ),
                TimeUnit.MILLISECONDS
        );

        return  availableLeaveBalance>=totalDaysApplied;
    }
}
