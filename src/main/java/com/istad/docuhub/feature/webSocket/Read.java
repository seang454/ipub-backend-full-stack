package com.istad.docuhub.feature.webSocket;

public record Read(
        String senderUuid,
        String receiverUuid
) {
}
