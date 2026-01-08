# Documentation technique - Splendor (Java)

## Table des matieres
- Chapitre 1 - Objectif et perimetre
- Chapitre 2 - Lancement du projet
- Chapitre 3 - Regles implementees et invariants
- Chapitre 4 - Architecture generale
- Chapitre 5 - Hierarchie et relations entre classes
- Chapitre 6 - Reference detaillee des classes
- Chapitre 7 - Flux metier principaux
- Chapitre 8 - Extension et maintenance
- Annexes - Format du CSV, conventions, glossaire

---

## Chapitre 1 - Objectif et perimetre

Ce projet est une implementation complete du jeu de societe Splendor en Java,
avec une interface graphique Swing qui affiche un plateau ASCII art.

Objectifs principaux :
- Proposer une partie jouable (joueurs humains et robots).
- Illustrer une architecture POO claire (Actions, Joueurs, Plateau, Cartes).
- Demonstrer la gestion de l etat du jeu, des regles, et de l affichage.

Perimetre :
- Partie complete, jusqu a 15 points de prestige.
- Jetons Or (joker) integres.
- Nobles geres et attribues automatiquement.
- Robots avec strategies simples et avancees.

---

## Chapitre 2 - Lancement du projet

Prerequis :
- Java 8+ (javac et java).
- Fichier `stats.csv` present a la racine du projet.
- Optionnel : `unifont.otf` pour un rendu Unicode stable (sinon police monospace par defaut).

Compilation :
```
javac *.java
```

Execution :
```
java Game
```

Le programme demande :
- Nombre total de joueurs (2 a 4)
- Repartition humains / robots
- Type de robots (Dumb, Rush, SmartRush)
- Noms des joueurs

---

## Chapitre 3 - Regles implementees et invariants

Regles principales :
- Objectif : atteindre 15 points de prestige.
- Un tour = une action par joueur.
- Prendre 2 jetons identiques : possible seulement si 4+ jetons disponibles.
- Prendre 3 jetons differents : possible si chaque type existe.
  - Extension : si moins de 3 types disponibles, on peut prendre 1 ou 2 jetons.
- Acheter une carte :
  - Bonus permanents des cartes reduisent le cout.
  - Jetons Or (GOLD) comblent automatiquement les manques.
- Reserver une carte :
  - Maximum 3 cartes reservees.
  - Donne 1 jeton Or si disponible sur le plateau.
- Limite de jetons : 10 maximum. Au dela, defausse obligatoire.
- Nobles :
  - Obtenus automatiquement apres un achat si conditions remplies.
  - Un seul noble max par tour.
- Fin de partie :
  - Verifiee en fin de tour (equite entre joueurs).
  - Egalite : departage par le plus petit nombre de cartes achetees.

Invariants importants :
- Les quantites de ressources ne deviennent jamais negatives.
- Les piles de cartes sont des structures LIFO.
- Le plateau conserve toujours 4 cartes visibles par tier (ou vide si pile epuisee).

---

## Chapitre 4 - Architecture generale

Composants majeurs :
- `Game` : orchestre la partie, la boucle de jeu et la fin.
- `Board` : etat du plateau (cartes, piles, jetons, nobles).
- `Player` : etat commun a tous les joueurs + logique de base.
- `Action` : contrat unique pour toutes les actions d un tour.
- `Display` / `Displayable` : affichage ASCII art et console interactive.

Flux principal :
```
Game.play()
  pour chaque joueur:
    afficher plateau + joueurs
    action = joueur.chooseAction(board)
    action.process(board, joueur)
    si action = achat: joueur.checkAndObtainNobles(board)
    si jetons > 10: defausse obligatoire
  si un joueur >= 15 points: fin de partie
```

---

## Chapitre 5 - Hierarchie et relations entre classes

### 5.1 Interfaces et classes abstraites

Interface `Action` :
- Implementee par toutes les actions du jeu.

Interface `Displayable` :
- Implementee par les elements affichables en ASCII art.

Classe abstraite `Player` :
- Base commune des joueurs humains et robots.

