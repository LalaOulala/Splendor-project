import java.util.List;
import java.util.ArrayList;

/**
 * Classe abstraite représentant un joueur dans le jeu Splendor.
 * 
 * Cette classe regroupe tous les éléments communs à tous les types de joueurs
 * (humains et robots) : identité, points de prestige, cartes achetées, et jetons possédés.
 * 
 * Elle est déclarée abstraite car un joueur générique ne peut pas exister :
 * il faut soit un joueur humain (qui interagit via le terminal), soit un robot
 * (qui calcule automatiquement ses actions selon sa stratégie). Les méthodes
 * abstraites chooseAction() et chooseDiscardingTokens() doivent être implémentées
 * différemment selon le type de joueur.
 * 
 * Un joueur est caractérisé par :
 * - Son identité (id et nom)
 * - Ses points de prestige (objectif : atteindre 15 points pour gagner)
 * - Ses cartes achetées (qui donnent des bonus permanents)
 * - Ses jetons de ressources (limités à 10 maximum)
 * 
 * Cette classe implémente Displayable pour permettre l'affichage de l'état
 * du joueur dans le terminal.
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public abstract class Player implements Displayable {
    
    /**
     * Identifiant unique du joueur (0, 1, 2 ou 3).
     * Détermine l'ordre de jeu et l'affichage "Player 1", "Player 2", etc.
     */
    private int id;
    
    /**
     * Nom du joueur.
     * Demandé à l'utilisateur pour les joueurs humains, ou généré automatiquement
     * pour les robots ("Robot 1", "Robot 2", etc.).
     */
    private String name;
    
    /**
     * Points de prestige du joueur.
     * Commence à 0 et augmente quand le joueur achète des cartes qui en donnent.
     * L'objectif est d'atteindre 15 points pour déclencher la fin de partie.
     */
    private int points;
    
    /**
     * Liste des cartes de développement achetées par le joueur.
     * Chaque carte donne un bonus permanent qui réduit le coût des futurs achats.
     */
    private ArrayList<DevCard> purchasedCards;
    
    /**
     * Jetons de ressources possédés par le joueur.
     * Utilisés pour payer le coût des cartes. Limités à 10 jetons maximum.
     */
    private Resources resources;
    
    /**
     * Liste des nobles obtenus par le joueur.
     * Chaque noble rapporte 3 points de prestige et s'obtient automatiquement
     * quand le joueur possède assez de bonus de cartes correspondant aux exigences du noble.
     * Un seul noble peut être obtenu par tour maximum.
     */
    private ArrayList<Noble> purchasedNobles;
    
    /**
     * Constructeur de Player.
     * Initialise un joueur avec son identité et ses attributs par défaut
     * (0 points, aucune carte, aucun jeton).
     * 
     * @param id identifiant unique du joueur (0 à 3)
     * @param name nom du joueur
     */
    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.points = 0;
        this.purchasedCards = new ArrayList<>();
        this.resources = new Resources();
        this.purchasedNobles = new ArrayList<Noble>();
    }


    // ============= ACCESSEURS =============
    
    /**
     * Retourne le nom du joueur.
     * 
     * @return le nom du joueur
     */
    public String getName() {
        return name;
    }
    
    /**
     * Retourne les points de prestige du joueur.
     * 
     * @return le nombre de points de prestige
     */
    public int getPoints() {
        return points;
    }
    
    /**
     * Retourne le nombre total de jetons possédés par le joueur.
     * Parcourt tous les types de ressources et additionne leurs quantités.
     * Utilisé pour vérifier la limite de 10 jetons.
     * 
     * @return le nombre total de jetons (toutes ressources confondues)
     */
    public int getNbTokens() {
        int total = 0;
        for (Resource res : Resource.values()) {
            total += resources.getNbResource(res);
        }
        return total;
    }
    
    /**
     * Retourne le nombre de cartes achetées par le joueur.
     * 
     * @return le nombre de cartes dans purchasedCards
     */
    public int getNbPurchasedCards() {
        return purchasedCards.size();
    }
    
    /**
     * Retourne le nombre de jetons d'un type de ressource spécifique.
     * 
     * @param res type de ressource à consulter
     * @return le nombre de jetons de ce type
     */
    public int getNbResource(Resource res) {
        return resources.getNbResource(res);
    }
    
    /**
     * Retourne l'objet Resources complet du joueur.
     * Permet d'accéder à toutes les ressources en une seule fois.
     * 
     * @return l'objet Resources contenant tous les jetons du joueur
     */
    public Resources getRessources() {
        return resources;
    }
    
    
    // ============= CALCUL DES BONUS =============
    
    /**
     * Compte le nombre de bonus d'un type de ressource donné provenant des cartes achetées.
     * Chaque carte achetée produit un bonus permanent d'un type de ressource,
     * qui réduit le coût effectif des futurs achats nécessitant cette ressource.
     * 
     * Par exemple, si le joueur a 3 cartes produisant du diamant, cette méthode
     * retournera 3 pour Resource.DIAMOND.
     * 
     * @param res type de ressource à compter
     * @return le nombre de cartes produisant ce type de ressource
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

    
    // ============= MODIFICATION DE L'ÉTAT =============
    
    /**
     * Ajoute ou retire des jetons d'un type de ressource.
     * Utilisé pour prendre des jetons sur le plateau (v > 0) ou
     * payer le coût d'une carte (v < 0).
     * 
     * @param res type de ressource à modifier
     * @param v quantité à ajouter si v > 0, ou à retirer si v < 0
     */
    public void updateNbResource(Resource res, int v) {
        resources.updateNbResource(res, v);
    }
    
    /**
     * Ajoute des points de prestige au joueur.
     * Appelée automatiquement lors de l'achat d'une carte.
     * 
     * @param pts nombre de points à ajouter
     */
    public void updatePoints(int pts) {
        points += pts;
    }
    
    /**
     * Ajoute une carte à la liste des cartes achetées.
     * Met automatiquement à jour les points de prestige du joueur
     * en ajoutant les points de la carte.
     * 
     * @param card la carte qui vient d'être achetée
     */
    public void addPurchasedCard(DevCard card) {
        purchasedCards.add(card);
        updatePoints(card.getPoints());
    }

    
    // ============= VÉRIFICATIONS =============
    
    /**
     * Vérifie si le joueur peut acheter une carte donnée.
     * 
     * Prend en compte à la fois les jetons possédés ET les bonus des cartes
     * déjà achetées. Pour chaque type de ressource requis, vérifie que :
     * (jetons possédés + bonus des cartes) >= coût requis
     * 
     * Par exemple, si une carte coûte 5 diamants et que le joueur a
     * 2 jetons diamants + 3 cartes produisant du diamant, il peut l'acheter.
     * 
     * @param card la carte que le joueur souhaite acheter
     * @return true si le joueur a suffisamment de ressources (jetons + bonus), false sinon
     */
    public boolean canBuyCard(DevCard card) {
        Resources cost = card.getCost();
        
        for (Resource res : Resource.values()) {
            int required = cost.getNbResource(res);
            int jetons = resources.getNbResource(res);
            int ressourcesCartes = getResFromCards(res);
            int available = jetons + ressourcesCartes;
            
            if (required > available) {
                return false;
            }
        }
        
        return true;
    }
    
    // ============= GESTION DES NOBLES =============
    
    /**
     * Ajoute un noble à la liste des nobles obtenus par le joueur.
     * Les points du noble (toujours 3) sont automatiquement ajoutés au score du joueur.
     * Affiche également un message dans la console pour notifier l'obtention du noble.
     * 
     * Cette méthode ne doit pas être appelée directement : utilisez plutôt
     * checkAndObtainNobles() qui gère toute la logique d'attribution.
     * 
     * @param noble Le noble à ajouter au joueur
     */
    public void addPurchasedNoble(Noble noble) {
        this.purchasedNobles.add(noble);
        this.points += noble.getPoints();
        
        // Message d'obtention du noble (sera affiché après l'action d'achat)
        Game.display.out.println();
        Game.display.out.println("\u269C " + this.name + " obtient un noble ! (+" 
                                + noble.getPoints() + " pts)");
    }

    
    /**
     * Retourne la liste des nobles obtenus par le joueur.
     * Permet d'accéder aux détails de chaque noble possédé.
     * 
     * @return ArrayList contenant tous les nobles du joueur
     */
    public int getNbPurchasedNobles() {
        return this.purchasedNobles.size();
    }
    
    /**
     * Retourne la liste des nobles obtenus par le joueur.
     * 
     * @return ArrayList des nobles
     */
    public ArrayList<Noble> getPurchasedNobles() {
        return this.purchasedNobles;
    }
    
    /**
     * Vérifie et obtient automatiquement les nobles disponibles.
     * Cette méthode doit être appelée après chaque achat de carte.
     * 
     * Un joueur obtient un noble s'il possède assez de bonus de cartes
     * correspondant aux exigences du noble.
     * 
     * Si plusieurs nobles sont éligibles, le joueur choisit lequel garder.
     * 
     * @param board Le plateau de jeu
     */
    public void checkAndObtainNobles(Board board) {
        // Trouver TOUS les nobles éligibles
        List<Noble> eligibleNobles = new ArrayList<>();
        
        for (Noble noble : board.getVisibleNobles()) {
            if (board.canObtainNoble(noble, this)) {
                eligibleNobles.add(noble);
            }
        }
        
        // Aucun noble disponible
        if (eligibleNobles.isEmpty()) {
            return;
        }
        
        // UN seul noble : l'obtenir directement
        if (eligibleNobles.size() == 1) {
            Noble chosenNoble = eligibleNobles.get(0);
            addPurchasedNoble(chosenNoble);
            board.removeNoble(chosenNoble);
            Game.display.out.println("\u269C " + this.name + " obtient un noble ! (+3 pts)");
            return;
        }
        
        // PLUSIEURS nobles : laisser le joueur choisir
        Noble chosenNoble = chooseNoble(eligibleNobles);
        addPurchasedNoble(chosenNoble);
        board.removeNoble(chosenNoble);
        Game.display.out.println("\u269C " + this.name + " obtient un noble ! (+3 pts)");
    }

    /**
     * Permet au joueur de choisir quel noble obtenir parmi plusieurs nobles éligibles.
     * 
     * Cette méthode abstraite est appelée par checkAndObtainNobles() uniquement
     * quand le joueur devient éligible pour plusieurs nobles en même temps.
     * 
     * Implémentations :
     * - HumanPlayer : affiche la liste des nobles éligibles et demande à l'utilisateur
     *   de choisir interactivement via le terminal
     * - DumbRobotPlayer : sélectionne automatiquement le premier noble de la liste
     *   (stratégie simple sans réflexion)
     * 
     * @param eligibleNobles Liste des nobles pour lesquels le joueur est éligible
     *                       (taille >= 2, sinon cette méthode n'est pas appelée)
     * @return Le noble choisi par le joueur (doit faire partie de eligibleNobles)
     */
    protected abstract Noble chooseNoble(List<Noble> eligibleNobles);

    
    // ============= MÉTHODES ABSTRAITES =============
    
    /**
     * Permet au joueur de choisir son action pendant son tour.
     * 
     * Cette méthode est abstraite car elle dépend du type de joueur :
     * - Un joueur humain affichera un menu et demandera à l'utilisateur de choisir
     * - Un robot calculera automatiquement la meilleure action selon sa stratégie
     * 
     * @param board le plateau de jeu actuel (pour consulter les cartes et jetons disponibles)
     * @return l'action choisie par le joueur
     */
    public abstract Action chooseAction(Board board);
    
    /**
     * Permet au joueur de choisir quels jetons défausser quand il en possède plus de 10.
     * 
     * Selon les règles du jeu, un joueur ne peut pas avoir plus de 10 jetons.
     * Si cette limite est dépassée, il doit défausser des jetons pour revenir à 10.
     * 
     * Cette méthode est abstraite car elle dépend du type de joueur :
     * - Un joueur humain choisira interactivement quels jetons défausser
     * - Un robot choisira aléatoirement (stratégie du robot stupide)
     * 
     * @return un objet Resources contenant les jetons à défausser
     */
    public abstract Resources chooseDiscardingTokens();

    
    // ============= AFFICHAGE =============
    
    /**
     * Convertit l'état du joueur en représentation ASCII pour l'affichage.
     * 
     * Affiche :
     * - L'identifiant et le nom du joueur (ex : "Player 1: Alice")
     * - Les points de prestige avec symbole Unicode circlé (①②③... ou ⓪ si 0 points)
     * - Pour chaque type de ressource : nombre de jetons entre () et nombre de bonus entre []
     * - Les nobles obtenus sur la même ligne que la première ressource : ⚜N (nb) {points Pts}
     *   où nb = nombre de nobles, points = total des points rapportés par les nobles
     * 
     * Exemple de rendu :
     * Player 1: Camille
     * ⑤pts
     * 
     * ♥R (3) [2]           ⚜N (1) {3 Pts}
     * ●O (1) [0]
     * ♣E (2) [1]
     * ♠S (0) [3]
     * ♦D (4) [1]
     * 
     * Les ressources sont affichées dans l'ordre inverse de l'énumération Resource
     * (Rubis, Onyx, Emerald, Sapphire, Diamond) pour correspondre au visuel attendu.
     * 
     * L'information sur les nobles est toujours affichée sur la ligne de la première
     * ressource (♥R), même si le joueur n'a aucun noble (dans ce cas : "⚜N (0) {0 Pts}").
     * 
     * @return un tableau de 8 String représentant l'état du joueur
     */
    public String[] toStringArray() {
        String pointStr = " ";
        String[] strPlayer = new String[8];
    
        // Affichage des points avec symbole Unicode circlé
        if(points > 0) {
            pointStr = new String(new int[] {getPoints() + 9311}, 0, 1);
        } else {
            pointStr = "\u24EA";
        }
    
        // Lignes 0-2 : Nom et points
        strPlayer[0] = "Player " + (id + 1) + ": " + name;
        strPlayer[1] = pointStr + "pts";
        strPlayer[2] = "";
        
        // Lignes 3-7 : Ressources dans l'ordre inverse (Rubis, Onyx, Emerald, Sapphire, Diamond)
        for(Resource res : Resource.values()) {
            strPlayer[3 + (Resource.values().length - 1 - res.ordinal())] = 
                res.toSymbol() + " (" + resources.getNbResource(res) + ") [" + getResFromCards(res) + "]";
        }
        
        // Ajouter les nobles sur la même ligne que la première ressource (index 3)
        // Format : 👑N (nombre) {points Pts}
        int nbNobles = this.purchasedNobles.size();
        int ptsNobles = nbNobles * 3;  // Chaque noble vaut 3 points
        String noblesInfo = "           \u269CN (" + nbNobles + ") {" + ptsNobles + " Pts}";
        
        strPlayer[3] += noblesInfo;  // Concaténer à la ligne de la première ressource (♥R)
        
        return strPlayer;
    }

}
