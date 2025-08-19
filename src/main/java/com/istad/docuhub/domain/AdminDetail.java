package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name = "admin_details")
public class AdminDetail {

    @Id
    private Integer id;
    private String slug;

    @OneToOne
    @JoinColumn(name = "user_uuid",referencedColumnName = "uuid")
    private User user;

    @ManyToMany
    private List<Specialize> specializeList;

}
