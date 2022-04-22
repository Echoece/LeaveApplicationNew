package com.example.leaveapplicationnew.service;

import com.example.leaveapplicationnew.auth.security.SecurityUtils;
import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.entity.rowmapper.LeaveApplicationRowMapper;
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

import java.sql.Date;
import java.time.Year;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class EmployeeService {
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ApplicationUserRepository userRepository;
    private final YearlyLeaveRepository yearlyLeaveRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final LeaveApplicationRowMapper leaveApplicationRowMapper;
    private final TotalLeaveRowMapper totalLeaveRowMapper;

    public LeaveApplicationDTO createApplication(LeaveApplicationDTO applicationDTO){
        if(applicationDTO==null){
            return null;
        }
        // get the leave type
        LeaveType leaveType = leaveTypeRepository
                .findById(applicationDTO.getLeaveTypeID())
                .orElseThrow(RuntimeException::new);

        ApplicationUser user = getApplicationUser();

        // build the application, save and return
        LeaveApplication application = LeaveApplication.builder()
                .fromDate(applicationDTO.getFromDate())
                .toDate(applicationDTO.getToDate())
                .remark(applicationDTO.getRemark())
                .leaveType(leaveType)
                .user(user)
                .build();

        application = leaveApplicationRepository.save(application);

        return LeaveApplicationDTO.builder()
                .id(application.getLeaveApplicationId())
                .remark(application.getRemark())
                .fromDate(application.getFromDate())
                .toDate(application.getToDate())
                .leaveTypeID(application.getLeaveType().getLeaveTypeId())
                .leaveTypeName(application.getLeaveType().getName())
                .userId(application.getUser().getUserId())
                .userName(application.getUser().getName())
                .build();
    }

    public LeaveApplicationDTO editApplication(LeaveApplicationDTO applicationDTO){
        if(applicationDTO==null){
            return null;
        }

        // can only edit if the application is not submitted, pending or rejected
        LeaveApplication application = leaveApplicationRepository.findById(applicationDTO.getId())
                .orElseThrow(RuntimeException::new);
        if(application.getStatus()!=null) return null;

        // can not edit other employee application
        ApplicationUser user = getApplicationUser();
        if(!user.getUserId().equals(application.getUser().getUserId()))
            return null;

        // find the leave type
        LeaveType leaveType = leaveTypeRepository.findById(applicationDTO.getLeaveTypeID())
                .orElseThrow(RuntimeException::new);

        // update changes
        application.setRemark(applicationDTO.getRemark());
        application.setFromDate(applicationDTO.getFromDate());
        application.setToDate(applicationDTO.getToDate());
        application.setLeaveType(leaveType);
        application.setUser(user);

        application = leaveApplicationRepository.save(application);

        // build the DTO and return
        String status = null;
        if(application.getStatus()!=null) status = application.getStatus().name();

        return LeaveApplicationDTO.builder()
                .id(application.getLeaveApplicationId())
                .remark(application.getRemark())
                .managerRemark(application.getManagerRemark())
                .fromDate(application.getFromDate())
                .toDate(application.getToDate())
                .leaveTypeID(application.getLeaveType().getLeaveTypeId())
                .leaveTypeName(application.getLeaveType().getName())
                .userId(application.getUser().getUserId())
                .userName(application.getUser().getName())
                .status(status)
                .build();
    }

    // done, all application for a user id
    public List<LeaveApplicationDTO> viewAllApplicationForUser(){
        ApplicationUser user = getApplicationUser();
        long userId = user.getUserId();

        String SQL = "SELECT l.id AS application_id, l.from_date, l.to_date, l.remark, l.status, l.manager_remark, l.leave_type_id, "
                + " lt.name AS leave_type_name, u.id AS user_id, u.name AS user_name "
                + " FROM leave_application_new.leave_application l "
                + " JOIN leave_application_new.user u ON l.user_id = u.id "
                + " JOIN leave_application_new.leave_type lt ON l.leave_type_id = lt.id "
                + " WHERE l.user_id =:userId";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource().addValue("userId",userId);

        return jdbcTemplate.query(SQL, sqlParameterSource, leaveApplicationRowMapper);
    }

    // TODO: need to work on this to get all the info in single SQL, this solution might have performance issue
    public List<TotalLeaveDTO>  showLeaveBalanceForEmployee(){
        String status = Status.APPROVED.name();

        // SQL query to find status, leave Type id + name , user id + name, maximum allowed leave for the year, total
        // leave taken by employee that year.

        long userId = getApplicationUser().getUserId();
        int searchYear = Year.now().getValue();

        String SQL = "SELECT SUM(DATEDIFF(to_date,from_date)) AS total_leave , leave_type_id, user_id "
                + "FROM leave_application "
                + "WHERE YEAR(from_date) = :searchYear AND status = :status AND user_id = :userId GROUP BY leave_type_id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("searchYear", searchYear)
                .addValue("status", status)
                .addValue("userId", userId);

        List<TotalLeaveDTO> totalLeaveDTO = jdbcTemplate.query(
                SQL,
                sqlParameterSource,
                (rs, rowNum) -> TotalLeaveDTO.builder()
                        .leaveTypeId(rs.getLong("leave_type_id"))
                        .userId(rs.getLong("user_id"))
                        .totalLeave(rs.getInt("total_leave"))
                        .build()
        );

        totalLeaveDTO.forEach(element -> {
            element.setLeaveTypeName(leaveTypeRepository.findById(element.getLeaveTypeId()).get().getName());
            element.setUserName(userRepository.findById(element.getUserId()).get().getName());
            element.setMaxAllowedLeave(yearlyLeaveRepository.findMaximumDayByYearAndLeaveTypeId(Year.now().getValue(), element.getLeaveTypeId()));
        });

        return totalLeaveDTO;
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
        // find the total leave balance for the user for current year
        List<TotalLeaveDTO> totalLeaveDTOS = showLeaveBalanceForEmployee();

        // find the available leave balance
        long leaveTypeId = application.getLeaveType().getLeaveTypeId();

        int year  = Year.from(application.getToDate().toLocalDate()).getValue();
        int maxAllowedLeave= yearlyLeaveRepository.findMaximumDayByYearAndLeaveTypeId(year, leaveTypeId);

        TotalLeaveDTO filteredDTO = totalLeaveDTOS.stream()
                .filter(element -> element.getLeaveTypeId() == leaveTypeId)
                .findFirst()
                .orElse(null);

        // if the leave type is not in the DTO, employee didnt take any vacation
        if(filteredDTO==null) return true;

        int availableLeaveBalance = maxAllowedLeave - filteredDTO.getTotalLeave();

        // find number of applied days in the current application
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

    // utility method to get current logged in user
    private ApplicationUser getApplicationUser() {
        String username = SecurityUtils
                .getCurrentLoggedInUserName()
                .orElseThrow(RuntimeException::new);

        ApplicationUser user = userRepository
                .getApplicationUserByName(username)
                .orElseThrow(RuntimeException::new);
        return user;
    }
}
