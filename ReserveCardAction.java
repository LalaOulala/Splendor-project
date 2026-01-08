/**
 * Action de réservation d'une carte de développement.
 * 
 * La réservation permet au joueur de mettre de côté une carte (visible ou face cachée)
 * pour l'acheter plus tard. Cette action offre plusieurs avantages stratégiques :
 * - Empêcher un adversaire de prendre une carte convoitée
 * - Obtenir un jeton Or (joker) si disponible
 * - Préparer un futur achat sans dépenser de ressources immédiatement
 * 
 * Limitations :
 * - Maximum 3 cartes réservées par joueur
 * - Les cartes réservées ne peuvent pas être achetées par d'autres joueurs
 * - Les jetons Or ne sont donnés que s'il en reste sur le plateau
 * 
 * Processus de réservation :
 * 1. Retirer la carte du plateau (visible ou piochée face cachée)
 * 2. Ajouter la carte aux réservations du joueur
 * 3. Donner 1 jeton Or au joueur (si disponible sur le plateau)
 * 4. Si carte visible : la remplacer par une nouvelle de la même pile
 * 
 * @author FONFREIDE Quentin & GONTIER Titouan
 * @version 02/01/2026
 */
public class ReserveCardAction implements Action {
    /**
     * Carte à réserver.
     * Peut être une carte visible du plateau ou une carte piochée face cachée.
     */
    private DevCard card;
    
    /**
     * Indique si la carte provient d'une pioche face cachée (true) 
     * ou si c'est une carte visible (false).
     * 
     * Cette information est cruciale pour savoir si on doit remplacer
     * la carte sur le plateau après réservation.
     */
    private boolean fromDeck;

    /**
     * Constructeur pour réserver une carte visible.
     * 
     * @param card la carte visible à réserver
     */
    public ReserveCardAction(DevCard card) {
        this.card = card;
        this.fromDeck = false;
    }
    
    /**
     * Constructeur pour réserver une carte face cachée ou visible.
     * 
     * @param card la carte à réserver
     * @param fromDeck true si la carte vient d'une pile face cachée, false si visible
     */
    public ReserveCardAction(DevCard card, boolean fromDeck) {
        this.card = card;
        this.fromDeck = fromDeck;
    }

    /**
     * Exécute l'action : effectue la réservation complète de la carte.
     * 
     * Processus en 3 étapes :
     * 1. Ajouter la carte aux réservations du joueur
     * 2. Donner 1 jeton Or au joueur si disponible sur le plateau
     * 3. Si carte visible : remplacer par une nouvelle carte de la pile
     * 
     * @param board le plateau de jeu (fournit le jeton Or, remplace la carte si visible)
     * @param player le joueur qui effectue l'action (reçoit la carte et le jeton Or)
     */
    @Override
    public void process(Board board, Player player) {
        // Étape 1 : Ajouter la carte aux réservations du joueur
        player.addReservedCard(card);
        
        // Étape 2 : Donner un jeton Or si disponible
        if (board.getNbResource(Resource.GOLD) > 0) {
            board.updateNbResource(Resource.GOLD, -1);  // Retirer du plateau
            player.updateNbResource(Resource.GOLD, 1);   // Donner au joueur
        }
        
        // Étape 3 : Si carte visible, la remplacer sur le plateau
        if (!fromDeck) {
            board.updateCard(card);
        }
    }

    /**
     * Retourne une description de l'action avec les détails de la carte réservée.
     * 
     * Format : "Réserver une carte [niveau X]" + détails de la carte
     * 
     * @return description de l'action avec les caractéristiques de la carte
     */
    @Override
    public String toString() {
        String source = fromDeck ? " (face cachée)" : "";
        return "Réserver " + card.toString() + source;
    }
}
