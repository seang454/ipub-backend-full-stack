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

    @Column(nullable = false)
    private String studentCardUrl;


    @Column(nullable = false, length = 100)
    private String university;


    @Column(nullable = false, length = 100)
    private String major;

    private Integer yearsOfStudy;

    // one to one user
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "uuid")
    private User user;

}











