package com.jobridge.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate extends User {

    private String prenom;
    private String nom;

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
}