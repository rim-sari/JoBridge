package com.jobridge.backend.repository;

import com.jobridge.backend.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CandidateRepository
        extends JpaRepository<Candidate, String> {
    Optional<Candidate> findByEmail(String email);
    boolean existsByEmail(String email);
}