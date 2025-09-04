package com.istad.docuhub.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Builder
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
    private String thumbnailUrl;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDate submittedAt;

    @Column(nullable = false)
    private LocalDate createdAt;

    @Column(nullable = true)
    private Integer downloadCount = 0;


    @Column(nullable = false)
    private Boolean isApproved; // status from admin approve or not


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

    @ManyToOne
    @JoinColumn(name = "category_uuid",referencedColumnName = "uuid")
    private Category category;

    @OneToMany(mappedBy = "paper")
    private List<Star> stars;

    @OneToMany(mappedBy = "paper")
    private List<Share> shares;

    @OneToMany(mappedBy = "paper")
    private List<Comment> comments;

    @OneToMany(mappedBy = "paper")
    private List<Feedback> feedbacks;

}
