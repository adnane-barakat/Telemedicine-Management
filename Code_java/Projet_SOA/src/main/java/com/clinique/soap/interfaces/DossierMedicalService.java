package com.clinique.soap.interfaces;

import com.clinique.soap.entites.Dossier;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Set;

@WebService
public interface DossierMedicalService {
    @WebMethod
    String creerDossier(Long patientId);

    @WebMethod
    Set<Dossier> lireTousLesDossiers();

    @WebMethod
    String supprimerDossier(Long dossierId);

    @WebMethod
    String mettreAJourDossierPrescriptions(Long dossierId,Long idprescriptions, String nouveauDetails);

    @WebMethod
    String mettreAJourDossierRapport(Long dossierId,Long idrapport, String nouveauRapport);

    @WebMethod
    String creerRapport(Long dossierId,Long idMedecin, String Rapport);

    @WebMethod
    String supprimerRapport(Long dossierId,Long idRapport);


}
