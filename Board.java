import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;

public class Board implements Displayable {

    private Stack<DevCard>[] stackCards;   // Piles de cartes faces cachées (3 piles : une par niveau)
    private DevCard[][] visibleCards;     // Cartes faces visibles (3 lignes × 4 colonnes)
    private Resources resources;          // Jetons disponibles sur le plateau
    
    /**
     * Constructeur de Board
     * Initialise le plateau de jeu : lit le CSV, crée les cartes, les mélange, et initialise les ressources
     * @param nbPlayers nombre de joueurs (2, 3 ou 4)
     */
    public Board(int nbPlayers) {
                
        // Création d'un tableau pour contenir les 3 Piles
        stackCards = new Stack[3];
        stackCards[0] = new Stack<>();  // Tier 1
        stackCards[1] = new Stack<>();  // Tier 2
        stackCards[2] = new Stack<>();  // Tier 3
        
        // Création d'un tableau pour contenir les cartes visibles des 3 piles
        visibleCards = new DevCard[3][4];
        
        // Création des piles de jeton de ressources
        int nombreJetons = 7;
        if (nbPlayers <=3 && nbPlayers > 1){
            nombreJetons = nbPlayers + 2;
        } else if (nbPlayers == 1){
            nombreJetons = 0;
        }
        resources = new Resources(nombreJetons, nombreJetons, nombreJetons, nombreJetons, nombreJetons);
        
        
        // Lecture du fichier CSV et création des cartes
        try {
            Scanner scanner = new Scanner(new File("stats.csv"));
            
            scanner.nextLine(); // Sauter la première ligne car pas de données
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] donnees = line.split(",");  // Séparer les données à l'aide des virgules
                
                // Tier de la carte
                int tierCarte = Integer.parseInt(donnees[0]);
                
                // Cout de la carte
                int coutDiamond = Integer.parseInt(donnees[1]);
                int coutSapphire = Integer.parseInt(donnees[2]);
                int coutEmerald = Integer.parseInt(donnees[3]);
                int coutRuby = Integer.parseInt(donnees[4]);
                int coutOnyx = Integer.parseInt(donnees[5]);
                Resources coutCarte = new Resources(coutDiamond, coutSapphire, coutEmerald, coutOnyx, coutRuby);
                
                // PV de la carte
                int pointsCarte = Integer.parseInt(donnees[6]);
                
                // Vérification que la carte n'est pas un noble, car cela causerait un problème lors de l'ajout à la pile.
                if (tierCarte != 0){
                    // Type de ressource d'apport de la carte
                    Resource typeCarte = Resource.valueOf(donnees[7]);
                    
                    // Création de la carte et ajout a sa pile respective
                    DevCard carte = new DevCard(tierCarte, coutCarte, pointsCarte, typeCarte);
                    stackCards[tierCarte - 1].push(carte);
                }
            }
            
            scanner.close();
            
