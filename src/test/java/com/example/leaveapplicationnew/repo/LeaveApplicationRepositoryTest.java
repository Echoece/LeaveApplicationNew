package com.example.leaveapplicationnew.repo;

import com.example.leaveapplicationnew.auth.ApplicationUser;
import com.example.leaveapplicationnew.entity.LeaveApplication;
import com.example.leaveapplicationnew.entity.LeaveType;
import com.example.leaveapplicationnew.entity.Status;
import com.example.leaveapplicationnew.entity.dto.LeaveApplicationDTO;
import com.example.leaveapplicationnew.entity.dto.TotalLeaveDTO;
import com.example.leaveapplicationnew.entity.rowmapper.LeaveApplicationRowMapper;
import com.example.leaveapplicationnew.entity.rowmapper.TotalLeaveRowMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;


@SpringBootTest
//@DataJpaTest
class LeaveApplicationRepositoryTest {
    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    @Autowired
    private ApplicationUserRepository userRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private LeaveApplicationRowMapper leaveApplicationRowMapper;

    @Autowired
    private TotalLeaveRowMapper totalLeaveRowMapper;

    @Test
    public void createApplication(){
        Date fromDate = Date.valueOf("2020-7-10");
        Date toDate = Date.valueOf("2020-7-20");

        LeaveType leaveType = leaveTypeRepository.findById(2L).get();
        ApplicationUser user = userRepository.findById(4L).get();

        LeaveApplication leaveApplication = LeaveApplication.builder()
                .remark("Sick application 2")
                .leaveType(leaveType)
                .toDate(toDate)
                .fromDate(fromDate)
                .status(Status.APPROVED)
                .user(user)
                .build();

        leaveApplicationRepository.save(leaveApplication);

    }

    @Test
    public void daysLeaveTaken(){
        LeaveApplication leaveApplication = leaveApplicationRepository.findById(1L).orElseThrow(RuntimeException::new);

        Date fromDate = leaveApplication.getFromDate();
        Date toDate = leaveApplication.getToDate();

        // TimeUnit is an enum, DAYS enum has constructor value of microsecond converted to 24hour.
        long days = TimeUnit.DAYS.convert(
                Math.abs(
                        toDate.getTime() - fromDate.getTime()
                ),
                TimeUnit.MILLISECONDS
        );
        System.out.println(days);
    }

    @Test
    public void findByUserId(){
        List<LeaveApplication> applications = leaveApplicationRepository.findAllByUser(4L);
        System.out.println(leaveApplicationRepository.findAllByUser(4L));
    }

    @Test
    public void showAllApplication(){
        long userId =3;
        String SQL = "SELECT l.id AS application_id, l.from_date, l.to_date, l.remark, l.status, l.manager_remark, l.leave_type_id, "
                + " lt.name AS leave_type_name, u.id AS user_id, u.name AS user_name "
                + " FROM leave_application_new.leave_application l "
                + " JOIN leave_application_new.user u ON l.user_id = u.id "
                + " JOIN leave_application_new.leave_type lt ON l.leave_type_id = lt.id "
                + " WHERE l.user_id =:userId";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource().addValue("userId",userId);

        List<LeaveApplicationDTO> leaveApplicationDTO =  jdbcTemplate.query(SQL, sqlParameterSource, leaveApplicationRowMapper);
        System.out.println(leaveApplicationDTO);
    }

    @Test
    public void showLeaveBalance(){
        long userId =4;
        int searchYear = 2020;
        String status = Status.APPROVED.name();

        // SQL query to find
        String SQL = "SELECT     l.status,  l.leave_type_id, yl.maximum_day AS max_allowed_leave, "
                                + " lt.name AS leave_type_name, u.id AS user_id, u.name AS user_name, "
                                + " SUM(DATEDIFF(l.to_date,l.from_date)) AS total_leave"
                + " FROM leave_application_new.leave_application l "
                + " JOIN leave_application_new.user u "
                            + "ON l.user_id = u.id "
                + " JOIN leave_application_new.leave_type lt "
                            + "ON l.leave_type_id = lt.id "
                + " JOIN yearly_leave yl "
                            + "ON lt.id = yl.leave_type_id"
                + " WHERE l.user_id =:userId and status = :status AND  YEAR(l.from_date) =:searchYear "
                + " GROUP BY l.leave_type_id";

        SqlParameterSource sqlParameterSource = new MapSqlParameterSource()
                .addValue("searchYear", searchYear)
                .addValue("status", status)
                .addValue("userId",userId);

        List<TotalLeaveDTO> totalLeaveDTOS =  jdbcTemplate.query(SQL, sqlParameterSource, totalLeaveRowMapper);
        System.out.println(totalLeaveDTOS);
    }

    @Test
    public void showLeaveApplicationsForManager(){
        // find user id if this manager
        int managerId = 2; // TODO: change this to get the user from security context

        String query = "SELECT l.*, lt.name AS leave_type_name, u.name as user_name  "
                + " FROM leave_application l"
                + " JOIN user u ON l.user_id = u.id"
                + " JOIN leave_type lt ON lt.id = l.leave_type_id"
                + " WHERE user_id IN (SELECT id FROM user WHERE manager_id = :managerID) AND status = 'PENDING'";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("managerID",managerId);

        System.out.println(
                jdbcTemplate.query(
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
                )
        );

    }

    // utility function to filter the date range
    private Predicate<LeaveApplicationDTO> filterByDateRange(Date toDate, Date fromDate) {
        return applicationForLeave -> {

            // application start date should be equal or after the filter start date
            int compareStartDate = applicationForLeave.getFromDate().compareTo(fromDate);
            // application end date should be equal or before the filter end date, value should be
            int compareEndDate = applicationForLeave.getToDate().compareTo(toDate);

            if (compareStartDate >= 0 && compareEndDate <= 0)
                return true;

            return false;
        };
    }
}
