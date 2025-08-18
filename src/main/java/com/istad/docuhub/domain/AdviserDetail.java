package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "adviser_details")
public class AdviserDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = true)
    private String adviserCode;


    @Column(nullable = true)
    private Integer specializeId;


    @Column(nullable = true)
    private Integer experienceYears;


    @Column(nullable = true)
    private String bio;


    @Column(nullable = false)
    private String contactNumber;


    @Column(nullable = false)
    private String status;



    @OneToMany(mappedBy = "adviserDetail")
    private List<AdviserAssignment> adviserAssignments;



    @OneToMany(mappedBy = "adviser")
    private List<User> user;


}
