import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Classe DumbRobotPlayer
 * Représente un joueur robot avec une stratégie simple et prévisible
 * Stratégie : acheter une carte > prendre 2 jetons identiques > prendre 3 jetons différents > passer
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public class DumbRobotPlayer extends Player {
    
    /**
     * Constructeur
     * @param id identifiant du joueur
     * @param name nom du robot
     */
    public DumbRobotPlayer(int id, String name) {
        super(id, name);
    }
    
    /**
     * Choisit une action selon la stratégie du robot stupide
     * Ordre de priorité :
     * 1. Acheter une carte (niveau 3 > 2 > 1)
     * 2. Prendre 2 jetons identiques
     * 3. Prendre 3 jetons différents
     * 4. Passer son tour
     * 
     * @param board le plateau de jeu
     * @return l'action choisie
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
     * Choisit aléatoirement les jetons à défausser pour revenir à 10 jetons
     * @return les ressources à défausser
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
