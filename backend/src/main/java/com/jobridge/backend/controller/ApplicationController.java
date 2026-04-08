package com.jobridge.backend.controller;

import java.util.Optional;
import com.jobridge.backend.model.Application;
import com.jobridge.backend.model.Notification;
import com.jobridge.backend.repository.ApplicationRepository;
import com.jobridge.backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired private ApplicationRepository   appRepo;
    @Autowired private NotificationRepository  notifRepo;

    @Value("${app.upload.dir:uploads/cv}")
    private String uploadDir;

    // ── Helper notification ───────────────────────
    private void createNotif(String recipientType,
                              String recipientId,
                              String type,
                              String message,
                              String resourceId) {
        Notification n = new Notification();
        n.setId(UUID.randomUUID().toString());
        n.setRecipientType(recipientType);
        n.setRecipientId(recipientId);
        n.setType(type);
        n.setMessage(message);
        n.setResourceId(resourceId);
        notifRepo.save(n);
    }

    // ── Postuler ──────────────────────────────────
    @PostMapping
    public ResponseEntity<?> apply(
            @RequestBody Application app) {
        if (appRepo.existsByOfferIdAndCandidateId(
                app.getOfferId(), app.getCandidateId()))
            return ResponseEntity.badRequest()
                    .body("Vous avez déjà postulé à cette offre");
        app.setId(UUID.randomUUID().toString());
        app.setStatus("pending");
        app.setDatePostulation(LocalDate.now());
        Application saved = appRepo.save(app);

        // Notifier l'entreprise : nouvelle candidature
        createNotif("company", app.getCompanyId(),
            "NEW_APPLICATION",
            app.getCandidateName() + " a postulé pour l'offre \""
                + app.getOfferTitle() + "\".",
            saved.getId());

        return ResponseEntity.ok(saved);
    }

    // ── Upload CV ─────────────────────────────────
    @PostMapping("/{id}/upload-cv")
    public ResponseEntity<?> uploadCv(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            String filename = id + "_" + file.getOriginalFilename();
            Path path = dir.resolve(filename);
            Files.copy(file.getInputStream(), path,
                StandardCopyOption.REPLACE_EXISTING);

            return appRepo.findById(id).map(app -> {
                app.setCvName(filename);
                return ResponseEntity.ok(appRepo.save(app));
            }).orElse(ResponseEntity.notFound().build());

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur upload CV : " + e.getMessage());
        }
    }

    // ── Télécharger CV (pour l'entreprise) ────────
    @GetMapping("/{id}/download-cv")
    public ResponseEntity<?> downloadCv(
            @PathVariable String id) {
        Optional<Application> optApp = appRepo.findById(id);
        if (optApp.isEmpty())
            return ResponseEntity.notFound().build();

        Application app = optApp.get();
        if (app.getCvName() == null)
            return ResponseEntity.notFound().build();

        try {
            Path path = Paths.get(uploadDir)
                    .resolve(app.getCvName()).normalize();
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists())
                return ResponseEntity.notFound().build();

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + app.getCvName() + "\"")
                .body(resource);

        } catch (MalformedURLException e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur fichier : " + e.getMessage());
        }
    }

    // ── Lire par candidat ─────────────────────────
    @GetMapping("/candidate/{candidateId}")
    public List<Application> getByCandidate(
            @PathVariable String candidateId) {
        return appRepo.findByCandidateId(candidateId);
    }

    // ── Lire par entreprise ───────────────────────
    @GetMapping("/company/{companyId}")
    public List<Application> getByCompany(
            @PathVariable String companyId) {
        return appRepo.findByCompanyId(companyId);
    }

    // ── Lire par offre ────────────────────────────
    @GetMapping("/offer/{offerId}")
    public List<Application> getByOffer(
            @PathVariable String offerId) {
        return appRepo.findByOfferId(offerId);
    }

    // ── Mettre à jour statut (entreprise répond) ──
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return appRepo.findById(id).map(app -> {
            String newStatus = body.get("status");
            String responseMsg = body.getOrDefault("responseMessage", "");
            app.setStatus(newStatus);
            Application saved = appRepo.save(app);

            // Notifier le candidat de la réponse
            String label = switch (newStatus) {
                case "accepted"    -> "acceptée ✅";
                case "rejected"    -> "rejetée ❌";
                case "interview"   -> "sélectionnée pour un entretien 📅";
                default            -> "mise à jour";
            };
            String msg = "Votre candidature pour \""
                + app.getOfferTitle() + "\" chez "
                + app.getCompanyName() + " a été " + label
                + (responseMsg.isEmpty() ? "." : ". Message : " + responseMsg);

            createNotif("candidate", app.getCandidateId(),
                "APP_RESPONSE", msg, id);

            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
}