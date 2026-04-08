package com.jobridge.backend.controller;

import com.jobridge.backend.model.*;
import com.jobridge.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private CandidateRepository      candidateRepo;
    @Autowired private CompanyRepository        companyRepo;
    @Autowired private OfferRepository          offerRepo;
    @Autowired private ApplicationRepository    appRepo;
    @Autowired private NotificationRepository   notifRepo;
    @Autowired private DeletedAccountRepository deletedRepo;

    private void createNotif(String recipientType, String recipientId,
                              String type, String message, String resourceId) {
        try {
            Notification n = new Notification();
            n.setId(UUID.randomUUID().toString());
            n.setRecipientType(recipientType);
            n.setRecipientId(recipientId);
            n.setType(type);
            n.setMessage(message);
            n.setResourceId(resourceId);
            notifRepo.save(n);
        } catch (Exception e) {
            System.err.println("Erreur notif : " + e.getMessage());
        }
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> data = new HashMap<>();
        List<Candidate> candidates = candidateRepo.findAll();
        List<Company>   companies  = companyRepo.findAll();
        data.put("totalCandidates",     candidates.size());
        data.put("pendingCandidates",   candidates.stream().filter(c -> "pending".equals(c.getStatus())).count());
        data.put("validatedCandidates", candidates.stream().filter(c -> "validated".equals(c.getStatus())).count());
        data.put("totalCompanies",      companies.size());
        data.put("pendingCompanies",    companies.stream().filter(c -> "pending".equals(c.getStatus())).count());
        data.put("validatedCompanies",  companies.stream().filter(c -> "validated".equals(c.getStatus())).count());
        data.put("totalOffers",         offerRepo.count());
        data.put("totalApplications",   appRepo.count());
        data.put("deletedAccounts",     deletedRepo.count());
        data.put("unreadNotifications", notifRepo.countByRecipientTypeAndIsReadFalse("admin"));
        return data;
    }

    @GetMapping("/candidates")
    public List<Candidate> getAllCandidates() { return candidateRepo.findAll(); }

    @GetMapping("/companies")
    public List<Company> getAllCompanies() { return companyRepo.findAll(); }

    @GetMapping("/offers")
    public List<Offer> getAllOffers() { return offerRepo.findAll(); }

    @GetMapping("/applications")
    public List<Application> getAllApplications() { return appRepo.findAll(); }

    @GetMapping("/deleted-accounts")
    public List<DeletedAccount> getDeletedAccounts() {
        return deletedRepo.findAllByOrderByDeletedAtDesc();
    }

    @PutMapping("/candidates/{id}/validate")
    public ResponseEntity<?> validateCandidate(@PathVariable String id,
            @RequestBody Map<String, String> body) {
        Optional<Candidate> opt = candidateRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Candidate c = opt.get();
        String newStatus = body.get("status");
        c.setStatus(newStatus);
        candidateRepo.save(c);
        createNotif("candidate", id,
            "validated".equals(newStatus) ? "ACCOUNT_VALIDATED" : "ACCOUNT_REJECTED",
            "validated".equals(newStatus)
                ? "Votre compte a ete valide. Vous pouvez maintenant postuler."
                : "Votre compte a ete rejete par l'administrateur.",
            id);
        return ResponseEntity.ok(c);
    }

    @PutMapping("/companies/{id}/validate")
    public ResponseEntity<?> validateCompany(@PathVariable String id,
            @RequestBody Map<String, String> body) {
        Optional<Company> opt = companyRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Company co = opt.get();
        String newStatus = body.get("status");
        co.setStatus(newStatus);
        companyRepo.save(co);
        createNotif("company", id,
            "validated".equals(newStatus) ? "ACCOUNT_VALIDATED" : "ACCOUNT_REJECTED",
            "validated".equals(newStatus)
                ? "Votre compte entreprise a ete valide."
                : "Votre compte entreprise a ete rejete.",
            id);
        return ResponseEntity.ok(co);
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable String id,
            @RequestBody(required = false) Map<String, String> body) {

        Optional<Candidate> opt = candidateRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Candidate c = opt.get();

        String reason = (body != null)
            ? body.getOrDefault("reason", "Supprime par l'administrateur")
            : "Supprime par l'administrateur";

        // ETAPE 1 : Sauvegarder dans deleted_accounts
        DeletedAccount da = new DeletedAccount();
        da.setId(UUID.randomUUID().toString());
        da.setAccountType("candidate");
        da.setAccountId(c.getId());
        da.setAccountName(c.getPrenom() + " " + c.getNom());
        da.setEmail(c.getEmail());
        da.setDeletedBy("admin");
        da.setReason(reason);
        deletedRepo.save(da);
        System.out.println(">>> ETAPE 1 OK : DeletedAccount sauvegarde id=" + da.getId());

        // ETAPE 2 : Marquer candidatures abandonnees
        List<Application> apps = appRepo.findByCandidateId(id);
        for (Application app : apps) {
            app.setStatus("Utilisateur n'existe plus ");
            appRepo.save(app);
        }
        System.out.println(">>> ETAPE 2 OK : " + apps.size() + " candidature(s) abandonnees");

        // ETAPE 3 : Notifications
        createNotif("admin", null, "ACCOUNT_DELETED",
            "Le candidat " + c.getPrenom() + " " + c.getNom()
                + " (" + c.getEmail() + ") a ete supprime. Motif : " + reason,
            c.getId());

        for (Application app : apps) {
            createNotif("company", app.getCompanyId(),
                "APPLICATION_ABANDONED",
                "Le candidat " + c.getPrenom() + " " + c.getNom()
                    + " a ete supprime. Sa candidature pour \""
                    + app.getOfferTitle() + "\" est abandonnee.",
                app.getId());
        }
        System.out.println(">>> ETAPE 3 OK : Notifications envoyees");

        // ETAPE 4 : Supprimer le candidat
        candidateRepo.deleteById(id);
        System.out.println(">>> ETAPE 4 OK : Candidat supprime");

        return ResponseEntity.ok(Map.of(
            "message", "Candidat supprime avec succes",
            "deletedAccountSaved", da.getId(),
            "applicationsAbandoned", apps.size()
        ));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable String id,
            @RequestBody(required = false) Map<String, String> body) {

        Optional<Company> opt = companyRepo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();
        Company co = opt.get();

        String reason = (body != null)
            ? body.getOrDefault("reason", "Supprime par l'administrateur")
            : "Supprime par l'administrateur";

        // ETAPE 1 : Sauvegarder dans deleted_accounts
        DeletedAccount da = new DeletedAccount();
        da.setId(UUID.randomUUID().toString());
        da.setAccountType("company");
        da.setAccountId(co.getId());
        da.setAccountName(co.getName());
        da.setEmail(co.getEmail());
        da.setDeletedBy("admin");
        da.setReason(reason);
        System.out.println(">>> AVANT SAVE : type=" + da.getAccountType() + " nom=" + da.getAccountName());
        try {
            DeletedAccount saved = deletedRepo.save(da);
            System.out.println(">>> ETAPE 1 OK : sauvegarde id=" + saved.getId());
        } catch (Exception ex) {
            System.err.println(">>> ERREUR SAVE deleted_accounts : " + ex.getMessage());
            ex.printStackTrace();
        }
        // ETAPE 2 : Traiter offres et candidatures
        List<Offer> offers = offerRepo.findByCompanyId(id);
        List<Application> allApps = new ArrayList<>();
        for (Offer offer : offers) {
            List<Application> offerApps = appRepo.findByOfferId(offer.getId());
            for (Application app : offerApps) {
                app.setStatus("abandoned");
                appRepo.save(app);
                allApps.add(app);
            }
        }
        System.out.println(">>> ETAPE 2 OK : " + allApps.size() + " candidature(s) abandonnees");

        // ETAPE 3 : Notifications
        createNotif("admin", null, "ACCOUNT_DELETED",
            "L'entreprise " + co.getName()
                + " (" + co.getEmail() + ") a ete supprimee. Motif : " + reason,
            co.getId());

        for (Application app : allApps) {
            createNotif("candidate", app.getCandidateId(),
                "APPLICATION_ABANDONED",
                "L'entreprise " + co.getName()
                    + " a ete supprimee. Votre candidature pour \""
                    + app.getOfferTitle() + "\" est abandonnee.",
                app.getId());
        }
        System.out.println(">>> ETAPE 3 OK : Notifications envoyees");

        // ETAPE 4 : Supprimer offres puis entreprise
        for (Offer offer : offers) {
            offerRepo.deleteById(offer.getId());
        }
        companyRepo.deleteById(id);
        System.out.println(">>> ETAPE 4 OK : Entreprise supprimee");

        return ResponseEntity.ok(Map.of(
            "message", "Entreprise supprimee avec succes",
            "deletedAccountSaved", da.getId(),
            "offersDeleted", offers.size(),
            "applicationsAbandoned", allApps.size()
        ));
    }
}
