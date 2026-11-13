# Wa-Tor

Simulation d'une mer sans bord où vivent des thons et des requins : ils se déplacent, se reproduisent, et les requins mangent les thons ou meurent de faim.

```
cd wator
mvn javafx:run
```

La commande se lance depuis ce dossier (celui qui contient `pom.xml`).

- **Espace** : pause / reprise
- **Échap** : quitter

L'écran est coupé en deux : la mer à gauche (une grille de cases de 10 px : bleu foncé = vide, vert = thon, rouge = requin) et un panneau de statistiques à droite (numéro du tour, nombre de thons, de requins et de cases vides, et le graphique des deux populations sur les 300 derniers tours). Un tour dure 100 ms (10 tours par seconde).

## Les classes

### Paquetage `model` (toute la logique, aucun import JavaFX)

| Classe | Rôle |
|---|---|
| `Position` | Une case de la mer, identifiée par sa ligne et sa colonne (record immuable). |
| `Species` | L'espèce d'un animal (thon ou requin) avec sa couleur ; sert d'étiquette pour compter et pour colorier, jamais pour décider. |
| `Animal` | Classe abstraite qui impose le déroulé d'un tour (choisir, se déplacer, vieillir, mourir de faim, se reproduire) et laisse chaque espèce remplir les détails. |
| `Tuna` | Un thon : va vers une case voisine vide au hasard, se reproduit tous les 3 tours, et est une proie. |
| `Shark` | Un requin : mange un thon voisin sinon avance sur une case vide, meurt s'il n'a rien mangé depuis 3 tours, se reproduit tous les 10 tours. |
| `Sea` | La grille sans bord : une seule bête par case, les quatre voisines (avec rebouclage), et les opérations placer, déplacer et retirer un animal. |
| `SeaPopulator` | Remplit la mer au départ : 30 % de thons et 3 % de requins, sur des cases distinctes tirées au hasard. |
| `Census` | La photo d'un tour : numéro du tour, thons, requins, cases vides (record immuable). |
| `History` | Garde les recensements des 300 derniers tours, pour le graphique. |
| `World` | La simulation : la mer, le numéro du tour, la pause, l'historique ; `nextTurn()` mélange les animaux et fait agir chacun une fois. |

### Paquetage `view` (affichage uniquement, environ 250 lignes)

| Classe | Rôle |
|---|---|
| `Main` | Ouvre la fenêtre plein écran, calcule la taille de la grille, transmet les touches et le rythme (10 tours par seconde) au `World`, puis demande de redessiner. |
| `SeaPane` | Dessine la grille de la mer sur un `Canvas`, une case de 10 px par cellule. |
| `StatsPane` | Le panneau de droite : affiche les quatre nombres et la mention « EN PAUSE ». |
| `PopulationPane` | Trace le graphique : une courbe par espèce, dans les couleurs de la mer. |
| `Colors` | Traduit la couleur d'une `Species` en couleur JavaFX. |

## Choix de conception

**Le modèle ne connaît pas la vue, et fonctionne sans fenêtre.**
`World.nextTurn()` suffit à faire tourner toute la simulation, donc on peut la vérifier sans JavaFX. La vue lit le modèle (`getSea()`, `getHistory()`, `getTurn()`), jamais l'inverse. Le modèle ne parle que de lignes et de colonnes, jamais de pixels : c'est la vue qui calcule combien de cases tiennent dans l'écran (`SeaPane.cellsIn`).

**Un animal ne sait pas se dessiner.**
`Animal`, `Tuna` et `Shark` n'ont ni dépendance JavaFX, ni couleur JavaFX : seulement une `Species`.

**Une classe abstraite pour l'héritage et le polymorphisme.**
`Animal.act` est le même pour tous : il appelle `startTurn`, `chooseDestination`, `moveTo`, `isStarving`, puis crée le nouveau-né si besoin. Il est `final` pour qu'aucune espèce ne change l'ordre des étapes. Les différences sont dans les méthodes redéfinies :

| | `Tuna` | `Shark` |
|---|---|---|
| `chooseDestination` | case voisine vide au hasard | thon voisin au hasard, sinon case vide |
| `reproductionPeriod` | 3 | 10 |
| `startTurn` / `isStarving` | rien | compte la faim, meurt à 3 tours sans manger |
| `moveTo` | déplace | mange d'abord la proie s'il y en a une |
| `isPrey` | vrai | faux (valeur par défaut) |

**Aucun test sur le type d'un animal.**
Pas de `instanceof`, pas de `switch`, pas de `if` sur la classe. La mer ne demande jamais « es-tu un thon ? » :
- elle distingue seulement une case vide d'une case occupée ;
- le requin sait si une voisine est mangeable en appelant `isPrey()` : c'est une capacité que `Tuna` redéfinit, pas un nom de classe ;
- pour compter et colorier, on regroupe par `Species` (`groupingBy`) ou on traduit sa couleur. C'est une étiquette qui sert de clé, jamais de condition sur le comportement.