### 5.2 Hierarchie POO (diagramme ASCII)

```
Displayable
  |-- Board
  |-- Player (abstract)
  |     |-- HumanPlayer
  |     |-- DumbRobotPlayer
  |     |-- RushRobotPlayer
  |     |-- SmartRushRobotPlayer
  |-- DevCard
  |-- Noble

Action
  |-- PickSameTokensAction
  |-- PickDiffTokensAction
  |-- BuyCardAction
  |-- ReserveCardAction
  |-- DiscardTokensAction
  |-- PassAction
```

Relations cles :
- `Game` utilise `Board`, `Player`, `Action`, `Display`.
- `Player` consulte `Board` pour choisir une action.
- `Action.process(...)` modifie `Board` et `Player`.
- `Board` cree `DevCard` et `Noble` a partir du CSV.

---

## Chapitre 6 - Reference detaillee des classes

### 6.1 `Game`

Role : point d entree et chef d orchestre de la partie.

Attributs :
- `display` (static) : instance unique de `Display`.
- `board` : plateau de jeu.
- `players` : liste ordonnee des joueurs.

Methodes :
- `main(String[] args)` : demande le nombre de joueurs, lance la partie, ferme l affichage.
- `Game(int nbOfPlayers)` : configure joueurs, robots, noms, et initialise le plateau.
- `getNbPlayers()` : retourne la taille de la liste des joueurs.
- `play()` : boucle principale (tours complets, fin de partie).
- `move(int currentPlayer)` : gere un tour de joueur (choix action, execution, nobles).
- `discardToken(int currentPlayer)` : applique la regle des 10 jetons.
- `display(int currentPlayer)` : compose l affichage plateau + joueurs.
- `isGameOver()` (private) : vrai si un joueur atteint 15 points.
- `gameOver()` (private) : determine le gagnant et gere les egalites.

### 6.2 `Board`

Role : etat du plateau (piles, cartes visibles, jetons, nobles).

Attributs principaux :
- `stackCards` : 3 piles LIFO (tiers 1, 2, 3).
- `visibleCards` : tableau 3x4 des cartes visibles.
- `resources` : jetons disponibles.
- `visibleNobles` : nobles encore disponibles.
- `nbNoblesSlots` : nombre initial d emplacements nobles.

Methodes publiques :
- `Board(int nbPlayers)` :
  - Charge `stats.csv`, cree cartes et nobles.
  - Melange les piles et revele 4 cartes par tier.
  - Initialise les jetons selon le nombre de joueurs.
- `getNbResource(Resource res)` : quantite d une ressource sur le plateau.
- `setNbResource(Resource res, int nb)` : initialise une ressource.
- `updateNbResource(Resource res, int v)` : modifie une ressource (prise ou paiement).
- `getResources()` : retourne l objet `Resources` du plateau.
- `getCard(int tier, int colonne)` : carte visible a la position demandee.
- `updateCard(DevCard carte)` : remplace une carte achetee (ou met `null`).
- `drawCard(int tier)` : pioche face cachee si la pile n est pas vide.
- `getVisibleNobles()` : liste des nobles disponibles.
- `removeNoble(Noble noble)` : retire un noble apres acquisition.
- `canObtainNoble(Noble noble, Player player)` : verifie bonus de cartes requis.
- `canGiveSameTokens(Resource res)` : 4+ jetons pour prendre 2 identiques.
- `canGiveDiffTokens(List<Resource> list)` : 1 a 3 types disponibles.
- `canDrawPile(int tier)` : true si pile non vide.
- `toStringArray()` : representation ASCII complete (implements Displayable).

Methodes privees d affichage :
- `boardToStringArray()` : compose nobles, piles, cartes, jetons.
- `deckToStringArray(int tier)` : rendu d une pile cachee.
- `resourcesToStringArray()` : rendu des jetons disponibles.

### 6.3 `Player` (abstraite)

Role : base commune des joueurs (etat + logique generique).

Attributs :
- `id`, `name`
- `points`
- `purchasedCards` : cartes achetees (bonus permanents)
- `resources` : jetons possedes
- `purchasedNobles`
- `reservedCards`

