# README - Projet Splendor

### 

### 📋 Description du projet



Implémentation en Java du jeu de société Splendor dans une version simplifiée en mode console. Ce projet a été réalisé dans le cadre d'un projet de programmation orientée objet en 2ème année de prépa informatique.



Le jeu Splendor est un jeu de stratégie où les joueurs incarnent des marchands de la Renaissance qui doivent collecter des gemmes pour acheter des cartes de développement et accumuler des points de prestige. Le premier joueur à atteindre 15 points remporte la partie.



### 🎮 Règles du jeu



Objectif

Être le premier joueur à atteindre 15 points de prestige en achetant des cartes de développement.



Tour de jeu

À chaque tour, un joueur peut effectuer une seule action parmi :



Prendre 2 jetons identiques (si au moins 4 jetons de ce type sont disponibles)



Prendre 3 jetons de types différents (1 de chaque)



Acheter une carte de développement



Passer son tour



Cartes de développement

Organisées en 3 niveaux de difficulté (1, 2, 3)



Chaque carte a un coût en ressources (gemmes)



Chaque carte rapporte des points de prestige (0 à 5 points)



Chaque carte produit un bonus permanent d'une ressource



Ressources

5 types de gemmes :



♦ Diamant (D)



♠ Saphir (S)



♣ Émeraude (E)



♥ Rubis (R)



● Onyx (O)



Limite de jetons

Un joueur ne peut pas posséder plus de 10 jetons. S'il dépasse, il doit défausser jusqu'à revenir à 10.



Fin de partie

La partie se termine dès qu'un joueur atteint 15 points. En cas d'égalité, le joueur avec le moins de cartes gagne.



### 🏗️ Architecture du projet



Structure des classes

text

splendor/

├── Resource.java              (Énumération des types de ressources)

├── Resources.java             (Gestion des quantités de ressources)

├── DevCard.java               (Carte de développement)

├── Board.java                 (Plateau de jeu)

├── Player.java                (Classe abstraite joueur)

├── HumanPlayer.java           (Joueur humain)

├── DumbRobotPlayer.java       (Robot avec IA simple)

├── Action.java                (Interface pour les actions)

├── PassAction.java            (Action : passer son tour)

├── PickSameTokensAction.java  (Action : prendre 2 jetons identiques)

├── PickDiffTokensAction.java  (Action : prendre 3 jetons différents)

├── BuyCardAction.java         (Action : acheter une carte)

├── DiscardTokensAction.java   (Action : défausser des jetons)

├── Game.java                  (Orchestration de la partie)

├── Display.java               (Interface graphique console)

├── Displayable.java           (Interface pour l'affichage)

└── stats.csv                  (Données des cartes)



Diagramme de classes 









### 🚀 Installation et lancement



Prérequis

Java 11 ou supérieur



BlueJ ou tout IDE Java (IntelliJ, Eclipse, VS Code)



Compilation

Avec BlueJ

Ouvrir le dossier du projet dans BlueJ



Compiler toutes les classes (bouton "Compile")



Clic droit sur Game → void main(String\[] args)



Avec ligne de commande

bash

\# Compilation

javac \*.java



\# Lancement

java Game

Configuration d'une partie

Au lancement, le programme demande :



Nombre de joueurs (2 à 4)



Nombre de joueurs humains (0 à nbJoueurs)



Nom de chaque joueur (humains et robots)



Exemple :



text

Nombre de joueurs (2-4) : 3

Combien de joueurs humains ? (0-3) : 2



=== Joueurs humains ===

Nom du joueur humain 1 : Alice

Nom du joueur humain 2 : Bob



=== Robots ===

Nom du robot 1 : Skynet





#### 🎯 Utilisation (Joueur humain)



Menu principal

text

=== Votre tour, Alice ===

1\. Prendre 2 jetons identiques

2\. Prendre 3 jetons différents

3\. Acheter une carte

4\. Passer votre tour

Votre choix (1-4) :

Prendre des jetons

2 identiques : Choisir le type (D/S/E/R/O)



3 différents : Choisir 3 types différents successivement



Taper 0 pour revenir au menu principal



Acheter une carte

Saisir le niveau (1-3)



Saisir la colonne (1-4)



Les bonus des cartes déjà possédées réduisent automatiquement le coût



Défausse (si > 10 jetons)

Le jeu demande automatiquement quels jetons défausser jusqu'à revenir à 10.



#### 🤖 Intelligence Artificielle (Robot)



Le DumbRobotPlayer suit une stratégie simple :



Acheter une carte si possible (priorité niveau 3 > 2 > 1)



Prendre 2 jetons identiques si possible



Prendre 3 jetons différents si possible



Passer son tour sinon



Cette stratégie est prévisible mais fonctionnelle pour tester le jeu.



### 📊 Fonctionnalités implémentées



#### ✅ Fonctionnalités principales



&nbsp;Lecture du fichier CSV des cartes



&nbsp;Plateau de jeu avec 3 niveaux de cartes



&nbsp;Gestion des ressources (jetons)



&nbsp;Système d'actions avec interface



&nbsp;Joueur humain avec interaction console



&nbsp;Robot avec IA basique



&nbsp;Système de bonus permanents des cartes



&nbsp;Calcul automatique du coût avec bonus



&nbsp;Limite de 10 jetons avec défausse obligatoire



&nbsp;Détection de fin de partie (15 points)



&nbsp;Gestion des égalités (départage par nombre de cartes)



#### ✅ Améliorations de l'interface



&nbsp;Retour au menu principal (touche 0)



&nbsp;Confirmation des actions



&nbsp;Affichage détaillé des ressources



&nbsp;Gestion des cas limites (pas assez de ressources sur le plateau)



&nbsp;Messages d'erreur clairs



&nbsp;Affichage immédiat des choix



### ❌ Non implémenté (version simplifiée)



&nbsp;Nobles (cartes de niveau 0)



&nbsp;Jetons Or (joker)



&nbsp;Stratégie de Jeu : Rush



&nbsp;Réservation de cartes



&nbsp;Interface graphique



### 📝 Exemples de parties



Partie rapide (2 joueurs)

text

Joueur 1 (Alice) : Humain

Joueur 2 (Robot 1) : Robot



Tour 1 - Alice prend 2 saphirs

Tour 2 - Robot 1 prend 2 diamants

Tour 3 - Alice achète une carte niveau 1 (bonus diamant)

...

Tour 18 - Alice achète une carte niveau 3 (5 points)

→ Alice atteint 15 points et remporte la partie !

Partie avec égalité

text

Alice : 15 points, 8 cartes

Bob : 15 points, 9 cartes

→ Alice remporte la partie (moins de cartes)





### 👨‍💻 Auteur



FONFREIDE Quentin

Étudiant en 2ème année de prépa informatique

Projet réalisé en décembre 2025



### 📚 Ressources



Règles officielles de Splendor



Cahier des charges du projet : 2025-Projet-Splendor.odt



Données des cartes : stats.csv (90 cartes de développement)



### 🎓 Compétences développées



Programmation Orientée Objet : héritage, polymorphisme, interfaces



Architecture logicielle : séparation des responsabilités, design patterns



Gestion de fichiers : lecture et parsing de CSV



Collections Java : List, ArrayList, Stack



Interaction utilisateur : Scanner, validation des entrées



Algorithmique : stratégie d'IA, gestion d'états de jeu



Débogage : résolution de problèmes de buffer, gestion d'exceptions



### 📄 Licence



Ce projet est réalisé dans un cadre pédagogique. Le jeu Splendor est la propriété de Space Cowboys.



Bonne partie ! 🎲💎

