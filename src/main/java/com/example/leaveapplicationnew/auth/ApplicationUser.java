package com.example.leaveapplicationnew.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "roles")
@Entity
@Builder
@Table(name = "user")
public class ApplicationUser {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    @Column(name = "id")
    private Long userId;

    @Email @NotBlank @Size(max = 50)
    @Column(unique = true)
    private String email;

    @NotBlank @Size(max = 120)
    @JsonIgnore
    private String password;

    @Column(name = "accessToken")
    private String token;

    @JsonFormat(pattern = "dd-MM-yyyy" , shape = JsonFormat.Shape.STRING)
    @Column(name = "tokenExpireTime")
    private Date tokenExpireTime;

    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonManagedReference   // for fixing the recursive call on jackson issue
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id",
                    referencedColumnName = "id"
            )
    )
    private Set<ApplicationUserRole> roles = new HashSet<>();

    // self referencing foreign key
    @ManyToOne
    @JoinColumn(
            name = "manager_id",
            referencedColumnName = "id"
    )
    private ApplicationUser managerId;

}
