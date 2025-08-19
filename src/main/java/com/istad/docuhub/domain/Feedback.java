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
    private Integer id;

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

    //paper_id //one paper to many feedbacks

    @ManyToOne
    @JoinColumn(name = "paper_uuid",referencedColumnName = "uuid")
    private Paper paper;

    @ManyToOne
    @JoinColumn(name = "advisor_uuid",referencedColumnName = "uuid")
    private User advisor;

    //send to student
    @ManyToOne
    @JoinColumn(name = "receiver_uuid",referencedColumnName = "uuid")
    private User receiver;
    //advisor_id one user to many feedbacks
    //student_id //





}
