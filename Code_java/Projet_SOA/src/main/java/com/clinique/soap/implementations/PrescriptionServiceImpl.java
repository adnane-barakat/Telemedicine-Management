package com.clinique.soap.implementations;

import com.clinique.jedis.JedisManager;
import com.clinique.soap.entites.*;
import com.clinique.soap.interfaces.PrescriptionService;

import javax.jws.WebService;


@WebService(endpointInterface = "com.clinique.soap.interfaces.PrescriptionService", targetNamespace = "")
public class PrescriptionServiceImpl implements PrescriptionService {

    // un compteur d'ID
    public static Long prescriptionIdCounter = 1L;

    @Override
    public String ajouterPrescription(Long idDossier, Long idMedecin, String details) {
        // Recherche du dossier médical correspondant à l'ID fourni
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(idDossier))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return "Dossier avec ID " + idDossier + " non trouvé.";
        }

        // Recherche du médecin correspondant à l'ID fourni (si nécessaire)
        Medecin medecin = JedisManager.getMedecins().stream()
                .filter(m -> m.getId().equals(idMedecin))
                .findFirst()
                .orElse(null);

        if (medecin == null) {
            return "Médecin avec ID " + idMedecin + " non trouvé.";
        }


        Prescription prescription = new Prescription(prescriptionIdCounter++, idMedecin, details);

        // Création de la nouvelle prescription, associée au médecin
        if(!dossier.getPrescriptions().contains(prescription)) {

            // Ajout de la prescription au dossier
            dossier.getPrescriptions().add(prescription);
            JedisManager.addPrescription(idDossier,prescription);

            return "Prescription ajoutée avec succès au dossier ID " + idDossier + " par le médecin " + medecin.getNom();

        }

        return "La Prescription existe ! ";
    }

    @Override
    public Dossier lirePrescriptions(Long idDossier) {
        // Recherche du dossier par ID
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(idDossier))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return null; // Dossier non trouvé
        }

        return dossier; // Retourner dossier
    }
}
