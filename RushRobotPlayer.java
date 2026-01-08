import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Joueur robot implémentant la stratégie "Rush" du jeu Splendor.
 * 
 * CONCEPT DE LA STRATÉGIE RUSH
 * 
 * La stratégie Rush est une approche agressive et ultra-concentrée sur une seule couleur.
 * Le robot identifie rapidement la couleur la plus demandée par les cartes à haut prestige
 * (T2/T3 avec 3-5 PV), puis monopolise cette couleur pour acheter ces cartes et atteindre
 * 15 points le plus rapidement possible.
 * 
 * PRINCIPE DIRECTEUR : FOCUS TOTAL sur UNE SEULE couleur
 * 
 * Étape 1 : Identification de la couleur cible
 * Parcourir les cartes T2 et T3 ayant 3 à 5 points de prestige.
 * Compter la QUANTITÉ TOTALE de jetons demandée pour chaque couleur.
 * La couleur avec le total le plus élevé devient la COULEUR CIBLE.
 * Cette couleur est CONSERVÉE toute la partie.
 * 
 * Exemple : Si les cartes T2/T3 demandent 18 rubis, 12 émeraudes, 8 saphirs...
 * → Couleur cible = RUBY (fixée pour toute la partie)
 * 
 * Étape 2 : Réservations stratégiques (2-3 d'affilée)
 * 
 * Ordre des réservations :
 * 1. T2 demandant la couleur cible (les plus accessibles, 3+ PV)
 * 2. T1 PRODUISANT la couleur cible (pour générer les ressources)
 * 3. T3 demandant massivement la couleur cible (5 PV)
 * 
 * Exemple de séquence :
 * - Tour 1 : Réserver T2 coûtant 6 rouges (3 PV)
 * - Tour 2 : Réserver T1 produisant des rouges
 * - Tour 3 : Réserver T3 coûtant 7 rouges (5 PV)
 * 
 * Étape 3 : Achats et accumulation
 * 
 * Priorités d'achat :
 * 1. Acheter cartes réservées (si possible)
 * 2. Acheter T2/T3 avec 3-5 PV demandant la couleur cible (focus rush)
 * 3. Acheter 2 T1 produisant la couleur cible (générateurs essentiels)
 * 4. Réserver agressivement (T2 → T1 → T3)
 * 5. Acheter des T1 supplémentaires si vraiment rien d'autre (priorité basse)
 * 6. Prendre des jetons de la couleur cible
 * 
 * RÈGLE IMPORTANTE : En fin de partie, le rush vise à avoir essentiellement
 * des cartes 3-5 PV et peu de T1 (2-3 suffisent pour générer les ressources).
 * 
 * DIFFÉRENCES AVEC DUMBROBOT
 * 
 * - DumbRobot : achète la première carte disponible sans stratégie
 * - RushRobot : se concentre sur UNE couleur conservée et vise les cartes 3-5 PV
 * - DumbRobot : ne réserve que si obligé
 * - RushRobot : réserve agressivement 2-3 fois de suite pour bloquer adversaires
 * - DumbRobot : achète beaucoup de T1
 * - RushRobot : limite les T1 (2 prioritaires, puis priorité basse)
 * 
 * @author FONFREIDE Quentin & TAHAR Elias
 * @version 03/01/2026
 */
public class RushRobotPlayer extends Player {
    
    // ==================== CONSTANTES ====================
    
    /**
     * Nombre de T1 de la couleur cible à acheter en priorité.
     * 
     * Le rush recommande 2-3 T1 maximum pour générer les bonus essentiels.
     * Au-delà de ce seuil, l'achat de T1 devient une priorité BASSE (après réservations).
     */
    private static final int T1_PRIORITY_THRESHOLD = 2;
    
    
    // ==================== ATTRIBUTS ====================
    
    /**
     * Générateur aléatoire pour les choix aléatoires (défausse, bris d'égalité).
     */
    private Random random;
    
    /**
     * Couleur cible de la stratégie Rush.
     * 
     * Cette couleur est déterminée au premier tour et CONSERVÉE toute la partie.
     * Elle représente la couleur la plus demandée par les cartes à haut prestige
     * (T2/T3 avec 3-5 PV) et guide tous les choix stratégiques du robot.
     * 
     * Valeur null tant que le premier tour n'est pas joué.
     */
    private Resource targetColor;
    
    
    // ==================== CONSTRUCTEUR ====================
    
    /**
     * Crée un nouveau joueur robot avec la stratégie Rush.
     * 
     * La couleur cible est initialisée à null et sera calculée au premier tour.
     * 
     * @param id identifiant unique du joueur (0 à 3)
     * @param name nom du joueur robot
     */
    public RushRobotPlayer(int id, String name) {
        super(id, name);
        this.random = new Random();
        this.targetColor = null;  // Sera calculée au premier tour
    }
    
    
    // ==================== MÉTHODE PRINCIPALE : chooseAction ====================
    
    /**
     * Choisit l'action à effectuer selon la stratégie Rush.
     * 
     * ALGORITHME DE DÉCISION
     * 
     * 1. Si premier tour : identifier et fixer la couleur cible (permanent)
     * 2. Vérifier les priorités dans l'ordre strict
     * 3. Retourner la première action exécutable
     * 
     * ORDRE DES PRIORITÉS (RUSH OPTIMISÉ)
     * 
     * 1. Acheter une carte réservée (priorité absolue)
     * 2. Acheter une grosse carte (T2/T3) avec 3-5 PV demandant la couleur cible
     * 3. Acheter une T1 produisant la couleur cible (SI < 2 T1 de cette couleur)
     * 4. Réserver une T2 demandant la couleur cible (3+ PV, coût faible)
     * 5. Réserver une T1 produisant la couleur cible
     * 6. Réserver une T3 demandant la couleur cible (5 PV)
     * 7. Réserver n'importe quelle grosse carte >= 3 PV (fallback)
     * 8. Acheter une T1 produisant la couleur cible (PRIORITÉ BASSE, si >= 2 déjà)
     * 9. Acheter n'importe quelle T1 (fallback très bas)
     * 10. Prendre 2 jetons identiques (couleur cible en priorité)
     * 11. Prendre 3 jetons différents (couleur cible en priorité)
     * 12. Passer son tour (dernier recours)
     * 
     * @param board le plateau de jeu contenant toutes les informations du jeu
     * @return une Action valide et exécutable immédiatement
     */
    @Override
    public Action chooseAction(Board board) {
        
        // ========== ANALYSE STRATÉGIQUE : IDENTIFIER LA COULEUR CIBLE ==========
        // La couleur cible est calculée UNE SEULE FOIS au premier tour
        if (this.targetColor == null) {
            this.targetColor = identifyTargetColor(board);
            
            if (this.targetColor != null) {
                Game.display.out.println("[" + this.getName() + "] Couleur cible choisie : " + 
                    this.targetColor.toSymbol() + " (conservée toute la partie)");
            }
        }
        
        // Récupérer les cartes réservées du joueur
        List<DevCard> reserved = this.getReservedCards();
        
        
        // ========== PRIORITÉ 1 : ACHETER UNE CARTE RÉSERVÉE ==========
        // Les cartes réservées ne peuvent pas être volées par les adversaires
        // C'est donc la priorité absolue si elles sont achetables
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
        // Prioriser celles demandant la couleur cible
        for (int tier = 2; tier <= 3; tier++) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(tier, col);
                
                // Filtrer : carte achetable + 3-5 PV (stratégie rush)
                if (card != null && this.canBuyCard(card) && 
                    card.getPoints() >= 3 && card.getPoints() <= 5) {
                    
                    Resources cost = card.getCost();
                    
                    // Vérifier si la carte demande la couleur cible
                    if (this.targetColor != null && cost.getNbResource(this.targetColor) > 0) {
                        Game.display.out.println(this.getName() + " achète une grosse carte T" + 
                            tier + " (3-5 PV) demandant " + this.targetColor.toSymbol() + 
                            " (" + card.getPoints() + " PV).");
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
                        tier + " (3-5 PV) (" + card.getPoints() + " PV).");
                    return new BuyCardAction(card, false);
                }
            }
        }
        
        
        // ========== PRIORITÉ 3 : ACHETER UNE CARTE T1 (PRIORITÉ HAUTE) ==========
        // RUSH : Acheter les 2 premières T1 de la couleur cible en PRIORITÉ HAUTE
        // Ces cartes sont essentielles pour générer les bonus nécessaires
        int nbT1ColoreCible = countT1OfColor(this.targetColor);
        
        if (this.targetColor != null && nbT1ColoreCible < T1_PRIORITY_THRESHOLD) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(1, col);
                if (card != null && this.canBuyCard(card)) {
                    if (card.getResourceType() == this.targetColor) {
                        Game.display.out.println(this.getName() + " achète une T1 produisant " + 
                            this.targetColor.toSymbol() + " [" + (nbT1ColoreCible + 1) + 
                            "/" + T1_PRIORITY_THRESHOLD + " prioritaires].");
                        return new BuyCardAction(card, false);
                    }
                }
            }
        }
        
        
        // ========== PRIORITÉ 4 : RÉSERVER UNE CARTE T2 ==========
        // Réserver les T2 demandant la couleur cible avec coût minimal
        // Les T2 sont prioritaires car plus accessibles que les T3
        if (this.canReserve() && this.targetColor != null) {
            DevCard bestT2 = findBestT2ToReserve(board, this.targetColor);
            if (bestT2 != null) {
                Game.display.out.println(this.getName() + " réserve une T2 demandant " + 
                    this.targetColor.toSymbol() + " (" + bestT2.getPoints() + " PV).");
                return new ReserveCardAction(bestT2, false);
            }
        }
        
        
        // ========== PRIORITÉ 5 : RÉSERVER UNE CARTE T1 ==========
        // Réserver les T1 produisant la couleur cible
        // Cela permet de sécuriser les générateurs de ressources
        if (this.canReserve() && this.targetColor != null) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(1, col);
                if (card != null && card.getResourceType() == this.targetColor) {
                    Game.display.out.println(this.getName() + " réserve une T1 produisant " + 
                        this.targetColor.toSymbol() + ".");
                    return new ReserveCardAction(card, false);
                }
            }
        }
        
        
        // ========== PRIORITÉ 6 : RÉSERVER UNE CARTE T3 ==========
        // Réserver les T3 demandant massivement la couleur cible
        // Les T3 donnent 5 PV et permettent de terminer la partie rapidement
        if (this.canReserve() && this.targetColor != null) {
            DevCard bestT3 = findBestT3ToReserve(board, this.targetColor);
            if (bestT3 != null) {
                Game.display.out.println(this.getName() + " réserve une T3 demandant " + 
                    this.targetColor.toSymbol() + " (" + bestT3.getPoints() + " PV).");
                return new ReserveCardAction(bestT3, false);
            }
        }
        
        
        // ========== PRIORITÉ 7 : RÉSERVER N'IMPORTE QUELLE GROSSE CARTE ==========
        // Fallback : réserver au moins une grosse carte >= 3 PV même si elle n'est pas de la couleur cible
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
        // On peut en acheter d'autres si vraiment rien d'autre n'est possible (avant jetons)
        // Rationale : 2-3 T1 suffisent pour générer les bonus, le rush se concentre sur les 3-5 PV
        if (this.targetColor != null && nbT1ColoreCible >= T1_PRIORITY_THRESHOLD) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(1, col);
                if (card != null && this.canBuyCard(card)) {
                    if (card.getResourceType() == this.targetColor) {
                        Game.display.out.println(this.getName() + " achète une T1 produisant " + 
                            this.targetColor.toSymbol() + " [priorité basse, " + 
                            (nbT1ColoreCible + 1) + " T1 total].");
                        return new BuyCardAction(card, false);
                    }
                }
            }
        }
        
        
        // ========== PRIORITÉ 9 : ACHETER N'IMPORTE QUELLE T1 ==========
        // Fallback très bas : acheter n'importe quelle T1 si vraiment rien d'autre
        for (int col = 0; col < 4; col++) {
            DevCard card = board.getCard(1, col);
            if (card != null && this.canBuyCard(card)) {
                Game.display.out.println(this.getName() + " achète une T1 (" + 
                    card.getResourceType().toSymbol() + ") [fallback].");
                return new BuyCardAction(card, false);
            }
        }
        
        
        // ========== PRIORITÉ 10 : PRENDRE 2 JETONS IDENTIQUES ==========
        // Prendre 2 jetons de la couleur cible en priorité
        List<Resource> available = board.getResources().getAvailableResources();
        
        if (this.targetColor != null && available.contains(this.targetColor) && 
            board.canGiveSameTokens(this.targetColor)) {
            Game.display.out.println(this.getName() + " prend 2 jetons " + 
                this.targetColor.toSymbol() + ".");
            return new PickSameTokensAction(this.targetColor);
        }
        
        // Si la couleur cible n'est pas disponible, prendre n'importe quelle paire
        for (Resource res : available) {
            if (res != Resource.GOLD && board.canGiveSameTokens(res)) {
                Game.display.out.println(this.getName() + " prend 2 jetons " + res.toSymbol());
                return new PickSameTokensAction(res);
            }
        }
        
        
        // ========== PRIORITÉ 11 : PRENDRE 3 JETONS DIFFÉRENTS ==========
        // Prendre 3 jetons différents avec la couleur cible en priorité
        if (available.size() >= 3) {
            List<Resource> chosen = new ArrayList<>();
            
            // Ajouter la couleur cible en premier si disponible
            if (this.targetColor != null && available.contains(this.targetColor)) {
                chosen.add(this.targetColor);
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
        
        
        // ========== PRIORITÉ 12 : PASSER LE TOUR ==========
        // Dernier recours si aucune action n'est possible
        Game.display.out.println(this.getName() + " passe son tour.");
        return new PassAction();
    }
    
    
    // ==================== CHOIX DU NOBLE ====================
    
    /**
     * Choisit quel noble obtenir parmi plusieurs nobles éligibles.
     * 
     * STRATÉGIE
     * 
     * Tous les nobles donnent 3 points de prestige, donc le choix importe peu.
     * Le robot choisit simplement le premier noble de la liste.
     * 
     * CONTEXTE D'APPEL
     * 
     * Cette méthode n'est appelée que si le robot devient éligible pour
     * PLUSIEURS nobles simultanément après avoir acheté une carte.
     * Si un seul noble est éligible, il est attribué automatiquement.
     * 
     * @param eligibleNobles liste des nobles pour lesquels le robot est éligible
     *                       (contient toujours au moins 2 éléments)
     * @return le premier noble de la liste
     */
    @Override
    protected Noble chooseNoble(List<Noble> eligibleNobles) {
        return eligibleNobles.get(0);
    }
    
    
    // ==================== MÉTHODES PRIVÉES D'ANALYSE ====================
    
    /**
     * Compte le nombre de cartes T1 possédées produisant une couleur donnée.
     * 
     * OBJECTIF
     * 
     * Déterminer combien de T1 de la couleur cible le robot possède déjà.
     * Cela permet d'appliquer la règle du rush : limiter les T1 (2-3 suffisent).
     * 
     * UTILISATION
     * 
     * Cette méthode est utilisée pour décider si l'achat de T1 doit être en priorité
     * haute (< 2 T1) ou en priorité basse (>= 2 T1).
     * 
     * EXEMPLE
     * 
     * Robot possède : 2 T1 rubis, 1 T1 émeraude, 1 T2 rubis
     * countT1OfColor(RUBY) → retourne 2
     * countT1OfColor(EMERALD) → retourne 1
     * countT1OfColor(SAPPHIRE) → retourne 0
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
     * Identifie la couleur cible de la stratégie Rush.
     * 
     * OBJECTIF
     * 
     * Trouver la couleur la plus demandée par les cartes à haut prestige (T2/T3 avec 3-5 PV).
     * Cette couleur devient le FOCUS TOTAL de la stratégie et est CONSERVÉE toute la partie.
     * 
     * ALGORITHME
     * 
     * 1. Parcourir toutes les cartes T2 et T3 du plateau visible
     * 2. Filtrer celles ayant 3 à 5 points de prestige (cartes rush)
     * 3. Pour chaque couleur, compter la QUANTITÉ TOTALE de jetons demandée
     * 4. Retourner la couleur avec le total maximum
     * 
     * EXEMPLE
     * 
     * Plateau T2/T3 avec 3-5 PV :
     * - Carte 1 : 7 rubis, 3 PV
     * - Carte 2 : 5 rubis, 3 PV
     * - Carte 3 : 6 rubis, 5 PV
     * - Carte 4 : 4 émeraudes, 3 PV
     * 
     * Total par couleur :
     * - RUBY : 7 + 5 + 6 = 18
     * - EMERALD : 4
     * 
     * → Couleur cible = RUBY (18 > 4)
     * → Cette couleur sera conservée toute la partie
     * 
     * CAS PARTICULIER
     * 
     * Si aucune carte T2/T3 avec 3-5 PV n'est visible, retourne null.
     * Le robot adoptera alors un comportement moins spécialisé.
     * 
     * @param board le plateau de jeu contenant les cartes visibles
     * @return la couleur cible, ou null si aucune carte éligible
     */
    private Resource identifyTargetColor(Board board) {
        
        // Tableau pour compter la quantité totale de jetons demandée par couleur
        int[] totalDemand = new int[5];  // Index 0-4 : DIAMOND, SAPPHIRE, EMERALD, RUBY, ONYX
        
        // Parcourir les cartes T2 et T3
        for (int tier = 2; tier <= 3; tier++) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(tier, col);
                
                // RUSH : Filtrer uniquement les cartes avec 3-5 PV (cartes à haut prestige)
                if (card == null || card.getPoints() < 3 || card.getPoints() > 5) {
                    continue;
                }
                
                // Compter la quantité de chaque ressource demandée
                Resources cost = card.getCost();
                for (Resource res : Resource.values()) {
                    if (res != Resource.GOLD) {
                        totalDemand[res.ordinal()] += cost.getNbResource(res);
                    }
                }
            }
        }
        
        // Trouver la couleur avec la demande maximale
        int maxDemand = 0;
        Resource targetColor = null;
        
        for (int i = 0; i < 5; i++) {
            if (totalDemand[i] > maxDemand) {
                maxDemand = totalDemand[i];
                targetColor = Resource.values()[i];
            }
        }
        
        return targetColor;
    }
    
    /**
     * Trouve la meilleure carte T2 à réserver.
     * 
     * OBJECTIF
     * 
     * Réserver une carte T2 demandant la couleur cible, avec le coût le plus faible.
     * Les T2 sont prioritaires car plus accessibles que les T3.
     * 
     * CRITÈRES DE SÉLECTION
     * 
     * 1. La carte doit être de tier 2
     * 2. La carte doit avoir au moins 3 points de prestige (stratégie rush)
     * 3. La carte doit demander la couleur cible (au moins 1 jeton)
     * 4. Parmi les cartes éligibles, choisir celle avec le coût total le plus faible
     * 
     * RATIONALE DU COÛT MINIMAL
     * 
     * Une carte avec un coût faible sera plus rapidement achetable, ce qui permet
     * de progresser plus vite vers les 15 points (objectif du rush).
     * 
     * EXEMPLE
     * 
     * Couleur cible : RUBY
     * Cartes T2 disponibles :
     * - Carte A : 6 rubis, 2 émeraudes (3 PV) → coût total = 8
     * - Carte B : 5 rubis, 1 saphir (3 PV) → coût total = 6
     * - Carte C : 4 émeraudes (3 PV) → pas de rubis, ignorée
     * 
     * → Retourne Carte B (coût total minimal = 6)
     * 
     * @param board le plateau de jeu
     * @param targetColor la couleur cible conservée
     * @return la meilleure carte T2 à réserver, ou null si aucune
     */
    private DevCard findBestT2ToReserve(Board board, Resource targetColor) {
        
        DevCard best = null;
        int minCost = Integer.MAX_VALUE;
        
        // Parcourir les cartes T2
        for (int col = 0; col < 4; col++) {
            DevCard card = board.getCard(2, col);
            
            // RUSH : Vérifier les critères d'éligibilité (>= 3 PV)
            if (card == null || card.getPoints() < 3) {
                continue;
            }
            
            Resources cost = card.getCost();
            
            // Vérifier si la carte demande la couleur cible
            if (cost.getNbResource(targetColor) == 0) {
                continue;
            }
            
            // Calculer le coût total de la carte
            int totalCost = 0;
            for (Resource res : Resource.values()) {
                if (res != Resource.GOLD) {
                    totalCost += cost.getNbResource(res);
                }
            }
            
            // Garder la carte avec le coût minimal
            if (totalCost < minCost) {
                minCost = totalCost;
                best = card;
            }
        }
        
        return best;
    }
    
    /**
     * Trouve la meilleure carte T3 à réserver.
     * 
     * OBJECTIF
     * 
     * Réserver une carte T3 demandant massivement la couleur cible.
     * Les T3 donnent généralement 5 PV et permettent de terminer rapidement.
     * 
     * CRITÈRES DE SÉLECTION
     * 
     * 1. La carte doit être de tier 3
     * 2. La carte doit avoir au moins 3 points de prestige
     * 3. La carte doit demander au moins 4 jetons de la couleur cible (usage "massif")
     * 4. Parmi les cartes éligibles, choisir celle avec le plus de PV
     * 
     * RATIONALE DU SEUIL 4 JETONS
     * 
     * Une T3 demandant 4+ jetons de la couleur cible justifie vraiment le rush sur
     * cette couleur. Cela garantit que l'accumulation de bonus sera bien utilisée.
     * 
     * EXEMPLE
     * 
     * Couleur cible : RUBY
     * Cartes T3 disponibles :
     * - Carte A : 7 rubis (5 PV) → OK
     * - Carte B : 6 rubis (4 PV) → OK
     * - Carte C : 3 rubis (3 PV) → pas assez de rubis, ignorée
     * 
     * → Retourne Carte A (5 PV > 4 PV)
     * 
     * @param board le plateau de jeu
     * @param targetColor la couleur cible conservée
     * @return la meilleure carte T3 à réserver, ou null si aucune
     */
    private DevCard findBestT3ToReserve(Board board, Resource targetColor) {
        
        DevCard best = null;
        int maxPoints = 0;
        
        // Parcourir les cartes T3
        for (int col = 0; col < 4; col++) {
            DevCard card = board.getCard(3, col);
            
            // RUSH : Vérifier les critères d'éligibilité (>= 3 PV)
            if (card == null || card.getPoints() < 3) {
                continue;
            }
            
            Resources cost = card.getCost();
            
            // Vérifier si la carte demande massivement la couleur cible (4+ jetons)
            if (cost.getNbResource(targetColor) < 4) {
                continue;
            }
            
            // Garder la carte avec le plus de PV
            if (card.getPoints() > maxPoints) {
                maxPoints = card.getPoints();
                best = card;
            }
        }
        
        return best;
    }
    
    /**
     * Trouve la meilleure carte réservée à acheter.
     * 
     * OBJECTIF
     * 
     * Identifier la carte réservée qui maximise les points de prestige.
     * 
     * CRITÈRE DE SÉLECTION
     * 
     * La carte avec le plus de points de prestige est prioritaire.
     * Cela rapproche directement de l'objectif (15 points = victoire).
     * 
     * RATIONALE
     * 
     * Dans le rush, chaque point compte. Acheter une carte à 5 PV plutôt qu'une à 3 PV
     * permet de gagner 2 tours (2 achats économisés).
     * 
     * EXEMPLE
     * 
     * Cartes réservées :
     * - Carte A : 3 PV
     * - Carte B : 5 PV
     * - Carte C : 1 PV
     * 
     * → Retourne Carte B (5 PV > 3 PV > 1 PV)
     * 
     * @return la carte réservée avec le maximum de points, ou null si aucune réservation
     */
    private DevCard findBestReservedCard() {
        
        List<DevCard> reserved = this.getReservedCards();
        
        if (reserved.isEmpty()) {
            return null;
        }
        
        // Trouver la carte avec le maximum de points
        DevCard best = reserved.get(0);
        for (DevCard card : reserved) {
            if (card.getPoints() > best.getPoints()) {
                best = card;
            }
        }
        
        return best;
    }
    
    
    // ==================== GESTION DE LA DÉFAUSSE ====================
    
    /**
     * Choisit quels jetons défausser quand le joueur dépasse 10 jetons.
     * 
     * RÈGLE DU JEU
     * 
     * Selon les règles de Splendor, un joueur ne peut pas avoir plus de 10 jetons.
     * Si cette limite est dépassée après une prise de jetons, le joueur doit
     * immédiatement défausser les jetons en excès.
     * 
     * STRATÉGIE DE DÉFAUSSE
     * 
     * Le robot défausse intelligemment pour conserver les jetons les plus utiles :
     * 
     * 1. Analyser les besoins futurs (cartes réservées uniquement)
     * 2. Pour chaque couleur, compter combien de jetons seront nécessaires
     * 3. Soustraire les bonus déjà possédés (réduisent les besoins)
     * 4. Identifier les couleurs les moins demandées
     * 5. Défausser en priorité les jetons de ces couleurs
     * 6. Si plusieurs couleurs ont la même utilité, choisir aléatoirement
     * 
     * RATIONALE
     * 
     * Dans le rush, conserver les jetons de la couleur cible et des couleurs
     * nécessaires pour les cartes réservées est crucial. Défausser les autres
     * couleurs permet de rester flexible tout en avançant vers l'objectif.
     * 
     * EXEMPLE
     * 
     * Jetons du robot : 4 rubis, 3 saphirs, 2 émeraudes, 2 diamants, 1 onyx (total 12)
     * Bonus possédés : 2 rubis
     * Cartes réservées demandent : 8 rubis, 4 saphirs, 3 émeraudes, 1 diamant, 0 onyx
     * 
     * Besoins après bonus :
     * - RUBY : 8 - 2 = 6 (très utile)
     * - SAPPHIRE : 4 (utile)
     * - EMERALD : 3 (moyennement utile)
     * - DIAMOND : 1 (peu utile)
     * - ONYX : 0 (inutile)
     * 
     * → Défausser 2 jetons : 1 onyx, puis 1 diamant
     * 
     * @return un objet Resources contenant les quantités de chaque type à défausser
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
        // (la vraie défausse sera effectuée par le système de jeu)
        for (Resource res : Resource.values()) {
            int defaussed = discard.getNbResource(res);
            if (defaussed > 0) {
                this.updateNbResource(res, defaussed);
            }
        }
        
        return discard;
    }
}
