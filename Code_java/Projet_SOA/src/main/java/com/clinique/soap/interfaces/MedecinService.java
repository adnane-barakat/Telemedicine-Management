package com.clinique.soap.interfaces;

import com.clinique.soap.entites.Medecin;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Set;

@WebService
public interface MedecinService {
    @WebMethod
    String ajouterMedecin(String nom, String prenom, String specialite, String email);

    @WebMethod
    Set<Medecin> lireTousLesMedecins();

    @WebMethod
    String modifierMedecin(Long id, String nom, String prenom, String specialite, String email);

    @WebMethod
    String supprimerMedecin(Long id);
}