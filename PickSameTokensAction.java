/**
 * Action PickSameTokensAction
 * Prendre 2 jetons de la même ressource
 * Règle : possible seulement s'il reste au moins 4 jetons de ce type sur le plateau
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public class PickSameTokensAction implements Action {
    
    private Resource resource;  // Type de ressource à prendre
    
    /**
     * Constructeur
     * @param resource le type de ressource à prendre (2 jetons)
     */
    public PickSameTokensAction(Resource resource) {
        this.resource = resource;
    }
    
    /**
     * Exécute l'action : retire 2 jetons du plateau et les donne au joueur
     * @param board le plateau de jeu
     * @param player le joueur qui effectue l'action
     */
    @Override
    public void process(Board board, Player player) {
        // Retirer 2 jetons du plateau
        board.updateNbResource(resource, -2);
        
        // Ajouter 2 jetons au joueur
        player.updateNbResource(resource, 2);
    }
    
    /**
     * Représentation textuelle
     * @return description de l'action
     */
    @Override
    public String toString() {
        return "Prendre 2 jetons " + resource.toSymbol();
    }
}