Accesseurs et utilitaires :
- `getName()`
- `getPoints()`
- `getNbTokens()` : total de jetons possedes.
- `getNbPurchasedCards()`
- `getPurchasedCards()`
- `getNbResource(Resource res)`
- `getRessources()` : renvoie `Resources` du joueur.
- `getResFromCards(Resource res)` : compte les bonus d une couleur.

Modification de l etat :
- `updateNbResource(Resource res, int v)` : modifie les jetons du joueur.
- `updatePoints(int pts)` : ajoute des points.
- `addPurchasedCard(DevCard card)` : ajoute carte + points.

Achats et nobles :
- `canBuyCard(DevCard card)` :
  - Calcule le manque apres bonus.
  - Utilise les jetons Or comme joker.
- `addPurchasedNoble(Noble noble)` : ajoute le noble + points.
- `getNbPurchasedNobles()`
- `getPurchasedNobles()`
- `checkAndObtainNobles(Board board)` : attribution auto des nobles.

Reservations :
- `getNbReservedCards()`
- `getReservedCards()`
- `addReservedCard(DevCard card)`
- `removeReservedCard(DevCard card)`
- `canReserve()` : max 3.

Methodes abstraites (a implementer) :
- `chooseAction(Board board)`
- `chooseDiscardingTokens()`
- `chooseNoble(List<Noble> eligibleNobles)`

Affichage :
- `toStringArray()` : representation ASCII du joueur.

### 6.4 `HumanPlayer`

Role : interactions avec un joueur humain via la console.

Methodes principales :
- `chooseAction(Board board)` : menu principal + redirections.
- `chooseDiscardingTokens()` : selection interactive des jetons a defausser.
- `chooseNoble(List<Noble>)` : choix interactif en cas de nobles multiples.

Methodes privees :
- `askPickSameTokens(...)` : selection de 2 jetons identiques.
- `askPickDiffTokens(...)` : selection de 1 a 3 jetons differents.
- `askBuyCard(...)` : achat plateau ou reservation.
- `askReserveCard(...)` : reservation carte visible ou face cachee.
- `parseResource(String input)` : conversion D/S/E/R/O -> enum.

### 6.5 `DumbRobotPlayer`

Role : robot tres simple, strategie fixe.

Methodes :
- `chooseAction(Board board)` :
  - Achete reserve -> achete plateau -> prend jetons -> reserve -> passe.
- `chooseDiscardingTokens()` : defausse aleatoire.
- `chooseNoble(List<Noble>)` : prend le premier noble.

### 6.6 `RushRobotPlayer`

Role : strategie "rush" sur une seule couleur cible.

Methodes :
- `chooseAction(Board board)` : priorites strictes (achats 3-5 PV, reservations, jetons).
- `chooseDiscardingTokens()` : conserve les couleurs utiles, defausse le reste.
- `chooseNoble(List<Noble>)` : prend le premier noble.

Methodes privees :
- `countT1OfColor(Resource color)` : nb de T1 produisant la couleur cible.
- `identifyTargetColor(Board board)` :
  - Analyse T2/T3 (3-5 PV) et calcule la couleur la plus demandee.
- `findBestT2ToReserve(...)` : T2 demandant la couleur cible, cout total minimal.
- `findBestT3ToReserve(...)` : T3 demandant 4+ jetons cible, max PV.
- `findBestReservedCard()` : carte reservee avec max points.

### 6.7 `SmartRushRobotPlayer`

Role : version optimisee du Rush, avec priorites dynamiques.

Methodes :
- `chooseAction(Board board)` :
  - Couleur cible fixe + priorites selon besoins reserves.
- `chooseDiscardingTokens()` : defausse intelligente selon besoins futurs.
- `chooseNoble(List<Noble>)` : prend le premier noble.

Methodes privees principales :
- `countT1OfColor(Resource color)`
- `identifyTargetColor(Board board)` :
  - Score par classement sur T2/T3.
  - Bris d egalite par bonus visibles.
