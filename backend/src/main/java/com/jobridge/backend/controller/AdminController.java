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

    @Autowired private CandidateRepository candidateRepo;
    @Autowired private CompanyRepository companyRepo;
    @Autowired private OfferRepository offerRepo;
    @Autowired private ApplicationRepository appRepo;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        Map<String, Object> data = new HashMap<>();
        List<Candidate> candidates = candidateRepo.findAll();
        List<Company> companies = companyRepo.findAll();
        data.put("totalCandidates", candidates.size());
        data.put("pendingCandidates", candidates.stream()
                .filter(c -> "pending".equals(c.getStatus()))
                .count());
        data.put("validatedCandidates", candidates.stream()
                .filter(c -> "validated".equals(c.getStatus()))
                .count());
        data.put("totalCompanies", companies.size());
        data.put("pendingCompanies", companies.stream()
                .filter(c -> "pending".equals(c.getStatus()))
                .count());
        data.put("validatedCompanies", companies.stream()
                .filter(c -> "validated".equals(c.getStatus()))
                .count());
        data.put("totalOffers", offerRepo.count());
        data.put("totalApplications", appRepo.count());
        return data;
    }

    @GetMapping("/candidates")
    public List<Candidate> getAllCandidates() {
        return candidateRepo.findAll();
    }

    @GetMapping("/companies")
    public List<Company> getAllCompanies() {
        return companyRepo.findAll();
    }

    @GetMapping("/offers")
    public List<Offer> getAllOffers() {
        return offerRepo.findAll();
    }

    @GetMapping("/applications")
    public List<Application> getAllApplications() {
        return appRepo.findAll();
    }

    @PutMapping("/candidates/{id}/validate")
    public ResponseEntity<?> validateCandidate(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return candidateRepo.findById(id).map(c -> {
            c.setStatus(body.get("status"));
            return ResponseEntity.ok(candidateRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/companies/{id}/validate")
    public ResponseEntity<?> validateCompany(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        return companyRepo.findById(id).map(c -> {
            c.setStatus(body.get("status"));
            return ResponseEntity.ok(companyRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
}