#### ================================================================================

#### &nbsp;                          SPLENDOR - MODE D'EMPLOI

#### &nbsp;                       Version Console - Projet Java

#### &nbsp;                         FONFREIDE Quentin - 2026

#### ================================================================================



#### TABLE DES MATIÈRES

###### ==================

###### 1\. Présentation du jeu

###### 2\. Installation et lancement

###### 3\. Règles du jeu

###### 4\. Interface et commandes

###### 5\. Types de joueurs

###### 6\. Stratégies des robots

###### 7\. Astuces et conseils





================================================================================

### 1\. PRÉSENTATION DU JEU

================================================================================



Splendor est un jeu de stratégie où vous incarnez un marchand de la Renaissance.

Votre objectif : collecter des gemmes, acheter des cartes de développement et

accumuler des points de prestige pour devenir le plus grand joaillier d'Europe.



OBJECTIF

--------

→ Être le premier joueur à atteindre 15 points de prestige

→ En cas d'égalité, le joueur avec le moins de cartes achetées gagne





================================================================================

### 2\. INSTALLATION ET LANCEMENT

================================================================================



PRÉREQUIS

---------

\- Java JDK 11 ou supérieur installé

\- BlueJ (IDE éducatif Java) installé

\- Fichier stats.csv dans le dossier du projet

\- Terminal/Console compatible avec les caractères Unicode





OUVERTURE DU PROJET

--------------------

1\. Lancer BlueJ

2\. Ouvrir le dossier du projet : Menu "Projet" → "Ouvrir un projet"

3\. Sélectionner le dossier contenant les fichiers .java

4\. BlueJ affiche le diagramme de classes





LANCEMENT DU JEU

----------------

1\. Clic droit sur la classe "Game" (rectangle jaune)

2\. Sélectionner "void main(String\[] args)"

3\. Dans la fenêtre qui s'ouvre, cliquer sur "OK" (args vide)

4\. L'interface graphique du jeu se lance automatiquement





CONFIGURATION DE LA PARTIE

---------------------------

Le jeu vous pose une série de questions dans l'ordre suivant :



╔═══════════════════════════════════╗

║   Bienvenue sur SPLENDOR !                ║

╚═══════════════════════════════════╝



ÉTAPE 1 : Nombre de joueurs

----------------------------

Nombre de joueurs (2-4) : \[tapez 2, 3 ou 4]



→ Validation : doit être entre 2 et 4

→ En cas d'erreur, le jeu redemande





ÉTAPE 2 : Répartition Humains/Robots

-------------------------------------

=== Bienvenue dans Splendor ===



Nombre total de joueurs : \[nombre choisi]



Combien de joueurs humains ? (0-\[total]) : \[tapez un nombre]



→ 0 = tous robots (partie automatique)

→ \[total] = tous humains (partie multijoueur locale)

→ Valeur intermédiaire = mélange humains/robots



Exemple pour 4 joueurs :

→ 2 joueur(s) humain(s)

→ 2 robot(s)





ÉTAPE 3 : Types de robots

--------------------------

Si vous avez choisi des robots, le jeu demande leur type :



Combien de RushRobots ? (0-\[nbRobots]) : \[tapez un nombre]



→ RushRobots = stratégie concentrée sur une couleur

→ Le reste sera des DumbRobots (stratégie simple)





ÉTAPE 3bis : SmartRush (si RushRobots > 0)

-------------------------------------------

Si vous avez choisi au moins 1 RushRobot :



Combien de SmartRushRobots ? (0-\[nbRushRobots]) : \[tapez un nombre]



→ SmartRushRobots = version optimisée du Rush (IA avancée)

→ Le reste sera des RushRobots classiques



Récapitulatif affiché automatiquement :

→ \[X] SmartRushRobot(s)

→ \[Y] RushRobot(s)

→ \[Z] DumbRobot(s)





ÉTAPE 4 : Noms des joueurs

---------------------------

