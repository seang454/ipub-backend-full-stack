package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Table(
        name = "adviser_assignments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"advisor_uuid", "paper_uuid"}) // prevent duplicate adviser-paper
        }
)
@Getter
@Setter
@NoArgsConstructor
public class AdviserAssignment {

    @Id
    private Integer id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private LocalDate assignedDate;

    private LocalDate updateDate;

    // adviser → many assignments allowed
    @ManyToOne
    @JoinColumn(name = "advisor_uuid", referencedColumnName = "uuid", nullable = false)
    private User advisor;

    // paper → many assignments allowed
    @ManyToOne
    @JoinColumn(name = "paper_uuid", referencedColumnName = "uuid", nullable = false)
    private Paper paper;

    // admin → many assignments allowed
    @ManyToOne
    @JoinColumn(name = "admin_uuid", referencedColumnName = "uuid", nullable = false)
    private User admin;
}

