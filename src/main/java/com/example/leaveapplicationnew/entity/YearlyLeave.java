package com.example.leaveapplicationnew.entity;


import com.example.leaveapplicationnew.entity.converter.YearConverter;
import lombok.*;

import javax.persistence.*;
import java.time.Year;


@Getter @Setter @NoArgsConstructor @AllArgsConstructor @ToString @Builder

@Entity
@Table(name = "yearly_leave")
public class YearlyLeave {
    @Id
    @SequenceGenerator(
            name = "yearlyLeave_sequence",
            sequenceName = "yearlyLeave_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "yearlyLeave_sequence"
    )
    @Column(name = "id")
    private Long yearlyLeaveId;

    @Convert( converter = YearConverter.class )
    private Year year;

    private int maximumDay;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "leave_type_id",
            referencedColumnName = "id"
    )
    private LeaveType leaveType;
}
