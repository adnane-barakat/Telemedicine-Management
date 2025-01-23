package com.clinique.rest.models;

import com.clinique.soap.entites.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class RendezVous {
    private Long id;
    private Patient patient;
    private Medecin medecin;
    private LocalDate date;
    private LocalTime heure;
    private String status;

    // Constructeur par défaut
    public RendezVous() {}

    // Constructeur avec tous les attributs
    public RendezVous(Long id, Patient patient, Medecin medecin, LocalDate date, LocalTime heure, String status) {
        this.id = id;
        this.patient = patient;
        this.medecin = medecin;
        this.date = date;
        this.heure = heure;
        this.status = status;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getHeure() {
        return heure;
    }

    public void setHeure(LocalTime heure) {
        this.heure = heure;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Méthode toString pour afficher les détails du rendez-vous
    @Override
    public String toString() {
        return "RendezVous{" +
                "id=" + id +
                ", patient=" + patient +
                ", medecin=" + medecin +
                ", date=" + date +
                ", heure=" + heure +
                ", status='" + status + '\'' +
                '}';
    }

    // Redéfinition de equals (comparaison avec tous les attributs sauf id)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RendezVous that = (RendezVous) o;
        return Objects.equals(patient, that.patient) &&
                Objects.equals(medecin, that.medecin) &&
                Objects.equals(date, that.date) &&
                Objects.equals(heure, that.heure) &&
                Objects.equals(status, that.status);
    }

    // Redéfinition de hashCode (basé sur tous les attributs sauf id)
    @Override
    public int hashCode() {
        return Objects.hash(patient, medecin, date, heure, status);
    }
}

