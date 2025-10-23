package com.istad.docuhub.feature.webSocket;

import com.istad.docuhub.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRes extends JpaRepository<Notification, Integer> {

}
