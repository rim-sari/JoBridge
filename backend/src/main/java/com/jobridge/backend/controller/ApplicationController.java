package com.jobridge.backend.controller;

import com.jobridge.backend.model.Application;
import com.jobridge.backend.repository.ApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    @Autowired
    private ApplicationRepository appRepo;

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
        return ResponseEntity.ok(appRepo.save(app));
    }

    @GetMapping("/candidate/{candidateId}")
    public List<Application> getByCandidate(
            @PathVariable String candidateId) {
        return appRepo.findByCandidateId(candidateId);
    }

    @GetMapping("/company/{companyId}")
    public List<Application> getByCompany(
            @PathVariable String companyId) {
        return appRepo.findByCompanyId(companyId);
    }

    @GetMapping("/offer/{offerId}")
    public List<Application> getByOffer(
            @PathVariable String offerId) {
        return appRepo.findByOfferId(offerId);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return appRepo.findById(id).map(app -> {
            app.setStatus(body.get("status"));
            return ResponseEntity.ok(appRepo.save(app));
        }).orElse(ResponseEntity.notFound().build());
    }
}