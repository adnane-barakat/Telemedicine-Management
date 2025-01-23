package com.clinique.rest.controllers;

import com.clinique.rest.models.RendezVous;
import com.clinique.soap.entites.Rapport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.clinique.rest.services.SuiviPatientService;

import java.util.Set;

@RestController
@RequestMapping("/suivi-patient")
public class SuiviPatientController {

    @Autowired
    private SuiviPatientService suiviPatientService;


    @GetMapping("/historique-consultation/{idDossier}")
    public Set<RendezVous> historiqueConsultation(@PathVariable Long idDossier) {
        return suiviPatientService.historiqueConsultation(idDossier);
    }


    @GetMapping("/rapports/{idDossier}")
    public Set<Rapport> affichageRapport(@PathVariable Long idDossier) {
        return suiviPatientService.affichageRapport(idDossier);
    }

    @PostMapping("/changer-status")
    public String changerStatusRendezVous(@RequestParam Long idRendezVous) {
        return suiviPatientService.changerStatusRendezVous(idRendezVous);
    }

}

