package com.jobridge.backend.controller;

import com.jobridge.backend.model.Offer;
import com.jobridge.backend.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/offers")
@CrossOrigin(origins = "*")
public class OfferController {

    @Autowired
    private OfferRepository offerRepo;

    @GetMapping
    public List<Offer> getAllOffers() {
        return offerRepo.findAll();
    }

    @GetMapping("/company/{companyId}")
    public List<Offer> getByCompany(
            @PathVariable String companyId) {
        return offerRepo.findByCompanyId(companyId);
    }

    @PostMapping
    public ResponseEntity<?> createOffer(
            @RequestBody Offer offer) {
        offer.setId(UUID.randomUUID().toString());
        offer.setDatePublication(LocalDate.now());
        return ResponseEntity.ok(offerRepo.save(offer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOffer(
            @PathVariable String id) {
        offerRepo.deleteById(id);
        return ResponseEntity.ok("Offre supprimée");
    }
}