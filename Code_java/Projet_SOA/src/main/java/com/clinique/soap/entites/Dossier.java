package com.clinique.soap.entites;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Dossier {
    private Long id;
    private Patient patient;
    private Set<Prescription> prescriptions; // Utilisation d'un Set pour éviter les doublons
    private Set<Rapport> rapports; // Utilisation d'un Set pour éviter les doublons


    // Constructeur

    public Dossier() {}

    public Dossier(Long iddossier,Patient patient) {
        this.id = iddossier;
        this.patient = patient;
        this.prescriptions = new HashSet<>(); // Initialisation d'un Set vide
        this.rapports = new HashSet<>(); // Initialisation d'un Set vide
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dossier dossier = (Dossier) o;
        return Objects.equals(patient, dossier.patient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patient); // Utilisation des autres attributs sauf id
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Set<Prescription> getPrescriptions() { return prescriptions; }
    public void setPrescriptions(Set<Prescription> prescriptions) { this.prescriptions = prescriptions; }

    public Set<Rapport> getRapports() { return rapports; }
    public void setRapports(Set<Rapport> rapports) { this.rapports = rapports; }

    // Méthode pour ajouter une prescription sans dupliquer
    public void addPrescription(Prescription prescription) {
        this.prescriptions.add(prescription);
    }

    // Méthode pour ajouter un rapport sans dupliquer
    public void addRapport(Rapport rapport) {
        this.rapports.add(rapport);
    }

    @Override
    public String toString() {
        return "Dossier{" +
                "id=" + id +
                '\'' +
                ", patient=" + patient +
                ", prescriptions=" + prescriptions +
                ", rapports=" + rapports +
                '}';
    }
}
