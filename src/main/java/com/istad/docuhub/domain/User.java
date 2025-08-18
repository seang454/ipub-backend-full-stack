package com.istad.docuhub.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false, unique = true)
    private String userName;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false,  unique = true)
    private String email;


    @Column(nullable = false)
    private String firstName;


    @Column(nullable = false)
    private String lastName;


    @Column(nullable = true)
    private String imageUrl;


    @Column(nullable = false)
    private Boolean status;


    @Column(nullable = false)
    private LocalDate createDate;

    @Column(nullable = true)
    private LocalDate updateDate;

    private String bio;

    private String address;

    @Column(nullable = true,unique = true)
    private String contactNumber;

    private String telegramId;

    private Boolean isUser;
    private Boolean isAdmin;
    private Boolean isStudent;
    private Boolean isAdvisor;


    @Column(nullable = false)
    private Boolean isDeleted;

    @OneToMany(mappedBy = "user")
    private List<Star> stars;

    @OneToMany(mappedBy = "author")
    private List<Paper> papers;

    @OneToMany(mappedBy = "user")
    private List<Share> shares;

    @OneToMany(mappedBy = "user")
    private List<Comment> comments;

    @OneToMany(mappedBy = "advisor")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "receiver")
    private List<Feedback> receivers;



}
