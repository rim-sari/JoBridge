package com.jobridge.backend.controller;

import com.jobridge.backend.model.Candidate;
import com.jobridge.backend.model.Company;
import com.jobridge.backend.repository.CandidateRepository;
import com.jobridge.backend.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private CandidateRepository candidateRepo;

    @Autowired
    private CompanyRepository companyRepo;

    @PostMapping("/register/candidate")
    public ResponseEntity<?> registerCandidate(
            @RequestBody Candidate c) {
        if (candidateRepo.existsByEmail(c.getEmail()))
            return ResponseEntity.badRequest()
                    .body("Email déjà utilisé");
        c.setId("CAN-" + (1000 + new Random().nextInt(9000)));
        c.setStatus("pending");
        c.setDateInscription(LocalDate.now());
        return ResponseEntity.ok(candidateRepo.save(c));
    }

    @PostMapping("/register/company")
    public ResponseEntity<?> registerCompany(
            @RequestBody Company co) {
        if (companyRepo.existsByEmail(co.getEmail()))
            return ResponseEntity.badRequest()
                    .body("Email déjà utilisé");
        co.setId("ENT-" + (1000 + new Random().nextInt(9000)));
        co.setStatus("pending");
        co.setDateInscription(LocalDate.now());
        return ResponseEntity.ok(companyRepo.save(co));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        // Admin codé en dur — pas de table en base
        if ("admin@jobridge.com".equals(email)
                && "admin123".equals(password))
            return ResponseEntity.ok(Map.of(
                    "role", "admin",
                    "name", "Administrateur"));

        // Candidat
        var cand = candidateRepo.findByEmail(email);
        if (cand.isPresent()
                && cand.get().getPassword().equals(password))
            return ResponseEntity.ok(Map.of(
                    "role", "candidate",
                    "user", cand.get()));

        // Entreprise
        var comp = companyRepo.findByEmail(email);
        if (comp.isPresent()
                && comp.get().getPassword().equals(password))
            return ResponseEntity.ok(Map.of(
                    "role", "company",
                    "user", comp.get()));

        return ResponseEntity.status(401)
                .body("Email ou mot de passe incorrect");
    }
}