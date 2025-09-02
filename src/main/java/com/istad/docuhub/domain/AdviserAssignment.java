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
    private Integer id;

    @Column(nullable = false, unique = true) // ✅ this makes uuid usable in FKs
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
    @JoinColumn(name = "advisor_uuid",referencedColumnName = "uuid")
    private User advisor;
    //one to one paper
    @OneToOne
    @JoinColumn(name = "paper_uuid",referencedColumnName = "uuid")
    private Paper paper;
    //one to one admin

    @OneToOne
    @JoinColumn(name = "admin_uuid",referencedColumnName = "uuid")
    private User admin;

}
