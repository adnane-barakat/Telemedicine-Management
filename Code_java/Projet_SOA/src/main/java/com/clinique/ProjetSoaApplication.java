package com.clinique;

import com.clinique.jedis.JedisManager;
import com.sun.net.httpserver.HttpServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collections;

import com.clinique.soap.implementations.*;


import javax.xml.ws.Endpoint;




@SpringBootApplication
public class ProjetSoaApplication {

	public static void main(String[] args) {


		//Pour juste publier les services et tester avec SOAPUi
/*
        Endpoint.publish("http://localhost:8080/ws/medecin", new MedecinServiceImpl());
        System.out.println("Service de gestion des médecins en cours d'exécution sur http://localhost:8080/ws/medecin");

        Endpoint.publish("http://localhost:8080/ws/prescription", new PrescriptionServiceImpl());
        System.out.println("Service de gestion des prescriptions en cours d'exécution sur http://localhost:8080/ws/prescription");

        Endpoint.publish("http://localhost:8080/ws/dossierMedical", new DossierMedicalServiceImpl());
        System.out.println("Service de gestion des dossiers médicaux en cours d'exécution sur http://localhost:8080/ws/dossierMedical");

        Endpoint.publish("http://localhost:8080/ws/patient", new PatientServiceImpl());
        System.out.println("Service de gestion des Patients en cours d'exécution sur http://localhost:8080/ws/patient");

*/

		//Pour publier les services et tester avec le front-end développer avec html

		JedisManager.initializeCounters();

		try {
			// Créer un serveur HTTP sur le port 8080
			HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

			// DossierMedicalService
			DossierMedicalServiceImpl dossierService = new DossierMedicalServiceImpl();
			server.createContext("/ws/dossierMedical", new CustomHttpHandler(dossierService));

			// MedecinService
			MedecinServiceImpl medecinService = new MedecinServiceImpl();
			server.createContext("/ws/medecin", new CustomHttpHandler(medecinService));

			// PatientService
			PatientServiceImpl patientService = new PatientServiceImpl();
			server.createContext("/ws/patient", new CustomHttpHandler(patientService));

			// PrescriptionService
			PrescriptionServiceImpl prescriptionService = new PrescriptionServiceImpl();
			server.createContext("/ws/prescription", new CustomHttpHandler(prescriptionService));


			// Démarrer le serveur
			server.start();
			System.out.println("Tous les services SOAP sont disponibles :");
			System.out.println("- DossierMedicalService : http://localhost:8080/ws/dossierMedical");
			System.out.println("- MedecinService : http://localhost:8080/ws/medecin");
			System.out.println("- PatientService : http://localhost:8080/ws/patient");
			System.out.println("- PrescriptionService : http://localhost:8080/ws/prescription");

		} catch (IOException e) {
			System.err.println("Erreur lors du démarrage du serveur : " + e.getMessage());
			e.printStackTrace();
		}





		SpringApplication app = new SpringApplication(ProjetSoaApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "8081"));
		app.run(args);



	}

}
