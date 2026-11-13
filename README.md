# Java avancé : projets

Ce dépôt contient mes projets du cours de Java avancé. Chaque projet est un projet Maven dans son propre dossier, avec son propre `README.md` qui liste les classes et justifie les choix de conception.

## Contenu du dépôt

| Projet | Dossier | Points | En une phrase | État |
|---|---|---|---|---|
| Particules | [`particule/`](particule/) | 8 | Des particules colorées se déplacent en plein écran et rebondissent entre elles. | Réalisé |
| Wa-Tor | [`wator/`](wator/) | 6 | Des thons et des requins vivent, se reproduisent et se mangent sur une planète recouverte d'eau. | Réalisé |
| Bataille navale | [`batailleNavale/`](batailleNavale/) | 6 | Le joueur pose sa flotte puis tire à tour de rôle contre un ordinateur qui cherche ses bateaux. | En cours |

```
java/
├── README.md          ce fichier
├── .gitignore         ignore les dossiers target/ produits par Maven
├── particule/         projet Particules
│   ├── pom.xml
│   ├── README.md      classes et choix de conception
│   └── src/main/java/
│       ├── model/     la logique (aucun import JavaFX)
│       └── view/      l'affichage JavaFX
├── wator/             projet Wa-Tor (même organisation)
│   ├── pom.xml
│   ├── README.md      classes et choix de conception
│   └── src/main/java/
│       ├── model/     la logique (aucun import JavaFX)
│       └── view/      l'affichage JavaFX
└── batailleNavale/    projet Bataille navale (même organisation)
    ├── pom.xml
    ├── README.md      classes et choix de conception
    └── src/main/java/
        ├── model/     la logique (aucun import JavaFX)
        └── view/      l'affichage JavaFX
```

## Lancer un projet

Il faut un JDK 21 ou plus et Maven ; JavaFX est téléchargé par Maven. Les commandes se lancent depuis le dossier du projet (celui qui contient son `pom.xml`), pas depuis la racine du dépôt :

| Projet | Commande |
|---|---|
| Particules | `cd particule` puis `mvn javafx:run` |
| Wa-Tor | `cd wator` puis `mvn javafx:run` |
| Bataille navale | `cd batailleNavale` puis `mvn javafx:run` (pas encore jouable : le projet est en cours) |

Dans Particules et Wa-Tor, **Espace** met en pause et relance, et **Échap** quitte. Dans la bataille navale, **Échap** quitte aussi ; le reste se joue à la souris (voir son README). Pour seulement compiler : `mvn clean compile`.

## Les projets en bref

### Particules ([détails](particule/README.md))

150 particules de 8 px de rayon, lancées à 150 px/s dans une direction aléatoire, rebondissent sur les quatre bords de l'écran et les unes sur les autres. Le choc est élastique, à masses égales.

- **Notions travaillées :** encapsulation, collections et streams, interfaces (`Body`, vue en lecture seule du modèle) et lambdas.
- **Choix principal :** les rebonds ne font que renverser ou échanger des composantes de vitesse, donc la somme des carrés des vitesses reste constante.
- **Modèle :** `Body`, `Particle`, `ParticleColor`, `ParticleFactory`, `World`. **Vue :** `Main`, `WorldPane`.

### Wa-Tor ([détails](wator/README.md))

Une mer sans bord, en grille de cases de 10 px, où des thons et des requins agissent chacun une fois par tour, dans un ordre aléatoire. Un panneau affiche le tour, les populations et un graphique des 300 derniers tours.

- **Notions travaillées :** héritage et polymorphisme (`Animal`, `Tuna`, `Shark`), encapsulation, collections et streams.
- **Choix principal :** aucun test sur le type d'un animal. Chaque espèce redéfinit son comportement, et la mer ne distingue qu'une case vide d'une case occupée.
- **Modèle :** `Position`, `Species`, `Animal`, `Tuna`, `Shark`, `Sea`, `SeaPopulator`, `Census`, `History`, `World`. **Vue :** `Main`, `SeaPane`, `StatsPane`, `PopulationPane`, `Colors`.

### Bataille navale ([détails](batailleNavale/README.md))

Sujet libre. Le joueur pose sa flotte de cinq bateaux sur une grille de 10 × 10, puis tire à tour de rôle sur la grille de l'ordinateur, qui cherche les siens. La première flotte entièrement coulée perd.

- **Notions travaillées :** énumérations qui portent des données et du comportement (`Orientation`, `BoatType`), record (`Position`), encapsulation, collections, générateur aléatoire injecté.
- **Choix principal :** la vue ne reçoit jamais les bateaux adverses, seulement l'état de chaque case (`CellState`), et le bot ne connaît que le résultat de ses propres tirs. Tous les bateaux se comportent pareil : seuls leur taille et leur nom changent, donc c'est une donnée (`BoatType`) et non une sous-classe.
- **Modèle :** `Position`, `Orientation`, `BoatType`, `Boat`, `ShotResult`, `CellState`, `Grid`, `FleetPlacer`, `Bot`, `Game`. **Vue :** `Main`, `GridPane`, `PiecePane`, `Colors`.
- **État :** en cours. Les fichiers sont créés, la logique reste à écrire.

## Notation

Les projets sont notés sur 20 points au total :

- Particules : 8 points ;
- Wa-Tor : 6 points ;
- Troisième projet : 6 points, au choix entre le sujet qui sera ajouté plus tard et un sujet libre (à faire valider par l'enseignant avant de commencer). J'ai choisi un sujet libre : la bataille navale.

## Règles communes à tous les projets

- On part de zéro : aucun code de départ n'est fourni, et la conception fait partie du travail.
- Ce sont des projets de Java, pas de JavaFX. JavaFX sert uniquement à afficher ; toute la logique est écrite en Java, dans des classes qui ne connaissent pas JavaFX.
- Deux paquetages au minimum : `model` pour la logique, `view` pour l'affichage. Aucun import `javafx` dans `model`.
- Le code est en anglais : noms de classes, de méthodes, de variables et commentaires. Les README sont en français.

### Comment elles sont respectées

| Règle | Particules | Wa-Tor |
|---|---|---|
| Logique dans `model`, affichage dans `view` | `model` : 5 classes, `view` : 2 | `model` : 10 classes, `view` : 5 |
| Aucun import `javafx` dans `model`, et `model` n'utilise pas `view` | oui | oui |
| Un animal ou une particule n'est pas une forme JavaFX | `Particle` n'hérite de rien et ne contient aucun `Circle` | `Animal` et ses sous-classes n'ont aucune dépendance JavaFX |
| Pas de `instanceof` | oui | oui, ni `switch` ni `if` sur le type d'un animal |
| Attributs privés, aucune variable `static` modifiable | oui | oui |
| Vue légère | environ 105 lignes (limite : environ 150) | environ 245 lignes (limite : environ 250) |
| Le modèle fonctionne sans fenêtre | `World.step(dt)` | `World.nextTurn()` |

La bataille navale suit les mêmes règles ; ses choix de conception sont dans [son README](batailleNavale/README.md). Ce tableau sera complété quand son code sera écrit.
