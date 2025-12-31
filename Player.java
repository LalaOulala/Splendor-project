import java.util.List;
import java.util.ArrayList;

public abstract class Player implements Displayable {
    
    private int id;                               // Identifiant du joueur (0, 1, 2, 3)
    private String name;                          // Nom du joueur
    private int points;                           // Points de prestige
    private ArrayList<DevCard> purchasedCards;    // Cartes achetées
    private Resources resources;                  // Jetons possédés

    
    /**
     * Constructeur de Player
     * @param id identifiant du joueur
     * @param name nom du joueur
     */
    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.points = 0;                           // Commence à 0 points
        this.purchasedCards = new ArrayList<>();   // Liste vide au départ
        this.resources = new Resources();          // Pas de jetons au départ
    }


    
    /**
     * @return le nom du joueur
     */
    public String getName() {
        return name;
    }
    
    /**
     * @return les points de prestige
     */
    public int getPoints() {
        return points;
    }
    
    /**
     * @return le nombre total de jetons possédés
     */
    public int getNbTokens() {
        int total = 0;
        for (Resource res : Resource.values()) {
            total += resources.getNbResource(res);
        }
        return total;
    }
    
    /**
     * @return le nombre de cartes achetées
     */
    public int getNbPurchasedCards() {
        return purchasedCards.size();
    }
    
    /**
     * @param res type de ressource
     * @return le nombre de jetons de ce type possédés
     */
    public int getNbResource(Resource res) {
        return resources.getNbResource(res);
    }
    

    
    /**
     * Compte le nombre de ressources d'un type donné sur les cartes achetées
     * @param res type de ressource
     * @return le nombre de cartes qui produisent ce type de ressource
     */
    public int getResFromCards(Resource res) {
        int count = 0;
        for (DevCard card : purchasedCards) {
            if (card.getResourceType() == res) {
                count++;
            }
        }
        return count;
    }

    
    
    /**
     * Ajoute ou retire des jetons
     * @param res type de ressource
     * @param v quantité à ajouter (v>0) ou retirer (v<0)
     */
    public void updateNbResource(Resource res, int v) {
        resources.updateNbResource(res, v);
    }
    
    /**
     * Ajoute des points de prestige
     * @param pts nombre de points à ajouter
     */
    public void updatePoints(int pts) {
        points += pts;
    }
    
    /**
     * Ajoute une carte à la liste des cartes achetées
     * Met automatiquement à jour les points
     * @param card la carte achetée
     */
    public void addPurchasedCard(DevCard card) {
        purchasedCards.add(card);
        updatePoints(card.getPoints());  // Ajouter les points de la carte
    }

    
    
    /**
     * Vérifie si le joueur peut acheter une carte
     * Prend en compte les jetons possédés ET les bonus des cartes achetées
     * @param card la carte à acheter
     * @return true si le joueur peut acheter, false sinon
     */
    public boolean canBuyCard(DevCard card) {
        Resources cost = card.getCost();
        
        // Vérifier pour chaque type de ressource
        for (Resource res : Resource.values()) {
            int required = cost.getNbResource(res);           // Ce qui est demandé
            int jetons = resources.getNbResource(res);         // Jetons possédés
            int ressourcesCartes = getResFromCards(res);                 // Bonus des cartes
            int available = jetons + ressourcesCartes;                    // Total disponible
            
            if (required > available) {
                return false;  // Pas assez de cette ressource
            }
        }
        
        return true;  // Toutes les ressources sont suffisantes
    }

    
    
    // Ces méthodes doivent être implémentées par les classes filles :
    
    /**
     * Permet au joueur de choisir son action
     * @param board le plateau de jeu
     * @return l'action choisie
     */
    public abstract Action chooseAction(Board board);
    
    /**
     * Permet au joueur de choisir quels jetons défausser (si > 10)
     * @return les ressources à défausser
     */
    public abstract Resources chooseDiscardingTokens();

    
    
    /* --- Stringers --- */
    
     
    public String[] toStringArray(){
        /** EXAMPLE. The number of resource tokens is shown in brackets (), and the number of cards purchased from that resource in square brackets [].
         * Player 1: Camille
         * ⓪pts
         * 
         * ♥R (0) [0]
         * ●O (0) [0]
         * ♣E (0) [0]
         * ♠S (0) [0]
         * ♦D (0) [0]
         */
        String pointStr = " ";
        String[] strPlayer = new String[8];

        if(points>0){
            pointStr = new String(new int[] {getPoints()+9311}, 0, 1);
        }else{
            pointStr = "\u24EA";
        }

        
        strPlayer[0] = "Player "+(id+1)+": "+name;
        strPlayer[1] = pointStr + "pts";
        strPlayer[2] = "";
        for(Resource res : Resource.values()){ //-- parcourir l'ensemble des resources (res) en utilisant l'énumération Resource
            strPlayer[3+(Resource.values().length-1-res.ordinal())] = res.toSymbol() + " ("+resources.getNbResource(res)+") ["+getResFromCards(res)+"]";
        }

        return strPlayer;
    }
}
