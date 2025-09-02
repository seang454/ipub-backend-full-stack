package com.istad.docuhub.feature.sendMail.dto;

public record SendMailResponse(
    String receiverEmail,
    String subject,
    String body
) {
}
