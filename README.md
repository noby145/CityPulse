# CityPulse

CityPulse est une application Android de découverte de lieux à proximité, développée avec **Kotlin**, **MVVM**, **Material 3**, **Google Maps** et **OpenTripMap**. 

Le projet met en avant une architecture claire, une interface moderne et une expérience centrée sur la localisation de l’utilisateur, la recherche de lieux et l’affichage détaillé des points d’intérêt.

---

## Présentation du projet

CityPulse permet de :

- afficher la position actuelle de l’utilisateur sur une carte Google Maps ;
- récupérer des lieux à proximité via l’API **OpenTripMap** ;
- filtrer et rechercher les lieux par catégorie ;
- consulter une fiche détaillée pour chaque lieu ;
- ajouter des notes personnelles ;
- partager un lieu ;
- gérer un système de favoris temporaire.

Ce projet est adapté à un **travail universitaire Android** grâce à une structure propre, une séparation des responsabilités et une présentation claire des données.

---

## Architecture logicielle : MVVM

Le projet suit le pattern **MVVM (Model - View - ViewModel)** afin de séparer la logique métier de l’interface utilisateur.

### Rôle des couches

- **View** : les `Fragment` et les layouts XML affichent les données et capturent les interactions utilisateur.
- **ViewModel** : expose l’état de l’écran, gère la logique de présentation et communique avec les repositories.
- **Model** : regroupe les modèles de données, les réponses réseau, ainsi que les couches d’accès aux données.

### Avantages

- code plus lisible et plus maintenable ;
- meilleure testabilité ;
- séparation nette entre UI, réseau et logique métier ;
- compatibilité avec les composants Android modernes (`StateFlow`, `ViewModel`, `Navigation`, `ViewBinding`).

---

## Intégration Google Maps

L’application intègre **Google Maps** pour afficher la position actuelle de l’utilisateur dans un fragment dédié.

### Fonctionnalités liées à la carte

- demande de permission runtime pour `ACCESS_FINE_LOCATION` ;
- affichage de la position courante ;
- déplacement automatique de la caméra vers la localisation de l’utilisateur ;
- affichage de marqueurs pour les lieux récupérés ;
- navigation vers la fiche d’un lieu à partir de la carte.

### Fichiers associés

- `app/src/main/AndroidManifest.xml`
- `app/src/main/java/com/example/citypulse/ui/maps/MapsFragment.kt`
- `app/src/main/java/com/example/citypulse/ui/maps/MapsViewModel.kt`

---

## Intégration OpenTripMap

Les lieux à proximité sont récupérés via l’API **OpenTripMap**.

### Chaîne technique utilisée

- **Retrofit** pour les appels réseau ;
- **OkHttp** pour le client HTTP ;
- **Gson** pour la sérialisation JSON ;
- **coroutines Kotlin** pour les requêtes asynchrones ;
- calcul de la distance entre l’utilisateur et chaque lieu ;
- tri des résultats du plus proche au plus éloigné.

### Résultat côté interface

- liste des lieux à proximité ;
- recherche textuelle ;
- filtres par catégories ;
- affichage de la distance ;
- accès rapide à une fiche détaillée.

---

## Fonctionnalités déjà implémentées

### Carte et localisation

- fragment Google Maps intégré ;
- demande de permission de localisation ;
- affichage de la position actuelle ;
- affichage de marqueurs sur la carte.

### Exploration des lieux

- récupération des lieux proches depuis OpenTripMap ;
- recherche par nom ;
- filtrage par catégories : restaurants, musées, parcs et boutiques ;
- tri par distance réelle.

### Détails d’un lieu

- nom ;
- catégorie ;
- coordonnées ;
- adresse ;
- distance ;
- identifiant et données complémentaires ;
- zone de notes personnelles ;
- bouton de favoris ;
- bouton de partage.

### Navigation

- navigation entre les fragments avec **Navigation Component** ;
- passage sécurisé des données via **Safe Args** ;
- ouverture d’une fiche lieu depuis la carte ou la liste.

### Interface moderne

- composants **Material 3** ;
- cartes modernes ;
- `SearchView` ;
- `FilterChip` ;
- `RecyclerView` avec `ListAdapter` et `DiffUtil`.

### Favoris et notes

- système de favoris temporaire actuellement en mémoire ;
- notes personnelles sauvegardées dans l’état de l’écran ;
- partage d’un lieu avec son nom, ses coordonnées et un lien Google Maps.

