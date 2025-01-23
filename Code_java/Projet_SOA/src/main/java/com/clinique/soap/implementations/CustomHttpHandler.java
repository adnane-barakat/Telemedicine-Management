package com.clinique.soap.implementations;

import com.clinique.soap.entites.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class CustomHttpHandler implements HttpHandler {
    private final Object service;

    public CustomHttpHandler(Object service) {
        this.service = service;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        // Ajouter les en-têtes CORS à toutes les réponses
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization,true");

        if ("OPTIONS".equalsIgnoreCase(method)) {
            // Répondre aux requêtes OPTIONS
            exchange.sendResponseHeaders(200, -1); // Réponse sans corps
            return;
        } else if ("POST".equalsIgnoreCase(method)) {
            // Gérer les requêtes SOAP
            InputStream requestBody = exchange.getRequestBody();
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = requestBody.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

            String soapRequest = output.toString("UTF-8");
            System.out.println("Requête SOAP reçue :\n" + soapRequest);

            // Appel de la méthode du service SOAP en fonction de la requête
            String result = handleSoapRequest(soapRequest);

            // Réponse SOAP simplifiée (sans balises enveloppantes)
            String soapResponse = createSoapResponse(result);

            // Répondre avec la réponse simplifiée
            exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=UTF-8"); // Ne pas envoyer de XML
            exchange.sendResponseHeaders(200, soapResponse.getBytes().length); // Taille correcte
            OutputStream responseBody = exchange.getResponseBody();
            responseBody.write(soapResponse.getBytes());
            responseBody.close();
        } else {
            // Méthode HTTP non supportée
            exchange.sendResponseHeaders(405, -1);
        }
    }

    private String handleSoapRequest(String soapRequest) {
        // Exemple d'extraction du nom, prénom et email et appel à la méthode appropriée
        if (soapRequest.contains("<tns:ajouterPatient>")) {
            String nom = extractXmlValue(soapRequest, "nom");
            String prenom = extractXmlValue(soapRequest, "prenom");
            String email = extractXmlValue(soapRequest, "email");

            // Appeler la méthode du service SOAP
            PatientServiceImpl patientService = new PatientServiceImpl();
            String result = patientService.ajouterPatient(nom, prenom, email);

            // Retourner la réponse sans balises SOAP
            return result; // Juste le texte, sans XML
        }

        // Modifier un patient
        if (soapRequest.contains("<tns:modifierPatient>")) {
            Long id = Long.parseLong(extractXmlValue(soapRequest, "id"));
            String nom = extractXmlValue(soapRequest, "nom");
            String prenom = extractXmlValue(soapRequest, "prenom");
            String email = extractXmlValue(soapRequest, "email");

            PatientServiceImpl patientService = new PatientServiceImpl();
            return patientService.modifierPatient(id, nom, prenom, email);
        }

        // Supprimer un patient
        if (soapRequest.contains("<tns:supprimerPatient>")) {
            Long id = Long.parseLong(extractXmlValue(soapRequest, "id"));

            PatientServiceImpl patientService = new PatientServiceImpl();
            return patientService.supprimerPatient(id);
        }

        // Gérer la requête SOAP pour afficher tous les patients
        if (soapRequest.contains("<tns:afficherTousLesPatients/>")) {
            // Appeler la méthode du service SOAP pour afficher tous les patients
            PatientServiceImpl patientService = new PatientServiceImpl();
            Set<Patient> patients = patientService.afficherTousLesPatients();

            // Créer la réponse SOAP avec la liste des patients
            return createPatientsSoapResponse(patients);
        }

        // Gérer la requête SOAP pour ajouter un dossier médical
        if (soapRequest.contains("<tns:creerDossier>")) {
            Long patientId = Long.valueOf(extractXmlValue(soapRequest, "patientId"));


            DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl();
            return dossierService.creerDossier(patientId); // Juste la réponse texte
        }

        if (soapRequest.contains("<tns:creerRapport>")) {
            Long dossierId = Long.valueOf(extractXmlValue(soapRequest, "dossierId"));
            Long idMedecin = Long.valueOf(extractXmlValue(soapRequest, "idMedecin"));
            String rapport = extractXmlValue(soapRequest, "rapport");

            DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl();
            return dossierService.creerRapport(dossierId, idMedecin, rapport); // Juste la réponse texte
        }


        // Mettre à jour un dossier médical
        if (soapRequest.contains("<tns:mettreAJourDossierPrescriptions>")) {
            Long dossierId = Long.parseLong(extractXmlValue(soapRequest, "dossierId"));
            Long idPrescription = Long.parseLong(extractXmlValue(soapRequest, "idPrescription"));
            String nouveauDetails = extractXmlValue(soapRequest, "nouveauDetails");

            DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl();
            return dossierService.mettreAJourDossierPrescriptions(dossierId, idPrescription, nouveauDetails);
        }

        if (soapRequest.contains("<tns:mettreAJourDossierRapport>")) {
            Long dossierId = Long.parseLong(extractXmlValue(soapRequest, "dossierId"));
            Long idRapport = Long.parseLong(extractXmlValue(soapRequest, "idRapport"));
            String nouveauRapport = extractXmlValue(soapRequest, "nouveauRapport");

            DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl();
            return dossierService.mettreAJourDossierRapport(dossierId, idRapport, nouveauRapport);
        }



        // Supprimer un dossier médical
        if (soapRequest.contains("<tns:supprimerDossier>")) {
            Long dossierId = Long.parseLong(extractXmlValue(soapRequest, "dossierId"));

            DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl();
            return dossierService.supprimerDossier(dossierId);
        }

        // Gérer la requête SOAP pour afficher tous les dossiers médicaux
        if (soapRequest.contains("<tns:lireTousLesDossiers/>")) {
            DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl();
            Set<Dossier> dossiers = dossierService.lireTousLesDossiers();
            return createDossiersSoapResponse(dossiers);
        }

        // Gérer la requête SOAP pour ajouter un médecin
        if (soapRequest.contains("<tns:ajouterMedecin>")) {
            String nom = extractXmlValue(soapRequest, "nom");
            String prenom = extractXmlValue(soapRequest, "prenom");
            String specialite = extractXmlValue(soapRequest, "specialite");
            String email = extractXmlValue(soapRequest, "email");

            MedecinServiceImpl medecinService = new MedecinServiceImpl();
            return medecinService.ajouterMedecin(nom, prenom, specialite, email); // Juste la réponse texte
        }

        // Modifier un médecin
        if (soapRequest.contains("<tns:modifierMedecin>")) {
            Long id = Long.parseLong(extractXmlValue(soapRequest, "id"));
            String nom = extractXmlValue(soapRequest, "nom");
            String prenom = extractXmlValue(soapRequest, "prenom");
            String specialite = extractXmlValue(soapRequest, "specialite");
            String email = extractXmlValue(soapRequest, "email");

            MedecinServiceImpl medecinService = new MedecinServiceImpl();
            return medecinService.modifierMedecin(id, nom, prenom, specialite, email);
        }

        // Supprimer un médecin
        if (soapRequest.contains("<tns:supprimerMedecin>")) {
            Long id = Long.parseLong(extractXmlValue(soapRequest, "id"));

            MedecinServiceImpl medecinService = new MedecinServiceImpl();
            return medecinService.supprimerMedecin(id);
        }

        // Gérer la requête SOAP pour afficher tous les médecins
        if (soapRequest.contains("<tns:lireTousLesMedecins/>")) {
            MedecinServiceImpl medecinService = new MedecinServiceImpl();
            Set<Medecin> medecins = medecinService.lireTousLesMedecins();
            return createMedecinsSoapResponse(medecins);
        }

        // Gérer la requête SOAP pour ajouter une prescription
        if (soapRequest.contains("<tns:ajouterPrescription>")) {
            // Extraire les paramètres de la requête SOAP
            Long idDossier = Long.valueOf(extractXmlValue(soapRequest, "idDossier"));
            Long idMedecin = Long.valueOf(extractXmlValue(soapRequest, "idMedecin"));
            String details = extractXmlValue(soapRequest, "details");

            // Créer une instance du service PrescriptionServiceImpl
            PrescriptionServiceImpl prescriptionService = new PrescriptionServiceImpl();

            // Appeler la méthode ajouterPrescription avec les paramètres extraits
            return prescriptionService.ajouterPrescription(idDossier, idMedecin, details); // Juste la réponse texte
        }


        // Gérer la requête SOAP pour afficher toutes les prescriptions
        if (soapRequest.contains("<tns:lirePrescriptions>")) {
            // Extraire l'ID du dossier à partir de la requête SOAP
            Long idDossier = Long.valueOf(extractXmlValue(soapRequest, "idDossier"));

            // Créer une instance du service PrescriptionServiceImpl
            PrescriptionServiceImpl prescriptionService = new PrescriptionServiceImpl();

            // Appeler la méthode lirePrescriptions avec l'ID du dossier
            Dossier dossier = prescriptionService.lirePrescriptions(idDossier);

            // Retourner la réponse SOAP contenant les prescriptions
            return createPrescriptionsSoapResponse(dossier);
        }



        if (soapRequest.contains("<tns:supprimerRapport>")) {
            // Extraire les valeurs de la requête SOAP
            Long dossierId = Long.valueOf(extractXmlValue(soapRequest, "dossierId"));
            Long idRapport = Long.valueOf(extractXmlValue(soapRequest, "idRapport"));

            // Créer une instance du service RapportServiceImpl
            DossierMedicalServiceImpl rapportService = new DossierMedicalServiceImpl();

            // Appeler la méthode pour supprimer le rapport
            String result = rapportService.supprimerRapport(dossierId, idRapport);

            // Retourner la réponse SOAP
            return result;
        }









        return "Service non trouvé";
    }

    private String extractXmlValue(String xml, String tag) {
        String startTag = "<" + tag + ">";
        String endTag = "</" + tag + ">";
        int start = xml.indexOf(startTag) + startTag.length();
        int end = xml.indexOf(endTag);
        return xml.substring(start, end);
    }

    private String createSoapResponse(String result) {
        // Retirer la structure SOAP et retourner uniquement le texte
        return result; // Juste la réponse texte, sans balises SOAP
    }


    private String createPatientsSoapResponse(Set<Patient> patients) {
        // Commencer la réponse SOAP
        StringBuilder response = new StringBuilder();
        response.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
        response.append("    <soapenv:Body>\n");
        response.append("        <patients>\n");

        // Ajouter chaque patient à la réponse
        for (Patient patient : patients) {
            response.append("            <patient>\n");
            response.append("                <id>" + patient.getId() + "</id>\n");
            response.append("                <nom>" + patient.getNom() + "</nom>\n");
            response.append("                <prenom>" + patient.getPrenom() + "</prenom>\n");
            response.append("                <email>" + patient.getEmail() + "</email>\n");
            response.append("            </patient>\n");
        }

        response.append("        </patients>\n");
        response.append("    </soapenv:Body>\n");
        response.append("</soapenv:Envelope>\n");

        return response.toString();
    }

//
private String createDossiersSoapResponse(Set<Dossier> dossiers) {
    StringBuilder response = new StringBuilder();
    response.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
    response.append("    <soapenv:Body>\n");
    response.append("        <dossiers>\n");

    // Parcourir les dossiers
    for (Dossier dossier : dossiers) {
        response.append("            <dossier>\n");

        // Ajouter l'ID du dossier et l'ID du patient
        response.append("                <id>" + dossier.getId() + "</id>\n");
        response.append("                <patientId>" + dossier.getPatient().getId() + "</patientId>\n");
        response.append("                <patientNom>" + dossier.getPatient().getNom() + "</patientNom>\n");
        response.append("                <patientPrenom>" + dossier.getPatient().getPrenom() + "</patientPrenom>\n");
        response.append("                <patientEmail>" + dossier.getPatient().getEmail() + "</patientEmail>\n");

        // Ajouter les prescriptions si elles existent
        Set<Prescription> prescriptions = dossier.getPrescriptions();
        if (!prescriptions.isEmpty()) {
            response.append("                <prescriptions>\n");
            for (Prescription prescription : prescriptions) {
                response.append("                    <prescription>\n");
                response.append("                        <id>" + prescription.getId() + "</id>\n");
                response.append("                        <medecinId>" + prescription.getIdMedecin() + "</medecinId>\n");
                response.append("                        <details>" + prescription.getDetails() + "</details>\n");
                response.append("                    </prescription>\n");
            }
            response.append("                </prescriptions>\n");
        }

        // Ajouter les rapports si ils existent
        Set<Rapport> rapports = dossier.getRapports();
        if (!rapports.isEmpty()) {
            response.append("                <rapports>\n");
            for (Rapport rapport : rapports) {
                response.append("                    <rapport>\n");
                response.append("                        <id>" + rapport.getId() + "</id>\n");
                response.append("                        <medecinId>" + rapport.getMedecinId() + "</medecinId>\n");
                response.append("                        <rapportDetails>" + rapport.getRapport() + "</rapportDetails>\n");
                response.append("                    </rapport>\n");
            }
            response.append("                </rapports>\n");
        }

        response.append("            </dossier>\n");
    }

    response.append("        </dossiers>\n");
    response.append("    </soapenv:Body>\n");
    response.append("</soapenv:Envelope>\n");

    return response.toString();
}


//

    private String createPrescriptionsSoapResponse(Dossier dossier) {
        StringBuilder response = new StringBuilder();
        response.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
        response.append("    <soapenv:Body>\n");
        response.append("        <dossier>\n");

        // Ajouter les informations du patient
        Patient patient = dossier.getPatient();
        response.append("            <dossierId>" + dossier.getId() + "</dossierId>\n");
        response.append("            <patientId>" + patient.getId() + "</patientId>\n");
        response.append("            <patientNom>" + patient.getNom() + "</patientNom>\n");
        response.append("            <patientPrenom>" + patient.getPrenom() + "</patientPrenom>\n");
        response.append("            <patientEmail>" + patient.getEmail() + "</patientEmail>\n");

        // Ajouter les prescriptions si elles existent
        Set<Prescription> prescriptions = dossier.getPrescriptions();
        if (!prescriptions.isEmpty()) {
            response.append("            <prescriptions>\n");
            for (Prescription prescription : prescriptions) {
                response.append("                <prescription>\n");
                response.append("                    <id>" + prescription.getId() + "</id>\n");
                response.append("                    <details>" + prescription.getDetails() + "</details>\n");
                response.append("                    <idMedecin>" + prescription.getIdMedecin() + "</idMedecin>\n");
                response.append("                </prescription>\n");
            }
            response.append("            </prescriptions>\n");
        } else {
            response.append("            <prescriptions>N/A</prescriptions>\n");
        }

        response.append("        </dossier>\n");
        response.append("    </soapenv:Body>\n");
        response.append("</soapenv:Envelope>\n");

        return response.toString();
    }



    private String createMedecinsSoapResponse(Set<Medecin> medecins) {
        StringBuilder response = new StringBuilder();

        // Commencer la réponse SOAP avec la structure de l'enveloppe
        response.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
        response.append("    <soapenv:Body>\n");
        response.append("        <medecins>\n");

        // Pour chaque médecin, ajouter ses détails dans la réponse
        for (Medecin medecin : medecins) {
            response.append("            <medecin>\n");
            response.append("                <id>" + medecin.getId() + "</id>\n");
            response.append("                <nom>" + medecin.getNom() + "</nom>\n");
            response.append("                <prenom>" + medecin.getPrenom() + "</prenom>\n");
            response.append("                <specialite>" + medecin.getSpecialite() + "</specialite>\n");
            response.append("                <email>" + medecin.getEmail() + "</email>\n");
            response.append("            </medecin>\n");
        }

        // Clore les balises
        response.append("        </medecins>\n");
        response.append("    </soapenv:Body>\n");
        response.append("</soapenv:Envelope>\n");

        // Retourner la réponse sous forme de chaîne de caractères
        return response.toString();
    }






}
