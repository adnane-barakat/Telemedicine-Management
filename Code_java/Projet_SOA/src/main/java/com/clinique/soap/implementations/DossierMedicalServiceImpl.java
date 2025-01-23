package com.clinique.soap.implementations;

import com.clinique.jedis.JedisManager;
import com.clinique.soap.entites.*;
import com.clinique.soap.interfaces.DossierMedicalService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.jws.WebService;
import java.util.Properties;
import java.util.Set;


@WebService(endpointInterface = "com.clinique.soap.interfaces.DossierMedicalService", targetNamespace = "")
public class DossierMedicalServiceImpl implements DossierMedicalService {


    public static Long rapportIdCounter = 1L;

    @Override
    public String creerDossier(Long patientId) {
        // Recherche du patient par ID
        Patient patient = JedisManager.getPatients().stream()
                .filter(p -> p.getId().equals(patientId))
                .findFirst()
                .orElse(null);

        if (patient == null) {
            return "Patient avec ID " + patientId + " non trouvé.";
        }

        // Création d'un nouveau dossier pour le patient
        Dossier dossier = new Dossier(JedisManager.incrementCounter("dossierCounter"),patient);
        if(!JedisManager.getDossiers().contains(dossier)) {

            JedisManager.addDossier(dossier);


            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("sandbox.smtp.mailtrap.io");
            mailSender.setPort(587);
            mailSender.setUsername("2663e6a28eb66d");
            mailSender.setPassword("955ecb34cfd355");

            Properties props = mailSender.getJavaMailProperties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.enable", "false");
            props.put("mail.debug", "true");


            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(patient.getEmail());
            email.setSubject("CREATION DE VOTRE DOSSIER MEDICAL");
            email.setText("Votre dossier médical a été créé dans notre clinique avec l'ID " + dossier.getId() +
                    ". Vous en aurez besoin pour accéder à nos services réservés au client.\n" +
                    "Votre Télémédecine,\nCordialement.");
            email.setFrom("notification@telemedecine.ma");

            try {
                mailSender.send(email);

                return "Dossier créé avec succès pour le patient avec  l'ID " + patientId +
                        "\nUn email a été envoyé au patient concerné.";


            } catch (Exception e) {
                return "Erreur lors de l'envoi de l'email : " + e.getMessage();
            }


        }

        return "Le Dossier existe ! ";

    }

    @Override
    public Set<Dossier> lireTousLesDossiers() {
        return JedisManager.getDossiers(); // Retourne la liste des dossiers
    }

    @Override
    public String supprimerDossier(Long dossierId) {
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(dossierId))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return "Dossier avec ID " + dossierId + " non trouvé.";
        }

        // Suppression du dossier de la liste globale et du patient
        //dossiers.remove(dossier);
        JedisManager.removeDossier(dossier.getId());
        return "Dossier supprimé avec succès.";
    }

    @Override
    public String mettreAJourDossierPrescriptions(Long dossierId, Long idPrescription, String nouveauDetails) {
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(dossierId))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return "Dossier avec ID " + dossierId + " non trouvé.";
        }


        // Recherche de la prescription à mettre à jour
        Prescription prescription = dossier.getPrescriptions().stream()
                .filter(p -> p.getId().equals(idPrescription))
                .findFirst()
                .orElse(null);

        if (prescription == null) {
            return "Prescription avec ID " + idPrescription + " non trouvée dans ce dossier.";
        }


        prescription.setDetails(nouveauDetails); // Mise à jour des détails de la prescription
        JedisManager.updatePrescription(dossierId,idPrescription,prescription);
        return "Prescription mise à jour avec succès.";
    }

    @Override
    public String mettreAJourDossierRapport(Long dossierId, Long idRapport, String nouveauRapport) {
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(dossierId))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return "Dossier avec ID " + dossierId + " non trouvé.";
        }



        // Recherche du rapport à mettre à jour
        Rapport rapport = dossier.getRapports().stream()
                .filter(r -> r.getId().equals(idRapport))
                .findFirst()
                .orElse(null);

        if (rapport == null) {
            return "Rapport avec ID " + idRapport + " non trouvé dans ce dossier.";
        }


        rapport.setRapport(nouveauRapport);// Mise à jour du rapport
        JedisManager.modifierRapport(dossierId,idRapport,nouveauRapport);
        return "Rapport mis à jour avec succès.";
    }


    @Override
    public String creerRapport(Long dossierId, Long idMedecin, String rapport) {
        // Recherche du dossier à partir de l'ID
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(dossierId))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return "Dossier avec ID " + dossierId + " non trouvé.";
        }



        Medecin medecin = JedisManager.getMedecins().stream()
                .filter(m -> m.getId().equals(idMedecin))
                .findFirst()
                .orElse(null);

        if (medecin == null) {
            return "Médecin avec ID " + idMedecin + " non trouvé.";
        }


        // Création et ajout du rapport
        Rapport newRapport = new Rapport(rapportIdCounter++,idMedecin, rapport);
        if(!dossier.getRapports().contains(newRapport)) {
            dossier.addRapport(newRapport);

            JedisManager.addRapportToDossier(dossierId,newRapport);


            return "Rapport créé et ajouté au dossier ID " + dossierId;

        }
        return "Le Rapport existe";

    }


    @Override
    public String supprimerRapport(Long dossierId, Long idRapport) {
        // Recherche du dossier à partir de l'ID
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(dossierId))
                .findFirst()
                .orElse(null);

        if (dossier == null) {
            return "Dossier avec ID " + dossierId + " non trouvé.";
        }



        // Recherche du rapport à partir de l'ID
        Rapport rapport = dossier.getRapports().stream()
                .filter(r -> r.getId().equals(idRapport))
                .findFirst()
                .orElse(null);

        if (rapport == null) {
            return "Rapport avec ID " + idRapport + " non trouvé dans ce dossier.";
        }

        // Suppression du rapport
        dossier.getRapports().remove(rapport);
        JedisManager.removeRapport(dossierId,rapport.getId());
        return "Rapport supprimé avec succès du dossier ID " + dossierId;
    }

}