Le jeu demande les noms dans l'ordre suivant :



=== Joueurs humains ===

Nom du joueur humain 1 : \[tapez un nom]

✓ Joueur humain '\[nom]' créé



=== SmartRushRobots ===

Nom du SmartRushRobot 1 : \[tapez un nom]

✓ SmartRushRobot '\[nom]' créé



=== RushRobots ===

Nom du RushRobot 1 : \[tapez un nom]

✓ RushRobot '\[nom]' créé



=== DumbRobots ===

Nom du DumbRobot 1 : \[tapez un nom]

✓ DumbRobot '\[nom]' créé





ÉTAPE 5 : Initialisation

-------------------------

Initialisation du plateau de jeu...



==================================================

Tous les joueurs sont prêts ! La partie commence !

==================================================



→ Le plateau s'affiche avec les cartes et jetons

→ Le premier tour commence automatiquement





CONFIGURATIONS RECOMMANDÉES

----------------------------



APPRENTISSAGE DU JEU :

→ 2 joueurs : 1 humain + 1 DumbRobot

→ Permet de comprendre les règles sans pression



PARTIE NORMALE :

→ 3 joueurs : 1 humain + 1 RushRobot + 1 DumbRobot

→ Bon équilibre de difficulté



DÉFI AVANCÉ :

→ 4 joueurs : 1 humain + 2 SmartRushRobots + 1 RushRobot

→ Maximum de challenge stratégique



DÉMONSTRATION IA :

→ 4 joueurs : 0 humains (tous robots)

→ Observer les stratégies des différentes IA





FIN DE PARTIE

-------------

À la fin de la partie, le message suivant s'affiche :



Appuyez sur Entrée pour fermer le jeu...



→ Tapez Entrée pour fermer l'interface graphique

→ Les résultats restent affichés jusqu'à votre confirmation





DÉPANNAGE

---------

PROBLÈME : "Erreur : veuillez entrer un nombre valide"

→ SOLUTION : Tapez seulement des chiffres (pas de lettres)



PROBLÈME : L'interface ne s'affiche pas correctement

→ SOLUTION : Vérifiez que votre terminal supporte l'Unicode

→ Sur Windows : utiliser Windows Terminal ou PowerShell moderne



PROBLÈME : "Fichier stats.csv introuvable"

→ SOLUTION : Vérifiez que stats.csv est dans le dossier du projet

→ Le fichier doit contenir les données des cartes



PROBLÈME : Plantage au lancement

→ SOLUTION : Vérifiez Java JDK 11+ avec : java -version

→ Recompilez tous les fichiers dans BlueJ (Ctrl+K)





================================================================================

### 3\. RÈGLES DU JEU

================================================================================



MATÉRIEL

--------

◆ 5 types de gemmes (ressources) :

&nbsp; • Diamant    ◆ (blanc)

&nbsp; • Saphir     ♦ (bleu)

&nbsp; • Émeraude   ♣ (vert)

&nbsp; • Rubis      ♥ (rouge)

&nbsp; • Onyx       ♠ (noir)



◆ Jetons Or ◉G : jokers remplaçant n'importe quelle gemme



◆ Cartes de développement : 3 niveaux (tiers)

&nbsp; • Tier 1 : cartes simples (0-1 point)

&nbsp; • Tier 2 : cartes moyennes (1-3 points)

&nbsp; • Tier 3 : cartes difficiles (3-5 points)



◆ Nobles : cartes spéciales valant 3 points (obtenus automatiquement)





DÉROULEMENT D'UN TOUR

----------------------

À chaque tour, un joueur effectue UNE SEULE action parmi :



1\. PRENDRE 2 JETONS IDENTIQUES

&nbsp;  • Condition : il doit y avoir au moins 4 jetons de ce type disponibles

&nbsp;  • Exemple : prendre 2 saphirs si ≥ 4 saphirs sur le plateau



2\. PRENDRE 3 JETONS DIFFÉRENTS

