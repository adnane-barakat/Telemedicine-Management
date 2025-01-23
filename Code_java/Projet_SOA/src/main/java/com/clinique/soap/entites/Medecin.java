package com.clinique.soap.entites;

import java.util.Objects;

public class Medecin {
    private Long id;
    private String nom;
    private String prenom;
    private String specialite;
    private String email;


    // Constructeur

    public Medecin() {}

    public Medecin(Long id, String nom, String prenom, String specialite, String email) {
        this.id = id;
        this.nom = nom.toLowerCase();
        this.prenom = prenom.toLowerCase();
        this.specialite = specialite.toLowerCase();
        this.email = email.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medecin medecin = (Medecin) o;
        return Objects.equals(nom, medecin.nom) &&
                Objects.equals(prenom, medecin.prenom) &&
                Objects.equals(specialite, medecin.specialite) &&
                Objects.equals(email, medecin.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, prenom, specialite, email); // Utilisation des autres attributs sauf id
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom.toLowerCase(); }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom.toLowerCase(); }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite.toLowerCase(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email.toLowerCase(); }

    @Override
    public String toString() {
        return "Medecin{" + "id=" + id + ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' + ", specialite='" + specialite + '\'' +
                ", email='" + email + '\'' + '}';
    }
}
