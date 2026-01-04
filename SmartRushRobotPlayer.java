import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Joueur robot implémentant la stratégie "Smart Rush" du jeu Splendor.
 * 
 * CONCEPT DE LA STRATÉGIE SMART RUSH
 * 
 * La stratégie Smart Rush est une version optimisée de la stratégie Rush classique.
 * Elle identifie UNE SEULE couleur cible au début de la partie et s'y concentre
 * exclusivement tout au long du jeu. Les priorités secondaires (besoins des cartes
 * réservées) sont recalculées dynamiquement à chaque tour.
 * 
 * PRINCIPE FONDAMENTAL : FOCUS TOTAL sur la couleur cible
 * 
 * Contrairement au Rush classique qui peut changer de couleur, le Smart Rush
 * reste fidèle à sa couleur cible initiale. Cela permet :
 * - Une accumulation cohérente de ressources
 * - Des réservations alignées sur un objectif unique
 * - Une progression rapide vers l'achat des cartes à haut prestige (3-5 PV)
 * 
 * INNOVATIONS PRINCIPALES
 * 
 * 1. Couleur cible unique et permanente
 * - Calculée UNE SEULE FOIS au premier tour via système de points
 * - Combine l'analyse des T2/T3 (3-5 PV) et des nobles
 * - Conservée dans un attribut pour toute la partie
 * - Bris d'égalité : bonus T1 → T2 → aléatoire
 * 
 * 2. Priorités dynamiques
 * - Recalculées à chaque tour selon les cartes réservées
 * - Besoins = coûts des réservations - bonus possédés
 * - Liste ordonnée du plus au moins nécessaire
 * 
 * 3. Réservations basées sur l'écart
 * - Privilégie les cartes de la couleur cible avec écart minimal
 * - Favorise la diversité des coûts (2+2+2 > 6)
 * 
 * 4. Achats optimisés avec filtres Rush
 * - Couleur cible → besoins prioritaires → coût minimal
 * - Filtrage 3-5 PV pour les grosses cartes (stratégie rush)
 * - Limite intelligente sur les T1 (2 en priorité haute, puis priorité basse)
 * - Système de points : 1→+1, 2→+2, 3→+5, 4→+6, 5→+7
 * - Pénalité saturation : +10 points si 5+ bonus d'une couleur
 * 
 * 5. Jetons intelligents
 * - Par défaut : 3 jetons différents (diversification)
 * - Exception : 2 jetons si couleur très dominante (cible = 1ère priorité + 4+ disponibles)
 * 
 * @author FONFREIDE Quentin
 * @version 04/01/2026
 */
public class SmartRushRobotPlayer extends Player {
    
    // ==================== CONSTANTES ====================
    
    /**
     * Nombre de T1 de la couleur cible à acheter en priorité.
     * 
     * Le rush recommande 2-3 T1 maximum pour générer les bonus essentiels.
     * Au-delà de ce seuil, l'achat de T1 devient une priorité BASSE (après réservations).
     */
    private static final int T1_PRIORITY_THRESHOLD = 2;
    
    /**
     * Pénalité de coût appliquée aux T1 produisant une couleur saturée (5+ bonus).
     * 
     * Cette pénalité rend ces cartes moins prioritaires sans les éliminer complètement.
     * Valeur recommandée : 10 points (équivalent à ~2 cartes normales)
     */
    private static final int SATURATED_COLOR_PENALTY = 10;
    
    
    // ==================== ATTRIBUTS ====================
    
    /** 
     * Générateur aléatoire pour les bris d'égalité.
     * Utilisé lors du choix de la couleur cible et de la défausse.
     */
    private Random random;
    
    /** 
     * Couleur cible de la stratégie Smart Rush.
     * 
     * Cette couleur est déterminée au premier tour et CONSERVÉE toute la partie.
     * Elle guide tous les choix stratégiques : réservations, achats, jetons.
     * 
     * Valeur null tant que le premier tour n'est pas joué.
     */
    private Resource targetColor;
    
    
    // ==================== CONSTRUCTEUR ====================
    