&nbsp;  • Prendre 1 jeton de 3 types différents

&nbsp;  • Exemple : 1 diamant + 1 rubis + 1 émeraude

&nbsp;  • Note : si moins de 3 types disponibles, prendre moins



3\. ACHETER UNE CARTE

&nbsp;  • Payer le coût en jetons (après déduction des bonus)

&nbsp;  • Les jetons Or peuvent remplacer n'importe quelle ressource

&nbsp;  • Les bonus des cartes achetées réduisent les coûts futurs

&nbsp;  • Récupérer la carte et ses points de prestige



4\. RÉSERVER UNE CARTE

&nbsp;  • Mettre une carte de côté (maximum 3 réservations)

&nbsp;  • Recevoir 1 jeton Or gratuitement

&nbsp;  • La carte réservée ne peut plus être achetée par les adversaires

&nbsp;  • Peut être achetée plus tard



5\. PASSER SON TOUR

&nbsp;  • Ne rien faire (action de dernier recours)





LIMITE DE JETONS

----------------

→ Un joueur ne peut JAMAIS avoir plus de 10 jetons

→ Si dépassement : défausser l'excédent immédiatement

→ Le joueur choisit quels jetons défausser





CARTES DE DÉVELOPPEMENT

------------------------

Chaque carte possède :

• Un coût (jetons à payer)

• Des points de prestige (0 à 5)

• Un bonus permanent (produit 1 ressource gratuite)



Exemple de carte T2 :

┌──────────┐

│ 3 PV     ◆ │  → Produit 1 diamant (bonus permanent)

│  Coût :    │

│  7 ♦       │  → Coûte 7 saphirs

│  3 ◆       │  → + 3 diamants

└──────────┘





NOBLES

------

• Valent toujours 3 points de prestige

• S'obtiennent AUTOMATIQUEMENT (pas d'action nécessaire)

• Condition : avoir assez de BONUS de cartes (pas de jetons)

• Maximum 1 noble par tour



Exemple :

Noble "3D 3S 3E" → obtenu automatiquement si vous possédez :

&nbsp; • ≥ 3 cartes produisant des diamants

&nbsp; • ET ≥ 3 cartes produisant des saphirs

&nbsp; • ET ≥ 3 cartes produisant des émeraudes





ACHAT AVEC BONUS

-----------------

Les bonus des cartes achetées réduisent les coûts futurs.



Exemple :

Carte à acheter : 5 diamants, 3 saphirs

Vous possédez :

&nbsp; • 2 jetons diamant

&nbsp; • 1 jeton saphir

&nbsp; • 3 bonus diamant (cartes achetées)

&nbsp; • 2 jetons Or



Calcul :

&nbsp; • Diamants : besoin 5, bonus 3 → reste 2 à payer (vous avez 2 jetons ✓)

&nbsp; • Saphirs : besoin 3, bonus 0 → reste 3 à payer (vous avez 1 jeton)

&nbsp; • Manque 2 saphirs → compensés par 2 jetons Or ✓

&nbsp; → Vous pouvez acheter la carte !





FIN DE PARTIE

-------------

La partie se termine dès qu'un joueur atteint 15 points à la fin du tour.



En cas d'égalité :

1\. Le joueur avec le MOINS de cartes achetées gagne (efficacité)

2\. Si encore égalité : partie nulle





================================================================================

### 4\. INTERFACE ET COMMANDES

================================================================================



AFFICHAGE DU PLATEAU

---------------------

Le plateau s'affiche en deux parties :



GAUCHE : Le plateau de jeu

┌─────────────────────────────────────┐

│ ⚜ NOBLE   ⚜ NOBLE   ⚜ NOBLE               │  ← Nobles disponibles

│                                             │

│ \[09 cartes] tier 3                          │  ← Cartes Tier 3 (4 visibles)

│ \[26 cartes] tier 2                          │  ← Cartes Tier 2 (4 visibles)

