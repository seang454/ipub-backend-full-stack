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
    private Integer id;

    private Integer experienceYears;

    @Column(nullable = false)
    private String status;

    private Boolean isDeleted;

    //one to one user
    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "uuid")
    private User user;

    //one to many specialize
    @OneToMany
    @JoinTable(
            name = "adviser_specializes",
            joinColumns = @JoinColumn(name = "user_id",referencedColumnName = "uuid"),
            inverseJoinColumns = @JoinColumn(name = "specialize_id",referencedColumnName = "uuid")
    )
    private List<Specialize> specialize;


}
