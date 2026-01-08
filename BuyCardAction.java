/**
 * Action d'achat d'une carte de développement.
 * 
 * C'est l'action la plus importante du jeu car elle permet de marquer des points
 * et d'obtenir des bonus permanents. L'achat d'une carte implique plusieurs opérations :
 * - Calculer le coût réel en tenant compte des bonus des cartes déjà possédées
 * - Utiliser automatiquement les jetons Or (jokers) si les ressources normales sont insuffisantes
 * - Payer avec les jetons (qui retournent sur le plateau)
 * - Prendre la carte et gagner ses points de prestige
 * - Remplacer la carte achetée par une nouvelle (si elle vient du plateau)
 * 
 * Mécanisme clé : chaque carte possédée donne un bonus permanent qui réduit le coût
 * des futurs achats. Par exemple, si une carte coûte 5 diamants et que le joueur
 * possède 3 cartes produisant du diamant, il ne paiera que 2 ressources (jetons ou Or).
 * 
 * Gestion automatique des jetons Or :
 * Les jetons Or sont utilisés automatiquement pour combler les manques de ressources.
 * Le joueur n'a pas à choisir : le système calcule combien de jetons Or sont nécessaires
 * et les utilise automatiquement si le joueur en possède suffisamment.
 * 
 * La vérification de disponibilité doit être effectuée avant de créer cette action
 * (via player.canBuyCard(card)).
 * 
 * @author FONFREIDE Quentin & GONTIER Titouan
 * @version 02/01/2026
 */
public class BuyCardAction implements Action {
    /**
     * Carte de développement à acheter.
     * Contient toutes les informations nécessaires : coût, points, type de bonus.
     */
    private DevCard card;
    
    /**
     * Indique si la carte provient des réservations du joueur (true)
     * ou du plateau visible (false).
     * 
     * Cette information détermine si on doit remplacer la carte sur le plateau
     * après l'achat ou la retirer des réservations du joueur.
     */
    private boolean fromReserved;

    /**
     * Constructeur pour acheter une carte du plateau.
     * 
     * @param card la carte de développement à acheter
     */
    public BuyCardAction(DevCard card) {
        this.card = card;
        this.fromReserved = false;
    }
    
    /**
     * Constructeur pour acheter une carte du plateau ou des réservations.
     * 
     * @param card la carte de développement à acheter
     * @param fromReserved true si la carte vient des réservations, false si du plateau
     */
    public BuyCardAction(DevCard card, boolean fromReserved) {
        this.card = card;
        this.fromReserved = fromReserved;
    }

    /**
     * Exécute l'action : effectue l'achat complet de la carte.
     * 
     * Processus en 5 étapes :
     * 1. Calcul du nombre de jetons Or nécessaires (manque après bonus + jetons normaux)
     * 2. Vérification de sécurité : le joueur a-t-il assez de jetons Or ?
     * 3. Paiement avec les jetons normaux : transfert du joueur vers le plateau
     * 4. Paiement avec les jetons Or : transfert du joueur vers le plateau
     * 5. Ajout de la carte au joueur et mise à jour du plateau/réservations
     * 
     * Gestion des jetons Or :
     * Pour chaque ressource, on calcule le manque après avoir utilisé les bonus et jetons normaux.
     * Les jetons Or comblent automatiquement tous ces manques. Le joueur n'a pas à choisir
     * quelles ressources remplacer : c'est calculé automatiquement.
     * 
     * @param board le plateau de jeu (reçoit les jetons payés, remplace la carte si du plateau)
     * @param player le joueur qui effectue l'action (paie et reçoit la carte)
     */
    @Override
    public void process(Board board, Player player) {
        Resources cost = card.getCost();
        
        // ========== ÉTAPE 1 : Calculer le nombre de jetons Or nécessaires ==========
        int goldNeeded = 0;
        
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD){
                int required = cost.getNbResource(res);
                int bonus = player.getResFromCards(res);
                int owned = player.getNbResource(res);
                
                // Calculer le manque après avoir utilisé bonus et jetons normaux
                int shortage = Math.max(0, required - bonus - owned);
                goldNeeded += shortage;
            }
        }
        
        // ========== ÉTAPE 2 : Payer avec les jetons normaux ==========
        for (Resource res : Resource.values()) {
            if (res != Resource.GOLD){
                int required = cost.getNbResource(res);
                int bonus = player.getResFromCards(res);
                
                // Ce qu'il reste à payer après le bonus de la carte
                int afterBonus = Math.max(0, required - bonus);
            
                int owned = player.getNbResource(res);
                int toPay = Math.min(afterBonus, owned);
                
                if (toPay > 0) {
                    // Payer : retirer du joueur, ajouter au plateau
                    player.updateNbResource(res, -toPay);
                    board.updateNbResource(res, toPay);
                }
            }
        }
        
        // ========== ÉTAPE 3 : Payer avec les jetons Or si nécessaire ==========
        if (goldNeeded > 0) {
            player.updateNbResource(Resource.GOLD, -goldNeeded);
            board.updateNbResource(Resource.GOLD, goldNeeded);
        }
        
        // ========== ÉTAPE 4 : Finaliser l'achat ==========
        // Ajouter la carte au joueur (ajoute aussi les points automatiquement)
        player.addPurchasedCard(card);
        
        // Gérer la provenance de la carte
        if (fromReserved) {
            // Retirer la carte des réservations du joueur
            player.removeReservedCard(card);
        } else {
            // Remplacer la carte achetée par une nouvelle de la pile
            board.updateCard(card);
        }
    }

    
    /**
     * Retourne une description de l'action avec les détails de la carte.
     * Utilise la méthode toString() de DevCard pour afficher le coût et les points.
     * Format : "Acheter 2pts, type ♠S | coût: 3♦D 2♣E [depuis réservations]"
     * 
     * @return description de l'action avec les caractéristiques de la carte
     */
    @Override
    public String toString() {
        String source = fromReserved ? " [depuis réservations]" : "";
        return "Acheter " + card.toString() + source;
    }
}
