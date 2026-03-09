# spring-boot-rest-api

Tour Odissey è un'applicazione backend sviluppata in Java Spring Boot che fornisce API REST per la gestione di tour, agenzie, filiali, prenotazioni e commenti. Il sistema include autenticazione basata su JWT, gestione ruoli (ADMIN, OPERATOR, CUSTOMER), validazione dati, gestione centralizzata delle eccezioni e documentazione interattiva con Swagger/OpenAPI.

L'obiettivo è offrire una piattaforma scalabile e sicura per operatori turistici e clienti finali.

Tecnologie utilizzate

    Java 25

    Spring Boot 3.x

    Spring Security (autenticazione JWT)

    Spring Data JPA (Hibernate)

    Spring Validation

    Spring Mail (invio email)

    JJWT (gestione token)

    Swagger (springdoc-openapi) per documentazione API

    Lombok (riduzione boilerplate)

    Maven (gestione dipendenze)

    H2/PostgreSQL/MySQL (database, a scelta)

    JUnit / Mockito (test - se presenti)

Prerequisiti

    Java 25

    Maven 3.8+

    Database (H2 in memoria per sviluppo, oppure PostgreSQL/MySQL per produzione)

    Account email (per invio OTP e reset password)

Funzionalità principali

    Gestione agenzie e filiali (CRUD, attivazione/disattivazione, associazione API key)

    Gestione paesi (codice, nome, valuta)

    Registrazione e autenticazione utenti con verifica email (OTP)

    Prenotazione tour con controllo sovrapposizioni e disponibilità

    Commenti sui tour (solo per clienti che hanno partecipato, possibilità di risposte e censura)

    Scadenza password e storico delle ultime 3 password

    Gestione centralizzata delle eccezioni con response personalizzate (RFC 7807)

    Documentazione OpenAPI integrata con JWT support
