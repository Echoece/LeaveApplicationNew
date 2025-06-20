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
import com.example.leaveapplicationnew.utility.Helper;
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

    public ApplicationUserDTO addUser(ApplicationUserDTO userDTO){
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
        userRepository.save(user);
        return userDTO;
    }

    public List<LeaveApplicationDTO> viewAllLeave(){

        String SQL = "SELECT l.id AS application_id, l.from_date, l.to_date, l.remark, l.status, l.manager_remark, l.leave_type_id, "
                + " lt.name AS leave_type_name, u.id AS user_id, u.email AS user_name "
                + " FROM leave_application l "
                + " JOIN user u ON l.user_id = u.id "
                + " JOIN leave_type lt ON l.leave_type_id = lt.id "
//                + " WHERE l.status = 'APPROVED' "
                + " WHERE year(l.from_date) = " + Year.now().getValue() + " "
                + " ORDER BY l.to_date DESC";

        return jdbcTemplate.query(SQL, leaveApplicationRowMapper);
    }

    public void setManagerToEmployee(long employeeId, long managerId) {
        ApplicationUser employee = userRepository.findById(employeeId).orElseThrow(RuntimeException::new);
        ApplicationUser manager = userRepository.findById(managerId).orElseThrow(RuntimeException::new);

        employee.setManagerId(manager);

        userRepository.save(employee);
    }

    public List<LeaveType> getAllLeaveType() {
        return leaveTypeRepository.findAll();
    }

    public LeaveType updateLeaveType(long id, LeaveType leaveType) {
        LeaveType savedLeaveType = leaveTypeRepository.findById(id).orElseThrow(RuntimeException::new);
        Helper.copyNonNullProperties(leaveType, savedLeaveType);
        return leaveTypeRepository.save(savedLeaveType);
    }

    public void deleteLeaveType(long id) {
        leaveTypeRepository.deleteById(id);
    }

    public List<YearlyLeave> getAllYearlyLeave() {
        return yearlyLeaveRepository.findAllByYear(Year.now());
    }

    public YearlyLeave updateYearlyLeave(long id, YearlyLeave leaveType) {
        YearlyLeave savedLeave = yearlyLeaveRepository.findById(id).orElseThrow(RuntimeException::new);
        Helper.copyNonNullProperties(leaveType, savedLeave);
        return yearlyLeaveRepository.save(savedLeave);
    }

    public void deleteYearlyLeave(long id) {
        yearlyLeaveRepository.deleteById(id);
    }
}
