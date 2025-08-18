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
    private LocalDate reviewDateline;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDate assignedDate;

    @Column(nullable = true)
    private LocalDate updateDate;




    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "adviser_id")
    private AdviserDetail adviserDetail;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paper_id")
    private Paper paper;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_id")   // who assigned the review
    private AdminDetail admin;


}
