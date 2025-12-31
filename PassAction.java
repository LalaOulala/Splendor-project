/**
 * Action PassAction
 * Le joueur passe son tour sans rien faire
 * Utilisée quand aucune autre action n'est possible
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public class PassAction implements Action {
    
    /**
     * Constructeur
     */
    public PassAction() {
        // Rien à initialiser
    }
    
    /**
     * Exécute l'action : ne fait rien
     * @param board le plateau de jeu (non utilisé)
     * @param player le joueur (non utilisé)
     */
    @Override
    public void process(Board board, Player player) {
        // Ne rien faire - c'est volontaire !
    }
    
    /**
     * Représentation textuelle
     * @return description de l'action
     */
    @Override
    public String toString() {
        return "Passer le tour";
    }
}
