/**
 * Action d'achat d'une carte de développement.
 * 
 * C'est l'action la plus importante du jeu car elle permet de marquer des points
 * et d'obtenir des bonus permanents. L'achat d'une carte implique plusieurs opérations :
 * - Calculer le coût réel en tenant compte des bonus des cartes déjà possédées
 * - Payer avec les jetons (qui retournent sur le plateau)
 * - Prendre la carte et gagner ses points de prestige
 * - Remplacer la carte achetée par une nouvelle du même niveau
 * 
 * Mécanisme clé : chaque carte possédée donne un bonus permanent qui réduit le coût
 * des futurs achats. Par exemple, si une carte coûte 5 diamants et que le joueur
 * possède 3 cartes produisant du diamant, il ne paiera que 2 jetons diamants.
 * 
 * La vérification de disponibilité doit être effectuée avant de créer cette action
 * (via player.canBuyCard(card)).
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class BuyCardAction implements Action {
    
    /**
     * Carte de développement à acheter.
     * Contient toutes les informations nécessaires : coût, points, type de bonus.
     */
    private DevCard card;
    
    /**
     * Constructeur.
     * 
     * @param card la carte de développement à acheter
     */
    public BuyCardAction(DevCard card) {
        this.card = card;
    }
    
    /**
     * Exécute l'action : effectue l'achat complet de la carte.
     * 
     * Processus en 4 étapes :
     * 1. Calcul du coût réel pour chaque ressource (coût requis - bonus des cartes)
     * 2. Paiement : transfert des jetons du joueur vers le plateau
     * 3. Ajout de la carte au joueur (qui met automatiquement à jour les points)
     * 4. Remplacement de la carte achetée sur le plateau par une nouvelle
     * 
     * Le Math.max(0, required - bonus) garantit qu'on ne paie jamais un montant négatif
     * si les bonus dépassent le coût requis.
     * 
     * @param board le plateau de jeu (reçoit les jetons payés, remplace la carte)
     * @param player le joueur qui effectue l'action (paie et reçoit la carte)
         */
    @Override
    public void process(Board board, Player player) {
        Resources cost = card.getCost();
        
        // Calculer le coût réel en tenant compte des bonus
        for (Resource res : Resource.values()) {
            int required = cost.getNbResource(res);
            int bonus = player.getResFromCards(res);
            int toPay = Math.max(0, required - bonus);
            
            if (toPay > 0) {
                // Payer : retirer du joueur, ajouter au plateau
                player.updateNbResource(res, -toPay);
                board.updateNbResource(res, toPay);
            }
        }
        
        // Ajouter la carte au joueur (ajoute aussi les points automatiquement)
        player.addPurchasedCard(card);
        
        // Remplacer la carte achetée par une nouvelle de la pile
        board.updateCard(card);
    }

    /**
     * Retourne une description de l'action avec les détails de la carte.
     * Utilise la méthode toString() de DevCard pour afficher le coût et les points.
     * Format : "Acheter 2pts, type ♠S | coût: 3♦D 2♣E"
     * 
     * @return description de l'action avec les caractéristiques de la carte
     */
    @Override
    public String toString() {
        return "Acheter " + card.toString();
    }
}