**Une seule bête par case, garantie par la mer.**
Les trois opérations qui modifient la grille (`place`, `move`, `remove`) sont réservées au paquetage `model` : la vue ne peut que lire. `place` refuse une case occupée, donc il est impossible d'avoir deux animaux dans la même case.

**Une mer sans bord.**
Les voisines d'une case se calculent avec `Math.floorMod` : la colonne à droite de la dernière est la première, et de même pour les lignes. La taille minimale est de 3 × 3, car en dessous des voisines se confondraient.

**Chaque animal agit exactement une fois par tour, dans un ordre aléatoire.**
`nextTurn` photographie les animaux présents, mélange cette liste, puis fait agir chaque animal encore vivant. Les nouveau-nés ne sont pas dans la photographie, donc ils ne jouent qu'au tour suivant. Un thon mangé pendant le tour est marqué mort (`die()`) et est sauté quand son tour arrive.

**Précisions de règles.**
- La faim est vérifiée avant la reproduction : un requin qui meurt ne laisse pas de petit.
- Le compteur de reproduction augmente à chaque tour, même si l'animal reste sur place. La reproduction se fait au premier déplacement une fois la période atteinte, dans la case que l'animal vient de quitter, puis le compteur repart à 0.
- Quand un requin mange, son compteur de faim repart à 0 ; un requin qui n'a rien mangé pendant 3 tours meurt à la fin du 3e tour.

**Les chiffres du sujet ne sont écrits qu'une fois.**

| Constante | Valeur | Où |
|---|---|---|
| `Tuna.REPRODUCTION_PERIOD` | 3 | `Tuna` |
| `Shark.REPRODUCTION_PERIOD` | 10 | `Shark` |
| `Shark.EAT_LIMIT` | 3 | `Shark` |
| `SeaPopulator.TUNA_START_RATIO` | 0,30 | `SeaPopulator` |
| `SeaPopulator.SHARK_START_RATIO` | 0,03 | `SeaPopulator` |
| `History.MAX_TURNS` | 300 | `History` |
| `SeaPane.CELL_SIZE` | 10 px | `SeaPane` |
| `Main.TURNS_PER_SECOND` | 10 | `Main` |

Ce sont toutes des `private static final`. Les seules autres constantes numériques du modèle ne sont pas des règles du sujet : la taille minimale de la mer (`Sea.MIN_SIZE`) et les valeurs de couleur de `Species`.

**Les comptages ne peuvent pas être faux.**
À chaque tour, `World.takeCensus` recompte la grille au lieu de tenir des compteurs à jour à la main. Thons, requins et cases vides viennent de la même photographie, donc leur somme vaut toujours le nombre total de cases.

**Pas d'état statique modifiable.**
Le générateur aléatoire est passé aux constructeurs (`World`, `SeaPopulator`) au lieu d'être un `static Random`, donc on peut rejouer une simulation avec une graine. Tous les attributs sont privés.

**La pause est dans le modèle.**
`togglePause()` et `isPaused()` sont dans `World`, et `nextTurn` ne fait rien en pause. La vue ne fait que relayer la touche Espace.

**Collections et streams là où ils simplifient.**
`Collections.shuffle` pour l'ordre des tours, `groupingBy(...counting())` pour les recensements, des filtres de flux pour les cases voisines vides ou occupées, `Optional` pour « choisir une case au hasard, sinon rester sur place », et une `Deque` bornée pour l'historique.

**Une vue légère.**
La mer est dessinée sur un seul `Canvas` plutôt qu'avec un nœud JavaFX par case (environ 17 000 cases en plein écran). La vue contient environ 250 lignes et aucune règle de la simulation. Le graphique utilise une échelle commune aux deux courbes, avec la valeur maximale affichée en haut à gauche ; tant que l'historique a moins de 300 tours, la courbe s'étire sur toute la largeur, puis la fenêtre glisse.

## Critères du sujet et où ils sont garantis

| Critère | Garantie |
|---|---|
| Jamais deux animaux dans une case | `Sea.place` refuse une case occupée. |
| Aucun animal n'agit deux fois dans un tour | `nextTurn` parcourt une photographie, sans les nouveau-nés. |
| Un animal qui sort par un bord réapparaît de l'autre côté | `Sea.neighbors` utilise `Math.floorMod`. |
| Sans requin, les thons remplissent la mer | Un thon se reproduit tous les 3 tours quand il peut bouger, et rien ne les tue. |
| Sans thon, plus aucun requin après 3 tours | `Shark.isStarving` à 3 tours sans manger. |
| Thons + requins + vides = nombre de cases | `World.takeCensus` recompte la grille. |
| Oscillation des deux populations | Règles du sujet appliquées telles quelles ; visible sur le graphique et sur les fronts de requins dans la mer. |
