package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", referencedColumnName = "id", nullable = false)
    private User sender;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", referencedColumnName = "id", nullable = false)
    private User receiver;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_type", referencedColumnName = "id", nullable = false)
    private NotificationType type;


    @Column(nullable = true)
    private Integer entityId;


    @Column(nullable = true, length = 255)
    private String title;


    @Column(nullable = false)
    private String message;


    @Column(nullable = false, length = 50)
    private String action;


    @Column(nullable = false)
    private Boolean isRead;

}
