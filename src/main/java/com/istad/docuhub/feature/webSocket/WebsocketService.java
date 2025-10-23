package com.istad.docuhub.feature.webSocket;

import com.istad.docuhub.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WebsocketService {
    private final NotificationRes notificationRes;

    public void saveChatMessage(Notification notification) {
        if (notification != null) {
            notificationRes.save(notification);
        }
    }
    public List<Notification> getChatMessage(String senderUUid,String receiverUUid) {
        if (senderUUid != null) {
            List<Notification> notifications = notificationRes.findAll().stream()
                    .filter(n -> n.getSenderUuid().equals(senderUUid) && n.getReceiverUuid().equals(receiverUUid))
                    .collect(Collectors.toList());
            notifications.forEach(System.out::println);
            return notifications;
        }
        return null;
    }
    public List<Notification> getChatMessages() {
        return notificationRes.findAll();
    }
}
