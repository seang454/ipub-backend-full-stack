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
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "paper_id", referencedColumnName = "id", nullable = false)
    private Paper paper;


    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;


    @Column(nullable = false)
    private String content;


    @Column(nullable = false)
    private LocalDate createdAt;

}
