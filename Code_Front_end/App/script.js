const endpoints = {
    dossier: "http://localhost:8080/ws/dossierMedical",
    medecin: "http://localhost:8080/ws/medecin",
    patient: "http://localhost:8080/ws/patient",
    prescription: "http://localhost:8080/ws/prescription"
};

async function sendSoapRequest(endpoint, soapBody) {
    try {
        console.log("Envoi de la requête SOAP à :", endpoint); // Log l'URL d'envoi
        const response = await fetch(endpoint, {
            method: "POST",
            headers: {
                "Content-Type": "text/xml",
            },
            body: soapBody,
        });

        console.log("Réponse obtenue : ", response.status); // Log le code de statut de la réponse

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(`HTTP Error: ${response.status}, Body: ${errorText}`);
        }

        const result = await response.text();
        console.log("Réponse SOAP reçue :\n", result); // Affiche la réponse SOAP dans la console
        return result;
    } catch (error) {
        console.error("Erreur SOAP :", error.message);
        return `Erreur : ${error.message}`;
    }
}





