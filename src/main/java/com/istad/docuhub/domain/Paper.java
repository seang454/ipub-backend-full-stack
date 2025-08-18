package com.istad.docuhub.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "papers")
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer studentId;


    @Column(nullable = false)
    private Integer mentorId;


    @Column(nullable = false)
    private Integer categoryId;


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
    private String rejectReason;


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


    private String content;



//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "author_uuid")
//    private User author;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "approver_uuid")
//    private User approver;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Category category;
}
