package com.jobridge.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company extends User {

    private String name;
    private String secteur;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSecteur() { return secteur; }
    public void setSecteur(String secteur) { this.secteur = secteur; }
}