- `assignRankingPoints(int[] demands)` : points par rang.
- `breakTieByTierBonuses(Board board, List<Resource> candidates, int tier)`
- `analyzeReservedNeeds()` : besoins = couts reserves - bonus.
- `buildPriorityList(int[] needs, Resource targetColor)`
- `calculateGap(DevCard card)` : ressources manquantes.
- `calculateDiversity(DevCard card)` : nombre de couleurs du cout.
- `findBestT1ToBuy(...)` : selection T1 avec priorites.
- `findLowestCostCard(List<DevCard>)` : cout minimal avec penalite.
- `calculateCostPoints(DevCard card)` : score de cout.
- `findBestCardToReserve(Board board, int tier, Resource targetColor)`
- `findBestReservedCard()`
- `buildTokenPriorities(Board board, Resource targetColor, List<Resource> priorities)`

### 6.8 `Action` (interface)

Role : contrat commun a toutes les actions d un tour.

Methodes :
- `process(Board board, Player player)` : execute l action.
- `toString()` : description textuelle de l action.

### 6.9 Actions concretes

`PickSameTokensAction` :
- Attribut : `resource`.
- `process(...)` : transfere 2 jetons du plateau vers le joueur.
- `toString()` : description simple.

`PickDiffTokensAction` :
- Attribut : `resources` (liste de 1 a 3 ressources).
- `process(...)` : transfere 1 jeton de chaque type.
- `toString()` : description avec symboles.

`BuyCardAction` :
- Attributs : `card`, `fromReserved`.
- `process(...)` :
  - Calcule les jetons Or requis.
  - Paie avec jetons normaux puis Or.
  - Ajoute la carte au joueur.
  - Met a jour le plateau ou les reservations.
- `toString()` : description de la carte achetee.

`ReserveCardAction` :
- Attributs : `card`, `fromDeck`.
- `process(...)` :
  - Ajoute la carte en reservation.
  - Donne 1 Or si disponible.
  - Remplace la carte si elle venait du plateau.
- `toString()` : description de la reservation.

`DiscardTokensAction` :
- Attribut : `toDiscard`.
- `process(...)` : renvoie les jetons au plateau.
- `toString()` : liste des jetons defausses.

`PassAction` :
- `process(...)` : ne modifie rien.
- `toString()` : "Passer le tour".

### 6.10 `DevCard`

Role : carte de developpement (tier 1/2/3).

Attributs :
- `tier`, `cost`, `points`, `resourceType`.

Methodes :
- `getTier()`
- `getCost()`
- `getPoints()`
- `getResourceType()`
- `toStringArray()` : rendu ASCII d une carte.
- `noCardStringArray()` (static) : rendu d emplacement vide.
- `toString()` : resume (points, bonus, cout).

### 6.11 `Noble`

Role : carte noble (3 points, bonus requis).

Attributs :
- `cost` : ressources requises en bonus (cartes), pas en jetons.
- `points`

Methodes :
- `getCost()`
- `getPoints()`
- `toStringArray()` : rendu ASCII du noble.
- `noNobleStringArray()` (static) : emplacement vide.
- `toString()` : resume textuel.

### 6.12 `Resource` (enum)

Valeurs : `DIAMOND`, `SAPPHIRE`, `EMERALD`, `ONYX`, `RUBY`, `GOLD`.

Methodes :
- `toString()` : nom + symbole Unicode (utilise dans l affichage).
- `toSymbol()` : symbole compact (ex: "D" pour diamant avec symbole).

Notes :
- `GOLD` est special, jamais pris directement sur le plateau (sauf reservation).

### 6.13 `Resources`

Role : conteneur de quantites par ressource.

Constructeurs :
- `Resources()` : toutes les quantites a 0.
- `Resources(int diamond, int sapphire, int emerald, int onyx, int ruby)`
- `Resources(int diamond, int sapphire, int emerald, int onyx, int ruby, int gold)`

Methodes :
- `getNbResource(Resource res)`
- `setNbResource(Resource res, int nb)`
- `updateNbResource(Resource res, int v)` : ajoute ou retire (jamais negatif).
- `getAvailableResources()` : ressources avec quantite > 0.
- `toString()` : resume textuel.

