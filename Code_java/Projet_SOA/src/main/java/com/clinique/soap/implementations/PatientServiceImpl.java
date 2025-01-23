package com.clinique.soap.implementations;


import com.clinique.jedis.JedisManager;
import com.clinique.soap.entites.Patient;
import com.clinique.soap.interfaces.PatientService;

import javax.jws.WebService;
import java.util.Set;

@WebService(endpointInterface = "com.clinique.interfaces.PatientService")
public class PatientServiceImpl implements PatientService {




    @Override
    public String ajouterPatient(String nom, String prenom, String email) {

        Patient patient = new Patient(JedisManager.incrementCounter("patientCounter"),nom, prenom, email);

        if(!JedisManager.getPatients().contains(patient)) {

            JedisManager.addPatient(patient);
            return "Patient ajouté avec succès : " + patient.toString();
        }

        return "Le Patient existe !" ;
    }

    @Override
    public String modifierPatient(Long id, String nom, String prenom, String email) {
        for (Patient patient : JedisManager.getPatients()) {
            if (patient.getId().equals(id)) {
                patient.setNom(nom);
                patient.setPrenom(prenom);
                patient.setEmail(email);
                //Patient pa = new Patient(id, nom, prenom, email);
                JedisManager.updatePatient(patient);
                return "Patient modifié avec succès : " + patient.toString();
            }
        }
        return "Patient avec l'ID " + id + " non trouvé.";
    }

    @Override
    public String supprimerPatient(Long id) {
        for (Patient patient : JedisManager.getPatients()) {
            if (patient.getId().equals(id)) {
                //DossierMedicalServiceImpl.patients.remove(patient);
                JedisManager.removePatient(patient);
                return "Patient supprimé avec succès : " + patient.toString();
            }
        }
        return "Patient avec l'ID " + id + " non trouvé.";
    }

    @Override
    public Set<Patient> afficherTousLesPatients() {
        return JedisManager.getPatients();
    }
}
