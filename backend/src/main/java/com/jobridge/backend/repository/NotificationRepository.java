package com.jobridge.backend.repository;

import com.jobridge.backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, String> {

    // Notifs admin
    List<Notification> findByRecipientTypeOrderByCreatedAtDesc(
            String recipientType);

    // Notifs d'un candidat ou entreprise spécifique
    List<Notification> findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(
            String recipientType, String recipientId);

    // Notifs non lues admin
    List<Notification> findByRecipientTypeAndIsReadFalse(
            String recipientType);

    // Notifs non lues d'un user
    List<Notification> findByRecipientTypeAndRecipientIdAndIsReadFalse(
            String recipientType, String recipientId);

    long countByRecipientTypeAndIsReadFalse(String recipientType);

    long countByRecipientTypeAndRecipientIdAndIsReadFalse(
            String recipientType, String recipientId);
}