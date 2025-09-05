package com.istad.docuhub.domain;

import com.istad.docuhub.utils.FeedBackStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feedback")
public class Feedback {

    @Id
    private Integer id;

    @Column(nullable = false)
    private String feedbackText;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FeedBackStatus status;

    @Column(nullable = true)
    private LocalDate deadline;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = true)
    private LocalDate updatedAt;

    @Column(nullable = true)
    private String fileUrl;

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

}
