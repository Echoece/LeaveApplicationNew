package com.example.leaveapplicationnew.service;

import com.example.leaveapplicationnew.entity.ApplicationUser;
import com.example.leaveapplicationnew.entity.ApplicationUserRole;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.YearlyLeave;
import com.example.leaveapplicationnew.entity.dto.ApplicationUserDTO;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.LeaveTypeDTO;
import com.example.leaveapplicationnew.entity.dto.YearlyLeaveDTO;
import com.example.leaveapplicationnew.entity.rowmapper.LeaveApplicationRowMapper;
import com.example.leaveapplicationnew.repo.ApplicationUserRepository;
import com.example.leaveapplicationnew.repo.ApplicationUserRoleRepository;
import com.example.leaveapplicationnew.repo.LeaveTypeRepository;
import com.example.leaveapplicationnew.repo.YearlyLeaveRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class AdminService {
    private final YearlyLeaveRepository yearlyLeaveRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final ApplicationUserRepository userRepository;
    private final ApplicationUserRoleRepository roleRepository;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final LeaveApplicationRowMapper leaveApplicationRowMapper;

    public LeaveType createLeaveType(LeaveTypeDTO leavetypeDTO){

        LeaveType leaveType = LeaveType.builder()
                .name(leavetypeDTO.getName())
                .remark(leavetypeDTO.getRemark())
                .build();

        return leaveTypeRepository.save(leaveType);
    }

    public YearlyLeave allocateYearlyLeave(YearlyLeaveDTO yearlyLeaveDTO){
        LeaveType leaveType = leaveTypeRepository
                .findById(yearlyLeaveDTO.getLeaveTypeId())
                .orElseThrow(RuntimeException::new);

        YearlyLeave yearlyLeave = YearlyLeave.builder()
                .year(Year.of(yearlyLeaveDTO.getYear()))
                .maximumDay(yearlyLeaveDTO.getMaximumDay())
                .leaveType(leaveType)
                .build();

        return yearlyLeaveRepository.save(yearlyLeave);
    }

    public ApplicationUser addUser(ApplicationUserDTO userDTO){
        // get the user role
        ApplicationUserRole role = roleRepository.findFirstByName(userDTO.getRole());

        // get the manager
        ApplicationUser manager = userRepository.findById(userDTO.getManagerId()).get();

        // build the user
        ApplicationUser user = ApplicationUser.builder()
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .password(userDTO.getPassword())
                .managerId(manager)
                .token(userDTO.getAccessToken())
                .tokenExpireTime(userDTO.getTokenExpireTime())
                .roles(Set.of(role))
                .build();

        // save and  return the user
        return userRepository.save(user);
    }

    public List<LeaveApplicationDTO> viewAllLeave(){

        String SQL = "SELECT l.id AS application_id, l.from_date, l.to_date, l.remark, l.status, l.manager_remark, l.leave_type_id, "
                + " lt.name AS leave_type_name, u.id AS user_id, u.name AS user_name "
                + " FROM leave_application_new.leave_application l "
                + " JOIN leave_application_new.user u ON l.user_id = u.id "
                + " JOIN leave_application_new.leave_type lt ON l.leave_type_id = lt.id "
                + " WHERE status IS NOT NULL";

        return jdbcTemplate.query(SQL, leaveApplicationRowMapper);
    }
}