│ \[36 cartes] tier 1                          │  ← Cartes Tier 1 (4 visibles)

│                                             │

│ Ressources disponibles :                    │  ← Jetons sur le plateau

│ 4◆  4♦  4♣  4♥  4♠  5◉G                   │

└─────────────────────────────────────┘



DROITE : Informations des joueurs

┌──────────────────────┐

│ Player 1 Alice            │  ← Nom du joueur

│ ⓪ pts                     │  ← Points de prestige

│                           │

│ ♥ 3 \[2] ⚜ N:1 3 Pts      │  ← Rubis : 3 jetons, 2 bonus, 1 noble

│ ♠ 1 \[0]                   │  ← Onyx : 1 jeton, 0 bonus

│ ♣ 2 \[1]   ▮ C:(0/3)      │  ← Émeraude : 2 jetons, 1 bonus, 0 carte réservée

│ ♦ 0 \[3]                   │  ← Saphir : 0 jeton, 3 bonus

│ ◆ 4 \[1]   ◉G 2           │  ← Diamant : 4 jetons, 1 bonus, 2 jetons Or

└──────────────────────┘



Le joueur dont c'est le tour est marqué par une flèche ➤ devant son nom.





MENU DES ACTIONS (Joueur Humain)

---------------------------------

À chaque tour, vous verrez :



--- Tour de Alice ---



1\. Prendre 2 jetons identiques

2\. Prendre 3 jetons différents

3\. Acheter une carte

4\. Réserver une carte

5\. Passer votre tour



Votre choix (1-5) :





COMMANDES DÉTAILLÉES

--------------------



PRENDRE 2 JETONS IDENTIQUES

→ Tapez le symbole de la ressource : D, S, E, R, O

→ Confirmez avec O (Oui) ou annulez avec N (Non)



PRENDRE 3 JETONS DIFFÉRENTS

→ Tapez 3 symboles différents un par un

→ Exemple : D, puis S, puis E

→ Confirmez l'action finale



ACHETER UNE CARTE

→ Choisissez P (Plateau) ou R (Réservations)

→ Si Plateau : indiquez le tier (1/2/3) et la colonne (1-4)

&nbsp; Exemple : tier 2, colonne 3 → carte en position 3 du tier 2

→ Si Réservations : indiquez le numéro de la carte réservée (1-3)

→ Confirmez l'achat



RÉSERVER UNE CARTE

→ Choisissez V (Visible) ou C (Cachée/pioche)

→ Si Visible : indiquez le tier (1/2/3) et la colonne (1-4)

→ Si Cachée : indiquez seulement le tier (1/2/3)

→ Vous recevez automatiquement 1 jeton Or



PASSER SON TOUR

→ Aucune action effectuée, passage au joueur suivant



ANNULATION

→ À tout moment, tapez 0 pour revenir au menu principal





DÉFAUSSE OBLIGATOIRE

--------------------

Si vous dépassez 10 jetons :



Vous avez 12 jetons. Vous devez en défausser 2.

Vos jetons actuels :

\- DIAMOND ◆ : 4

\- SAPPHIRE ♦ : 3

\- EMERALD ♣ : 2

\- RUBY ♥ : 2

\- ONYX ♠ : 1



Jeton (1/2) à défausser \[D/S/E/R/O] :



→ Tapez les symboles un par un jusqu'à revenir à 10 jetons





================================================================================

### 5\. TYPES DE JOUEURS

================================================================================



JOUEUR HUMAIN (HumanPlayer)

---------------------------

• Contrôlé par le joueur via le terminal

• Peut effectuer toutes les actions du jeu

• Doit faire des choix stratégiques à chaque tour

• Peut annuler et revenir en arrière (tapez 0)





ROBOT SIMPLE (DumbRobotPlayer)

-------------------------------

• Stratégie fixe et prévisible

• Ordre des priorités :

&nbsp; 1. Acheter une carte réservée (si possible)

