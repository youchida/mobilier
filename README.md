# Suivi Coûts Mobilier

Application web Java (Spring Boot 3, Thymeleaf, Spring Data JPA, Spring Security, PostgreSQL) dédiée au suivi des coûts de production de mobilier pour un petit atelier de menuiserie au Bénin.

## 1. Logiciels à installer

- **Java JDK 17** (Eclipse Temurin 17 ou OpenJDK 17)
- **Apache Maven 3.8+** (ou le Maven intégré à NetBeans)
- **Apache NetBeans** (version 18 ou plus récente recommandée)
- **PostgreSQL** (version 14 ou plus récente, avec pgAdmin ou `psql`)
- **Git**

## 2. Ouvrir le projet Maven dans NetBeans

1. Ouvrez Apache NetBeans.
2. Cliquez sur **File > Open Project...**.
3. Sélectionnez le dossier racine contenant le fichier `pom.xml`.
4. Cliquez sur **Open Project**. NetBeans télécharge automatiquement les dépendances Maven.

## 3. Créer et configurer la base PostgreSQL locale

Connectez-vous à PostgreSQL (via `psql` ou pgAdmin) et exécutez :

```sql
CREATE DATABASE suivi_couts_mobilier;
```

Notez votre nom d'utilisateur PostgreSQL (souvent `postgres`) et son mot de passe.
Les tables sont créées automatiquement au premier démarrage grâce à Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## 4. Définir les variables d'environnement et lancer le projet

L'application utilise les variables d'environnement suivantes :

- `PORT` : port d'écoute du serveur web (`8080` par défaut en local).
- `DB_URL` : URL JDBC PostgreSQL au format strict `jdbc:postgresql://HOTE:PORT/NOM_DE_LA_BASE`.
- `DB_USERNAME` : utilisateur PostgreSQL.
- `DB_PASSWORD` : mot de passe PostgreSQL.
- `DEMO_MODE` : `true` pour activer la création des comptes et données de démonstration lors de la première initialisation, sinon `false`.
- `DEMO_PASSWORD` : mot de passe initial des comptes de démonstration lorsque `DEMO_MODE=true` (exemple pour la démonstration : `demo1234`).

### Lancer depuis le terminal (Linux / macOS)

```bash
export PORT=8080
export DB_URL=jdbc:postgresql://localhost:5432/suivi_couts_mobilier
export DB_USERNAME=postgres
export DB_PASSWORD=votre_mot_de_passe
export DEMO_MODE=true
export DEMO_PASSWORD=demo1234

mvn spring-boot:run
```

### Lancer depuis Windows (PowerShell)

```powershell
$env:PORT="8080"
$env:DB_URL="jdbc:postgresql://localhost:5432/suivi_couts_mobilier"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="votre_mot_de_passe"
$env:DEMO_MODE="true"
$env:DEMO_PASSWORD="demo1234"

mvn spring-boot:run
```

### Configurer dans NetBeans

1. Faites un clic droit sur le projet > **Properties**.
2. Allez dans **Run** (ou **Actions > Run project**).
3. Ajoutez les variables d'environnement (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DEMO_MODE`, `DEMO_PASSWORD`).
4. Cliquez sur **Run** puis ouvrez `http://localhost:8080`.

### Remarque importante sur le mode démonstration

Lorsque `DEMO_MODE=true` et `DEMO_PASSWORD=demo1234`, l'application crée une seule fois au démarrage :
- **Administrateur** : `admin@mobilier.bj` / `demo1234`
- **Agent** : `agent@mobilier.bj` / `demo1234`

Ces comptes sont strictement réservés à une démonstration avec des données fictives. Après la démonstration, vous devez impérativement **changer leurs mots de passe ou désactiver ces comptes** depuis la page Administration. Passer `DEMO_MODE` à `false` empêche uniquement la création initiale, mais ne supprime pas ni ne désactive les comptes déjà créés en base.

## 5. Créer un dépôt GitHub et y envoyer le projet

1. Créez un nouveau dépôt vide sur GitHub (sans README ni `.gitignore` si le projet les contient déjà).
2. Dans le dossier du projet, exécutez :

```bash
git init
git add .
git commit -m "Initialisation du projet Suivi Couts Mobilier"
git branch -M main
git remote add origin https://github.com/VOTRE_COMPTE/VOTRE_DEPOT.git
git push -u origin main
```

Vérifiez qu'aucun fichier `.env` ni aucun mot de passe réel n'apparaît dans le dépôt GitHub.

## 6. Créer une base PostgreSQL et un Web Service Docker sur Render

