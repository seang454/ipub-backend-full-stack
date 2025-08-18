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
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "paper_id", referencedColumnName = "id", nullable = false)
    private Paper paper;


    @ManyToOne
    @JoinColumn(name = "adviser_id", referencedColumnName = "id", nullable = false)
    private AdviserDetail adviserDetail;


    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    private StudentDetail studentDetail;


    @Column(nullable = false)
    private String feedbackText;


    @Column(nullable = false)
    private String status;


    @Column(nullable = false)
    private LocalDate deadline;


    @Column(nullable = false)
    private LocalDate createdAt;


    @Column(nullable = true)
    private LocalDate updatedAt;


}
