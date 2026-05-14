# 📚 Système de Gestion des Inscriptions Académiques

Application Java Swing pour la gestion des inscriptions aux sessions d'examen/enseignement avec interface administrateur et frontend étudiant.

---

## 📋 Table des matières

- [Description](#description)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration](#configuration)
- [Lancement](#lancement)
- [Structure du Projet](#structure-du-projet)
- [Fonctionnalités](#fonctionnalités)

---

## Description

Ce projet est une application de gestion académique permettant :
- **Administrateurs** : Gestion complète des campagnes, sessions, inscriptions et utilisateurs
- **Étudiants** : Consultation et inscription aux sessions disponibles
- **Caching** : Système multi-niveaux pour optimiser les requêtes base de données
- **Gestion de capacité** : Vérification et mise à jour automatique des places disponibles

**Auteurs :** Cédric GUIDI & Baptiste DUCROCQ  
**Langage :** Java Swing + Oracle 10g+  
**Version :** 2.2

---

## Architecture

```
MVC (Model-View-Controller)
│
├─ Model/              → Entités (Department, Student, Session, Registration...)
├─ DAO/                → Accès à la base de données (8 DAOs avec cache)
├─ Service/            → Logique métier (AppCache, AppSession singletons)
└─ GUI/                → Interface Swing (Backoffice admin + Frontend étudiant)
```

### Technologies

- **Base de données** : Oracle 10g+
- **Interface graphique** : Java Swing (AWT/Swing)
- **Gestion des dépendances** : Bibliothèques JDBC incluses dans `lib/`
- **Tests** : JUnit-compatible sans dépendance externe

---

## Prérequis

### Système

- **Java Development Kit (JDK)** : 8 ou supérieur
  ```bash
  java -version
  ```
- **Serveur Oracle** : Oracle 10g ou supérieur avec accès réseau
- **Système d'exploitation** : Windows, Linux ou macOS

### Base de données

- Base de données Oracle opérationnelle
- Tables créées via le script : `sql/createDB_DUCROCQ_GUIDI.sql`
- Données initiales chargées via : `sql/insertDB_DUCROCQ_GUIDI.sql`

---

## Installation

### 1. Cloner le projet

```bash
git clone https://github.com/cedric20061/pdl_project.git
cd pdl_project
```

### 2. Vérifier la structure

```bash
ls -la
# Doit contenir : src/, sql/, lib/, config.properties, etc.
```

### 3. Compiler le projet

```bash
# Compilation complète (Windows)
javac -encoding UTF-8 -cp "lib/*" -d bin `Get-ChildItem -Recurse -Filter *.java src | % { $_.FullName }`

# Compilation complète (Linux/macOS)
find src -name "*.java" | xargs javac -cp "lib/*" -d bin

# Compilation sélective (tous les DAOs)
javac -cp "lib/*" -d bin src/model/*.java src/service/*.java src/dao/*.java
```

---

## Configuration

### 1. Créer la base de données

Si la base n'existe pas :

```bash
sqlplus system/password@oracle.esigelec.fr:1521:orcl
SQL> @sql/createDB_DUCROCQ_GUIDI.sql
SQL> @sql/insertDB_DUCROCQ_GUIDI.sql
SQL> exit
```

### 2. Configurer les identifiants

Éditer `config.properties` :

```properties
# URL Oracle (format : jdbc:oracle:thin:@HOST:PORT:SID)
# À adapter selon votre serveur Oracle
URL=jdbc:oracle:thin:@oracle.esigelec.fr:1521:orcl

# Identifiants de la base
LOGIN=your_username
PASS=your_password
```

### 3. Vérifier la connectivité

```bash
cd PDL\Code
javac -cp "lib/*" -d bin src/dao/ConnectionDAO.java

# La compilation réussit → Connexion possible
```

---

## Lancement

### Mode Administrateur (Backoffice)

```bash
# Depuis le répertoire du projet
java -cp "lib/*;bin" gui.backoffice.Main
```

**Accès administrateur :**
- Login : `admin`
- Password : `admin123`

### Mode Étudiant (Frontend)

```bash
# À partir du backoffice :
# Menu → Étudiant → Frontend
# Ou lancer directement (si AppSession configurée)
java -cp "lib/*;bin" gui.frontend.Main
```

### Exécution des Tests

```bash
# Test complet des Departments
java -cp "lib/*;bin" tests.DepartmentDAOTest

# Test complet des Specializations
java -cp "lib/*;bin" tests.SpecializationDAOCompleteTest

# Tous les tests (script optionnel)
./run_tests.bat          # Windows
./run_tests.sh           # Linux/macOS
```

---
**Opérations testées :**
- CREATE : Insertion d'un nouveau département
- READ : Récupération par ID
- UPDATE : Modification des informations
- DELETE : Suppression sécurisée
- GET_ALL : Liste complète et cache

**Résultat attendu :**
```
✓ Department création réussie (ID: xxx)
✓ Department lecture réussie
✓ Department modification réussie
✓ Department suppression réussie
✓ Department tests complètement nettoyés
```

#### SpecializationDAOCompleteTest ✅

```bash
java -cp "lib/*;bin" tests.SpecializationDAOCompleteTest
```

**Opérations testées :**
- CREATE : Création avec dépendance Department
- READ : Recherche et joins
- UPDATE : Modification tous les champs
- DELETE : Suppression cascadée
- CACHE : Validation du cache applicatif

**Résultat attendu :**
```
✓ Specialization création réussie (ID: xxx)
✓ Specialization lecture réussie
✓ Specialization modification réussie
✓ Specialization suppression réussie
✓ Specialization tests complètement nettoyés
```

### Ajouter un nouveau test

---

## Structure du Projet

```
PDL/Code/
│
├── src/                          # Code source
│   ├── model/                    # 9 classes modèle (Department, User, etc.)
│   ├── dao/                      # 8 Data Access Objects avec cache
│   ├── service/                  # Services (AppCache, AppSession)
│   ├── gui/                      # Interface Swing
│   │   ├── backoffice/          # Module administrateur
│   │   │   ├── Main.java
│   │   │   ├── components/
│   │   │   ├── editorFrames/
│   │   │   ├── mainPanels/
│   │   │   └── utils/
│   │   └── frontend/            # Module étudiant
│   │       ├── Main.java
│   │       ├── components/
│   │       ├── mainPanels/
│   │       ├── services/
│   │       └── utils/
│   ├── common/                   # Code partagé (LoginPage, etc.)
│   └── tests/                    # Tests unitaires
│       ├── TestUtils.java
│       ├── DepartmentDAOTest.java
│       ├── SpecializationDAOCompleteTest.java
│       └── ...
│
├── bin/                          # Fichiers compilés (.class)
│
├── lib/                          # Dépendances externes
│   ├── ojdbc8.jar               # Driver Oracle JDBC
│   ├── *.jar                     # Autres dépendances
│
├── sql/                          # Scripts base de données
│   ├── createDB_DUCROCQ_GUIDI.sql
│   └── insertDB_DUCROCQ_GUIDI.sql
│
├── doc/                          # JavaDoc généré
│
├── config.properties             # Configuration (identifiants BDD)
│
├── README.md                     # Ce fichier
├── .gitignore
└── .project                      # Configuration Eclipse
```

---

## Fonctionnalités

### ✅ Gestion des Utilisateurs
- Authentification (Admin/Étudiant)
- Sessions utilisateur persistantes
- Gestion des rôles et permissions

### ✅ Gestion des Inscriptions
- Inscription aux sessions avec vérification de capacité
- Gestion du statut (PENDING, CONFIRMED, VALIDATED, REJECTED)
- Modification du rang de préférence
- Suppression sécurisée avec libération des places

### ✅ Gestion des Sessions
- Création/Modification/Suppression de sessions
- Gestion de la capacité (remaining_capacity)
- Filtrage par campagne, spécialisation, date
- Affichage des places disponibles

### ✅ Gestion des Campagnes
- Création de campagnes d'inscription
- Paramétrage des périodes d'inscription
- Activation/Désactivation

### ✅ Système de Cache
- Cache multi-niveaux en mémoire
- Invalidation automatique après mutations
- Caches spécialisés : par étudiant, session, campagne
- Amélioration drastique des performances

### ✅ Interface Utilisateur
- Backoffice pour administrateurs
- Frontend pour étudiants
- Tableaux avec tri et filtrage
- Dialogs modaux pour édition
- Validation des entrées

---

## Troubleshooting

### Erreur : "package oracle.jdbc does not exist"

```bash
# Solution : Vérifier que lib/ contient ojdbc8.jar
ls lib/ojdbc*

# Recompiler avec bon classpath
javac -cp "lib/*" -d bin src/dao/*.java
```

### Erreur : "Cannot connect to database"

```bash
# Vérifier config.properties
cat config.properties

# Vérifier la connexion
sqlplus username/password@oracle.esigelec.fr:1521:orcl

# Vérifier le pare-feu
ping oracle.esigelec.fr
```

### Erreur : "Table or view does not exist"

```bash
# Recréer les tables
sqlplus system/password@oracle.esigelec.fr:1521:orcl
SQL> @sql/createDB_DUCROCQ_GUIDI.sql
SQL> @sql/insertDB_DUCROCQ_GUIDI.sql
```

### Tests qui échouent

```bash
# Vérifier que la base est accessible
java -cp "lib/*;bin" tests.TestUtils

# Relancer le test avec le classpath complet
java -cp "lib/*;bin" tests.DepartmentDAOTest
```

---

## Support et Contact

**Auteurs :**
- Cédric GUIDI (cedric20061)
- Baptiste DUCROCQ

**Repository :** https://github.com/cedric20061/pdl_project

**Issues :** Signaler les bugs via GitHub Issues

---

## Licence

Projet académique - ESIGEL

---

## Notes Importantes

⚠️ **Avant de lancer l'application :**
1. ✅ Vérifier `config.properties` avec les identifiants corrects
2. ✅ Tester la connexion à la base de données
3. ✅ S'assurer que les tables existent (scripts SQL)
4. ✅ Compiler tous les fichiers avant lancement

⚠️ **À propos des tests :**
- Les tests **restaurent la base de données à son état initial**
- Pas d'effet de bord après exécution
- Idéals pour validation en environnement de développement

⚠️ **Performance :**
- La première requête est plus lente (cache vide)
- Les requêtes suivantes sont instantanées (cache hit)
- Cache invalidé automatiquement après modifications

---

**Dernière mise à jour :** 14 mai 2026  
**Version :** 2.2
