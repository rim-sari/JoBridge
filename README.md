
<div align="center">
  
# JoBridge — Plateforme de recrutement en ligne

**JoBridge connecte les talents avec les entreprises qui recrutent.**
  
<img width="400" height="400" alt="image" src="https://github.com/user-attachments/assets/7854dc05-1dd1-4ffa-aba1-84f333884494" />


</div>

---

## Sommaire

- [Présentation](#présentation)
- [Fonctionnalités](#fonctionnalités)
- [Architecture](#architecture)
- [Technologies utilisées](#technologies-utilisées)
- [Structure du projet](#structure)
- [API REST](#api-rest)
- [Comptes de test](#comptes-de-test)
- [Améliorations prévues](#améliorations-prévues)
- [Équipe](#équipe)

---

## Présentation

JoBridge est une application web de recrutement développée dans le cadre du **Semestre 6** d'un cursus en développement web. Elle propose une architecture complète **Client-Serveur** avec :

- Un backend Java Spring Boot exposant une API REST
- Un frontend HTML/CSS/JavaScript en Single Page Application (SPA)
- Une base de données MySQL gérée automatiquement par Hibernate/JPA

```
Navigateur (port 5500) ←──── REST API JSON ────→ Spring Boot (port 8080) ←──→ MySQL (port 3306)
```

---

## Fonctionnalités

### Portail Candidat

| Fonctionnalité      | Description                                         |
| ------------------- | --------------------------------------------------- |
| Inscription         | Nom, prénom, email (validé), mot de passe           |
| Validation admin    | Compte activé uniquement après validation           |
| Identifiant unique  | Format `CAN-XXXX` généré à l'inscription            |
| Consultation offres | Voir et rechercher toutes les offres disponibles    |
| Candidature         | Lettre de motivation + upload CV (PDF)              |
| Suivi               | Statuts : En attente / Accepté / Rejeté / Entretien |
| Notifications       | Badge en temps réel, marquage lu/non lu             |
| Suppression         | Suppression du compte avec archivage                |

### Portail Entreprise

| Fonctionnalité       | Description                                              |
| -------------------- | -------------------------------------------------------- |
| Inscription          | Nom entreprise, email, mot de passe, secteur             |
| Validation admin     | Compte activé uniquement après validation                |
| Publication d'offres | Titre, type contrat, lieu, salaire, description          |
| Gestion candidatures | Voir lettres de motivation et CV                         |
| Téléchargement CV    | Accès direct au CV de chaque candidat                    |
| Réponse              | Accepter / Rejeter / Convoquer en entretien              |
| Notifications        | Alerte à chaque nouvelle candidature                     |
| Suppression          | Suppression du compte (offres et candidatures archivées) |

### Portail Administrateur

| Fonctionnalité    | Description                                            |
| ----------------- | ------------------------------------------------------ |
| Validation        | Valider ou rejeter candidats et entreprises            |
| Rejet             | Avec notification automatique à l'utilisateur          |
| Dashboard         | Statistiques globales en temps réel                    |
| Offres            | Visualiser toutes les offres publiées                  |
| Suppression       | Supprimer tout compte avec motif (archivé)             |
| Comptes supprimés | Historique complet des suppressions                    |
| Notifications     | Alertes sur les nouvelles inscriptions et suppressions |

### Accès public (sans connexion)

- Consulter toutes les offres d'emploi disponibles
- Recherche en temps réel (titre, lieu, entreprise, secteur)
- Statistiques globales de la plateforme

---

## Architecture

| Élément         | Technologie             | Rôle                                               |
| --------------- | ----------------------- | -------------------------------------------------- |
| Frontend        | HTML / CSS / JavaScript | Interface utilisateur (SPA, interaction dynamique) |
| Backend         | Java Spring Boot        | API REST, logique métier                           |
| Base de données | MySQL                   | Stockage persistant des données                    |
| ORM             | Hibernate / JPA         | Mapping objet-relationnel, génération des tables   |
| Communication   | HTTP / JSON             | Échange de données entre frontend et backend       |

---

## Technologies utilisées

### Backend

| Technologie        | Rôle                                |
| ------------------ | ----------------------------------- |
| Java               | Langage principal                   |
| Spring Boot        | Création API REST                   |
| Spring Data JPA    | Accès simplifié aux données         |
| Hibernate          | ORM, gestion automatique des tables |
| Maven              | Gestion des dépendances             |
| Jakarta Validation | Validation des données              |

### Frontend

| Technologie  | Rôle                               |
| ------------ | ---------------------------------- |
| HTML5        | Structure                          |
| CSS3         | Design (Flexbox, Grid, animations) |
| JavaScript   | Logique client (Fetch API, DOM)    |
| SPA (manuel) | Navigation sans rechargement       |

### Base de données & Outils

| Outil           | Rôle                          |
| --------------- | ----------------------------- |
| MySQL           | Base de données relationnelle |
| MySQL Workbench | Gestion de la base            |
| Eclipse IDE     | Développement backend         |
| VS Code         | Développement frontend        |
| Thunder Client  | Test des API                  |

---

## Structure 

```
└── 📁 jobridge
    ├── 📁 backend
    │   ├── 📁 .mvn
    │   │   └── 📁 wrapper
    │   │       └── 📄 maven-wrapper.properties
    │   ├── 📁 .settings
    │   │   ├── 📄 org.eclipse.core.resources.prefs
    │   │   ├── 📄 org.eclipse.jdt.apt.core.prefs
    │   │   ├── 📄 org.eclipse.jdt.core.prefs
    │   │   └── 📄 org.eclipse.m2e.core.prefs
    │   ├── 📁 src
    │   │   ├── 📁 main
    │   │   │   ├── 📁 java
    │   │   │   │   └── 📁 com
    │   │   │   │       └── 📁 jobridge
    │   │   │   │           └── 📁 backend
    │   │   │   │               ├── 📁 config
    │   │   │   │               │   ├── ☕ CorsConfig.java
    │   │   │   │               │   └── ☕ SecurityConfig.java
    │   │   │   │               ├── 📁 controller
    │   │   │   │               │   ├── ☕ AdminController.java
    │   │   │   │               │   ├── ☕ ApplicationController.java
    │   │   │   │               │   ├── ☕ AuthController.java
    │   │   │   │               │   ├── ☕ NotificationController.java
    │   │   │   │               │   └── ☕ OfferController.java
    │   │   │   │               ├── 📁 model
    │   │   │   │               │   ├── ☕ Application.java
    │   │   │   │               │   ├── ☕ Candidate.java
    │   │   │   │               │   ├── ☕ Company.java
    │   │   │   │               │   ├── ☕ DeletedAccount.java
    │   │   │   │               │   ├── ☕ Notification.java
    │   │   │   │               │   ├── ☕ Offer.java
    │   │   │   │               │   └── ☕ User.java
    │   │   │   │               ├── 📁 repository
    │   │   │   │               │   ├── ☕ ApplicationRepository.java
    │   │   │   │               │   ├── ☕ CandidateRepository.java
    │   │   │   │               │   ├── ☕ CompanyRepository.java
    │   │   │   │               │   ├── ☕ DeletedAccountRepository.java
    │   │   │   │               │   ├── ☕ NotificationRepository.java
    │   │   │   │               │   └── ☕ OfferRepository.java
    │   │   │   │               ├── 📁 service
    │   │   │   │               └── ☕ BackendApplication.java
    │   │   │   └── 📁 resources
    │   │   │       ├── 📁 static
    │   │   │       ├── 📁 templates
    │   │   │       └── 📄 application.properties
    │   │   └── 📁 test
    │   │       └── 📁 java
    │   │           └── 📁 com
    │   │               └── 📁 jobridge
    │   │                   └── 📁 backend
    │   │                       └── ☕ BackendApplicationTests.java
    │   ├── 📁 uploads
    │   │   └── 📁 cv
    │   │       ├── 📕 5d8266d9-8fe8-4130-a1af-55949d2915ab_CvDeFahmiMeriem.pdf
    │   │       ├── 📕 8131e085-fc64-4524-b583-ef86fe8dd33e_CvDeFahmiMeriem.pdf
    │   │       └── 📕 9bca41de-cd1a-4894-84b6-f5e4c7f72ce9_CV-de-Meriem-Fahmi.pdf
    │   ├── ⚙️ .classpath
    │   ├── ⚙️ .gitattributes
    │   ├── ⚙️ .gitignore
    │   ├── ⚙️ .project
    │   ├── 📝 HELP.md
    │   ├── 📄 mvnw
    │   ├── 📄 mvnw.cmd
    │   └── ⚙️ pom.xml
    ├── 📁 frontend
    │   ├── 🌐 index.html
    │   └── 🎨 style.css
    └── 📁 images
        ├── 📁 icons
        │   ├── 🖼️ add.png
        │   ├── 🖼️ admin.png
        │   ├── 🖼️ admin_attente.png
        │   ├── 🖼️ alert.png
        │   ├── 🖼️ arrow-up.png
        │   ├── 🖼️ candidate.png
        │   ├── 🖼️ candidate1.png
        │   ├── 🖼️ check.png
        │   ├── 🖼️ danger.png
        │   ├── 🖼️ decline.png
        │   ├── 🖼️ download.svg
        │   ├── 🖼️ entreprise.png
        │   ├── 🖼️ home.png
        │   ├── 🖼️ id.png
        │   ├── 🖼️ interview.png
        │   ├── 🖼️ loupe.png
        │   ├── 🖼️ mail.svg
        │   ├── 🖼️ notification.png
        │   ├── 🖼️ offre.png
        │   ├── 🖼️ resume.png
        │   ├── 🖼️ supprime.png
        │   ├── 🖼️ vue.png
        │   ├── 🖼️ waiting-list.png
        │   └── 🖼️ worker.png
        └── 📁 logo
            └── 🖼️ transparent-logo.png
```

Certains icons dans le dossier "images/icons/" n'ont pas été utilisés dans notre site mais pourraient être implémentés.


---

## Comptes de test

Page d'acceuil : 

<div align="center"> 
<img width="1590" height="808" alt="image" src="https://github.com/user-attachments/assets/796dffa5-9d42-4ecc-a3e5-d6afb264a5de" />
</div>

Portail Admin : 

<div align="center">
<img width="1626" height="679" alt="image" src="https://github.com/user-attachments/assets/1faf1c09-bb8e-4f40-bbee-cbac45a91d49" />
</div>

Portail Entreprise :

<div align="center">
<img width="1590" height="431" alt="image" src="https://github.com/user-attachments/assets/ee4ade7d-340b-4dc6-b326-ccaacf62a780" />
</div>

Portail Candidat :

<div align="center">
<img width="1690" height="438" alt="image" src="https://github.com/user-attachments/assets/66db4c66-c849-4f9e-b036-57dccb92052b" />
</div>

---

## Améliorations prévues

| Limites                                                            | Améliorations                                                                                               | Explication                                                                                                                                 |
| ------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------- |
| Absence de vérification de l’email lors de l’inscription           | Mettre en place une vérification par email (lien ou code de validation)                                     | Permet de s’assurer que l’utilisateur possède une adresse email valide, réduit les faux comptes et améliore la sécurité globale du système. |
| Mot de passe stocké en clair dans la base de données               | Hachage des mots de passe (BCrypt)                                                                          | Actuellement, les mots de passe sont vulnérables. Le hachage permet de sécuriser les données même en cas de fuite.                          |
| Impossible de voir le mot de passe lors de l’inscription/connexion | Ajouter une option “afficher/masquer le mot de passe”                                                       | Améliore l’expérience utilisateur en réduisant les erreurs de saisie.                                                                       |
| Mot de passe trop simple (pas de règles de sécurité)               | Imposer une politique de mot de passe (majuscule, minuscule, chiffre, caractère spécial, longueur minimale) | Renforce la sécurité des comptes en réduisant les risques de piratage par attaques par force brute ou dictionnaire.                         |
| Pas de fonctionnalité “mot de passe oublié”                        | Implémenter une récupération par email avec token                                                           | Permet à l’utilisateur de réinitialiser son mot de passe de manière sécurisée.                                                              |
| Session non persistante (déconnexion à la fermeture)               | Ajouter un système de session persistante (JWT / cookies)                                                   | Permet de rester connecté jusqu’à déconnexion ou expiration de session.                                                                     |
| Un seul administrateur codé en dur                                 | Créer une table admin avec gestion des rôles                                                                | Rend le système plus flexible et adapté à un usage réel avec plusieurs administrateurs.                                                     |
| Gestion des CV limitée (stockage local uniquement)                 | Stocker les métadonnées des CV en base + lien pour l’entreprise                                             | Permet aux entreprises d’accéder facilement aux CV depuis la base de données et améliore la traçabilité.                                    |

---

## Équipe

Réalisé par : SARI Rim et FAHMI Meriem

---

## Licence

<div align="center">
  
**JoBridge** — Développé dans le cadre du Semestre 6 — Licence 3 Informatique.

</div>

---
