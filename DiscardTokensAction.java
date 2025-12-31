/**
 * Action DiscardTokensAction
 * Défausser des jetons quand le joueur en possède plus de 10
 * Les jetons défaussés retournent sur le plateau
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public class DiscardTokensAction implements Action {
    
    private Resources toDiscard;  // Les ressources à défausser
    
    /**
     * Constructeur
     * @param toDiscard les ressources à défausser
     */
    public DiscardTokensAction(Resources toDiscard) {
        this.toDiscard = toDiscard;
    }
    
    /**
     * Exécute l'action : retire les jetons du joueur et les remet sur le plateau
     * @param board le plateau de jeu
     * @param player le joueur qui effectue l'action
     */
    @Override
    public void process(Board board, Player player) {
        // Pour chaque type de ressource
        for (Resource res : Resource.values()) {
            int nb = toDiscard.getNbResource(res);
            
            if (nb > 0) {
                // Retirer les jetons du joueur
                player.updateNbResource(res, -nb);
                
                // Remettre les jetons sur le plateau
                board.updateNbResource(res, nb);
            }
        }
    }
    
    /**
     * Représentation textuelle
     * @return description de l'action
     */
    @Override
    public String toString() {
        String result = "Défausser : ";
        for (Resource res : Resource.values()) {
            int nb = toDiscard.getNbResource(res);
            if (nb > 0) {
                result += nb + res.toSymbol() + " ";
            }
        }
        return result;
    }
}
