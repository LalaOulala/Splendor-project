import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Joueur robot avec une stratégie simple et prévisible.
 * 
 * Ce robot applique une stratégie fixe dans un ordre de priorité strict :
 * 1. Acheter n'importe quelle carte achetable (en commençant par le niveau le plus élevé)
 * 2. Prendre 2 jetons identiques (première ressource disponible)
 * 3. Prendre 3 jetons différents (les 3 premières ressources disponibles)
 * 4. Passer son tour
 * 
 * Cette stratégie est qualifiée de "stupide" car :
 * - Elle ne planifie pas ses achats (achète la première carte possible)
 * - Elle ne s'adapte pas à la situation du jeu
 * - Elle ne considère pas quelles cartes donnent le plus de points
 * - Elle ne bloque pas les adversaires
 * - Elle défausse des jetons au hasard sans réflexion stratégique
 * 
 * Un bon joueur analyserait quelles cartes sont accessibles, lesquelles donnent
 * le plus de points, et quels bonus sont les plus utiles pour la suite.
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class DumbRobotPlayer extends Player {
    
    /**
     * Constructeur.
     * Appelle le constructeur parent pour initialiser l'identité et l'état du joueur.
     * 
     * @param id identifiant unique du robot (0 à 3)
     * @param name nom du robot (généralement "Robot 1", "Robot 2", etc.)
     */
    public DumbRobotPlayer(int id, String name) {
        super(id, name);
    }
    
    /**
     * Choisit une action selon la stratégie du robot stupide.
     * 
     * Applique un ordre de priorité strict et prévisible :
     * 1. Acheter une carte (priorité aux niveaux élevés : 3 > 2 > 1)
     * 2. Prendre 2 jetons identiques (première ressource ayant 4+ jetons)
     * 3. Prendre 3 jetons différents (les 3 premières ressources disponibles)
     * 4. Passer son tour (si aucune action n'est possible)
     * 
     * Cette stratégie ne cherche pas à optimiser : elle prend la première action
     * possible sans analyser si c'est le meilleur choix.
     * 
     * @param board le plateau de jeu (pour consulter les cartes et jetons disponibles)
     * @return l'action choisie selon la stratégie
     */
    @Override
    public Action chooseAction(Board board) {
        
        // ========== PRIORITÉ 1 : Essayer d'acheter une carte ==========
        // On commence par les cartes de plus haut niveau (3 > 2 > 1)
        for (int tier = 3; tier >= 1; tier--) {
            for (int col = 0; col < 4; col++) {
                DevCard card = board.getCard(tier, col);
                
                // Vérifier que la carte existe et qu'on peut l'acheter
                if (card != null && canBuyCard(card)) {
                    return new BuyCardAction(card);
                }
            }
        }
        
        // ========== PRIORITÉ 2 : Prendre 2 jetons identiques ==========
        for (Resource res : Resource.values()) {
            if (board.canGiveSameTokens(res)) {
                return new PickSameTokensAction(res);
            }
        }
        
        // ========== PRIORITÉ 3 : Prendre 3 jetons différents ==========
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
        
        // ========== PRIORITÉ 4 : Passer son tour ==========
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
        Random random = new Random();
        
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
}
