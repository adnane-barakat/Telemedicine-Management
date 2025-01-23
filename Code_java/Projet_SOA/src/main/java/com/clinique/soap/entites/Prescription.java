package com.clinique.soap.entites;

import java.util.Objects;

public class Prescription {
    private Long id;
    private Long idMedecin;
    private String details;

    // Constructeur

    public Prescription() {}

    public Prescription(Long id,Long idMedecin, String details) {
        this.id = id;
        this.idMedecin=idMedecin;
        this.details = details.toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prescription that = (Prescription) o;
        return Objects.equals(idMedecin, that.idMedecin) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idMedecin, details); // Utilisation des autres attributs sauf id
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdMedecin() { return idMedecin; }
    public void setIdMedecin(Long idMedecin) { this.idMedecin = idMedecin; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }


    @Override
    public String toString() {
        return "Prescription{" + "id=" + id + ", details='" + details + '\'' +
                 '}';
    }
}
