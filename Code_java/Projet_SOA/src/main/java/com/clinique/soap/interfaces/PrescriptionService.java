package com.clinique.soap.interfaces;

import com.clinique.soap.entites.Dossier;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface PrescriptionService {
    @WebMethod
    String ajouterPrescription(Long idDossier,Long idMedecin, String details);

    @WebMethod
    Dossier lirePrescriptions(Long idDossier);

}
