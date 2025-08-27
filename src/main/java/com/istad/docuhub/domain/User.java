package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private String fullName;

    private String gender;

//    @Column(nullable = false)
//    private String keyUserId;

//    @Column(nullable = false)
//    private String password;
//
//    @Column(nullable = false)
//    private String confirmPassword;

//    @Column(nullable = false, unique = true)
//    private String email;

//    private String firstName;
//
//    private String lastName;

    private String imageUrl;
    private Boolean status;

    private LocalDate createDate;
    private LocalDate updateDate;

    private String bio;
    private String address;

    @Column(unique = true)
    private String contactNumber;

    private String telegramId;

    private Boolean isUser;
    private Boolean isAdmin;
    private Boolean isStudent;
    private Boolean isAdvisor;
    private Boolean isDeleted;

    private String slug;

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
