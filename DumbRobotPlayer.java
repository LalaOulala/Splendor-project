import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Joueur robot avec une stratégie simple et prévisible.
 * 
 * Ce robot applique une stratégie fixe dans un ordre de priorité strict :
 * 1. Acheter une carte réservée (si possible)
 * 2. Acheter n'importe quelle carte achetable (en commençant par le niveau le plus élevé)
 * 3. Prendre 2 jetons identiques (première ressource disponible)
 * 4. Prendre 3 jetons différents (les 3 premières ressources disponibles)
 * 5. Réserver au hasard une carte T1
 * 6. Passer son tour
 * 
 * Cette stratégie est qualifiée de "stupide" car :
 * - Elle ne planifie pas ses achats (achète la première carte possible)
 * - Elle ne s'adapte pas à la situation du jeu
 * - Elle ne considère pas quelles cartes donnent le plus de points
 * - Elle ne bloque pas les adversaires
 * - Elle défausse des jetons au hasard sans réflexion stratégique
 * - Elle réserve au hasard sans analyser l'intérêt des cartes
 * 
 * Un bon joueur analyserait quelles cartes sont accessibles, lesquelles donnent
 * le plus de points, et quels bonus sont les plus utiles pour la suite.
 * 
 * @author FONFREIDE Quentin & TAHAR Elias
 * @version 04/01/2026
 */
public class DumbRobotPlayer extends Player {
    
    /**
     * Générateur aléatoire pour les choix aléatoires (réservation T1, défausse).
     */
    private Random random;
    
    /**
     * Constructeur.
     * Appelle le constructeur parent pour initialiser l'identité et l'état du joueur.
     * 
     * @param id identifiant unique du robot (0 à 3)
     * @param name nom du robot (généralement "Robot 1", "Robot 2", etc.)
     */
    public DumbRobotPlayer(int id, String name) {
        super(id, name);
        this.random = new Random();
    }
    
    /**
     * Choisit une action selon la stratégie du robot stupide.
     * 
     * Applique un ordre de priorité strict et prévisible :
     * 1. Acheter une carte réservée (priorité aux cartes déjà sécurisées)
     * 2. Acheter une carte (priorité aux niveaux élevés : 3 > 2 > 1)
     * 3. Prendre 2 jetons identiques (première ressource ayant 4+ jetons)
     * 4. Prendre 3 jetons différents (les 3 premières ressources disponibles)
     * 5. Réserver au hasard une carte T1
     * 6. Passer son tour (si aucune action n'est possible)
     * 
     * Cette stratégie ne cherche pas à optimiser : elle prend la première action
     * possible sans analyser si c'est le meilleur choix.
     * 
     * @param board le plateau de jeu (pour consulter les cartes et jetons disponibles)
     * @return l'action choisie selon la stratégie
     */
    @Override
    public Action chooseAction(Board board) {
        
        // ========== PRIORITÉ 1 : Acheter une carte réservée ==========
        // Parcourir les cartes réservées et acheter la première achetable
        List<DevCard> reserved = getReservedCards();
        
        for (DevCard card : reserved) {
            if (canBuyCard(card)) {
                return new BuyCardAction(card, true);
            }
        }
        
        // ========== PRIORITÉ 2 : Essayer d'acheter une carte du plateau ==========
        // On commence par les cartes de plus haut niveau (3 > 2 > 1)
        for (int tier = 3; tier >= 1; tier--) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(tier, col);
                
                // Vérifier que la carte existe et qu'on peut l'acheter
                if (card != null && canBuyCard(card)) {
                    return new BuyCardAction(card, false);
                }
            }
        }
        
        // ========== PRIORITÉ 3 : Prendre 2 jetons identiques ==========
        for (Resource res : Resource.values()) {
            if (board.canGiveSameTokens(res)) {
                return new PickSameTokensAction(res);
            }
        }
        
        // ========== PRIORITÉ 4 : Prendre 3 jetons différents ==========
        List<Resource> availableRes = board.getResources().getAvailableResources();
        
        if (availableRes.size() >= 3) {
            // Prendre les 3 premières ressources disponibles
            List<Resource> chosen = new ArrayList<>();
            chosen.add(availableRes.get(0));
            chosen.add(availableRes.get(1));
            chosen.add(availableRes.get(2));
            
            if (board.canGiveDiffTokens(chosen)) {
                return new PickDiffTokensAction(chosen);
            }
        }
        
        // ========== PRIORITÉ 5 : Réserver au hasard une carte T1 ==========
        // Si le robot peut réserver (< 3 cartes réservées)
        if (canReserve()) {
            // Collecter toutes les cartes T1 disponibles
            List<DevCard> t1Cards = new ArrayList<>();
            
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(1, col);
                if (card != null) {
                    t1Cards.add(card);
                }
            }
            
            // Si des cartes T1 existent, en réserver une au hasard
            if (!t1Cards.isEmpty()) {
                DevCard randomT1 = t1Cards.get(random.nextInt(t1Cards.size()));
                return new ReserveCardAction(randomT1, false);
            }
        }
        
        // ========== PRIORITÉ 6 : Passer son tour ==========
        return new PassAction();
    }
    
    /**
     * Choisit aléatoirement les jetons à défausser pour revenir à 10 jetons.
     * 
     * Cette méthode défausse des jetons un par un de manière complètement aléatoire
     * jusqu'à atteindre la limite de 10 jetons. C'est une stratégie non optimale :
     * un bon joueur garderait les jetons les plus utiles selon ses cartes visées.
     * 
     * Note : cette méthode modifie temporairement les ressources du joueur pendant
     * le calcul (pour éviter de défausser plus de jetons qu'on en possède).
     * 
     * @return un objet Resources contenant les quantités de chaque type à défausser
     */
    @Override
    public Resources chooseDiscardingTokens() {
        int totalTokens = getNbTokens();
        int toRemove = totalTokens - 10;
        
        Resources discard = new Resources();
        
        // Défausser aléatoirement jusqu'à avoir 10 jetons
        while (toRemove > 0) {
            // Récupérer les ressources disponibles
            List<Resource> available = getRessources().getAvailableResources();
            
            // Choisir une ressource au hasard
            Resource res = available.get(random.nextInt(available.size()));
            
            // Défausser 1 jeton de cette ressource
            discard.updateNbResource(res, 1);
            
            // Mettre à jour temporairement pour le prochain tour de boucle
            updateNbResource(res, -1);
            toRemove--;
        }
        
        return discard;
    }
    
    /**
     * Choisit un noble parmi plusieurs nobles éligibles selon la stratégie du robot stupide.
     * 
     * Le robot applique la stratégie la plus simple possible : il choisit toujours
     * le premier noble de la liste (index 0), sans réfléchir ni analyser les coûts
     * ou les avantages futurs.
     * 
     * Cette méthode est appelée uniquement quand le robot devient éligible pour
     * plusieurs nobles en même temps après avoir acheté une carte.
     * 
     * Stratégie :
     * - Pas d'analyse des nobles
     * - Pas de comparaison des coûts
     * - Choix systématique du premier élément de la liste
     * 
     * @param eligibleNobles Liste des nobles pour lesquels le robot est éligible.
     *                       Taille toujours >= 2 (sinon cette méthode n'est pas appelée)
     * @return Le premier noble de la liste (eligibleNobles.get(0))
     */
    @Override
    protected Noble chooseNoble(List<Noble> eligibleNobles) {
        // Le robot prend toujours le premier noble (le plus simple)
        return eligibleNobles.get(0);
    }
}