&nbsp; 2. Acheter une carte du plateau (T3 > T2 > T1)

&nbsp; 3. Prendre 2 jetons identiques

&nbsp; 4. Prendre 3 jetons différents

&nbsp; 5. Réserver une carte T1 au hasard

&nbsp; 6. Passer son tour



• Ne planifie pas ses achats

• Achète la première carte disponible sans réfléchir

• Niveau : Débutant





ROBOT RUSH (RushRobotPlayer)

-----------------------------

• Stratégie agressive concentrée sur UNE couleur

• Identifie la couleur la plus demandée par les cartes 3-5 PV

• Focus total sur cette couleur toute la partie

• Réserve 2-3 cartes d'affilée pour bloquer les adversaires

• Achète 2 cartes T1 de la couleur cible (générateurs)

• Vise à acheter rapidement les grosses cartes (3-5 PV)

• Niveau : Intermédiaire





ROBOT SMART RUSH (SmartRushRobotPlayer)

----------------------------------------

• Version optimisée du Rush avec priorisation dynamique

• Système de points pour choisir la couleur cible (T2/T3 + nobles)

• Recalcule les priorités à chaque tour selon les besoins

• Réserve basé sur l'écart (carte la plus proche d'être achetable)

• Système de coûts pondérés (pénalise les cartes mono-couleur)

• Gère intelligemment les jetons (3 différents par défaut)

