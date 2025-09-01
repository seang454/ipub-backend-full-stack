package com.istad.docuhub.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "send_mail")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class SendMail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String uuid;
    private String senderEmail;
    private String receiverEmail;
    private String subject;
    private String body;
}
