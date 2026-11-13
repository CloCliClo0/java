# Particules

Simulation de 150 particules qui rebondissent sur les bords de l'écran et les unes sur les autres.

```
cd particule
mvn javafx:run
```

La commande se lance depuis ce dossier (celui qui contient `pom.xml`).

- **Espace** : pause / reprise
- **Échap** : quitter

## Les classes

### Paquetage `model` (toute la logique, aucun import JavaFX)

| Classe | Rôle |
|---|---|
| `Body` | Interface en lecture seule (position, rayon, couleur) : c'est tout ce que la vue a le droit de savoir d'une particule. |
| `Particle` | Un disque de 8 px de rayon avec sa position et sa vitesse ; elle sait avancer, rebondir sur les murs et sur une autre particule. |
| `ParticleColor` | Une couleur (rouge, vert, bleu) choisie par le modèle, sous forme de simple valeur immuable. |
| `ParticleFactory` | Crée les particules de départ : position libre, direction aléatoire, 150 px/s, couleur aléatoire. |
| `World` | Le rectangle de jeu, la liste de particules et l'état de pause ; `step(dt)` fait avancer toute la simulation. |

### Paquetage `view` (affichage uniquement, environ 110 lignes)

| Classe | Rôle |
|---|---|
| `Main` | Ouvre la fenêtre plein écran, transmet les touches (Espace, Échap) et le temps écoulé au `World`, puis demande de redessiner. |
| `WorldPane` | Crée un `Circle` par particule et recopie leur position à chaque image ; ne décide de rien. |

## Choix de conception

**Le modèle ne connaît pas la vue, et fonctionne sans fenêtre.**
`World.step(dt)` suffit à faire tourner toute la simulation, donc on peut la vérifier sans JavaFX (1 000 pas, conservation de l'énergie, etc.).
La vue lit le modèle, jamais l'inverse : `WorldPane` associe chaque `Body` à un `Circle`, et `Particle` ne contient aucun `Circle`.

**Une interface en lecture seule (`Body`).**
`World.getBodies()` renvoie une liste non modifiable de `Body`. La vue peut donc dessiner, mais pas déplacer une particule, ni en ajouter ou en retirer : le nombre de 150 ne peut pas changer.

**La pause est dans le modèle.**
`togglePause()` et `isPaused()` sont dans `World`, et `step` ne fait rien en pause. La vue reste très petite et la pause est testable sans fenêtre. La boucle d'animation continue de tourner pendant la pause, donc il n'y a pas de saut de temps à la reprise.

**La couleur est choisie par le modèle.**
`ParticleColor` est une valeur du modèle (pas un `javafx.scene.paint.Color`). La vue ne fait que la traduire.

**Pas d'état statique modifiable.**
Le générateur aléatoire est passé à `ParticleFactory` par le constructeur, au lieu d'être un `static Random`. Les seuls `static` sont des constantes `private static final`.
Tous les attributs sont privés ; la vue passe par des accesseurs.

**Un rebond qui conserve l'énergie.**
- Sur un mur, on remet la particule dans l'écran et on rend la composante de vitesse concernée dirigée vers l'intérieur (`abs`). Seul un signe change.
- Entre deux particules de même masse, un choc élastique échange les composantes de vitesse le long de la ligne des centres. La vitesse ne change que si les particules se rapprochent, sinon deux particules déjà en train de s'éloigner seraient « recollées ».
- Les deux opérations ne font que renverser ou échanger des composantes : la somme des carrés des vitesses ne change donc pas (à l'erreur d'arrondi près, de l'ordre de 1e-15).
- Si deux disques se chevauchent, on les écarte pour qu'ils ne restent pas collés. Cela modifie les positions, jamais les vitesses.

**Ordre des opérations dans un pas : avancer, collisions, murs.**
Les murs passent en dernier : à la fin de chaque pas, toutes les particules sont dans l'écran.

**Pas de particule qui en traverse une autre.**
`World.step` découpe un long intervalle de temps en sous-pas d'au plus 0,01 s, et ne simule jamais plus de 0,1 s d'un coup (après une mise en veille, par exemple). À 150 px/s, une particule ne se déplace alors que de 1,5 px par sous-pas, bien moins qu'un diamètre (16 px).

**Départ sans chevauchement.**
`ParticleFactory` tire une position au hasard et la refuse si le disque dépasse de l'écran ou touche une particule déjà placée. Si l'écran est trop petit, elle lève une exception claire au lieu de boucler indéfiniment.

**Streams et lambdas là où ils simplifient.**
`totalSpeedSquared`, `allParticlesInside`, la recherche d'un emplacement libre (`noneMatch(candidate::touches)`) et la création des cercles dans `WorldPane` utilisent des streams. La vue s'abonne aux touches avec une lambda.

**Pas de `instanceof`.**
Le polymorphisme (l'interface `Body`) suffit.
