# Exemples SOAP — Olympic Management System

Web service SOAP contract-first (Spring-WS). Contrat : `backend/src/main/resources/xsd/olympic-management.xsd`.

## Accès

| Élément | URL |
|---|---|
| Endpoint SOAP | `http://localhost:8080/ws` |
| WSDL | `http://localhost:8080/ws/olympic-management.wsdl` |
| Namespace cible | `http://olympic.dakar.com/soap/olympic-management` |

## Utilisation avec SoapUI

1. **Nouveau projet SOAP** → coller l'URL du WSDL ci-dessus → SoapUI génère automatiquement une requête par opération.
2. Remplir les valeurs des balises (`athleteId`, `eventId`, `nationality`) et envoyer.

## Utilisation avec Postman

1. Nouvelle requête **POST** vers `http://localhost:8080/ws`.
2. Header `Content-Type: text/xml; charset=utf-8` (le header `SOAPAction` peut être vide : `""`).
3. Body → `raw` → coller l'enveloppe XML d'un exemple ci-dessous.

---

## 1. Consulter un athlète — `getAthleteRequest`

### Requête
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                   xmlns:tns="http://olympic.dakar.com/soap/olympic-management">
   <soapenv:Header/>
   <soapenv:Body>
      <tns:getAthleteRequest>
         <tns:athleteId>1</tns:athleteId>
      </tns:getAthleteRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Réponse (capturée sur un appel réel)
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Header/>
   <SOAP-ENV:Body>
      <ns2:getAthleteResponse xmlns:ns2="http://olympic.dakar.com/soap/olympic-management">
         <ns2:athlete>
            <ns2:id>1</ns2:id>
            <ns2:firstName>Usain</ns2:firstName>
            <ns2:lastName>Bolt</ns2:lastName>
            <ns2:gender>MALE</ns2:gender>
            <ns2:dateOfBirth>1986-08-21</ns2:dateOfBirth>
            <ns2:nationality>Jamaique</ns2:nationality>
            <ns2:disciplineId>1</ns2:disciplineId>
            <ns2:disciplineName>Athletisme</ns2:disciplineName>
            <ns2:height>195</ns2:height>
            <ns2:weight>94.0</ns2:weight>
         </ns2:athlete>
      </ns2:getAthleteResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

### Cas d'erreur — athlète introuvable (SOAP Fault, HTTP 500)
Requête identique avec `<tns:athleteId>999999</tns:athleteId>` →
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <SOAP-ENV:Fault>
         <faultcode>SOAP-ENV:Client</faultcode>
         <faultstring>Ressource introuvable</faultstring>
      </SOAP-ENV:Fault>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

## 2. Résultats d'un athlète — `getAthleteResultsRequest`

### Requête
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                   xmlns:tns="http://olympic.dakar.com/soap/olympic-management">
   <soapenv:Header/>
   <soapenv:Body>
      <tns:getAthleteResultsRequest>
         <tns:athleteId>1</tns:athleteId>
      </tns:getAthleteResultsRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Réponse (capturée sur un appel réel)
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:getAthleteResultsResponse xmlns:ns2="http://olympic.dakar.com/soap/olympic-management">
         <ns2:result>
            <ns2:id>1</ns2:id>
            <ns2:eventId>1</ns2:eventId>
            <ns2:eventName>100m</ns2:eventName>
            <ns2:athleteId>1</ns2:athleteId>
            <ns2:athleteFirstName>Usain</ns2:athleteFirstName>
            <ns2:athleteLastName>Bolt</ns2:athleteLastName>
            <ns2:position>1</ns2:position>
            <ns2:time>9.58s</ns2:time>
            <ns2:medal>GOLD</ns2:medal>
         </ns2:result>
      </ns2:getAthleteResultsResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

## 3. Résultats d'une épreuve — `getEventResultsRequest`

### Requête
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                   xmlns:tns="http://olympic.dakar.com/soap/olympic-management">
   <soapenv:Header/>
   <soapenv:Body>
      <tns:getEventResultsRequest>
         <tns:eventId>1</tns:eventId>
      </tns:getEventResultsRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Réponse (capturée sur un appel réel)
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:getEventResultsResponse xmlns:ns2="http://olympic.dakar.com/soap/olympic-management">
         <ns2:result>
            <ns2:id>1</ns2:id>
            <ns2:eventId>1</ns2:eventId>
            <ns2:eventName>100m</ns2:eventName>
            <ns2:athleteId>1</ns2:athleteId>
            <ns2:athleteFirstName>Usain</ns2:athleteFirstName>
            <ns2:athleteLastName>Bolt</ns2:athleteLastName>
            <ns2:position>1</ns2:position>
            <ns2:time>9.58s</ns2:time>
            <ns2:medal>GOLD</ns2:medal>
         </ns2:result>
      </ns2:getEventResultsResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

---

## 4. Historique des médailles d'une nation — `getNationMedalHistoryRequest`

### Requête
```xml
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                   xmlns:tns="http://olympic.dakar.com/soap/olympic-management">
   <soapenv:Header/>
   <soapenv:Body>
      <tns:getNationMedalHistoryRequest>
         <tns:nationality>Jamaique</tns:nationality>
      </tns:getNationMedalHistoryRequest>
   </soapenv:Body>
</soapenv:Envelope>
```

### Réponse (capturée sur un appel réel)
```xml
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
   <SOAP-ENV:Body>
      <ns2:getNationMedalHistoryResponse xmlns:ns2="http://olympic.dakar.com/soap/olympic-management">
         <ns2:result>
            <ns2:id>1</ns2:id>
            <ns2:eventId>1</ns2:eventId>
            <ns2:eventName>100m</ns2:eventName>
            <ns2:athleteId>1</ns2:athleteId>
            <ns2:athleteFirstName>Usain</ns2:athleteFirstName>
            <ns2:athleteLastName>Bolt</ns2:athleteLastName>
            <ns2:position>1</ns2:position>
            <ns2:time>9.58s</ns2:time>
            <ns2:medal>GOLD</ns2:medal>
         </ns2:result>
      </ns2:getNationMedalHistoryResponse>
   </SOAP-ENV:Body>
</SOAP-ENV:Envelope>
```

Si aucune médaille n'existe pour la nation demandée, `getNationMedalHistoryResponse` est renvoyé sans élément `result` (liste vide), sans erreur.

---

## Notes

- Seules les opérations de **consultation** sont exposées en SOAP (conforme au sujet : le système historique consomme des données, il n'en crée pas). L'écriture (création/modification d'athlètes, résultats...) reste réservée à l'API REST.
- Le champ `medal` peut valoir `GOLD`, `SILVER`, `BRONZE` ou `NONE` (position hors podium).
- Toutes les requêtes/réponses ci-dessus ont été vérifiées contre une instance réelle (MySQL), pas seulement testées unitairement.
