package com.istad.docuhub.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "paper")
@AllArgsConstructor
@NoArgsConstructor
public class Paper {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uuid;
    private String title;
    private String content;
    private String fileUrl;
    private LocalDate submitDate;
    private LocalDate createDate;
    private Boolean published;
    private LocalDate publishDate;
    private  Boolean approved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_uuid")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_uuid")
    private User approver;

    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
}
