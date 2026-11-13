# Bataille navale

Le joueur affronte l'ordinateur sur deux grilles de 10 × 10 : chacun pose sa flotte, puis ils tirent à tour de rôle sur la grille de l'autre. Le premier dont toute la flotte est coulée perd.

```
cd batailleNavale
mvn javafx:run
```

La commande se lance depuis ce dossier (celui qui contient `pom.xml`).

> **État d'avancement :** les fichiers listés plus bas sont créés, mais le code est encore à écrire. Ce README décrit la conception visée, et `mvn javafx:run` ne fonctionnera qu'une fois `view.Main` écrit. Cette note sera à supprimer à la fin.

## Les règles du jeu

- **Deux grilles de 10 × 10 :** la sienne à gauche (bateaux visibles) et celle de l'adversaire à droite (bateaux cachés).
- **La flotte, identique pour les deux camps (17 cases) :**

| Bateau | Taille |
|---|---|
| Porte-avions | 5 |
| Cuirassé | 4 |
| Croiseur | 3 |
| Sous-marin | 3 |
| Torpilleur | 2 |

- **Placement :** chaque bateau est horizontal ou vertical, entièrement dans la grille, sans en chevaucher un autre. L'ordinateur place les siens au hasard.
- **Un tir par tour,** qu'il touche ou non. Le résultat est « à l'eau », « touché » ou « coulé ». Une case déjà visée ne peut pas l'être une deuxième fois, et cela ne fait pas perdre le tour.
- **Fin de partie :** dès qu'une flotte est entièrement coulée, l'autre camp gagne.

**Commandes :**
- **Clic sur sa grille :** pose le bateau en cours ;
- **R :** tourne le bateau (horizontal ou vertical) ;
- **Clic sur la grille adverse :** tire sur cette case ;
- **Échap :** quitter.

## Les classes

### Paquetage `model` (toute la logique, aucun import JavaFX)

