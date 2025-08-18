package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "student_detail")
public class StudentDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    @Column(nullable = false, length = 50)
    private String studentCode;


    @Column(nullable = false, length = 100)
    private String university;


    @Column(nullable = false, length = 100)
    private String faculty;


    @Column(nullable = false, length = 100)
    private String major;


    @Column(nullable = false)
    private Integer yearsOfStudy;


    @Column(nullable = true)
    private String bio;


    @Column(nullable = false)
    private String contact;


    @Column(nullable = false)
    private String address;


    @Column(nullable = false)
    private LocalDate dateOfBirth;


    @Column(nullable = false, length = 10)
    private String gender;


    @Column(nullable = false)
    private LocalDate joinedAt;


    @Column(nullable = true)
    private Integer graduationYear;
}