1. Connectez-vous à [Render](https://render.com).
2. Créez une base de données PostgreSQL : **New + > PostgreSQL**.
   - Choisissez un nom et une région (par exemple Frankfurt ou Oregon).
   - Une fois créée, ouvrez l'onglet **Info / Connections** de la base PostgreSQL pour relever :
     - **Hostname** (hôte)
     - **Port** (généralement `5432`)
     - **Database** (nom de la base)
     - **Username** (utilisateur)
     - **Password** (mot de passe)
   - Si votre forfait Render ne propose pas de base PostgreSQL ou si vous préférez utiliser un autre hébergeur PostgreSQL accessible sur Internet, vous pouvez utiliser n'importe quelle base PostgreSQL externe en renseignant ses paramètres de connexion dans les variables d'environnement de l'application sur Render.
3. Créez le service web : **New + > Web Service**.
   - Reliez votre dépôt GitHub.
   - Dans **Language / Runtime**, choisissez **Docker** (le fichier `Dockerfile` à la racine est détecté automatiquement).
   - Choisissez la même région que votre base PostgreSQL si possible.

## 7. Renseigner les variables d'environnement sur Render

Dans les paramètres du Web Service Render (**Environment**), ajoutez :

- `DB_URL` : construisez l'URL JDBC à partir des informations PostgreSQL sous la forme :
  `jdbc:postgresql://HOTE:PORT/NOM_DE_LA_BASE`
  Exemple : `jdbc:postgresql://dpg-xxxxxx-a.frankfurt-postgres.render.com:5432/suivi_couts_mobilier`
  Ne collez pas directement une URL commençant par `postgres://` ou `postgresql://` sans le préfixe `jdbc:`.
- `DB_USERNAME` : l'utilisateur fourni par votre base PostgreSQL.
- `DB_PASSWORD` : le mot de passe fourni par votre base PostgreSQL.
- `DEMO_MODE` : `true` pour le premier lancement de démonstration (ou `false` si vous créez les comptes autrement).
- `DEMO_PASSWORD` : `demo1234` (uniquement pour la démonstration initiale).

Render fournit automatiquement la variable `PORT`, et l'application écoute sur `0.0.0.0:${PORT}`.

## 8. Obtenir l'URL réelle de l'application

Une fois le déploiement Docker terminé (`Live`), Render affiche l'adresse publique du service en haut de la page du Web Service, sous la forme :

`https://nom-du-service.onrender.com`

## 9. Vérifier la persistance des données après redémarrage

1. Connectez-vous sur l'URL Render et ajoutez par exemple une matière première ou un achat.
2. Sur Render, cliquez sur **Manual Deploy > Restart service** (ou redémarrez le Web Service).
3. Reconnectez-vous à l'application et vérifiez que la matière ou l'achat ajouté est toujours présent. Les données étant stockées dans PostgreSQL, elles ne sont pas perdues lors du redémarrage du conteneur.

## 10. Limites du forfait Render choisi

Selon le forfait Render sélectionné :
- Une instance d'entrée de gamme peut se mettre en veille après une période d'inactivité ; le premier chargement après veille peut prendre plusieurs dizaines de secondes.
- La disponibilité continue et la durée de conservation d'une base PostgreSQL dépendent des conditions du forfait Render actif au moment du déploiement (certaines bases de test ont une durée limitée). Consultez toujours les conditions de votre forfait Render ou sauvegardez régulièrement vos données.

## 11. Tests manuels de vérification

1. **Connexion des deux comptes** : connectez-vous avec `admin@mobilier.bj` puis avec `agent@mobilier.bj`.
2. **Contrôle des droits** : connecté en tant qu'agent, vérifiez que le lien Administration est absent et que l'accès direct à `/administration` retourne un refus d'accès (403).
3. **Matière et achat** : créez une matière première, enregistrez un premier achat puis un second à un prix différent ; vérifiez la mise à jour du stock et du coût unitaire moyen pondéré.
4. **Fabrication complète** : créez une fabrication, ajoutez des consommations de matières, de la main-d'œuvre et des charges de fabrication.
5. **Protection du stock** : essayez d'ajouter une consommation supérieure au stock disponible et vérifiez le refus avec message explicatif.
6. **Clôture et coût unitaire** : clôturez la fabrication et vérifiez le calcul du coût total et du coût de revient unitaire (ainsi que l'écart avec le prix de vente habituel s'il est renseigné).
7. **Verrouillage** : vérifiez qu'une fabrication terminée ne permet plus d'ajouter, modifier ou supprimer ses lignes.
8. **Tableau de bord** : vérifiez que tous les indicateurs du mois en cours correspondent aux opérations enregistrées et que les charges générales apparaissent séparément.
9. **Persistance** : redémarrez l'application et vérifiez la conservation des données.
