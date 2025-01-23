package com.clinique.rest.services.impl;

import com.clinique.jedis.JedisManager;
import com.clinique.rest.models.RendezVous;
import com.clinique.rest.services.RendezVousService;
import com.clinique.soap.entites.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;


@Service
public class RendezVousServiceImpl implements RendezVousService {

    // Stockage des rendez-vous
    public static Set<RendezVous> rendezVousSet = new HashSet<>();
    public static long counter = 1L;
    private final NotificationServiceImpl notificationServiceImpl;

    public RendezVousServiceImpl(NotificationServiceImpl notificationServiceImpl) {
        this.notificationServiceImpl = notificationServiceImpl;
    }

    @Override
    public String prendreRendezVous(Patient patient, Medecin medecin, LocalDate date, LocalTime heure) {

        String retour = "";
        Medecin me = new Medecin();
        boolean medExits = false;

        //Verif Medecin

        for (Medecin med : JedisManager.getMedecins()){
            if(med.getNom().equals(medecin.getNom()) && med.getPrenom().equals(medecin.getPrenom())){
                medExits = true;
                me = med;
            }
        }

        if(medExits){

            //verif patient

            Patient p = new Patient(JedisManager.incrementCounter("patientCounter"),patient.getNom(), patient.getPrenom(), patient.getEmail());
            Dossier d = new Dossier(JedisManager.incrementCounter("dossierCounter"),p);

            if(!JedisManager.getDossiers().contains(d)){

                JedisManager.addPatient(p);
                JedisManager.addDossier(d);
                retour+="Votre Dossier a été crée avec l'id "+d.getId();
            }

            // Vérifier si le rendez-vous existe déjà

            RendezVous nouveauRendezVous = new RendezVous(counter++, patient, me, date, heure, "Actif");

            if(!rendezVousSet.contains(nouveauRendezVous)){
                rendezVousSet.add(nouveauRendezVous);
                String message = retour+"\nVotre rendez-vous est programmé pour le : \n"+date+"\nA\n"+heure+"\nCordialement,\nVotre télémeedecine";
                notificationServiceImpl.sendNotification(patient.getEmail(), "CONFIRMATION DE RENDEZ-VOUS",message);
                return retour+"\nRendez-vous pris avec succès.";
            }

            return "Un rendez-vous existe déjà pour ce créneau.";

        }
        else{
            return "Ce Médecin n'existe pas";
        }



    }

    @Override
    public Set<RendezVous> consulterRendezVous(Patient patient) {
        Set<RendezVous> rendezVousDuPatient = new HashSet<>();
        for (RendezVous rendezVous : rendezVousSet) {
            if (rendezVous.getPatient().equals(patient)) {
                rendezVousDuPatient.add(rendezVous);
            }
        }

        if (rendezVousDuPatient.isEmpty()) {
            System.out.println("Aucun rendez-vous trouvé pour le patient " + patient.getNom());
        }

        return rendezVousDuPatient;
    }

    @Override
    public String annulerRendezVous(Long rendezVousId) {
        for (RendezVous rendezVous : rendezVousSet) {
            if (rendezVous.getId().equals(rendezVousId)) {
                rendezVous.setStatus("Annulé");
                return "Le rendez-vous avec l'ID " + rendezVousId + " a été annulé avec succès.";

            }
        }

        return "Aucun rendez-vous trouvé avec l'ID " + rendezVousId + ".";

    }

}

