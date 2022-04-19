package com.example.leaveapplicationnew.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "leave_application")
public class LeaveApplication {
    @Id
    @SequenceGenerator(
            name = "leaveApplication_sequence",
            sequenceName = "leaveApplication_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "leaveApplication_sequence"
    )
    @Column(name = "id")
    private Long leaveApplicationId;

    @JsonFormat(pattern = "dd-MM-yyyy" , shape = JsonFormat.Shape.STRING)
    @Column(name = "from_date")
    private Date fromDate;

    @JsonFormat(pattern = "dd-MM-yyyy" , shape = JsonFormat.Shape.STRING)
    @Column(name = "to_date")
    private Date toDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String remark;
    private String managerRemark;


    // TODO: user_id foreign key

}
