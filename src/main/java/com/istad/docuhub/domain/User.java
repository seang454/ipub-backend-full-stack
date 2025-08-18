package com.istad.docuhub.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "user_profile")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(nullable = false)
    private String uuid;


    @Column(nullable = false, unique = true)
    private String userName;


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


    @Column(nullable = false)
    private LocalDate lastLoginDate;



    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private AdminDetail adminDetail;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adviser_id")
    private AdviserDetail adviser;



}
