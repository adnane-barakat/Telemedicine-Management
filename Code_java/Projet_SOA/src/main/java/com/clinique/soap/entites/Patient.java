package com.clinique.soap.entites;


import java.util.Objects;

public class Patient {
    private Long id;
    private String nom;
    private String prenom;
    private String email;


    public Patient() {}


    // Constructeur
    public Patient(Long idp,String nom, String prenom, String email) {
        this.id = idp;
        this.nom = nom.toLowerCase();
        this.prenom = prenom.toLowerCase();
        this.email = email.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Patient patient = (Patient) o;
        return Objects.equals(nom, patient.nom) &&
                Objects.equals(prenom, patient.prenom) &&
                Objects.equals(email, patient.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nom, prenom, email); // Utilisation des autres attributs sauf id
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom.toLowerCase(); }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom.toLowerCase(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email.toLowerCase(); }


    @Override
    public String toString() {
        return "Patient{" + "id=" + id + ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' + ", email='" + email + '\'' + '}';
    }
}