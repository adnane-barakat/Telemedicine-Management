package com.clinique.rest.services.impl;

import com.clinique.jedis.JedisManager;
import com.clinique.rest.models.RendezVous;
import com.clinique.soap.entites.Dossier;
import com.clinique.soap.entites.Patient;
import com.clinique.soap.entites.Rapport;
import com.clinique.rest.services.SuiviPatientService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class SuiviPatientServiceImpl implements SuiviPatientService {


    @Override
    public Set<RendezVous> historiqueConsultation(Long idDossier) {
        // Trouver le dossier correspondant à l'idDossier
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(idDossier))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return new HashSet<>(); // Retourne un ensemble vide si le dossier n'existe pas
        }

        // Récupérer tous les rendez-vous confirmés associés au patient du dossier
        Patient patient = dossier.getPatient();
        return RendezVousServiceImpl.rendezVousSet.stream()
                .filter(rendezVous -> rendezVous.getPatient().equals(patient) && "Confirmé".equalsIgnoreCase(rendezVous.getStatus()))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Rapport> affichageRapport(Long idDossier) {
        // Trouver le dossier correspondant à l'idDossier
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(idDossier))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return new HashSet<>(); // Retourne un ensemble vide si le dossier n'existe pas
        }

        // Retourner les rapports associés au dossier
        return dossier.getRapports();
    }

    @Override
    public String changerStatusRendezVous(Long idRendezVous){

        for (RendezVous rd : RendezVousServiceImpl.rendezVousSet) {
            if (rd.getId().equals(idRendezVous)) {
                rd.setStatus("Confirmé");
                return "Le rendez-vous est a présent une consultation";
            }
        }

        return "Ce rendez-vous est n'existe pas,vérifier l'id du rendez-vous";

    }
}

