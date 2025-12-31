import java.util.List;

/**
 * Action PickDiffTokensAction
 * Prendre 3 jetons de ressources différentes
 * Règle : chaque ressource doit être disponible sur le plateau
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public class PickDiffTokensAction implements Action {
    
    private List<Resource> resources;  // Liste des 3 ressources à prendre
    
    /**
     * Constructeur
     * @param resources liste de 3 types de ressources différentes
     */
    public PickDiffTokensAction(List<Resource> resources) {
        this.resources = resources;
    }
    
    /**
     * Exécute l'action : retire 1 jeton de chaque type du plateau et les donne au joueur
     * @param board le plateau de jeu
     * @param player le joueur qui effectue l'action
     */
    @Override
    public void process(Board board, Player player) {
        // Pour chaque ressource demandée
        for (Resource res : resources) {
            // Retirer 1 jeton du plateau
            board.updateNbResource(res, -1);
            
            // Ajouter 1 jeton au joueur
            player.updateNbResource(res, 1);
        }
    }
    
    /**
     * Représentation textuelle
     * @return description de l'action
     */
    @Override
    public String toString() {
        int nbTokens = resources.size();
        String result = "Prendre " + nbTokens + " jeton" + (nbTokens > 1 ? "s" : "") + " : ";
        for (Resource res : resources) {
            result += res.toSymbol() + " ";
        }
        return result;
    }

}