            // Gestion des Erreurs de fichier
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : fichier stats.csv contenant les cartes introuvable !");
            e.printStackTrace();
        }
        
        
        // Mélange des 3 piles avec Collections
        Collections.shuffle(stackCards[0]); 
        Collections.shuffle(stackCards[1]); 
        Collections.shuffle(stackCards[2]); 
        
        
        // Retourner 4 cartes de chaque pile
        for (int tier = 0; tier < 3; tier++) {
            for (int colonne = 0; colonne < 4; colonne++) {
                visibleCards[tier][colonne] = stackCards[tier].pop();
            }
        }
    }
    
    // Gestion Jetons
    
    /**
     * @param res type de ressource
     * @return nombre de jetons de ce type sur le plateau
     */
    public int getNbResource(Resource res) {
        return resources.getNbResource(res);
    }
    
    /**
     * Initialise le nombre de jetons d'un type donné
     * @param res type de ressource
     * @param nb nombre de jetons
     */
    public void setNbResource(Resource res, int nb) {
        resources.setNbResource(res, nb);
    }
    
    /**
     * Ajoute ou retire des jetons sur le plateau
     * @param res type de ressource
     * @param v quantité à ajouter (v>0) ou retirer (v<0)
     */
    public void updateNbResource(Resource res, int v) {
        resources.updateNbResource(res, v);
    }
    
    // Gestion Cartes
    
    // ============= GESTION DES CARTES =============

    /**
     * Récupère une carte visible
     * @param tier niveau de la carte (1, 2 ou 3)
     * @param colonne colonne (0, 1, 2 ou 3)
     * @return la carte à cette position
     */
    public DevCard getCard(int tier, int colonne) {
        return visibleCards[tier - 1][colonne];  // tier 1 → index 0
    }
    
    /**
     * Remplace une carte achetée par une nouvelle de la pile
     * @param carte la carte qui a été achetée
     */
    public void updateCard(DevCard carte) {
        for (int tier = 0; tier < 3; tier++) {
            for (int colonne = 0; colonne < 4; colonne++) {
                if (visibleCards[tier][colonne] == carte) {
                    if (stackCards[tier].isEmpty()) {
                        visibleCards[tier][colonne] = null;
                    } else {
                        visibleCards[tier][colonne] = stackCards[tier].pop();
                    }
                }
            }
        }
    }
    
    /**
     * Pioche une carte face cachée
     * @param tier niveau de la pile (1, 2 ou 3)
     * @return la carte piochée, ou null si la pile est vide
     */
    public DevCard drawCard(int tier) {
        if (canDrawPile(tier)) {
            return stackCards[tier - 1].pop();
        }
        return null;
    }
    
    // Vérification des actions possibles
    
    /**
     * Vérifie si on peut prendre 2 jetons identiques
     * @param res type de ressource
     * @return true s'il reste au moins 4 jetons de ce type
     */
    public boolean canGiveSameTokens(Resource res) {
        return getNbResource(res) >= 4;
    }
    
    /**
     * Vérifie si on peut prendre 3 jetons différents
     * @param requestedResources liste de 3 ressources différentes
     * @return true si chaque ressource est disponible (au moins 1 jeton)
     */
    public boolean canGiveDiffTokens(List<Resource> requestedResources) {
        
        if (requestedResources.size() != 3) {
            return false;
        }

        for (Resource res : requestedResources) {
            if (getNbResource(res) < 1) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Vérifie si on peut prendre 2 jetons identiques
     * @param res type de ressource
     * @return true s'il reste au moins 4 jetons de ce type
     */
    public boolean canDrawPile(int tier) {
            return !stackCards[tier - 1].isEmpty();
    }
    
    
    
    private String[] deckToStringArray(int tier){
        /** EXAMPLE
         * ┌────────┐
         * │        │╲ 
         * │ reste: │ │
         * │   16   │ │
         * │ cartes │ │
         * │ tier 3 │ │
         * │        │ │
         * └────────┘ │
         *  ╲________╲│
         */
        
        int nbCards = stackCards[tier - 1].size();

        String[] deckStr = {"\u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510  ",
                            "\u2502        \u2502\u2572 ",
                            "\u2502 reste: \u2502 \u2502",
                            "\u2502   "+String.format("%02d", nbCards)+"   \u2502 \u2502",
                            "\u2502 carte"+(nbCards>1 ? "s" : " ")+" \u2502 \u2502",
                            "\u2502 tier "+tier+" \u2502 \u2502",
                            "\u2502        \u2502 \u2502",
                            "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518 \u2502",
                            " \u2572________\u2572\u2502"};
        return deckStr;
    }

    private String[] resourcesToStringArray(){
        /** EXAMPLE
         * Resources disponibles : 4♥R 4♣E 4♠S 4♦D 4●O
         */
        String[] resStr = {"Resources disponibles : "};

        for(Resource res : Resource.values()){
            resStr[0] += resources.getNbResource(res)+res.toSymbol()+" ";
        }

        resStr[0] += "        ";
        return resStr;
    }

    private String[] boardToStringArray(){
        String[] res = Display.emptyStringArray(0, 0);

        //Deck display
        String[] deckDisplay = Display.emptyStringArray(0, 0);
        for(int i=3;i>0;i--){
            deckDisplay = Display.concatStringArray(deckDisplay, deckToStringArray(i), true);
        }

        //Card display
        String[] carteDisplay = Display.emptyStringArray(0, 0);
        for(int i = 0; i < 3; i++){ //-- parcourir les différents niveaux de carte (i)
            String[] tierCardsDisplay = Display.emptyStringArray(8, 0);
            for(int j = 0; j < 4; j++){ //-- parcourir les 4 cartes faces visibles pour un niveau donné (j)
                tierCardsDisplay = Display.concatStringArray(tierCardsDisplay, visibleCards[i][j]!=null ? visibleCards[i][j].toStringArray() : DevCard.noCardStringArray(), false);
            }
            carteDisplay = Display.concatStringArray(carteDisplay, Display.emptyStringArray(1, 40), true);
            carteDisplay = Display.concatStringArray(carteDisplay, tierCardsDisplay, true);
        }
        
        res = Display.concatStringArray(deckDisplay, carteDisplay, false);
        res = Display.concatStringArray(res, Display.emptyStringArray(1, 52), true);
        res = Display.concatStringArray(res, resourcesToStringArray(), true);
        res = Display.concatStringArray(res, Display.emptyStringArray(35, 1, " \u250A"), false);
        res = Display.concatStringArray(res, Display.emptyStringArray(1, 54, "\u2509"), true);

        return res;
    }

    @Override
    public String[] toStringArray() {
        return boardToStringArray();
    }
}
