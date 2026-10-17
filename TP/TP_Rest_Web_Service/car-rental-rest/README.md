# REST Web Service — Car Rental

Projet Java 21 / Spring Boot 3 / Gradle conforme au TP « Rest Web Service ».

## Démarrage

1. Ouvrir le dossier `car-rental-rest` dans IntelliJ IDEA (Open) ou Eclipse (Import > Existing Gradle Project).
2. Attendre le téléchargement des dépendances Gradle (connexion Internet nécessaire la première fois).
3. Exécuter `CarRentalApplication.java`.
4. Ouvrir http://localhost:8080 : le texte `hello` doit s'afficher.

**Alternative avec Gradle installé** : `gradle bootRun` (le wrapper Gradle n'est pas fourni).

## Routes

| Méthode | URL | Fonction |
|---|---|---|
| GET | `/` | Vérification : `hello` |
| GET | `/cars` | Liste des voitures **non louées** |
| GET | `/cars/11AA22` | Détails d'une voiture |
| PUT | `/cars/11AA22?rent=true` | Louer une voiture (corps JSON obligatoire) |
| PUT | `/cars/11AA22?rent=false` | Restituer une voiture (sans corps) |

Le sujet contient une incohérence : un exemple utilise `/voiture/{plateNumber}` alors que toutes les URL demandées sont `/cars/{plateNumber}`. Le projet utilise `/cars/{plateNumber}`.

## Essais avec curl

```bash
curl http://localhost:8080/
curl http://localhost:8080/cars
curl http://localhost:8080/cars/11AA22
curl -X PUT 'http://localhost:8080/cars/11AA22?rent=true' -H 'Content-Type: application/json' -d '{"begin":"11/11/2017","end":"1/1/2018"}'
curl http://localhost:8080/cars
curl -X PUT 'http://localhost:8080/cars/11AA22?rent=false'
curl http://localhost:8080/cars
```

Sous Windows PowerShell, préférer `curl.exe` à `curl`, ou utiliser Postman/Thunder Client. Les URL `PUT` ne se testent pas par simple saisie dans la barre d'adresse d'un navigateur.

## Résultats attendus

- `GET /cars/11AA22` retourne notamment `{"plateNumber":"11AA22","brand":"Ferrari","price":100,"rented":false}`.
- Après la location, `11AA22` disparaît de `GET /cars`.
- Après restitution, `11AA22` réapparaît.
- Erreurs HTTP : `404` voiture inconnue, `409` location déjà active ou restitution impossible, `400` dates invalides ou manquantes.
- Format des dates : `jour/mois/année` (ex. `1/1/2018`). La date de fin ne doit pas précéder la date de début.

## Tests automatisés

Exécuter la tâche Gradle `test` dans l'IDE, ou `gradle test` si Gradle est installé.

## Choix de conception

- `Car` : données d'une voiture et état de location.
- `Dates` : objet JSON de période de location.
- `CarService` : liste en mémoire, règles métier et validation.
- `CarController` : endpoints REST.
- `CarRentalApplication` : démarrage Spring Boot.

Les données sont **en mémoire** et réinitialisées à chaque redémarrage. Aucune base de données n'est demandée. Le prix est une valeur d'exemple sans devise ni unité imposée dans le sujet.
