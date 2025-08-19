package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "stars")
public class Star {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true) // ✅ this makes uuid usable in FKs
    private String uuid;

    @Column(nullable = false)
    private LocalDate staredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paper_uuid", referencedColumnName = "uuid", nullable = false)
    private Paper paper;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "user_uuid", referencedColumnName = "uuid", nullable = false)
    private User user;

}
