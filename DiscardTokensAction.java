/**
 * Action de défausse de jetons lorsque le joueur en possède plus de 10.
 * 
 * Selon les règles du jeu, un joueur ne peut pas posséder plus de 10 jetons.
 * Si cette limite est dépassée après avoir pris des jetons, il doit défausser
 * l'excédent. Les jetons défaussés retournent sur le plateau et sont à nouveau
 * disponibles pour tous les joueurs.
 * 
 * Cette action est obligatoire et déclenchée automatiquement par la classe Game
 * après chaque tour si le joueur a plus de 10 jetons. Le joueur peut défausser
 * un ou plusieurs jetons jusqu'à revenir à 10 (ou moins).
 * 
 * @author FONFREIDE Quentin & GONTIER Titouan
 * @version 01/01/2026
 */
public class DiscardTokensAction implements Action {
    
    /**
     * Ressources à défausser.
     * Contient les quantités de chaque type de jeton que le joueur choisit de rendre.
     */
    private Resources toDiscard;
    
    /**
     * Constructeur.
     * 
     * @param toDiscard les ressources que le joueur souhaite défausser
     */
    public DiscardTokensAction(Resources toDiscard) {
        this.toDiscard = toDiscard;
    }
    
    /**
     * Exécute l'action : retire les jetons du joueur et les remet sur le plateau.
     * 
     * Pour chaque type de ressource à défausser, effectue un transfert du joueur vers le plateau.
     * C'est l'opération inverse de prendre des jetons : le joueur perd des jetons et le plateau
     * les récupère.
     * 
     * @param board le plateau de jeu (reçoit les jetons défaussés)
     * @param player le joueur qui défausse les jetons
     */
    @Override
    public void process(Board board, Player player) {
        for (Resource res : Resource.values()) {
            int nb = toDiscard.getNbResource(res);
            
            if (nb > 0) {
                player.updateNbResource(res, -nb);
                board.updateNbResource(res, nb);
            }
        }
    }
    
    /**
     * Retourne une description de l'action avec les jetons défaussés.
     * 
     * Affiche uniquement les ressources dont la quantité est supérieure à 0.
     * Format : "Défausser : 2♦D 1♥R"
     * 
     * @return description de l'action avec les quantités et symboles des ressources défaussées
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