### 6.14 `Displayable` (interface)

Role : contrat pour les objets affichables dans la zone plateau.

Methodes :
- `toStringArray()` : retourne un tableau de lignes ASCII.

### 6.15 `Display`

Role : interface Swing avec une zone plateau et une zone console.

API publique principale :
- `Display(int rowsBoard, int rowsConsole, int cols)` : creation fenetre.
- `close()` : ferme la fenetre.
- `getBoardColumns()` / `getBoardRows()` : dimensions.

Utilisation :
- `Game.display.outBoard` : zone plateau.
- `Game.display.out` : zone console.
- `Game.display.in` : entree utilisateur (Readable).

Outils statiques utiles :
- `concatStringArray(...)` : compose des tableaux de lignes.
- `emptyStringArray(...)` : cree un tableau de lignes vides.

Note : la classe contient des utilitaires internes de padding et de largeur.

---

## Chapitre 7 - Flux metier principaux

### 7.1 Tour complet d un joueur
1) `Game.display(...)` affiche plateau + joueurs.
2) `Player.chooseAction(board)` renvoie une `Action`.
3) `Action.process(board, player)` applique les effets.
4) Si achat : `Player.checkAndObtainNobles(board)`.
5) Si jetons > 10 : `Game.discardToken(...)`.

### 7.2 Achat d une carte
- `Player.canBuyCard(card)` verifie jetons + bonus + Or.
- `BuyCardAction.process(...)` effectue le paiement et ajoute la carte.
- `Board.updateCard(card)` remplace la carte visible.

### 7.3 Reservation d une carte
- `ReserveCardAction.process(...)` :
  - Ajoute la carte aux reservations.
  - Donne un Or si disponible.
  - Remplace la carte visible si besoin.

### 7.4 Obtention d un noble
- `Player.checkAndObtainNobles(board)` :
  - Cherche les nobles eligibles.
  - Auto attribution si un seul.
  - Choix si plusieurs.

### 7.5 Defausse obligatoire
- `Game.discardToken(...)` boucle tant que jetons > 10.
- `Player.chooseDiscardingTokens()` renvoie un `Resources` a defausser.
- `DiscardTokensAction.process(...)` retourne les jetons au plateau.

---

## Chapitre 8 - Extension et maintenance

Ajouter une nouvelle action :
1) Creer une classe `XAction implements Action`.
2) Implementer `process` et `toString`.
3) Exposer l action dans `HumanPlayer` ou dans un robot.

Ajouter un nouveau joueur :
1) Creer une classe `XPlayer extends Player`.
2) Implementer `chooseAction`, `chooseDiscardingTokens`, `chooseNoble`.
3) Integrer dans `Game` lors de l initialisation.

Conseils de maintenance :
- Garder `Game.display` comme point unique d affichage.
- Eviter la logique metier dans l affichage.
- Isoler les strategies robots pour faciliter les tests.

---

## Annexes

### A. Format attendu pour `stats.csv`

Lecture dans `Board` :
- Colonne 0 : tier (0 = noble, 1..3 = carte)
- Colonne 1 : cout diamant
- Colonne 2 : cout saphir
- Colonne 3 : cout emeraude
- Colonne 4 : cout rubis
- Colonne 5 : cout onyx
- Colonne 6 : points de prestige
- Colonne 7 : type de ressource (enum Resource, ignore si tier = 0)

Notes :
- Les nobles ont `tier = 0` et un cout exprime en bonus de cartes.
- Les jetons Or ne figurent pas dans le CSV.

### B. Conventions d affichage

- Les symboles Unicode sont utilises pour les ressources et bordures ASCII art.
- `Display` et `Noble` ajustent la largeur visuelle pour ces symboles.

### C. Glossaire

- Bonus : ressource permanente fournie par une carte achetee.
- Jeton : ressource physique temporaire (monnaie).
- Tier : niveau de carte (1, 2, 3).
- Or (GOLD) : jeton joker qui remplace n importe quelle ressource.
