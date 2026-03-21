package com.jobridge.backend.repository;

import com.jobridge.backend.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OfferRepository
        extends JpaRepository<Offer, String> {
    List<Offer> findByCompanyId(String companyId);
}