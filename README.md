# Java avancé : projets

Ce dépôt contient mes projets du cours de Java avancé. Chaque projet est un projet Maven dans son propre dossier, avec son propre `README.md` qui liste les classes et justifie les choix de conception.

## Contenu du dépôt

| Projet | Dossier | Points | En une phrase | État |
|---|---|---|---|---|
| Particules | [`particule/`](particule/) | 8 | Des particules colorées se déplacent en plein écran et rebondissent entre elles. | Réalisé |
| Wa-Tor | à créer | 6 | Des thons et des requins vivent, se reproduisent et se mangent sur une planète recouverte d'eau. | À faire |
| Troisième projet | à créer | 6 | Le sujet qui sera ajouté plus tard, ou un sujet libre validé au préalable par l'enseignant. | À venir |

```
java/
├── README.md        ce fichier
├── .gitignore       ignore les dossiers target/ produits par Maven
└── particule/       projet Particules
    ├── pom.xml
    ├── README.md    classes et choix de conception
    └── src/main/java/
        ├── model/   la logique (aucun import JavaFX)
        └── view/    l'affichage JavaFX
```

## Lancer un projet

Il faut un JDK 21 ou plus et Maven ; JavaFX est téléchargé par Maven. Les commandes se lancent depuis le dossier du projet (celui qui contient son `pom.xml`), pas depuis la racine du dépôt :

```
cd particule
mvn javafx:run
```

## Notation

Les projets sont notés sur 20 points au total :

- Particules : 8 points ;
- Wa-Tor : 6 points ;
- Troisième projet : 6 points, au choix entre le sujet qui sera ajouté plus tard et un sujet libre, à faire valider par l'enseignant avant de commencer.

## Règles communes à tous les projets

- On part de zéro : aucun code de départ n'est fourni, et la conception fait partie du travail.
- Ce sont des projets de Java, pas de JavaFX. JavaFX sert uniquement à afficher ; toute la logique est écrite en Java, dans des classes qui ne connaissent pas JavaFX.
- Deux paquetages au minimum : `model` pour la logique, `view` pour l'affichage. Aucun import `javafx` dans `model`.
- Le code est en anglais : noms de classes, de méthodes, de variables et commentaires. Les README sont en français.
