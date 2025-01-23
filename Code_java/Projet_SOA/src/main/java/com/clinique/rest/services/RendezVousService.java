package com.clinique.rest.services;

import com.clinique.rest.models.RendezVous;
import com.clinique.soap.entites.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

public interface RendezVousService {

    String prendreRendezVous(Patient patient, Medecin medecin, LocalDate date, LocalTime heure);

    Set<RendezVous> consulterRendezVous(Patient patient);


    String annulerRendezVous(Long idrendezVous);
}

