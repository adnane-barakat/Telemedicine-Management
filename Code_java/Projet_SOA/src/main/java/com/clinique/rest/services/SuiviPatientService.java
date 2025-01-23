package com.clinique.rest.services;

import com.clinique.rest.models.RendezVous;
import com.clinique.soap.entites.Rapport;


import java.util.Set;

public interface SuiviPatientService {


    Set<RendezVous> historiqueConsultation(Long idDossier);


    Set<Rapport> affichageRapport(Long idDossier);

    String changerStatusRendezVous(Long idRendezVous);

}

