package com.clinique.jedis;


import com.clinique.soap.entites.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import com.google.gson.Gson;
import java.util.HashSet;
import java.util.Set;

public class JedisManager {

    private static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), "redis://localhost:6379");

    private static Gson gson = new Gson();  // Utilisation de Gson pour la sérialisation/désérialisation JSON

    public static void initializeCounters() {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.exists("patientCounter")) {
                jedis.set("patientCounter", "0");
            }
            if (!jedis.exists("dossierCounter")) {
                jedis.set("dossierCounter", "0");
            }
        }
    }

    public static long incrementCounter(String counterName) {
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.incr(counterName); // Incrémente et retourne la nouvelle valeur
        }
    }



    // Méthode pour ajouter un rapport à un dossier existant
    public static String addRapportToDossier(Long dossierId, Rapport rapport) {
        try (Jedis jedis = jedisPool.getResource()) {
            String dossierJson = jedis.get("dossier:" + dossierId);  // Récupère le dossier par son ID
            if (dossierJson == null) {
                return "Dossier non trouvé.";
            }

            Dossier dossier = gson.fromJson(dossierJson, Dossier.class);  // Désérialisation du dossier
            if (dossier.getRapports().contains(rapport)) {
                return "Le rapport existe déjà.";
            }

            // Ajout du rapport au dossier
            dossier.addRapport(rapport);

            // Mise à jour du dossier dans Redis
            jedis.set("dossier:" + dossierId, gson.toJson(dossier));

            return "Rapport ajouté avec succès.";
        }
    }

    public static String modifierRapport(Long dossierId, Long rapportId, String nouveauContenu) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Récupérer le dossier depuis Redis
            String dossierJson = jedis.get("dossier:" + dossierId);
            if (dossierJson == null) {
                return "Dossier avec ID " + dossierId + " non trouvé.";
            }

            // Désérialiser le JSON en objet Dossier
            Dossier dossier = gson.fromJson(dossierJson, Dossier.class);

            // Rechercher le rapport à modifier
            Rapport rapport = dossier.getRapports().stream()
                    .filter(r -> r.getId().equals(rapportId))
                    .findFirst()
                    .orElse(null);

            if (rapport == null) {
                return "Rapport avec ID " + rapportId + " non trouvé dans le dossier ID " + dossierId + ".";
            }

            // Modifier le contenu du rapport
            rapport.setRapport(nouveauContenu);

            // Mettre à jour Redis avec le dossier modifié
            jedis.set("dossier:" + dossierId, gson.toJson(dossier));

            return "Rapport avec ID " + rapportId + " modifié avec succès.";
        }
    }



    public static void addMedecin(Medecin medecin) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd("medecins", gson.toJson(medecin)); // Sérialisation du médecin en JSON
        }
    }

    public static Set<Medecin> getMedecins() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> medecinSet = jedis.smembers("medecins");
            Set<Medecin> medecins = new HashSet<>();
            for (String medecinJson : medecinSet) {
                medecins.add(gson.fromJson(medecinJson, Medecin.class)); // Désérialisation
            }
            return medecins;
        }
    }

    public static void removeMedecin(Medecin medecin) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem("medecins", gson.toJson(medecin)); // Supprime le médecin sérialisé
        }
    }


    public static void addPatient(Patient patient) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.sadd("patients", gson.toJson(patient)); // Sérialisation du patient
        }
    }


    public static Set<Patient> getPatients() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> patientSet = jedis.smembers("patients");
            Set<Patient> patients = new HashSet<>();
            for (String patientJson : patientSet) {
                patients.add(gson.fromJson(patientJson, Patient.class)); // Désérialisation
            }
            return patients;
        }
    }


    public static void removePatient(Patient patient) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.srem("patients", gson.toJson(patient)); // Supprime le patient sérialisé
        }
    }


    public static void addDossier(Dossier dossier) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set("dossier:" + dossier.getId(), gson.toJson(dossier)); // Sérialisation avec clé unique
        }
    }


    public static Set<Dossier> getDossiers() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys("dossier:*"); // Récupère toutes les clés des dossiers
            Set<Dossier> dossiers = new HashSet<>();
            for (String key : keys) {
                String dossierJson = jedis.get(key); // Récupère le dossier par clé
                dossiers.add(gson.fromJson(dossierJson, Dossier.class)); // Désérialisation
            }
            return dossiers;
        }
    }


    public static void removeDossier(Long dossierId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del("dossier:" + dossierId); // Supprime le dossier avec la clé unique
        }
    }


    public static void addPrescription(Long dossierId, Prescription prescription) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Récupérer le dossier depuis Redis
            String dossierJson = jedis.get("dossier:" + dossierId);
            if (dossierJson == null) {
                System.out.println("Dossier avec l'ID " + dossierId + " introuvable.");
                return;
            }

            // Désérialiser le dossier
            Dossier dossier = gson.fromJson(dossierJson, Dossier.class);

            // Ajouter la prescription au Set de prescriptions
            if (!dossier.getPrescriptions().contains(prescription)) {
                dossier.getPrescriptions().add(prescription);

                // Mettre à jour le dossier dans Redis
                jedis.set("dossier:" + dossierId, gson.toJson(dossier));
                System.out.println("Prescription ajoutée avec succès au dossier ID " + dossierId);
            } else {
                System.out.println("La prescription existe déjà dans le dossier.");
            }
        }
    }

    public static void updatePrescription(Long dossierId, Long prescriptionId, Prescription newPrescription) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Récupérer le dossier depuis Redis
            String dossierJson = jedis.get("dossier:" + dossierId);
            if (dossierJson == null) {
                System.out.println("Dossier avec l'ID " + dossierId + " introuvable.");
                return;
            }

            // Désérialiser le dossier
            Dossier dossier = gson.fromJson(dossierJson, Dossier.class);

            // Modifier la prescription dans le Set
            boolean updated = dossier.getPrescriptions().removeIf(p -> p.getId().equals(prescriptionId));
            if (updated) {
                dossier.getPrescriptions().add(newPrescription); // Ajouter la nouvelle prescription
                jedis.set("dossier:" + dossierId, gson.toJson(dossier)); // Mettre à jour Redis
                System.out.println("Prescription mise à jour avec succès pour le dossier ID " + dossierId);
            } else {
                System.out.println("Prescription avec ID " + prescriptionId + " introuvable.");
            }
        }
    }


    public static void removePrescriptionLong(Long dossierId, Long prescriptionId) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Récupérer le dossier depuis Redis
            String dossierJson = jedis.get("dossier:" + dossierId);
            if (dossierJson == null) {
                System.out.println("Dossier avec l'ID " + dossierId + " introuvable.");
                return;
            }

            // Désérialiser le dossier
            Dossier dossier = gson.fromJson(dossierJson, Dossier.class);

            // Supprimer la prescription du Set
            boolean removed = dossier.getPrescriptions().removeIf(p -> p.getId().equals(prescriptionId));
            if (removed) {
                jedis.set("dossier:" + dossierId, gson.toJson(dossier)); // Mettre à jour Redis
                System.out.println("Prescription supprimée avec succès pour le dossier ID " + dossierId);
            } else {
                System.out.println("Prescription avec ID " + prescriptionId + " introuvable.");
            }
        }
    }


    public static void removeRapport(Long dossierId, Long rapportId) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Récupérer le dossier depuis Redis
            String dossierJson = jedis.get("dossier:" + dossierId);
            if (dossierJson == null) {
                System.out.println("Dossier avec l'ID " + dossierId + " introuvable.");
                return;
            }

            // Désérialiser le dossier
            Dossier dossier = gson.fromJson(dossierJson, Dossier.class);

            // Trouver et supprimer le rapport dans le Set de rapports du dossier
            boolean removed = dossier.getRapports().removeIf(rapport -> rapport.getId().equals(rapportId));

            if (!removed) {
                System.out.println("Rapport avec l'ID " + rapportId + " introuvable dans le dossier.");
                return;
            }

            // Mettre à jour le dossier dans Redis
            jedis.set("dossier:" + dossierId, gson.toJson(dossier));
            System.out.println("Rapport supprimé avec succès du dossier ID " + dossierId);
        }
    }


    public static void updateMedecin(Medecin medecin) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Trouver l'ancien objet basé sur l'ID
            Set<String> medecins = jedis.smembers("medecins");
            String medecinToRemove = medecins.stream()
                    .filter(m -> gson.fromJson(m, Medecin.class).getId().equals(medecin.getId()))
                    .findFirst()
                    .orElse(null);

            if (medecinToRemove != null) {
                jedis.srem("medecins", medecinToRemove); // Supprimer l'ancien objet
            }

            jedis.sadd("medecins", gson.toJson(medecin)); // Ajouter le nouvel objet
        }
    }

    public static void updatePatient(Patient patient) {
        try (Jedis jedis = jedisPool.getResource()) {
            // Trouver l'ancien objet basé sur l'ID
            Set<String> patients = jedis.smembers("patients");
            String patientToRemove = patients.stream()
                    .filter(p -> gson.fromJson(p, Patient.class).getId().equals(patient.getId()))
                    .findFirst()
                    .orElse(null);

            if (patientToRemove != null) {
                jedis.srem("patients", patientToRemove); // Supprimer l'ancien objet
            }

            jedis.sadd("patients", gson.toJson(patient)); // Ajouter le nouvel objet
        }
    }












}

