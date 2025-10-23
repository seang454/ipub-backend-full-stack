package com.istad.docuhub.feature.webSocket;


import com.istad.docuhub.domain.Notification;
import com.istad.docuhub.feature.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class NotificationController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebsocketService websocketService;

    /**
     * Client publishes to /app/private-message
     * We fan out to two user-specific topics:
     *   /topic/user.{receiverId}
     *   /topic/user.{senderId}
     */
    @MessageMapping("/private-message")
    public void sendPrivateMessage(@Payload Notification notification) {
        log.info("Incoming message: {}", notification);
        // Persist first (so IDs/timestamps are set if your service does that)
        websocketService.saveChatMessage(notification);

        // Fan-out to receiver
        messagingTemplate.convertAndSend("/topic/user." + notification.getReceiverUuid(), notification);

        //you will send message twice when you add this
        // Fan-out to sender (to confirm delivery/update temp IDs, etc.)
//        messagingTemplate.convertAndSend("/topic/user." + message.getSenderId(), message);

        // Optional simple auto-reply example
//        if (message.getMessage() != null && message.getMessage().equalsIgnoreCase("hi")) {
//            ChatMessage autoReply = new ChatMessage();
//            autoReply.setSenderId(message.getReceiverId());
//            autoReply.setReceiverId(message.getSenderId());
//            autoReply.setMessage("Hello! I got your message.");
//            chatServer.saveChatMessage(autoReply);
//            messagingTemplate.convertAndSend("/topic/user." + autoReply.getReceiverId(), autoReply);
//        }
    }
    @MessageMapping("/update-read")
    public void updateRead(@Payload Read read) {
        websocketService.readChatMessage(read.senderUuid(),read.receiverUuid());
    }

    @GetMapping("/history/{currentUserId}/{selectedUserId}")
    public List<Notification> getHistory(@PathVariable String currentUserId,
                                        @PathVariable String selectedUserId) {
        log.info("History request: {} <-> {}", currentUserId, selectedUserId);
        return websocketService.getChatMessage(currentUserId,selectedUserId);
    }
    @GetMapping("")
    public List<Notification> getNotifications() {
        return websocketService.getChatMessages();
    }
}

