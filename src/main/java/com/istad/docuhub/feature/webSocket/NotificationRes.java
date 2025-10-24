package com.istad.docuhub.feature.webSocket;

import com.istad.docuhub.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRes extends JpaRepository<Notification, Integer> {
    Optional<Notification> findBySenderUuidAndReceiverUuidAndIsReadIsFalse(String senderUuid, String receiverUuid);

}
