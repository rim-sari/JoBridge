package com.jobridge.backend.repository;

import com.jobridge.backend.model.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, String> {
    List<Application> findByCandidateId(String candidateId);
    List<Application> findByCompanyId(String companyId);
    List<Application> findByOfferId(String offerId);
    boolean existsByOfferIdAndCandidateId(
            String offerId, String candidateId);
}