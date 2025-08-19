package com.istad.docuhub.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "papers")
public class Paper {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String title;


    @Column(nullable = true)
    private String abstractText;


    @Column(nullable = true)
    private String fileUrl;


    @Column(nullable = false)
    private String status;


    @Column(nullable = false)
    private LocalDate submittedAt;


    @Column(nullable = false)
    private LocalDate createdAt;



    @Column(nullable = true)
    private Integer downloadCount = 0;


    @Column(nullable = false)
    private Boolean isApproved;


    @Column(nullable = false)
    private Boolean isDeleted;


    @Column(nullable = false)
    private Boolean isPublished;


    @Column(nullable = true)
    private LocalDate publishedAt;

    //one student_id to many paper
    @ManyToOne
    @JoinColumn(name = "author_uuid",referencedColumnName = "uuid")
    private User author;

    //one assigned to one paper
    @OneToOne
    @JoinColumn(name = "assigned_uuid",referencedColumnName = "uuid")
    private AdviserAssignment assignedId;

    @OneToMany(mappedBy = "paper")
    private List<Category> categoryId;

    @OneToMany(mappedBy = "paper")
    private List<Star> stars;

    @OneToMany(mappedBy = "paper")
    private List<Share> shares;

    @OneToMany(mappedBy = "paper")
    private List<Comment> comments;

    @OneToMany(mappedBy = "paper")
    private List<Feedback> feedbacks;


}
