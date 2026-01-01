import java.util.List;

/**
 * Action de prise de jetons de ressources différentes.
 * 
 * Cette action permet à un joueur de prendre des jetons de types différents sur le plateau.
 * C'est l'action la plus courante dans Splendor car elle permet de diversifier rapidement
 * ses ressources pour acheter différentes cartes.
 * 
 * Règle standard : prendre 3 jetons de 3 types différents (chaque ressource doit avoir
 * au moins 1 jeton disponible).
 * 
 * Amélioration personnelle : en fin de partie, si moins de 3 types de ressources sont
 * disponibles sur le plateau, le joueur peut prendre seulement 1 ou 2 jetons.
 * La vérification doit être effectuée avant de créer cette action
 * (via board.canGiveDiffTokens(resources)).
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class PickDiffTokensAction implements Action {
    
    /**
     * Liste des ressources à prendre (1 jeton par type).
     * Contient normalement 3 types différents, mais peut en contenir 1 ou 2
     * si le plateau n'a plus assez de types de ressources disponibles.
     */
    private List<Resource> resources;
    
    /**
     * Constructeur.
     * 
     * @param resources liste des types de ressources à prendre (normalement 3 types différents)
     */
    public PickDiffTokensAction(List<Resource> resources) {
        this.resources = resources;
    }
    
    /**
     * Exécute l'action : transfère 1 jeton de chaque type du plateau vers le joueur.
     * 
     * Parcourt la liste des ressources demandées et pour chacune :
     * 1. Retire 1 jeton du plateau
     * 2. Ajoute 1 jeton au joueur
     * 
     * @param board le plateau de jeu (perd 1 jeton par type demandé)
     * @param player le joueur qui effectue l'action (gagne 1 jeton par type)
     */
    @Override
    public void process(Board board, Player player) {
        for (Resource res : resources) {
            board.updateNbResource(res, -1);
            player.updateNbResource(res, 1);
        }
    }
    
    /**
     * Retourne une description de l'action avec les types de ressources.
     * 
     * S'adapte au nombre de jetons pris (gestion du pluriel).
     * Format : "Prendre 3 jetons : ♦D ♠S ♣E"
     * 
     * @return description de l'action avec le nombre et les symboles des ressources
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
