/**
 * Action de prise de 2 jetons de la même ressource.
 * 
 * Cette action permet à un joueur de prendre 2 jetons d'un même type sur le plateau.
 * Règle importante : cette action n'est possible que s'il reste au moins 4 jetons
 * de ce type sur le plateau. Cette contrainte empêche un joueur de vider complètement
 * une ressource en deux tours et maintient un équilibre dans le jeu.
 * 
 * La vérification de disponibilité doit être effectuée avant de créer cette action
 * (via board.canGiveSameTokens(resource)).
 * 
 * C'est une action courante en début de partie pour accumuler rapidement une ressource
 * spécifique nécessaire à l'achat d'une carte ciblée.
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class PickSameTokensAction implements Action {
    
    /**
     * Type de ressource à prendre en double.
     * Les 2 jetons seront du même type (DIAMOND, SAPPHIRE, EMERALD, RUBY ou ONYX).
     */
    private Resource resource;
    
    /**
     * Constructeur.
     * 
     * @param resource le type de ressource à prendre (2 jetons du même type)
     */
    public PickSameTokensAction(Resource resource) {
        this.resource = resource;
    }
    
    /**
     * Exécute l'action : transfère 2 jetons du plateau vers le joueur.
     * 
     * Opération en deux étapes :
     * 1. Retire 2 jetons du plateau (updateNbResource avec valeur négative)
     * 2. Ajoute 2 jetons au joueur (updateNbResource avec valeur positive)
     * 
     * @param board le plateau de jeu (perd 2 jetons)
     * @param player le joueur qui effectue l'action (gagne 2 jetons)
     */
    @Override
    public void process(Board board, Player player) {
        board.updateNbResource(resource, -2);
        player.updateNbResource(resource, 2);
    }
    
    /**
     * Retourne une description de l'action avec le type de ressource.
     * Format : "Prendre 2 jetons ♦D"
     * 
     * @return description de l'action avec le symbole de la ressource
     */
    @Override
    public String toString() {
        return "Prendre 2 jetons " + resource.toSymbol();
    }
}
