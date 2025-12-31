/**
 * Action BuyCardAction
 * Acheter une carte de développement
 * Le joueur paie le coût (en tenant compte des bonus), prend la carte, et gagne ses points
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public class BuyCardAction implements Action {
    
    private DevCard card;  // La carte à acheter
    
    /**
     * Constructeur
     * @param card la carte de développement à acheter
     */
    public BuyCardAction(DevCard card) {
        this.card = card;
    }
    
    /**
     * Exécute l'action : paie le coût, prend la carte, gagne les points, remplace la carte sur le plateau
     * @param board le plateau de jeu
     * @param player le joueur qui effectue l'action
     */
    @Override
    public void process(Board board, Player player) {
        Resources cost = card.getCost();
        
        // Pour chaque type de ressource
        for (Resource res : Resource.values()) {
            int required = cost.getNbResource(res);        // Coût demandé
            int bonus = player.getResFromCards(res);       // Bonus des cartes possédées
            int toPay = Math.max(0, required - bonus);     // Ce qu'il faut vraiment payer
            
            if (toPay > 0) {
                // Retirer les jetons du joueur
                player.updateNbResource(res, -toPay);
                
                // Remettre les jetons sur le plateau
                board.updateNbResource(res, toPay);
            }
        }
        
        // Le joueur prend la carte (ajoute automatiquement les points)
        player.addPurchasedCard(card);
        
        // Remplacer la carte sur le plateau
        board.updateCard(card);
    }
    
    /**
     * Représentation textuelle
     * @return description de l'action
     */
    @Override
    public String toString() {
        return "Acheter " + card.toString();
    }
}