---

## Structure du projet

La structure générale du dépôt est conservée telle qu’elle existe actuellement :

```text
CityPulse/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/citypulse/
│   │   │   ├── data/
│   │   │   ├── ui/
│   │   │   └── MainActivity.kt
│   │   └── res/
│   └── build.gradle.kts
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

> Cette structure met en évidence la séparation entre les sources Kotlin, les ressources XML, la configuration Gradle et les fichiers de documentation.

---

## Prérequis

- **Android Studio** récent ;
- **JDK 11** ou configuration compatible avec le projet ;
- un appareil physique ou un émulateur Android ;
- une clé **Google Maps SDK for Android** ;
- une clé **OpenTripMap**.

Le projet cible actuellement :

- `minSdk = 26`
- `targetSdk = 36`

---

## Installation du projet

### 1) Cloner ou ouvrir le projet

Ouvrez le dossier racine `CityPulse` dans Android Studio.

### 2) Synchroniser Gradle

Laissez Android Studio synchroniser automatiquement le projet, ou lancez une synchronisation manuelle.

### 3) Compiler l’application

```bash
./gradlew :app:assembleDebug
```

Sous Windows PowerShell :

```powershell
.\gradlew.bat :app:assembleDebug
```

### 4) Exécuter sur un appareil ou un émulateur

```powershell
.\gradlew.bat :app:installDebug
```

Ensuite, lancez l’application depuis Android Studio ou depuis l’appareil connecté.

---

## Configuration des API

### Google Maps

La clé Google Maps est configurée dans :

- `app/src/main/res/values/strings.xml`
- clé : `google_maps_api_key`

Le manifeste référence cette valeur via :

- `app/src/main/AndroidManifest.xml`
- méta-donnée : `com.google.android.geo.API_KEY`

#### Étapes à suivre

1. Activer **Maps SDK for Android** dans Google Cloud Console.
2. Remplacer la valeur de `google_maps_api_key` par votre propre clé.
3. Vérifier que la facturation et les restrictions de clé sont correctement configurées.

> Pour un projet universitaire, il est recommandé de **ne pas versionner une clé sensible** dans un dépôt public.

### ripMap est injectée via `BuildConfig` dans :

- `app/build.gradle.kts`

La clé est utilisée par la couche réseau dans `PlacesRepository`.

#### Étapes à suivre

1. Créer un compte OpenTripMap et récupérer une clé API.
2. Remplacer la valeur de `OPEN_TRIP_MAP_API_KEY` dans `app/build.gradle.kts`.
3. Synchroniser Gradle après modification.

> Une valeur de secours est également présente dans `app/src/main/res/values/strings.xml`, mais l’application lit la clé OpenTripMap depuis `BuildConfig`.

---

## État d’avancement du projet

### Fonctionnalités terminées

- carte Google Maps avec localisation actuelle ;
- demande de permission runtime ;
- récupération de lieux à proximité ;
- affichage des résultats dans une liste ;
- recherche et filtrage par catégorie ;
- calcul de distance entre l’utilisateur et chaque lieu ;
- navigation vers la fiche d’un lieu ;
- écran de détail en Material 3 ;
- ajout de notes personnelles ;
- partage d’un lieu ;
- favoris temporaires en mémoire.

### 🛠️ Fonctionnalités restantes / à améliorer

- persistance des favoris avec **Room** ;
- persistance des notes personnelles en base locale ;
- injection de dépendances plus structurée (par exemple Hilt) ;
- tests unitaires et instrumentés supplémentaires ;
- amélioration de la gestion des erreurs réseau ;
- optimisation et nettoyage final du code avant soutenance.

---

## Pertinence pour un projet universitaire

CityPulse est particulièrement adapté à un projet universitaire Android car il démontre :

- la maîtrise des composants modernes d’Android ;
- l’utilisation d’une architecture MVVM propre ;
- l’intégration d’API externes ;
- la gestion de la localisation et des permissions ;
- la création d’une interface Material Design cohérente ;
- la navigation entre écrans avec passage sécurisé de données.

---

## Remarques finales

- Le projet est pensé pour être évolutif.
- Les fonctionnalités principales sont déjà en place.
- Les prochaines étapes naturelles concernent la persistance locale et la robustesse de l’architecture.

---