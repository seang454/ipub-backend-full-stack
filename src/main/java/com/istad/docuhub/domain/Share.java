package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "shares")
public class Share {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true) // ✅ this makes uuid usable in FKs
    private String uuid;

    @Column(nullable = false)
    private String platform;

    @Column(nullable = false)
    private LocalDate startDate;


    @Column(nullable = false)
    private Integer shareCount = 0;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_uuid", referencedColumnName = "id", nullable = false)
    private Paper paper;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", referencedColumnName = "id", nullable = false)
    private User user;

}