• Pénalité de saturation (évite d'acheter trop de bonus identiques)

• Niveau : Avancé





================================================================================

### 6\. STRATÉGIES DES ROBOTS

================================================================================



DUMB ROBOT - Stratégie Simple

------------------------------

Exemple de partie typique :

Tour 1 : Achète T3 (si possible avec 0 jetons → impossible)

Tour 2 : Prend 2 jetons saphir

Tour 3 : Prend 2 jetons diamant

Tour 4 : Achète T1 coûtant 3 rubis (si possible)

...



Forces :

✓ Facile à battre pour un humain

✓ Bon pour apprendre le jeu



Faiblesses :

✗ Aucune planification

✗ Achète sans stratégie

✗ Ne bloque pas les adversaires





RUSH ROBOT - Stratégie Concentrée

----------------------------------

Exemple de partie typique :

Tour 1 : Analyse → Couleur cible = RUBY (conservée toute la partie)

Tour 2 : Réserve T2 rubis (3 PV)

Tour 3 : Achète T1 rubis \[1/2]

Tour 4 : Achète T1 rubis \[2/2]

Tour 5 : Réserve T1 rubis

Tour 6 : Prend jetons rubis

Tour 7 : Achète carte réservée T2 rubis (3 PV) → 3 points

...



Forces :

✓ Accumule rapidement les bonus d'une couleur

✓ Bloque les adversaires par réservations

✓ Vise directement les grosses cartes (3-5 PV)



Faiblesses :

✗ Vulnérable si la couleur cible est rare

✗ Peut manquer d'opportunités sur d'autres couleurs





SMART RUSH ROBOT - Stratégie Optimisée

---------------------------------------

Exemple de partie typique :

Tour 1 : Analyse T2/T3 (3-5 PV) + nobles → Couleur cible = EMERALD

Tour 2 : Réserve T2 émeraude avec écart minimal (plus accessible)

Tour 3 : Achète T1 émeraude (coût pondéré minimal)

Tour 4 : Analyse besoins réservations → Priorité 2 = RUBY

Tour 5 : Achète T1 rubis (besoin secondaire)

Tour 6 : Prend 3 jetons différents (émeraude + rubis + saphir)

Tour 7 : Achète carte réservée T2 émeraude (3 PV) → 3 points

...



Forces :

✓ Adaptation dynamique selon les besoins

✓ Équilibre entre focus et flexibilité

✓ Optimise les coûts (évite les cartes chères)

✓ Pénalise la saturation (évite 5+ bonus identiques)



Faiblesses :

✗ Complexe à battre pour un débutant

✗ Peut sur-optimiser et manquer des opportunités simples





================================================================================

### 7\. ASTUCES ET CONSEILS

================================================================================



POUR LES DÉBUTANTS

------------------

1\. RÉSERVEZ STRATÉGIQUEMENT

&nbsp;  → Réservez les cartes que vous voulez vraiment acheter

&nbsp;  → Bloquez les cartes que vos adversaires veulent

&nbsp;  → Bonus : vous recevez 1 jeton Or gratuit



2\. PRIVILÉGIEZ LES BONUS

&nbsp;  → Les bonus réduisent les coûts futurs (permanent !)

&nbsp;  → 3 cartes avec bonus diamant = 3 diamants gratuits à vie

&nbsp;  → Visez 3-4 bonus par couleur pour déclencher les nobles



3\. NE SOUS-ESTIMEZ PAS LES NOBLES

&nbsp;  → 3 points gratuits sans action

&nbsp;  → Planifiez vos achats pour les obtenir

&nbsp;  → Regardez les nobles disponibles en début de partie



4\. GÉREZ VOS JETONS

&nbsp;  → Limite de 10 jetons → anticipez la défausse

&nbsp;  → Les jetons Or sont précieux (remplacent n'importe quoi)

&nbsp;  → Ne prenez pas de jetons "par défaut" sans plan





STRATÉGIES AVANCÉES

-------------------

1\. STRATÉGIE RUSH (Concentration)

&nbsp;  → Choisissez UNE couleur dès le début

&nbsp;  → Achetez tous les T1 de cette couleur (2-3 suffisent)

&nbsp;  → Réservez les T2/T3 demandant cette couleur

&nbsp;  → Visez 15 points avec 4-5 grosses cartes (3-5 PV)



2\. STRATÉGIE ÉQUILIBRÉE (Diversification)

&nbsp;  → Achetez des T1 de toutes les couleurs

&nbsp;  → Ciblez plusieurs nobles (3 points chacun)

&nbsp;  → Privilégiez les cartes à coûts diversifiés (2+2+2 > 6)

&nbsp;  → Flexibilité maximale



3\. STRATÉGIE BLOQUAGE (Contrôle)

&nbsp;  → Réservez les cartes que vos adversaires veulent

&nbsp;  → Monopolisez une couleur rare

&nbsp;  → Forcez-les à défausser (prenez les derniers jetons)



4\. STRATÉGIE OR (Joker)

&nbsp;  → Réservez beaucoup (3 cartes = 3 jetons Or)

&nbsp;  → Utilisez l'Or pour combler les manques

&nbsp;  → Flexibilité pour acheter n'importe quelle carte





ERREURS À ÉVITER

----------------

✗ Acheter trop de T1 (4-5 T1 = tours perdus)

✗ Réserver sans plan (3 réservations inutiles)

✗ Ignorer les nobles (9 points gratuits possibles)

✗ Prendre des jetons sans objectif

✗ Défausser des jetons nécessaires

✗ Oublier la limite de 10 jetons





COMBOS PUISSANTS

----------------

1\. RÉSERVATION + OR

&nbsp;  → Réservez 3 cartes d'affilée → 3 jetons Or

&nbsp;  → Utilisez l'Or pour acheter une grosse carte rapidement



2\. RUSH + NOBLES

&nbsp;  → Concentrez-vous sur 2-3 couleurs

&nbsp;  → Obtenez 2-3 nobles automatiquement

&nbsp;  → 6-9 points gratuits !



3\. DÉFAUSSE STRATÉGIQUE

&nbsp;  → Forcez-vous à défausser en prenant 3 jetons

&nbsp;  → Défaussez les couleurs inutiles

&nbsp;  → Optimisez votre main





================================================================================



BON JEU ! 💎👑



Pour toute question ou bug, contactez : FONFREIDE Quentin

Version : 04/01/2026



================================================================================



