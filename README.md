# 📚 Système de Gestion des Inscriptions Académiques

Application Java Swing pour la gestion complète des campagnes d'inscription aux sessions académiques avec interface administrateur et interface étudiant.

**Auteurs :** Cédric GUIDI & Baptiste DUCROCQ  
**Langage :** Java Swing + Oracle 10g+  
**Version :** 2.0

---

## 📋 Table des matières

- [Lancement Rapide](#lancement-rapide)
- [Configuration Initiale](#configuration-initiale)
- [Point d'Entrée](#point-dentrée)
- [Identifiants de Test](#identifiants-de-test)
- [Fonctionnalités Implémentées](#fonctionnalités-implémentées)
- [Remarques Importantes](#remarques-importantes)
- [Architecture](#architecture)
- [Troubleshooting](#troubleshooting)


---

## 🚀 Lancement Rapide

### 1. Récupérer le projet

Télécharger le fichier ZIP du projet et l'extraire :
```
PDL-project.zip → Extraire
```

### 2. Importer dans Eclipse

1. Ouvrir **Eclipse IDE**
2. Menu : `File` → `Import...` → `General` → `Existing Projects into Workspace`
3. Cliquer sur `Select root directory` et naviguer vers le dossier extrait
4. Vérifier que `Copy projects into workspace` est cochée
5. Cliquer `Finish`

### 3. Configurer le projet

Une fois importé dans Eclipse :

1. **Créer le fichier `config.properties` à la racine du projet** (même niveau que `src/`, `sql/`, `lib/`)

2. **Remplir `config.properties` avec vos identifiants Oracle :**

```properties
# URL de connexion Oracle
# Utiliser OBLIGATOIREMENT une des deux valeurs suivantes :

# OPTION 1 : Si vous êtes sur une machine de l'école
URL=jdbc:oracle:thin:@oracle.esigelec.fr:1521:orcl

# OPTION 2 : Si vous êtes sur une machine personnelle / en VPN
URL=jdbc:oracle:thin:@//srvoracledb.intranet.int:1521/orcl.intranet.int

# Identifiants de base de données
LOGIN=your_oracle_username
PASS=your_oracle_password
```

---

## 🔧 Configuration Initiale

### Créer la base de données (UNE SEULE FOIS)

1. Ouvrir un terminal Oracle SQL ou votre outil d'administration Oracle
2. Exécuter le script de création (DANS CET ORDRE) :
   - **D'abord :** `sql/createDB_DUCROCQ _GUIDI.sql` (crée les tables)
   - **Puis :** `sql/insertDB_DUCROCQ _GUIDI.sql` (insère les données de test)

---

## 📍 Point d'Entrée

### Lancer l'application

Le **point d'entrée** de l'application est : **`src/common/LoginPage.java`**

Pour lancer l'application :

1. Dans Eclipse, ouvrir `src/common/LoginPage.java`
2. Clic-droit → `Run As` → `Java Application`
3. La fenêtre de connexion s'affiche

---

## 👥 Identifiants de Test

### Accès Administrateur (Backoffice)

**Identifiant :** `thomas.petit@wish.fr`  
**Mot de passe :** `thomas123`

*Autres administrateurs disponibles :*
- alice.martin@wish.fr / alice123
- lucas.bernard@wish.fr / lucas123
- sophie.durand@wish.fr / sophie123

### Accès Étudiant (Frontend)

**Identifiant :** `emma.dupont@etu.fr`  
**Mot de passe :** `pwd123`

*Autres étudiants disponibles :*
- noah.lefevre@etu.fr / pwd123
- chloe.moreau@etu.fr / pwd123
- liam.simon@etu.fr / pwd123

---

## ✅ Fonctionnalités Implémentées

### 1. **Gestion des Campagnes (Administrateur)**
- ✓ Créer une nouvelle campagne
- ✓ Filtrer par statut (OPEN, CLOSED, PLANNED, ARCHIVED)
- ✓ Archiver une campagne (validation : pas d'inscriptions PENDING)
- ✓ Modifier les détails d'une campagne

### 2. **Gestion des Sessions (Administrateur)**
- ✓ Créer une session avec dates/horaires validés
- ✓ Protection de la pause déjeuner (12:30-13:30) - impossible créer session qui chevauche
- ✓ Empêcher la création de session pour campagne ARCHIVED
- ✓ Gérer les capacités des sessions

### 3. **Gestion des Inscriptions (Administrateur)**
- ✓ Validation automatique avec détection des conflits d'horaires
- ✓ Si conflit d'horaire : conserver inscription meilleur rang, rejeter les autres
- ✓ Seules les campagnes CLOSED peuvent être traitées automatiquement
- ✓ Consulter les inscriptions par étudiant/session

### 4. **Interface Étudiant**
- ✓ S'inscrire aux sessions disponibles (campagnes OPEN/PLANNED uniquement)
- ✓ Consulter ses inscriptions avec statut (PENDING, ACCEPTED, REJECTED)
- ✓ Annuler/modifier inscriptions (uniquement pour campagnes OPEN/PLANNED)
- ✓ Impossible s'inscrire à campagnes ARCHIVED ou CLOSED

### 5. **Sécurité et Contraintes**
- ✓ Authentification admin/étudiant
- ✓ Vérification capacité des sessions
- ✓ Gestion des conflits d'horaires
- ✓ Statuts de campagne : OPEN → CLOSED → ARCHIVED (ou PLANNED)

---

## ⚠️ Remarques Importantes

### Affichage et Actualisation

L'application implémente toutes les fonctionnalités demandées, **MAIS** certains affichages peuvent ne pas se mettre à jour automatiquement après certaines actions.

**En particulier :**
- Les **statuts des inscriptions** sur la page de liste n'se mettent pas toujours à jour immédiatement après une validation
- Les **capacités des sessions** peuvent rester en cache après une inscription

**Solution :** Cliquer sur le bouton **"Actualiser"** (ou rafraîchir) dans l'interface, ou redémarrer l'application pour voir les changements à jour.

### Cache et Performance

L'application utilise un système de cache multi-niveaux (`AppCache.java`) pour optimiser les performances. Ce cache peut parfois conserver des données obsolètes après modification.

**Actions recommandées :**
- Utiliser les boutons **"Actualiser"** présents dans les panneaux
- En cas d'affichage incohérent : redémarrer l'application

---

## 🏗️ Architecture

```
Système de Gestion Académique
│
├── src/
│   ├── common/          → LoginPage (point d'entrée)
│   ├── model/           → Classes métier (Student, Campaign, Session, Registration)
│   ├── dao/             → Couche d'accès BD (8 DAOs + ConnectionDAO)
│   ├── service/         → Logique métier (AppCache singleton, AppSession)
│   └── gui/
│       ├── backoffice/  → Interface administrateur
│       │   ├── mainPanels/     → Panels des campagnes/sessions/inscriptions
│       │   └── editorFrames/   → Dialogs d'édition et validation
│       └── frontend/    → Interface étudiant
│           ├── components/    → Composants réutilisables
│           ├── mainPanels/    → Panels de consultation et inscription
│           └── utils/        → Utilitaires d'interface
│
├── sql/
│   ├── createDB_DUCROCQ _GUIDI.sql  → Création tables et contraintes
│   └── insertDB_DUCROCQ _GUIDI.sql  → Données de test
│
├── lib/                 → Bibliothèques (Oracle JDBC)
├── bin/                 → Fichiers compilés
├── config.properties    → Configuration base de données
└── README.md           → Ce fichier
```

### Technologies

- **Language :** Java 8+
- **Interface :** Swing (AWT)
- **Base de données :** Oracle 10g+
- **Connexion :** JDBC (Oracle Thin Client)
- **Architecture :** MVC avec DAO

---

## 🐛 Troubleshooting

### Erreur de connexion à la base de données

```
java.sql.SQLException: ORA-12514: TNS:listener does not know of service requested
```

**Solution :**
- Vérifier que l'URL dans `config.properties` est correcte
- Si à l'école : utiliser `oracle.esigelec.fr`
- Si personnelle/VPN : utiliser `srvoracledb.intranet.int`
- Vérifier la connectivité réseau vers le serveur Oracle

### Application blanche / interface ne s'affiche pas

**Solution :**
- Vérifier que Java Swing est bien configuré
- En Eclipse : Run → Run Configurations → Arguments → `VM arguments` : `-Xms512m -Xmx1024m`
- Parfois nécessaire sur machines lentes/virtualisées

### Statuts d'inscription ne se mettent pas à jour

**Solution :**
- Utiliser le bouton **"Actualiser"** dans le panel des inscriptions
- Redémarrer l'application pour forcer rechargement du cache
- Vérifier que le traitement automatique a bien exécuté (ValidateRegistrationsDialog)

### Impossible de créer une session pour une campagne

**Vérifications :**
1. La campagne existe-t-elle ? → Vérifier dans la liste des campagnes
2. La campagne n'est pas ARCHIVED ? → Les campagnes ARCHIVED n'apparaissent pas
3. L'horaire ne chevauche pas 12:30-13:30 ? → Impossible créer session pendant pause déjeuner

---

## 📝 Notes Additionnelles

### Validation des Données

L'application valide :
- **Dates :** Date fin > Date début pour campagnes
- **Horaires :** Heure fin > Heure début, pas d'overlap dans lunch break (12:30-13:30)
- **Capacités :** Pas inscription au-delà de capacité max
- **Statuts :** Transitions d'état validées (PENDING → ACCEPTED/REJECTED, etc.)

### Performance

Avec ~24 sessions et ~15 inscriptions (données de test) :
- Affichage liste : < 1 seconde
- Recherche/Filtre : < 500ms
- Validation auto inscriptions : 1-2 secondes

Pour plus grandes données, optimiser avec indexes Oracle.

### Support

En cas de problème :
1. Vérifier les logs en bas de l'écran de connexion
2. Consulter le fichier `config.properties` (identifiants corrects ?)
3. Réinitialiser BD avec scripts SQL
4. Redémarrer Eclipse et l'application

---

**Dernière mise à jour :** Mai 2026  
**État :** Production  
**Version :** 2.0 (avec validation des conflits d'horaires et archivage)
