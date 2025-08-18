package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "adviser_assignments")
public class AdviserAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDate assignedDate;

    @Column(nullable = true)
    private LocalDate updateDate;


    @OneToOne
    @JoinColumn(name = "advisor_id",referencedColumnName = "uuid")
    private User advisor;
    //one to one paper
    @OneToOne
    @JoinColumn(name = "paper_id",referencedColumnName = "uuid")
    private Paper paper;
    //one to one admin

    @OneToOne
    @JoinColumn(name = "admin_id",referencedColumnName = "uuid")
    private User admin;

}
