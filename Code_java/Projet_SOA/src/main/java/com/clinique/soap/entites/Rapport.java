package com.clinique.soap.entites;

import java.util.Objects;

public class Rapport {
    private Long id; // Identifiant du rapport
    private Long medecinId; // Identifiant du médecin
    private String rapport; // Contenu du rapport

    // Constructeur

    public Rapport() {}

    public Rapport(Long id, Long medecinId, String rapport) {
        this.id = id;
        this.medecinId = medecinId;
        this.rapport = rapport;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rapport rapport1 = (Rapport) o;
        return Objects.equals(medecinId, rapport1.medecinId) &&
                Objects.equals(rapport, rapport1.rapport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(medecinId, rapport); // Utilisation des autres attributs sauf id
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMedecinId() {
        return medecinId;
    }

    public void setMedecinId(Long medecinId) {
        this.medecinId = medecinId;
    }

    public String getRapport() {
        return rapport;
    }

    public void setRapport(String rapport) {
        this.rapport = rapport;
    }

    // Méthode toString pour afficher les informations du rapport
    @Override
    public String toString() {
        return "Rapport{" +
                "id=" + id +
                ", medecinId=" + medecinId +
                ", rapport='" + rapport + '\'' +
                '}';
    }
}
