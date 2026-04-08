package com.jobridge.backend.controller;

import com.jobridge.backend.model.Notification;
import com.jobridge.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationRepository notifRepo;

    // ── GET notifs admin ──────────────────────────
    @GetMapping("/admin")
    public List<Notification> getAdminNotifs() {
        return notifRepo.findByRecipientTypeOrderByCreatedAtDesc("admin");
    }

    // ── GET notifs candidat ───────────────────────
    @GetMapping("/candidate/{id}")
    public List<Notification> getCandidateNotifs(
            @PathVariable String id) {
        return notifRepo
            .findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(
                "candidate", id);
    }

    // ── GET notifs entreprise ─────────────────────
    @GetMapping("/company/{id}")
    public List<Notification> getCompanyNotifs(
            @PathVariable String id) {
        return notifRepo
            .findByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(
                "company", id);
    }

    // ── Nombre non lues admin ─────────────────────
    @GetMapping("/admin/unread-count")
    public Map<String, Long> getAdminUnreadCount() {
        long count = notifRepo
            .countByRecipientTypeAndIsReadFalse("admin");
        return Map.of("count", count);
    }

    // ── Nombre non lues candidat ──────────────────
    @GetMapping("/candidate/{id}/unread-count")
    public Map<String, Long> getCandidateUnreadCount(
            @PathVariable String id) {
        long count = notifRepo
            .countByRecipientTypeAndRecipientIdAndIsReadFalse(
                "candidate", id);
        return Map.of("count", count);
    }

    // ── Nombre non lues entreprise ────────────────
    @GetMapping("/company/{id}/unread-count")
    public Map<String, Long> getCompanyUnreadCount(
            @PathVariable String id) {
        long count = notifRepo
            .countByRecipientTypeAndRecipientIdAndIsReadFalse(
                "company", id);
        return Map.of("count", count);
    }

    // ── Marquer une notif comme lue ───────────────
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(
            @PathVariable String id) {
        return notifRepo.findById(id).map(n -> {
            n.setRead(true);
            return ResponseEntity.ok(notifRepo.save(n));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Marquer toutes admin comme lues ──────────
    @PutMapping("/admin/read-all")
    public ResponseEntity<?> markAllAdminRead() {
        List<Notification> notifs = notifRepo
            .findByRecipientTypeAndIsReadFalse("admin");
        notifs.forEach(n -> n.setRead(true));
        notifRepo.saveAll(notifs);
        return ResponseEntity.ok(Map.of(
            "message", "Toutes les notifs admin marquées lues"));
    }

    // ── Marquer toutes candidat comme lues ───────
    @PutMapping("/candidate/{id}/read-all")
    public ResponseEntity<?> markAllCandidateRead(
            @PathVariable String id) {
        List<Notification> notifs = notifRepo
            .findByRecipientTypeAndRecipientIdAndIsReadFalse(
                "candidate", id);
        notifs.forEach(n -> n.setRead(true));
        notifRepo.saveAll(notifs);
        return ResponseEntity.ok(Map.of("message", "ok"));
    }

    // ── Marquer toutes entreprise comme lues ─────
    @PutMapping("/company/{id}/read-all")
    public ResponseEntity<?> markAllCompanyRead(
            @PathVariable String id) {
        List<Notification> notifs = notifRepo
            .findByRecipientTypeAndRecipientIdAndIsReadFalse(
                "company", id);
        notifs.forEach(n -> n.setRead(true));
        notifRepo.saveAll(notifs);
        return ResponseEntity.ok(Map.of("message", "ok"));
    }
}