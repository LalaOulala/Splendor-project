/**
 * Interface représentant une action qu'un joueur peut effectuer pendant son tour.
 * 
 * Cette interface définit le contrat pour toutes les actions possibles dans le jeu Splendor.
 * Grâce au polymorphisme, toutes les actions sont traitées de manière uniforme dans
 * la boucle de jeu : on récupère une Action via player.chooseAction(), on l'exécute
 * avec process(), et on l'affiche avec toString().
 * 
 * Les actions implémentant cette interface sont :
 * - PickSameTokensAction : prendre 2 jetons de la même ressource
 * - PickDiffTokensAction : prendre 3 jetons de ressources différentes
 * - BuyCardAction : acheter une carte de développement
 * - DiscardTokensAction : défausser des jetons (quand on en a plus de 10)
 * - PassAction : passer son tour sans rien faire
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public interface Action {
    
    /**
     * Exécute l'action en modifiant l'état du jeu.
     * 
     * Cette méthode effectue les modifications nécessaires sur le plateau et/ou le joueur
     * selon la nature de l'action. Par exemple :
     * - Prendre des jetons : retire du plateau, ajoute au joueur
     * - Acheter une carte : retire les jetons du joueur, remet sur le plateau, ajoute la carte, met à jour le plateau
     * - Défausser : retire du joueur, remet sur le plateau
     * - Passer : ne fait rien
     * 
     * @param board le plateau de jeu (contient les cartes et jetons disponibles)
     * @param player le joueur qui effectue l'action
     */
    void process(Board board, Player player);
    
    /**
     * Retourne une représentation textuelle de l'action.
     * 
     * Cette méthode génère une description lisible de l'action effectuée,
     * qui sera affichée dans la console pour informer les joueurs.
     * 
     * Exemples :
     * - "Prendre 2 jetons ♦D"
     * - "Prendre 3 jetons : ♦D ♠S ♣E"
     * - "Acheter une carte niveau 2"
     * - "Défausser : 2♦D 1♥R"
     * - "Passer le tour"
     * 
     * @return une chaîne décrivant l'action de manière compacte
     */
    String toString();
}
