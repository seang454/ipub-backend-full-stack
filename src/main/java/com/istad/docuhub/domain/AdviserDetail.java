package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "adviser_details")
public class AdviserDetail {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private Integer experienceYears;

    @Column(nullable = false)
    private String linkedinUrl;

    @Column(nullable = false)
    private String publication;

    @Column(nullable = false)
    private String socialLinks;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Boolean isDeleted;

    // AdviserDetail → User (One-to-One via uuid)
    @OneToOne
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid")
    private User user;

    // AdviserDetail → Specialize (Many-to-Many via uuid)
    @ManyToMany
    @JoinTable(
            name = "adviser_specializes",
            joinColumns = @JoinColumn(name = "adviser_uuid", referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(name = "specialize_uuid", referencedColumnName = "uuid")
    )
    private List<Specialize> specialize;
}
