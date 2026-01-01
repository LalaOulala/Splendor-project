import java.io.File;
import java.io.FileNotFoundException;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;

/**
 * Classe représentant le plateau de jeu Splendor.
 * 
 * Le plateau est l'élément central du jeu et contient :
 * - 3 piles de cartes de développement faces cachées (une par niveau 1, 2, 3)
 * - 12 cartes visibles (4 par tas de niveau) que les joueurs peuvent acheter
 * - Les jetons de ressources disponibles pour tous les joueurs
 * 
 * Cette classe gère l'initialisation complète du jeu :
 * - Lecture du fichier stats.csv contenant toutes les cartes
 * - Création et mélange des piles de cartes
 * - Initialisation du nombre de jetons selon le nombre de joueurs
 * - Mise à jour de l'état du plateau pendant la partie
 * 
 * Choix d'implémentation :
 * - Stack pour les piles de cartes (structure LIFO adaptée aux piles physiques)
 * - Tableau 2D pour les cartes visibles (3 niveaux × 4 cartes)
 * - Objet Resources pour gérer les jetons disponibles
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class Board implements Displayable {

    /**
     * Piles de cartes faces cachées.
     * Tableau de 3 Stack, une pour chaque niveau (1, 2, 3).
     * Index 0 = niveau 1, index 1 = niveau 2, index 2 = niveau 3.
     * Les cartes sont piochées du dessus de ces piles quand une carte visible est achetée.
     */
    private Stack<DevCard>[] stackCards;
    
    /**
     * Cartes faces visibles que les joueurs peuvent acheter.
     * Tableau 2D de dimensions 3 lignes (niveaux) × 4 colonnes (cartes).
     * Contient null si une pile est épuisée et qu'il n'y a plus de carte à afficher.
     */
    private DevCard[][] visibleCards;
    
    /**
     * Jetons de ressources disponibles sur le plateau.
     * Ces jetons peuvent être pris par les joueurs ou rendus lors du paiement d'une carte.
     * Le nombre initial dépend du nombre de joueurs (règles du jeu).
     */
    private Resources resources;
    
    
    /**
     * Constructeur du plateau de jeu.
     * Initialise complètement le plateau en plusieurs étapes :
     * 1. Création des structures de données (piles, cartes visibles, ressources)
     * 2. Lecture du fichier stats.csv et création de toutes les cartes
     * 3. Mélange aléatoire des 3 piles de cartes
     * 4. Révélation de 4 cartes par niveau
     * 5. Initialisation des jetons selon le nombre de joueurs
     * 
     * Nombre de jetons par type selon les règles :
     * - 2 joueurs : 4 jetons
     * - 3 joueurs : 5 jetons
     * - 4 joueurs : 7 jetons
     * 
     * @param nbPlayers nombre de joueurs (2, 3 ou 4)
     */
    public Board(int nbPlayers) {
        
        // Initialisation des 3 piles de cartes faces cachées
        stackCards = new Stack[3];
        stackCards[0] = new Stack<>();  // Niveau 1
        stackCards[1] = new Stack<>();  // Niveau 2
        stackCards[2] = new Stack<>();  // Niveau 3
        
        // Initialisation du tableau des cartes visibles
        visibleCards = new DevCard[3][4];
        
        // Calcul du nombre de jetons selon le nombre de joueurs
        int nombreJetons = nbPlayers + 2;
        if (nbPlayers == 4) {
            nombreJetons = 7;
        }
        resources = new Resources(nombreJetons, nombreJetons, nombreJetons, nombreJetons, nombreJetons);
        
        
        // Lecture du fichier CSV et création des cartes
        try {
            Scanner scanner = new Scanner(new File("stats.csv"));
            
            scanner.nextLine(); // Sauter l'en-tête du fichier
            
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] donnees = line.split(",");
                
                // Extraction du niveau
                int tierCarte = Integer.parseInt(donnees[0]);
                
                // Extraction du coût
                int coutDiamond = Integer.parseInt(donnees[1]);
                int coutSapphire = Integer.parseInt(donnees[2]);
                int coutEmerald = Integer.parseInt(donnees[3]);
                int coutRuby = Integer.parseInt(donnees[4]);
                int coutOnyx = Integer.parseInt(donnees[5]);
                Resources coutCarte = new Resources(coutDiamond, coutSapphire, coutEmerald, coutOnyx, coutRuby);
                
                // Extraction des points de prestige
                int pointsCarte = Integer.parseInt(donnees[6]);
                
                // Ignorer les nobles (tier = 0) car non implémentés dans la version simplifiée
                if (tierCarte != 0) {
                    // Extraction du type de ressource
                    Resource typeCarte = Resource.valueOf(donnees[7]);
                    
                    // Création de la carte et ajout à sa pile respective
                    DevCard carte = new DevCard(tierCarte, coutCarte, pointsCarte, typeCarte);
                    stackCards[tierCarte - 1].push(carte);
                }
            }
            
            scanner.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : fichier stats.csv contenant les cartes introuvable !");
            e.printStackTrace();
        }
        
        
        // Mélange des 3 piles pour randomiser l'ordre des cartes
        Collections.shuffle(stackCards[0]);
        Collections.shuffle(stackCards[1]);
        Collections.shuffle(stackCards[2]);
        
        
        // Révélation des 4 premières cartes de chaque pile
        for (int tier = 0; tier < 3; tier++) {
            for (int colonne = 0; colonne < 4; colonne++) {
                visibleCards[tier][colonne] = stackCards[tier].pop();
            }
        }
    }
    
    
    // ============= GESTION DES JETONS =============
    
    /**
     * Retourne le nombre de jetons d'un type donné sur le plateau.
     * 
     * @param res type de ressource à consulter
     * @return nombre de jetons disponibles de ce type
     */
    public int getNbResource(Resource res) {
        return resources.getNbResource(res);
    }
    
    /**
     * Initialise le nombre de jetons d'un type donné.
     * Cette méthode est principalement utilisée lors de l'initialisation du plateau.
     * 
     * @param res type de ressource
     * @param nb nombre de jetons à définir
     */
    public void setNbResource(Resource res, int nb) {
        resources.setNbResource(res, nb);
    }
    
    /**
     * Ajoute ou retire des jetons sur le plateau.
     * Utilisé quand un joueur prend des jetons (v négatif pour le plateau)
     * ou quand il paie/défausse des jetons (v positif pour le plateau).
     * 
     * @param res type de ressource
     * @param v quantité à ajouter (v > 0) ou retirer (v < 0)
     */
    public void updateNbResource(Resource res, int v) {
        resources.updateNbResource(res, v);
    }
    
    /**
     * Retourne l'objet Resources complet du plateau.
     * Permet d'accéder à toutes les ressources en une seule fois.
     * 
     * @return l'objet Resources contenant tous les jetons du plateau
     */
    public Resources getResources() {
        return resources;
    }
    
    
    // ============= GESTION DES CARTES =============
    
    /**
     * Récupère une carte visible à une position donnée.
     * Les coordonnées utilisent un système 1-indexed pour le tier (1, 2, 3)
     * et 0-indexed pour la colonne (0, 1, 2, 3).
     * 
     * @param tier niveau de la carte (1, 2 ou 3)
     * @param colonne colonne de la carte (0, 1, 2 ou 3)
     * @return la carte à cette position, ou null si aucune carte n'est disponible
     */
    public DevCard getCard(int tier, int colonne) {
        return visibleCards[tier - 1][colonne];
    }
    
    /**
     * Remplace une carte achetée par une nouvelle de la pile correspondante.
     * Cherche la carte dans le tableau des cartes visibles, puis :
     * - Si la pile n'est pas vide : remplace par la carte du dessus de la pile
     * - Si la pile est vide : met null à cet emplacement
     * 
     * Cette méthode est appelée automatiquement après l'achat d'une carte
     * pour maintenir toujours 4 cartes visibles par niveau (ou moins si épuisé).
     * 
     * @param carte la carte qui vient d'être achetée
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
     * Pioche une carte face cachée depuis le dessus d'une pile.
     * Cette méthode peut être utilisée pour des mécanismes bonus
     * (réservation de carte non implémentée dans la version simplifiée).
     * 
     * @param tier niveau de la pile (1, 2 ou 3)
     * @return la carte piochée, ou null si la pile est vide
     */
    public DevCard drawCard(int tier) {
        if (canDrawPile(tier)) {
            return stackCards[tier - 1].pop();
        }
        return null;
    }
    
    
    // ============= VÉRIFICATIONS DES ACTIONS POSSIBLES =============
    
    /**
     * Vérifie si le plateau peut donner 2 jetons identiques.
     * Selon les règles du jeu, il faut au moins 4 jetons disponibles
     * pour pouvoir en prendre 2 du même type.
     * 
     * @param res type de ressource demandée
     * @return true si le plateau a au moins 4 jetons de ce type
     */
    public boolean canGiveSameTokens(Resource res) {
        return getNbResource(res) >= 4;
    }
    
    /**
     * Vérifie si le plateau peut donner des jetons différents.
     * Vérifie que chaque ressource demandée a au moins 1 jeton disponible.
     * 
     * Note : Cette méthode accepte de 1 à 3 ressources pour gérer le cas
     * où il ne reste plus assez de types de ressources sur le plateau
     * (amélioration personnelle permettant de prendre moins de 3 jetons en fin de partie).
     * 
     * @param requestedResources liste des ressources demandées (1 à 3 types différents)
     * @return true si chaque ressource demandée est disponible
     */
    public boolean canGiveDiffTokens(List<Resource> requestedResources) {
        
        // Vérifier qu'il y a entre 1 et 3 ressources
        if (requestedResources.size() < 1 || requestedResources.size() > 3) {
            return false;
        }
        
        // Vérifier que chaque ressource est disponible
        for (Resource res : requestedResources) {
            if (getNbResource(res) < 1) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Vérifie si une pile contient encore des cartes.
     * Utile pour savoir si on peut piocher ou remplacer une carte visible.
     * 
     * @param tier niveau de la pile à vérifier (1, 2 ou 3)
     * @return true si la pile contient au moins une carte
     */
    public boolean canDrawPile(int tier) {
        return !stackCards[tier - 1].isEmpty();
    }
    
    
    // ============= MÉTHODES D'AFFICHAGE =============
    
    /**
     * Génère la représentation ASCII d'une pile de cartes faces cachées.
     * Affiche le dos de la pile avec le nombre de cartes restantes.
     * 
     * Exemple de rendu :
     * ┌────────┐
     * │        │╲
     * │ reste: │ │
     * │   16   │ │
     * │ cartes │ │
     * │ tier 3 │ │
     * │        │ │
     * └────────┘ │
     *  ╲________╲│
     * 
     * @param tier niveau de la pile (1, 2 ou 3)
     * @return un tableau de String représentant la pile
     */
    private String[] deckToStringArray(int tier) {
        int nbCards = stackCards[tier - 1].size();
        
        String[] deckStr = {
            "\u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510  ",
            "\u2502        \u2502\u2572 ",
            "\u2502 reste: \u2502 \u2502",
            "\u2502   " + String.format("%02d", nbCards) + "   \u2502 \u2502",
            "\u2502 carte" + (nbCards > 1 ? "s" : " ") + " \u2502 \u2502",
            "\u2502 tier " + tier + " \u2502 \u2502",
            "\u2502        \u2502 \u2502",
            "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518 \u2502",
            " \u2572________\u2572\u2502"
        };
        
        return deckStr;
    }
    
    /**
     * Génère la représentation textuelle des ressources disponibles.
     * Affiche tous les jetons disponibles sur le plateau avec leurs symboles.
     * 
     * Exemple : "Resources disponibles : 4♦D 4♠S 4♣E 4♥R 4●O"
     * 
     * @return un tableau contenant une seule ligne avec toutes les ressources
     */
    private String[] resourcesToStringArray() {
        String[] resStr = {"Resources disponibles : "};
        
        for (Resource res : Resource.values()) {
            resStr[0] += resources.getNbResource(res) + res.toSymbol() + " ";
        }
        
        resStr[0] += "        ";
        return resStr;
    }
    
    /**
     * Génère la représentation complète du plateau de jeu.
     * Assemble visuellement :
     * - Les 3 piles de cartes faces cachées (à gauche)
     * - Les 12 cartes visibles organisées par niveau (à droite)
     * - Les ressources disponibles (en bas)
     * 
     * Utilise la classe Display pour concaténer les différents éléments
     * en respectant l'alignement et les espacements.
     * 
     * @return un tableau de String représentant tout le plateau
     */
    private String[] boardToStringArray() {
        String[] res = Display.emptyStringArray(0, 0);
        
        // Affichage des piles faces cachées (niveau 3, 2, 1 de haut en bas)
        String[] deckDisplay = Display.emptyStringArray(0, 0);
        for (int i = 3; i > 0; i--) {
            deckDisplay = Display.concatStringArray(deckDisplay, deckToStringArray(i), true);
        }
        
        // Affichage des cartes visibles (niveau 3, 2, 1 de haut en bas)
        String[] carteDisplay = Display.emptyStringArray(0, 0);
        for (int i = 2; i >= 0; i--) {
            String[] tierCardsDisplay = Display.emptyStringArray(8, 0);
            
            // Affichage des 4 cartes du niveau
            for (int j = 0; j < 4; j++) {
                tierCardsDisplay = Display.concatStringArray(
                    tierCardsDisplay,
                    visibleCards[i][j] != null ? visibleCards[i][j].toStringArray() : DevCard.noCardStringArray(),
                    false
                );
            }
            
            carteDisplay = Display.concatStringArray(carteDisplay, Display.emptyStringArray(1, 40), true);
            carteDisplay = Display.concatStringArray(carteDisplay, tierCardsDisplay, true);
        }
        
        // Assemblage final : piles + cartes + ressources + bordures
        res = Display.concatStringArray(deckDisplay, carteDisplay, false);
        res = Display.concatStringArray(res, Display.emptyStringArray(1, 52), true);
        res = Display.concatStringArray(res, resourcesToStringArray(), true);
        res = Display.concatStringArray(res, Display.emptyStringArray(35, 1, " \u250A"), false);
        res = Display.concatStringArray(res, Display.emptyStringArray(1, 54, "\u2509"), true);
        
        return res;
    }
    
    /**
     * Implémentation de l'interface Displayable.
     * Retourne la représentation complète du plateau pour l'affichage.
     * 
     * @return un tableau de String représentant le plateau complet
     */
    @Override
    public String[] toStringArray() {
        return boardToStringArray();
    }
}
