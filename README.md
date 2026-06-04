# AlmaMensa

AlmaMensa è un'applicazione Android dedicata alla gestione e alla consultazione delle mense universitarie, sviluppata come progetto per l'esame di **Programmazione di Sistemi Mobile** all'interno del corso di laurea in **Ingegneria e Scienze Informatiche** presso l'**Università di Bologna** (Campus di Cesena), anno accademico 2025/2026.

## Autori
* **Enrico Bartocetti** - [enrico.bartocetti@studio.unibo.it](mailto:enrico.bartocetti@studio.unibo.it)
* **Nicholas Benedetti** - [nicholas.benedetti@studio.unibo.it](mailto:nicholas.benedetti@studio.unibo.it)

## Descrizione del Progetto
L'obiettivo di AlmaMensa è fornire agli studenti uno strumento intuitivo per esplorare le mense universitarie, visualizzare i menu, consultare recensioni e trovare la mensa più vicina alla propria posizione. L'app integra funzionalità social come la possibilità di lasciare recensioni e gestire un profilo utente personalizzato.

## Funzionalità Principali
- **Autenticazione**: Registrazione, login e gestione della sessione tramite Supabase. Supporto per il cambio password e integrazione biometrica.
- **Esplorazione Mense**: Ricerca e visualizzazione dell'elenco delle mense disponibili.
- **Dettaglio Mensa**: Informazioni complete su ogni mensa, inclusi orari, posizione e recensioni degli utenti.
- **Mappa Interattiva**: Visualizzazione delle mense su mappa (OSMDroid) per una localizzazione rapida.
- **Near Me**: Funzionalità basata sulla geolocalizzazione per trovare le mense più vicine all'utente.
- **Recensioni**: Sistema di feedback dove gli utenti possono scrivere, modificare e visualizzare le proprie recensioni.
- **Profilo Utente**: Gestione delle informazioni personali e visualizzazione dello storico delle proprie attività.
- **Impostazioni e Preferiti**: Personalizzazione del tema dell'app (Chiaro/Scuro) e gestione delle mense preferite tramite DataStore.

## Stack Tecnologico
L'applicazione è sviluppata seguendo le moderne linee guida per lo sviluppo Android:
- **Linguaggio**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architettura**: MVVM (Model-View-ViewModel)
- **Dependency Injection**: Koin
- **Backend as a Service (BaaS)**: Supabase (Auth, Postgrest per il database, Storage per le immagini)
- **Networking**: Ktor Client
- **Local Storage**: Jetpack DataStore per le preferenze e i preferiti
- **Mappe**: OSMDroid & OpenRouteService (via API)
- **Image Loading**: Coil
- **Localizzazione**: Google Play Services Location

## Requisiti
- Android 7.0 (API level 24) o superiore.
- Connessione a Internet.

## Struttura del Repository
- `app/src/main/java/it/unibo/almamensa/ui`: Contiene tutti i componenti dell'interfaccia utente, organizzati per schermi (screens), componenti riutilizzabili e navigazione.
- `app/src/main/java/it/unibo/almamensa/data`: Gestione dei dati, repository e modelli (DTO e Mapper).
- `app/src/main/java/it/unibo/almamensa/utils`: Classi di utilità, estensioni e definizioni globali (es. Dimensioni).

## Configurazione
Per avviare il progetto è necessario configurare il file `local.properties` con le seguenti chiavi:
```properties
SUPABASE_URL="tua_url_supabase"
SUPABASE_KEY="tua_chiave_anon_supabase"
ORS_API_KEY="tua_chiave_open_route_service"
```
