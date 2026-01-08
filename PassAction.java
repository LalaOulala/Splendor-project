/**
 * Action de passage de tour sans effectuer d'opération.
 * 
 * Cette action est utilisée quand un joueur ne peut ou ne souhaite effectuer aucune autre action.
 * Elle peut survenir dans plusieurs situations :
 * - Le plateau n'a pas assez de jetons disponibles pour en prendre
 * - Le joueur ne peut acheter aucune carte visible
 * - Le joueur humain choisit volontairement de ne rien faire
 * - Le robot n'a trouvé aucune action possible selon sa stratégie
 * 
 * C'est l'action la plus simple : elle ne modifie rien à l'état du jeu,
 * elle fait simplement passer le tour au joueur suivant.
 * 
 * @author FONFREIDE Quentin & GONTIER Titouan
 * @version 01/01/2026
 */
public class PassAction implements Action {
    
    /**
     * Constructeur.
     * Aucun paramètre nécessaire car cette action ne stocke aucune donnée.
     */
    public PassAction() {
        // Rien à initialiser
    }
    
    /**
     * Exécute l'action : ne fait rien.
     * 
     * Cette méthode est volontairement vide. Passer son tour signifie
     * ne rien changer à l'état du jeu : ni le plateau ni le joueur
     * ne sont modifiés.
     * 
     * @param board le plateau de jeu (non utilisé)
     * @param player le joueur (non utilisé)
     */
    @Override
    public void process(Board board, Player player) {
        // Ne rien faire - c'est volontaire !
    }
    
    /**
     * Retourne une description simple de l'action.
     * 
     * @return la chaîne "Passer le tour"
     */
    @Override
    public String toString() {
        return "Passer le tour";
    }
}
