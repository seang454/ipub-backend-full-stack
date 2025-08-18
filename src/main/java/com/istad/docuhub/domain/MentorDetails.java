package com.istad.docuhub.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "mentor_details")
public class MentorDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String specialize;

    private Integer yearsExperience;

    private String idCard;

    private String linkedinUrl;

    private String publication;

    private String address;

    private String certifications;

    private String availability;

    private String socialLinks;

    // Foreign key to user table
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uuid")
    private User user;
}