| Classe | Rôle |
|---|---|
| `Position` | Une case, identifiée par sa ligne et sa colonne (record immuable). Elle ne connaît pas la taille de la grille : c'est `Grid` qui vérifie. |
| `Orientation` | Horizontal ou vertical, chacune avec son pas (0, 1) ou (1, 0). `cellsFrom(début, longueur)` donne les cases d'un bateau, donc aucun `if` sur l'orientation. |
| `BoatType` | Les cinq bateaux de la flotte, avec leur longueur et leur nom français. C'est une donnée, pas une sous-classe. |
| `Boat` | Un bateau posé : son type, ses cases (liste non modifiable) et celles qui sont touchées. Il sait dire s'il occupe une case et s'il est coulé. |
| `ShotResult` | Le résultat d'un tir : à l'eau, touché ou coulé. |
| `CellState` | Ce que la vue a le droit de dessiner pour une case : inconnue, à l'eau, touchée, coulée, ou bateau intact (visible seulement sur sa propre grille). |
| `Grid` | La grille 10 × 10 d'un camp : ses bateaux et les tirs reçus. Elle sait si un bateau peut être posé, le pose, reçoit un tir, dit si toute la flotte est coulée et donne l'état d'une case pour l'affichage. |
| `FleetPlacer` | Place une flotte complète au hasard sur une grille (générateur aléatoire donné de l'extérieur). Sert à l'ordinateur et, si on l'ajoute, à un bouton « placement aléatoire » pour le joueur. |
| `Bot` | L'adversaire qui choisit où tirer. En mode chasse, il tire au hasard sur une case sur deux (damier). Après un touché, il vise les cases voisines, et il revient en chasse dès qu'un bateau est coulé. |
| `Game` | La partie : les deux grilles, le bot et la phase (placement, tour du joueur, tour du bot, fin). `placeBoat`, `playerShoots` et `botPlays` suffisent à jouer une partie complète sans fenêtre. |

### Paquetage `view` (affichage uniquement)

| Classe | Rôle |
|---|---|
| `Main` | Ouvre la fenêtre, transmet les clics et les touches au `Game`, puis demande de redessiner. Un court délai (environ 0,6 s) retarde la riposte du bot pour qu'on la voie. |
| `GridPane` | Dessine une grille de 10 × 10 sur un `Canvas` d'après les `CellState`, et transforme un clic en `Position`. On en crée deux : celle du joueur (bateaux visibles) et celle de l'adversaire (bateaux cachés). |
| `PiecePane` | Le panneau de la flotte : bateau en cours de pose, orientation, bateaux déjà coulés et message de la partie. |
| `Colors` | Traduit un `CellState` en couleur JavaFX : un seul endroit pour toutes les couleurs. |

## Choix de conception

**Le modèle ne connaît pas la vue, et fonctionne sans fenêtre.**
`Game` suffit à dérouler une partie entière, donc on peut la vérifier sans JavaFX. La vue lit le modèle et lui relaie les clics, jamais l'inverse. Le modèle ne parle que de lignes et de colonnes, jamais de pixels : c'est `GridPane` qui calcule la case sous la souris.

**La vue ne voit jamais les bateaux adverses.**
`Grid` ne donne à la vue que des `CellState`, et sur la grille de l'adversaire un bateau intact n'est jamais renvoyé : la case reste « inconnue » tant qu'elle n'est pas touchée. Il n'y a donc aucun moyen de tricher en lisant la vue, et la vue ne peut ni déplacer un bateau, ni tirer à la place du jeu.

**Le bot ne triche pas.**
`Bot` ne connaît ni les bateaux du joueur, ni leur placement. Il sait seulement quelles cases ont déjà été visées et ce que ses tirs ont donné (`ShotResult`, qu'on lui transmet). Il ne peut donc pas tirer deux fois sur la même case.

**Des énumérations plutôt que des `if`.**
`Orientation` porte son pas, `BoatType` porte sa taille et son nom, `ShotResult` et `CellState` nomment les résultats et les états. Il n'y a ni `instanceof`, ni `switch`, ni `if` sur le type d'un bateau.

**Pas d'héritage entre bateaux.**
Les cinq bateaux se comportent exactement pareil (occuper des cases, être touché, couler). Seuls leur taille et leur nom changent : c'est une donnée portée par `BoatType`, donc une classe `Boat` unique suffit. Des sous-classes n'apporteraient rien.

**Une grille qui garantit les règles.**
`Grid` refuse de poser un bateau qui sort de la grille ou en chevauche un autre, et refuse un deuxième tir sur la même case. La vue et le bot n'ont pas à vérifier : les règles sont gardées au même endroit.

**Le tour de jeu est dans le modèle.**
`Game` tient la phase de la partie, accepte un tir du joueur seulement pendant son tour, et décide de la fin. Seul le délai avant la riposte du bot est dans la vue, car c'est une affaire d'affichage.

**Les chiffres ne sont écrits qu'une fois.**

| Constante | Valeur | Où |
|---|---|---|
| `Grid.SIZE` | 10 | `Grid` |
| Longueur de chaque bateau | 5, 4, 3, 3, 2 | `BoatType` |
| `GridPane.CELL_SIZE` (taille d'une case à l'écran) | à fixer, en pixels | `GridPane` |
| Délai de riposte du bot | environ 0,6 s | `Main` |

Ce sont toutes des `private static final` (ou des valeurs d'énumération).

**Pas d'état statique modifiable.**
Le générateur aléatoire est donné aux constructeurs (`FleetPlacer`, `Bot`) au lieu d'être un `static Random`, donc on peut rejouer une partie avec une graine. Tous les attributs sont privés.

**Collections là où elles simplifient.**
Les cases d'un bateau sont une liste non modifiable, les cases touchées et les tirs reçus sont des ensembles, et les cases à viser par le bot sont une file.

**Une vue légère.**
Chaque grille est dessinée sur un seul `Canvas`. La même classe `GridPane` sert pour les deux grilles ; seul un paramètre dit si les bateaux sont visibles, sans sous-classe.

## Règles du jeu et où elles seront garanties

| Règle | Garantie |
|---|---|
| Un bateau reste dans la grille et ne chevauche pas un autre bateau | `Grid` refuse la pose. |
| Une case ne se vise qu'une fois | `Grid` refuse le deuxième tir, et le tour n'est pas perdu. |
| Un bateau est coulé quand toutes ses cases sont touchées | `Boat`. |
| La partie finit dès qu'une flotte est coulée | `Grid` signale la flotte coulée, `Game` change de phase. |
| Le joueur ne voit pas les bateaux adverses | `Grid` ne renvoie que des `CellState`, jamais un `Boat`. |
| Le joueur ne tire pas pendant le tour du bot | `Game` ne l'accepte qu'à son tour. |
