package com.clinique.soap.implementations;

import com.clinique.jedis.JedisManager;
import com.clinique.soap.entites.Medecin;
import com.clinique.soap.interfaces.MedecinService;

import javax.jws.WebService;
import java.util.Set;

@WebService(endpointInterface = "com.clinique.interfaces.MedecinService", targetNamespace = "")
public class MedecinServiceImpl implements MedecinService {


    public static Long medecinIdCounter = 1L;

    @Override
    public String ajouterMedecin(String nom, String prenom, String specialite, String email) {

        Medecin medecin = new Medecin(medecinIdCounter++, nom, prenom, specialite, email);
        if(!JedisManager.getMedecins().contains(medecin)) {
            JedisManager.addMedecin(medecin);
            return "Médecin ajouté avec succès : " + medecin.toString();
        }

        return "Le Médecin existe ! ";
    }

    @Override
    public Set<Medecin> lireTousLesMedecins() {
        return JedisManager.getMedecins();
    }

    @Override
    public String modifierMedecin(Long id, String nom, String prenom, String specialite, String email) {

        Medecin medecin = JedisManager.getMedecins().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (medecin == null) {
            return "Médecin avec ID " + id + " non trouvé.";
        }



        Medecin med = new Medecin (id,nom,prenom,specialite,email);
        JedisManager.updateMedecin(med);
        return "Médecin modifié avec succès : " + medecin.toString();
    }

    @Override
    public String supprimerMedecin(Long id) {
        Medecin medecin = JedisManager.getMedecins().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (medecin == null) {
            return "Médecin avec ID " + id + " non trouvé.";
        }

        //medecins.remove(medecin);
        JedisManager.removeMedecin(medecin);
        return "Médecin supprimé avec succès.";
    }
}
