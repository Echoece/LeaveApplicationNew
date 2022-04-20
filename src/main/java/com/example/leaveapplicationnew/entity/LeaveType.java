package com.example.leaveapplicationnew.entity;

import lombok.*;

import javax.persistence.*;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Builder
@Entity
@Table(name = "leave_type")
public class LeaveType {
    @Id
    @SequenceGenerator(
            name = "leaveType_sequence",
            sequenceName = "leaveType_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "leaveType_sequence"
    )
    @Column(name = "id")
    private Long leaveTypeId;
    private String name;
    private String remark;
}
