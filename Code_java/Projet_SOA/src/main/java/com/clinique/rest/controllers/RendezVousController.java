package com.clinique.rest.controllers;

import com.clinique.jedis.JedisManager;
import com.clinique.rest.models.RendezVous;
import com.clinique.soap.entites.Dossier;
import com.clinique.soap.entites.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.clinique.rest.services.RendezVousService;
import java.util.Set;

@RestController
@RequestMapping("/rendezvous")
public class RendezVousController {

    @Autowired
    private RendezVousService rendezVousService;


    @PostMapping("/prendre")
    public String prendreRendezVous(@RequestBody RendezVous request) {
        return rendezVousService.prendreRendezVous(request.getPatient(), request.getMedecin(), request.getDate(), request.getHeure());
    }


    @GetMapping("/consulter")
    public Set<RendezVous> consulterRendezVous(@RequestParam Long idDossier) {

        // Récupérer le dossier du patient à partir de l'ID du dossier
        Dossier dossier = JedisManager.getDossiers().stream()
                .filter(d -> d.getId().equals(idDossier))
                .findFirst()
                .orElse(null);


        if (dossier == null) {
            return null;
            //throw new IllegalArgumentException("Dossier introuvable.");
        }

        // Récupérer le patient à partir du dossier
        Patient patient = dossier.getPatient();

        // Consulter les rendez-vous du patient
        return rendezVousService.consulterRendezVous(patient);
    }


    @DeleteMapping("/annuler/{idRendezVous}")
    public String annulerRendezVous(@PathVariable Long idRendezVous) {
        return rendezVousService.annulerRendezVous(idRendezVous);
    }
}

