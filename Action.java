/**
 * Interface Action
 * Représente une action qu'un joueur peut effectuer pendant son tour
 * 
 * Toutes les actions doivent implémenter cette interface pour être traitées
 * de manière uniforme dans la boucle de jeu
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public interface Action {
    
    /**
     * Exécute l'action
     * Modifie l'état du plateau et/ou du joueur selon l'action effectuée
     * 
     * @param board le plateau de jeu
     * @param player le joueur qui effectue l'action
     */
    void process(Board board, Player player);
    
    /**
     * Représentation textuelle de l'action
     * Utilisée pour afficher ce que le joueur a fait
     * 
     * @return une description de l'action
     */
    String toString();
}
