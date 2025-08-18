package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "admin_details")
public class AdminDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true, referencedColumnName = "id")
    private User user;


    @Column(nullable = false)
    private String adminCode;


    @Column(nullable = true)
    private String department;


    @Column(nullable = true)
    private String bio;


    @Column(nullable = false)
    private String contactNumber;


    @Column(nullable = false)
    private String status;



    @OneToMany(mappedBy = "admin")
    private List<AdviserAssignment> adviserAssignments;

}
