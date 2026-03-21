package com.jobridge.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    private String id;
    private String offerId;
    private String offerTitle;
    private String companyId;
    private String companyName;
    private String candidateId;
    private String candidateName;

    @Column(columnDefinition = "TEXT")
    private String letter;

    private String cvName;
    private String status = "pending";
    private LocalDate datePostulation;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOfferId() { return offerId; }
    public void setOfferId(String offerId) { this.offerId = offerId; }

    public String getOfferTitle() { return offerTitle; }
    public void setOfferTitle(String offerTitle) { this.offerTitle = offerTitle; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getCandidateId() { return candidateId; }
    public void setCandidateId(String candidateId) { this.candidateId = candidateId; }

    public String getCandidateName() { return candidateName; }
    public void setCandidateName(String candidateName) { this.candidateName = candidateName; }

    public String getLetter() { return letter; }
    public void setLetter(String letter) { this.letter = letter; }

    public String getCvName() { return cvName; }
    public void setCvName(String cvName) { this.cvName = cvName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDatePostulation() { return datePostulation; }
    public void setDatePostulation(LocalDate d) { this.datePostulation = d; }
}