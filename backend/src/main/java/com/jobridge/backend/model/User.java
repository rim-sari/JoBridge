package com.jobridge.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@MappedSuperclass
public abstract class User {

    @Id
    private String id;

    @Column(unique = true)
    private String email;

    private String password;
    private String status = "pending";
    private LocalDate dateInscription;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate d) { this.dateInscription = d; }
}