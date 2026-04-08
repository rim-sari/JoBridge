package com.jobridge.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deleted_accounts")
public class DeletedAccount {

    @Id
    private String id;

    // "candidate" ou "company"
    private String accountType;

    private String accountId;
    private String accountName;
    private String email;

    // Qui a supprimé : "admin" | "self"
    private String deletedBy;

    @Column(columnDefinition = "TEXT")
    private String reason;

    private LocalDateTime deletedAt;

    @PrePersist
    protected void onCreate() {
        deletedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDeletedBy() { return deletedBy; }
    public void setDeletedBy(String deletedBy) { this.deletedBy = deletedBy; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }
}