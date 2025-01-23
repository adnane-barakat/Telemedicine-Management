package com.clinique.soap.interfaces;

import com.clinique.soap.entites.Patient;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.Set;

@WebService
public interface PatientService {
    @WebMethod
    String ajouterPatient(String nom, String prenom, String email);

    @WebMethod
    String modifierPatient(Long id, String nom, String prenom, String email);

    @WebMethod
    String supprimerPatient(Long id);

    @WebMethod
    Set<Patient> afficherTousLesPatients();
}