    /**
     * Crée un nouveau joueur robot avec la stratégie Smart Rush.
     * 
     * La couleur cible est initialisée à null et sera calculée au premier tour.
     * 
     * @param id identifiant unique du joueur (0 à 3)
     * @param name nom du joueur robot
     */
    public SmartRushRobotPlayer(int id, String name) {
        super(id, name);
        this.random = new Random();
        this.targetColor = null;  // Sera calculée au premier tour
    }
    
    
    // ==================== MÉTHODE PRINCIPALE : chooseAction ====================
    
    /**
     * Choisit l'action à effectuer selon la stratégie Smart Rush.
     * 
     * ALGORITHME DE DÉCISION
     * 
     * PHASE 1 : Analyses stratégiques
     * 1. Si premier tour : identifier et fixer la couleur cible (permanent)
     * 2. Analyser les besoins des cartes réservées (dynamique, chaque tour)
     * 3. Construire la liste de priorités basée sur ces besoins
     * 
     * PHASE 2 : Exécution des priorités (Rush optimisé)
     * 1. Acheter une carte réservée (meilleur PV)
     * 2. Acheter une grosse carte (T2/T3) 3-5 PV de la couleur cible
     * 3. Acheter une T1 optimisée (SI < 2 T1 de la couleur cible)
     * 4. Réserver une T2 de la couleur cible (écart minimal, >= 3 PV)
     * 5. Réserver une T1 produisant la couleur cible
     * 6. Réserver une T3 de la couleur cible (écart minimal, >= 3 PV)
     * 7. Réserver n'importe quelle grosse carte >= 3 PV (fallback)
     * 8. Acheter une T1 optimisée (PRIORITÉ BASSE, si >= 2 déjà)
     * 9. Prendre 3 jetons différents (priorités)
     * 10. Prendre 2 jetons identiques (si couleur dominante)
     * 11. Passer son tour
     * 
     * @param board le plateau de jeu contenant toutes les informations
     * @return une Action valide et exécutable immédiatement
     */
    @Override
    public Action chooseAction(Board board) {
        
        // ========== PHASE 1 : ANALYSES STRATÉGIQUES ==========
        
        // ÉTAPE 1 : Identifier la couleur cible (UNE SEULE FOIS au premier tour)
        if (this.targetColor == null) {
            this.targetColor = identifyTargetColor(board);
        }
        
        // ÉTAPE 2 : Analyser les besoins des cartes réservées (coût - bonus)
        // Ceci est recalculé à chaque tour pour s'adapter aux changements
        int[] reservedNeeds = analyzeReservedNeeds();
        
        // ÉTAPE 3 : Construire la liste de priorités basée sur les besoins
        // La couleur cible reste prioritaire, mais les autres couleurs sont triées dynamiquement
        List<Resource> priorityColors = buildPriorityList(reservedNeeds, this.targetColor);
        
        // Récupérer les cartes réservées
        List<DevCard> reserved = this.getReservedCards();
        
        
        // ========== PRIORITÉ 1 : ACHETER UNE CARTE RÉSERVÉE ==========
        if (!reserved.isEmpty()) {
            DevCard bestReserved = findBestReservedCard();
            if (bestReserved != null && this.canBuyCard(bestReserved)) {
                Game.display.out.println(this.getName() + " achète une carte réservée (" + 
                    bestReserved.getPoints() + " PV).");
                return new BuyCardAction(bestReserved, true);
            }
        }
        
        
        // ========== PRIORITÉ 2 : ACHETER UNE GROSSE CARTE (T2/T3) 3-5 PV ==========
        // RUSH : Acheter uniquement les cartes T2/T3 avec 3-5 PV (haut prestige)
        for (int tier = 2; tier <= 3; tier++) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(tier, col);
                
                // Filtrer : carte achetable + 3-5 PV (stratégie rush)
                if (card != null && this.canBuyCard(card) && 
                    card.getPoints() >= 3 && card.getPoints() <= 5) {
                    
                    Resources cost = card.getCost();
                    
                    // Prioriser celles demandant la couleur cible
                    if (this.targetColor != null && cost.getNbResource(this.targetColor) > 0) {
                        Game.display.out.println(this.getName() + " achète une grosse carte T" + 
                            tier + " (" + card.getPoints() + " PV).");
                        return new BuyCardAction(card, false);
                    }
                }
            }
        }
        
        // Si aucune carte 3-5 PV avec couleur cible, acheter n'importe quelle T2/T3 de 3-5 PV
        for (int tier = 2; tier <= 3; tier++) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(tier, col);
                
                if (card != null && this.canBuyCard(card) && 
                    card.getPoints() >= 3 && card.getPoints() <= 5) {
                    Game.display.out.println(this.getName() + " achète une grosse carte T" + 
                        tier + " (" + card.getPoints() + " PV).");
                    return new BuyCardAction(card, false);
                }
            }
        }
        
        
        // ========== PRIORITÉ 3 : ACHETER UNE CARTE T1 (PRIORITÉ HAUTE) ==========
        // RUSH : Acheter les 2 premières T1 optimisées en PRIORITÉ HAUTE
        int nbT1ColoreCible = countT1OfColor(this.targetColor);
        
        if (nbT1ColoreCible < T1_PRIORITY_THRESHOLD) {
            DevCard bestT1 = findBestT1ToBuy(board, this.targetColor, priorityColors);
            if (bestT1 != null && this.canBuyCard(bestT1)) {
                Game.display.out.println(this.getName() + " achète une T1 produisant " + 
                    bestT1.getResourceType().toSymbol() + ".");
                return new BuyCardAction(bestT1, false);
            }
        }
        
        
        // ========== PRIORITÉ 4 : RÉSERVER UNE CARTE T2 ==========
        if (this.canReserve() && this.targetColor != null) {
            DevCard bestT2 = findBestCardToReserve(board, 2, this.targetColor);
            if (bestT2 != null) {
                Game.display.out.println(this.getName() + " réserve une T2 (" + 
                    bestT2.getPoints() + " PV).");
                return new ReserveCardAction(bestT2, false);
            }
        }
        
        
        // ========== PRIORITÉ 5 : RÉSERVER UNE CARTE T1 ==========
        if (this.canReserve() && this.targetColor != null) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(1, col);
                if (card != null && card.getResourceType() == this.targetColor) {
                    Game.display.out.println(this.getName() + " réserve une T1.");
                    return new ReserveCardAction(card, false);
                }
            }
        }
        
        
        // ========== PRIORITÉ 6 : RÉSERVER UNE CARTE T3 ==========
        if (this.canReserve() && this.targetColor != null) {
            DevCard bestT3 = findBestCardToReserve(board, 3, this.targetColor);
            if (bestT3 != null) {
                Game.display.out.println(this.getName() + " réserve une T3 (" + 
                    bestT3.getPoints() + " PV).");
                return new ReserveCardAction(bestT3, false);
            }
        }
        
        
        // ========== PRIORITÉ 7 : RÉSERVER N'IMPORTE QUELLE GROSSE CARTE ==========
        // RUSH : Filtrer >= 3 PV pour rester cohérent avec la stratégie
        if (this.canReserve()) {
            for (int tier = 2; tier <= 3; tier++) {
                for (int col = 0; col < 4; col++) {
                    DevCard card = board.getCard(tier, col);
                    if (card != null && card.getPoints() >= 3) {
                        Game.display.out.println(this.getName() + " réserve une carte T" + 
                            tier + " (" + card.getPoints() + " PV).");
                        return new ReserveCardAction(card, false);
                    }
                }
            }
        }
        
        
        // ========== PRIORITÉ 8 : ACHETER UNE CARTE T1 (PRIORITÉ BASSE) ==========
        // RUSH : Après 2 T1 de la couleur cible, l'achat de T1 devient une priorité BASSE
        if (nbT1ColoreCible >= T1_PRIORITY_THRESHOLD) {
            DevCard bestT1 = findBestT1ToBuy(board, this.targetColor, priorityColors);
            if (bestT1 != null && this.canBuyCard(bestT1)) {
                Game.display.out.println(this.getName() + " achète une T1 produisant " + 
                    bestT1.getResourceType().toSymbol() + ".");
                return new BuyCardAction(bestT1, false);
            }
        }
        
        
        // ========== PRIORITÉ 9 : PRENDRE 3 JETONS DIFFÉRENTS ==========
        List<Resource> available = board.getResources().getAvailableResources();
        List<Resource> tokenPriorities = buildTokenPriorities(board, this.targetColor, priorityColors);
        
        if (available.size() >= 3) {
            List<Resource> chosen = new ArrayList<>();
            
            // Ajouter les couleurs prioritaires d'abord
            for (Resource priority : tokenPriorities) {
                if (available.contains(priority) && !chosen.contains(priority) && chosen.size() < 3) {
                    chosen.add(priority);
                }
            }
            
            // Compléter avec d'autres ressources disponibles
            for (Resource res : available) {
                if (res != Resource.GOLD && !chosen.contains(res) && chosen.size() < 3) {
                    chosen.add(res);
                }
            }
            
            if (board.canGiveDiffTokens(chosen)) {
                Game.display.out.println(this.getName() + " prend " + chosen.size() + 
                    " jetons différents.");
                return new PickDiffTokensAction(chosen);
            }
        }
        
        
        // ========== PRIORITÉ 10 : PRENDRE 2 JETONS IDENTIQUES ==========
        // EXCEPTION : prendre 2 jetons identiques si la couleur prioritaire est très dominante
        
        boolean isDominant = false;
        Resource dominantColor = null;
        
        if (this.targetColor != null && !tokenPriorities.isEmpty()) {
            Resource firstPriority = tokenPriorities.get(0);
            if (this.targetColor == firstPriority) {
                isDominant = true;
                dominantColor = this.targetColor;
            }
        }
        
        // Si une couleur dominante existe et qu'il y a 4+ jetons disponibles, prendre 2 jetons
        if (isDominant && dominantColor != null && available.contains(dominantColor) && 
            board.canGiveSameTokens(dominantColor)) {
            Game.display.out.println(this.getName() + " prend 2 jetons " + 
                dominantColor.toSymbol() + ".");
            return new PickSameTokensAction(dominantColor);
        }
        
        // Sinon, essayer de prendre 2 jetons d'une autre couleur prioritaire
        for (Resource priority : tokenPriorities) {
            if (available.contains(priority) && board.canGiveSameTokens(priority)) {
                Game.display.out.println(this.getName() + " prend 2 jetons " + 
                    priority.toSymbol() + ".");
                return new PickSameTokensAction(priority);
            }
        }
        
        
        // ========== PRIORITÉ 11 : PASSER LE TOUR ==========
        Game.display.out.println(this.getName() + " passe son tour.");
        return new PassAction();
    }
    
    
    // ==================== CHOIX DU NOBLE ====================
    
    /**
     * Choisit quel noble obtenir parmi plusieurs nobles éligibles.
     * 
     * Tous les nobles donnent 3 points de prestige, donc le choix importe peu.
     * Le robot choisit simplement le premier noble de la liste.
     * 
     * @param eligibleNobles liste des nobles éligibles (contient toujours au moins 2 éléments)
     * @return le premier noble de la liste
     */
    @Override
    protected Noble chooseNoble(List<Noble> eligibleNobles) {
        return eligibleNobles.get(0);
    }
    
    
    // ==================== GESTION DE LA DÉFAUSSE ====================
    
    /**
     * Choisit quels jetons défausser quand le joueur dépasse 10 jetons.
     * 
     * STRATÉGIE DE DÉFAUSSE
     * 
     * 1. Analyser les besoins futurs (cartes réservées uniquement)
     * 2. Pour chaque couleur, compter combien de jetons seront nécessaires
     * 3. Identifier les couleurs les moins demandées
     * 4. Défausser en priorité les jetons de ces couleurs
     * 5. Si plusieurs couleurs ont la même utilité, choisir aléatoirement
     * 
     * @return un objet Resources contenant les quantités à défausser pour chaque type
     */
    @Override
    public Resources chooseDiscardingTokens() {
        
        Resources discard = new Resources();
        int totalTokens = this.getNbTokens();
        int toRemove = totalTokens - 10;
        
        Game.display.out.println(this.getName() + " doit défausser " + toRemove + " jetons.");
        
        // Analyser les besoins futurs (cartes réservées uniquement)
        int[] futureNeeds = new int[5];
        
        for (DevCard card : this.getReservedCards()) {
            Resources cost = card.getCost();
            for (Resource res : Resource.values()) {
                if (res != Resource.GOLD) {
                    futureNeeds[res.ordinal()] += cost.getNbResource(res);
                }
            }
        }
        
        // Soustraire les bonus déjà possédés (réduisent les besoins)
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD) {
                int bonus = this.getResFromCards(res);
                futureNeeds[res.ordinal()] = Math.max(0, futureNeeds[res.ordinal()] - bonus);
            }
        }
        
        // Défausser les jetons les moins utiles un par un
        while (toRemove > 0) {
            
            // Trouver le minimum de demandes parmi les jetons possédés
            int minNeeds = Integer.MAX_VALUE;
            for (int i = 0; i < 5; i++) {
                if (this.getNbResource(Resource.values()[i]) > 0) {
                    minNeeds = Math.min(minNeeds, futureNeeds[i]);
                }
            }
            
            // Lister les ressources avec le minimum de demandes
            List<Resource> leastUseful = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                Resource res = Resource.values()[i];
                if (this.getNbResource(res) > 0 && futureNeeds[i] == minNeeds) {
                    leastUseful.add(res);
                }
            }
            
            // Si aucune ressource disponible, sortir
            if (leastUseful.isEmpty()) {
                break;
            }
            
            // Choisir aléatoirement parmi les ressources les moins utiles
            Resource toDiscard = leastUseful.get(random.nextInt(leastUseful.size()));
            
            // Défausser ce jeton
            discard.updateNbResource(toDiscard, 1);
            this.updateNbResource(toDiscard, -1);
            futureNeeds[toDiscard.ordinal()]--;
            toRemove--;
        }
        
        // Restaurer les jetons temporairement retirés
        for (Resource res : Resource.values()) {
            int defaussed = discard.getNbResource(res);
            if (defaussed > 0) {
                this.updateNbResource(res, defaussed);
            }
        }
        
        return discard;
    }
    
    
    // ==================== MÉTHODES PRIVÉES D'ANALYSE ====================
    
    /**
     * Compte le nombre de cartes T1 possédées produisant une couleur donnée.
     * 
     * Cette méthode est utilisée pour décider si l'achat de T1 doit être en priorité
     * haute (< 2 T1) ou en priorité basse (>= 2 T1).
     * 
     * @param color la couleur à rechercher
     * @return le nombre de T1 possédées produisant cette couleur
     */
    private int countT1OfColor(Resource color) {
        if (color == null) {
            return 0;
        }
        
        int count = 0;
        for (DevCard card : this.getPurchasedCards()) {
            if (card.getTier() == 1 && card.getResourceType() == color) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Identifie la couleur cible optimale avec système de points.
     * 
     * Combine l'analyse des T2/T3 (3-5 PV) et des nobles pour déterminer
     * la couleur la plus stratégique.
     * 
     * ALGORITHME
     * 
     * 1. Analyser T2/T3 (3-5 PV) : attribuer points par classement
     * 2. Analyser nobles : attribuer points par classement
     * 3. Additionner et choisir la couleur avec le plus de points
     * 4. Bris d'égalité : bonus T1 → T2 → aléatoire
     * 
     * @param board le plateau de jeu à analyser
     * @return la couleur cible optimale, ou null si aucune carte éligible
     */
    private Resource identifyTargetColor(Board board) {
        
        // Compter la quantité totale de jetons demandée par couleur (T2/T3 avec 3-5 PV)
        int[] t2t3Demand = new int[5];
        
        for (int tier = 2; tier <= 3; tier++) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(tier, col);
                
                // RUSH : Filtrer uniquement les cartes avec 3-5 PV
                if (card == null || card.getPoints() < 3 || card.getPoints() > 5) {
                    continue;
                }
                
                Resources cost = card.getCost();
                for (Resource res : Resource.values()) {
                    if (res != Resource.GOLD) {
                        t2t3Demand[res.ordinal()] += cost.getNbResource(res);
                    }
                }
            }
        }
        
        // Attribuer des points selon le classement T2/T3
        int[] t2t3Points = assignRankingPoints(t2t3Demand);
        
        // Nobles (TODO : implémenter si board.getNobles() disponible)
        int[] noblesPoints = new int[5];
        
        // Additionner les points
        int[] totalPoints = new int[5];
        for (int i = 0; i < 5; i++) {
            totalPoints[i] = t2t3Points[i] + noblesPoints[i];
        }
        
        // Trouver le maximum de points
        int maxPoints = 0;
        for (int i = 0; i < 5; i++) {
            if (totalPoints[i] > maxPoints) {
                maxPoints = totalPoints[i];
            }
        }
        
        if (maxPoints == 0) {
            return null;
        }
        
        // Lister les couleurs avec le maximum de points
        List<Resource> topColors = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (totalPoints[i] == maxPoints) {
                topColors.add(Resource.values()[i]);
            }
        }
        
        if (topColors.size() == 1) {
            return topColors.get(0);
        }
        
        // Bris d'égalité
        Resource tieBreaker = breakTieByTierBonuses(board, topColors, 1);
        if (tieBreaker != null) {
            return tieBreaker;
        }
        
        tieBreaker = breakTieByTierBonuses(board, topColors, 2);
        if (tieBreaker != null) {
            return tieBreaker;
        }
        
        return topColors.get(random.nextInt(topColors.size()));
    }
    
    /**
     * Attribue des points selon le classement des demandes.
     * 
     * Trie du plus au moins demandé : 1er=5pts, 2e=4pts, 3e=3pts, 4e=2pts, 5e=1pt
     * 
     * @param demands tableau de demandes par couleur
     * @return tableau de points par couleur
     */
    private int[] assignRankingPoints(int[] demands) {
        
        int[] points = new int[5];
        int[] rankingPoints = {5, 4, 3, 2, 1};
        
        for (int rank = 0; rank < 5; rank++) {
            int maxDemand = -1;
            int maxIndex = -1;
            
            for (int i = 0; i < 5; i++) {
                if (demands[i] > maxDemand && points[i] == 0) {
                    maxDemand = demands[i];
                    maxIndex = i;
                }
            }
            
            if (maxIndex != -1) {
                points[maxIndex] = rankingPoints[rank];
            }
        }
        
        return points;
    }
    
    /**
     * Brise l'égalité entre plusieurs couleurs en regardant les bonus d'un tier.
     * 
     * Compte combien de cartes visibles produisent chaque couleur candidate.
     * La couleur la plus produite gagne.
     * 
     * @param board le plateau de jeu
     * @param candidates liste des couleurs à égalité
     * @param tier le tier à analyser (1, 2 ou 3)
     * @return la couleur gagnante, ou null si l'égalité n'est pas brisée
     */
    private Resource breakTieByTierBonuses(Board board, List<Resource> candidates, int tier) {
        
        int[] bonusCounts = new int[5];
        
        for (int col = 0; col < 4; col++) {
            DevCard card = board.getCard(tier, col);
            if (card != null) {
                Resource bonus = card.getResourceType();
                if (candidates.contains(bonus)) {
                    bonusCounts[bonus.ordinal()]++;
                }
            }
        }
        
        int maxCount = 0;
        Resource winner = null;
        
        for (Resource candidate : candidates) {
            int count = bonusCounts[candidate.ordinal()];
            if (count > maxCount) {
                maxCount = count;
                winner = candidate;
            }
        }
        
        // Vérifier unicité du gagnant
        int winnersCount = 0;
        for (Resource candidate : candidates) {
            if (bonusCounts[candidate.ordinal()] == maxCount) {
                winnersCount++;
            }
        }
        
        if (winnersCount > 1) {
            return null;
        }
        
        return winner;
    }
    
    /**
     * Analyse les besoins des cartes réservées.
     * 
     * Calcule pour chaque couleur : besoin = coût total - bonus possédés
     * 
     * @return tableau des besoins par couleur
     */
    private int[] analyzeReservedNeeds() {
        
        int[] needs = new int[5];
        
        for (DevCard card : this.getReservedCards()) {
            Resources cost = card.getCost();
            for (Resource res : Resource.values()) {
                if (res != Resource.GOLD) {
                    needs[res.ordinal()] += cost.getNbResource(res);
                }
            }
        }
        
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD) {
                int bonus = this.getResFromCards(res);
                needs[res.ordinal()] = Math.max(0, needs[res.ordinal()] - bonus);
            }
        }
        
        return needs;
    }
    
    /**
     * Construit une liste de priorités des couleurs selon les besoins.
     * 
     * Couleur cible toujours en tête, suivie des autres triées par besoin décroissant.
     * 
     * @param needs tableau des besoins par couleur
     * @param targetColor la couleur cible (toujours prioritaire)
     * @return liste ordonnée des couleurs par priorité décroissante
     */
    private List<Resource> buildPriorityList(int[] needs, Resource targetColor) {
        
        List<Resource> priorities = new ArrayList<>();
        
        if (targetColor != null) {
            priorities.add(targetColor);
        }
        
        for (int rank = 0; rank < 5; rank++) {
            int maxNeed = -1;
            Resource bestRes = null;
            
            for (int i = 0; i < 5; i++) {
                Resource res = Resource.values()[i];
                if (needs[i] > maxNeed && !priorities.contains(res)) {
                    maxNeed = needs[i];
                    bestRes = res;
                }
            }
            
            if (bestRes != null) {
                priorities.add(bestRes);
            }
        }
        
        return priorities;
    }
    
    /**
     * Calcule l'écart entre le coût d'une carte et les possessions du joueur.
     * 
     * Écart = somme des ressources manquantes (jetons + bonus)
     * 
     * @param card la carte à analyser
     * @return l'écart (nombre total de ressources manquantes)
     */
    private int calculateGap(DevCard card) {
        
        int gap = 0;
        Resources cost = card.getCost();
        
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD) {
                int needed = cost.getNbResource(res);
                int owned = this.getNbResource(res) + this.getResFromCards(res);
                gap += Math.max(0, needed - owned);
            }
        }
        
        return gap;
    }
    
    /**
     * Calcule la diversité du coût d'une carte.
     * 
     * Diversité = nombre de couleurs différentes demandées
     * 
     * @param card la carte à analyser
     * @return le nombre de couleurs différentes demandées (1 à 5)
     */
    private int calculateDiversity(DevCard card) {
        
        int diversity = 0;
        Resources cost = card.getCost();
        
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD && cost.getNbResource(res) > 0) {
                diversity++;
            }
        }
        
        return diversity;
    }
    
    
    // ==================== MÉTHODES DE SÉLECTION ====================
    
    /**
     * Trouve la meilleure carte T1 à acheter.
     * 
     * Priorité 1 : T1 couleur cible
     * Priorité 2 : T1 couleurs prioritaires (besoins réservations)
     * Priorité 3 : Coût minimal (système de points + pénalité saturation)
     * 
     * @param board le plateau de jeu
     * @param targetColor la couleur cible
     * @param priorityColors liste des couleurs prioritaires
     * @return la meilleure T1 à acheter, ou null
     */
    private DevCard findBestT1ToBuy(Board board, Resource targetColor, List<Resource> priorityColors) {
        
        List<DevCard> targetColorT1 = new ArrayList<>();
        List<DevCard> priorityT1 = new ArrayList<>();
        List<DevCard> otherT1 = new ArrayList<>();
        
        for (int col = 0; col < 4; col++) {
            DevCard card = board.getCard(1, col);
            if (card == null) {
                continue;
            }
            
            Resource bonus = card.getResourceType();
            
            if (targetColor != null && bonus == targetColor) {
                targetColorT1.add(card);
            } else if (priorityColors.contains(bonus)) {
                priorityT1.add(card);
            } else {
                otherT1.add(card);
            }
        }
        
        DevCard best = findLowestCostCard(targetColorT1);
        if (best != null) {
            return best;
        }
        
        best = findLowestCostCard(priorityT1);
        if (best != null) {
            return best;
        }
        
        return findLowestCostCard(otherT1);
    }
    
    /**
     * Trouve la carte avec le coût le plus faible selon le système de points.
     * 
     * Système de points : 1→1pt, 2→2pts, 3→5pts, 4→6pts, 5→7pts
     * Pénalité saturation : +10pts si la carte produit une couleur avec 5+ bonus
     * 
     * @param cards liste des cartes candidates
     * @return la carte avec le coût minimal, ou null
     */
    private DevCard findLowestCostCard(List<DevCard> cards) {
        
        if (cards.isEmpty()) {
            return null;
        }
        
        DevCard best = null;
        int minCostPoints = Integer.MAX_VALUE;
        
        for (DevCard card : cards) {
            int costPoints = calculateCostPoints(card);
            
            // PÉNALITÉ : +10 si couleur saturée (5+ bonus)
            Resource producedColor = card.getResourceType();
            int currentBonusCount = this.getResFromCards(producedColor);
            
            if (currentBonusCount >= 5) {
                costPoints += SATURATED_COLOR_PENALTY;
            }
            
            if (costPoints < minCostPoints) {
                minCostPoints = costPoints;
                best = card;
            }
        }
        
        return best;
    }
    
    /**
     * Calcule les points de coût d'une carte selon le système de points.
     * 
     * 1 jeton→1pt, 2→2pts, 3→5pts, 4→6pts, 5→7pts, 6+→7+(qté-5)pts
     * 
     * @param card la carte à évaluer
     * @return le nombre total de points de coût
     */
    private int calculateCostPoints(DevCard card) {
        
        int points = 0;
        Resources cost = card.getCost();
        int[] pointsTable = {0, 1, 2, 5, 6, 7};
        
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD) {
                int quantity = cost.getNbResource(res);
                
                if (quantity > 0 && quantity <= 5) {
                    points += pointsTable[quantity];
                } else if (quantity > 5) {
                    points += 7 + (quantity - 5);
                }
            }
        }
        
        return points;
    }
    
    /**
     * Trouve la meilleure carte à réserver dans un tier spécifique.
     * 
     * Critères : tier spécifié, >= 3 PV, demande couleur cible, écart minimal
     * 
     * @param board le plateau de jeu
     * @param tier le tier à analyser (2 ou 3)
     * @param targetColor la couleur cible
     * @return la meilleure carte à réserver, ou null
     */
    private DevCard findBestCardToReserve(Board board, int tier, Resource targetColor) {
        
        DevCard best = null;
        int minGap = Integer.MAX_VALUE;
        int maxDiversity = 0;
        
        for (int col = 0; col < 4; col++) {
            DevCard card = board.getCard(tier, col);
            
            // RUSH : Filtrer >= 3 PV
            if (card == null || card.getPoints() < 3) {
                continue;
            }
            
            Resources cost = card.getCost();
            
            // Vérifier si la carte demande la couleur cible
            if (cost.getNbResource(targetColor) == 0) {
                continue;
            }
            
            int gap = calculateGap(card);
            int diversity = calculateDiversity(card);
            
            if (gap < minGap || (gap == minGap && diversity > maxDiversity)) {
                minGap = gap;
                maxDiversity = diversity;
                best = card;
            }
        }
        
        return best;
    }
    
    /**
     * Trouve la meilleure carte réservée à acheter.
     * 
     * Choisit celle avec le plus de points de prestige.
     * 
     * @return la carte réservée avec le maximum de points, ou null
     */
    private DevCard findBestReservedCard() {
        
        List<DevCard> reserved = this.getReservedCards();
        
        if (reserved.isEmpty()) {
            return null;
        }
        
        DevCard best = reserved.get(0);
        for (DevCard card : reserved) {
            if (card.getPoints() > best.getPoints()) {
                best = card;
            }
        }
        
        return best;
    }
    
    /**
     * Construit une liste de priorités pour les jetons à prendre.
     * 
     * Priorités : jetons manquants pour T1 cible → couleur cible → besoins réservations → autres
     * 
     * @param board le plateau de jeu
     * @param targetColor la couleur cible
     * @param priorityColors les couleurs prioritaires
     * @return liste ordonnée des couleurs prioritaires pour les jetons
     */
    private List<Resource> buildTokenPriorities(Board board, Resource targetColor,
                                                 List<Resource> priorityColors) {
        
        List<Resource> priorities = new ArrayList<>();
        
        // PRIORITÉ 1 : Jetons manquants pour acheter T1 de la couleur cible
        if (targetColor != null) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(1, col);
                
                if (card != null && card.getResourceType() == targetColor && !this.canBuyCard(card)) {
                    Resources cost = card.getCost();
                    for (Resource res : Resource.values()) {
                        if (res != Resource.GOLD) {
                            int needed = cost.getNbResource(res);
                            int owned = this.getNbResource(res) + this.getResFromCards(res);
                            
                            if (needed > owned && !priorities.contains(res)) {
                                priorities.add(res);
                            }
                        }
                    }
                }
            }
        }
        
        // PRIORITÉ 2 : Couleur cible elle-même
        if (targetColor != null && !priorities.contains(targetColor)) {
            priorities.add(targetColor);
        }
        
        // PRIORITÉ 3 : Couleurs prioritaires (besoins réservations)
        for (Resource priority : priorityColors) {
            if (!priorities.contains(priority)) {
                priorities.add(priority);
            }
        }
        
        // PRIORITÉ 4 : Autres couleurs
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD && !priorities.contains(res)) {
                priorities.add(res);
            }
        }
        
        return priorities;
    }
}
