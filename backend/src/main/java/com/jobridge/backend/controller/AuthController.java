package com.jobridge.backend.controller;

import com.jobridge.backend.model.*;
import com.jobridge.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired private CandidateRepository      candidateRepo;
    @Autowired private CompanyRepository        companyRepo;
    @Autowired private NotificationRepository   notifRepo;
    @Autowired private DeletedAccountRepository deletedRepo;
    @Autowired private ApplicationRepository    appRepo;
    @Autowired private OfferRepository          offerRepo;

    private void createNotif(String recipientType, String recipientId,
                              String type, String message, String resourceId) {
        Notification n = new Notification();
        n.setId(UUID.randomUUID().toString());
        n.setRecipientType(recipientType);
        n.setRecipientId(recipientId);
        n.setType(type);
        n.setMessage(message);
        n.setResourceId(resourceId);
        notifRepo.save(n);
    }

    @PostMapping("/register/candidate")
    public ResponseEntity<?> registerCandidate(@RequestBody Candidate c) {
        if (candidateRepo.existsByEmail(c.getEmail()))
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        c.setId("CAN-" + (1000 + new Random().nextInt(9000)));
        c.setStatus("pending");
        c.setDateInscription(LocalDate.now());
        Candidate saved = candidateRepo.save(c);
        createNotif("admin", null, "NEW_CANDIDATE",
            "Nouveau candidat en attente : " + c.getPrenom() + " " + c.getNom()
                + " (" + c.getEmail() + ")", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/register/company")
    public ResponseEntity<?> registerCompany(@RequestBody Company co) {
        if (companyRepo.existsByEmail(co.getEmail()))
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        co.setId("ENT-" + (1000 + new Random().nextInt(9000)));
        co.setStatus("pending");
        co.setDateInscription(LocalDate.now());
        Company saved = companyRepo.save(co);
        createNotif("admin", null, "NEW_COMPANY",
            "Nouvelle entreprise en attente : " + co.getName()
                + " (" + co.getEmail() + ")", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email    = body.get("email");
        String password = body.get("password");
        if ("admin@jobridge.com".equals(email) && "admin123".equals(password))
            return ResponseEntity.ok(Map.of("role", "admin", "name", "Administrateur"));
        var cand = candidateRepo.findByEmail(email);
        if (cand.isPresent() && cand.get().getPassword().equals(password)) {
            Candidate c = cand.get();
            if ("pending".equals(c.getStatus()))
                return ResponseEntity.status(403).body("Votre compte est en attente de validation.");
            if ("rejected".equals(c.getStatus()))
                return ResponseEntity.status(403).body("Votre compte a été rejeté.");
            return ResponseEntity.ok(Map.of("role", "candidate", "user", c));
        }
        var comp = companyRepo.findByEmail(email);
        if (comp.isPresent() && comp.get().getPassword().equals(password)) {
            Company co = comp.get();
            if ("pending".equals(co.getStatus()))
                return ResponseEntity.status(403).body("Votre compte entreprise est en attente de validation.");
            if ("rejected".equals(co.getStatus()))
                return ResponseEntity.status(403).body("Votre compte entreprise a été rejeté.");
            return ResponseEntity.ok(Map.of("role", "company", "user", co));
        }
        return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
    }

    @DeleteMapping("/delete-account")
    public ResponseEntity<?> deleteOwnAccount(@RequestBody Map<String, String> body) {
        String id     = body.get("id");
        String role   = body.get("role");
        String reason = body.getOrDefault("reason", "Suppression demandée par l'utilisateur");

        if ("candidate".equals(role)) {
            return candidateRepo.findById(id).map(c -> {
                // ETAPE 1 : Archiver dans deleted_accounts
                DeletedAccount da = new DeletedAccount();
                da.setId(UUID.randomUUID().toString());
                da.setAccountType("candidate");
                da.setAccountId(c.getId());
                da.setAccountName(c.getPrenom() + " " + c.getNom());
                da.setEmail(c.getEmail());
                da.setDeletedBy("self");
                da.setReason(reason);
                deletedRepo.save(da);

                // ETAPE 2 : Marquer candidatures abandonnées
                List<Application> apps = appRepo.findByCandidateId(id);
                for (Application app : apps) {
                    app.setStatus("abandoned");
                    appRepo.save(app);
                    createNotif("company", app.getCompanyId(), "APPLICATION_ABANDONED",
                        "Le candidat " + c.getPrenom() + " " + c.getNom()
                            + " a supprimé son compte. Sa candidature pour \""
                            + app.getOfferTitle() + "\" est abandonnée.", app.getId());
                }

                // ETAPE 3 : Notifier admin
                createNotif("admin", null, "ACCOUNT_DELETED",
                    "Le candidat " + c.getPrenom() + " " + c.getNom()
                        + " (" + c.getEmail() + ") a supprimé son compte. Motif : " + reason, id);

                // ETAPE 4 : Supprimer
                candidateRepo.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Compte supprimé", "archived", da.getId()));
            }).orElse(ResponseEntity.notFound().build());
        }

        if ("company".equals(role)) {
            return companyRepo.findById(id).map(co -> {
                // ETAPE 1 : Archiver
                DeletedAccount da = new DeletedAccount();
                da.setId(UUID.randomUUID().toString());
                da.setAccountType("company");
                da.setAccountId(co.getId());
                da.setAccountName(co.getName());
                da.setEmail(co.getEmail());
                da.setDeletedBy("self");
                da.setReason(reason);
                deletedRepo.save(da);

                // ETAPE 2 : Traiter offres et candidatures
                List<Offer> offers = offerRepo.findByCompanyId(id);
                for (Offer offer : offers) {
                    List<Application> apps = appRepo.findByOfferId(offer.getId());
                    for (Application app : apps) {
                        app.setStatus("abandoned");
                        appRepo.save(app);
                        createNotif("candidate", app.getCandidateId(), "APPLICATION_ABANDONED",
                            "L'entreprise " + co.getName()
                                + " a supprimé son compte. Votre candidature pour \""
                                + app.getOfferTitle() + "\" est abandonnée.", app.getId());
                    }
                    offerRepo.deleteById(offer.getId());
                }

                // ETAPE 3 : Notifier admin
                createNotif("admin", null, "ACCOUNT_DELETED",
                    "L'entreprise " + co.getName()
                        + " (" + co.getEmail() + ") a supprimé son compte. Motif : " + reason, id);

                // ETAPE 4 : Supprimer
                companyRepo.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Compte supprimé", "archived", da.getId()));
            }).orElse(ResponseEntity.notFound().build());
        }

        return ResponseEntity.badRequest().body("Rôle non reconnu");
    }
}