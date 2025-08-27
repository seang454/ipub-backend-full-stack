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
@Entity(name = "categories")
@NoArgsConstructor
public class Category {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true) // ✅ this makes uuid usable in FKs
    private String uuid;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private LocalDate createdDate;

    @OneToMany(mappedBy = "category")
    private List<Paper> paper;

}
