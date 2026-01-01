/**
 * Classe représentant une carte de développement (Development Card) dans le jeu Splendor.
 * 
 * Une carte de développement possède quatre caractéristiques principales :
 * - Un tier : 1, 2 ou 3, correspondant à la difficulté et la valeur de la carte
 * - Un coût en ressources : nombre de jetons nécessaires pour acheter la carte
 * - Des points de prestige : contribuent à la victoire (objectif : 15 points)
 * - Un type de ressource bonus : bonus permanent accordé au joueur qui achète la carte
 * 
 * Les cartes sont chargées depuis le fichier stats.csv lors de l'initialisation du plateau.
 * Dans le jeu physique Splendor, il existe 90 cartes de développement réparties en 3 tierx.
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class DevCard implements Displayable {
    
    /**
     * tier de la carte : 1 (facile), 2 (moyen) ou 3 (difficile).
     * Les cartes de tier supérieur coûtent généralement plus cher
     * mais donnent plus de points de prestige.
     */
    private int tier;
    
    /**
     * Coût de la carte en ressources.
     * Représente le nombre de jetons de chaque type nécessaires pour acheter la carte.
     * Exemple : 3 saphirs + 2 rubis.
     */
    private Resources cost;
    
    /**
     * Points de prestige rapportés par la carte.
     * Certaines cartes ne donnent aucun point (0), d'autres en donnent entre 1 et 5.
     * L'objectif du jeu est d'atteindre 15 points pour gagner.
     */
    private int points;
    
    /**
     * Type de ressource produite par la carte (bonus permanent).
     * Une fois achetée, la carte donne un bonus permanent de ce type,
     * réduisant le coût des prochaines cartes achetées.
     */
    private Resource resourceType;
    
    
    /**
     * Construit une nouvelle carte de développement avec ses caractéristiques.
     * Ce constructeur est appelé lors de la lecture du fichier stats.csv pour créer
     * toutes les cartes du jeu.
     * 
     * @param tier tier de la carte (1, 2 ou 3)
     * @param cost coût en ressources pour acheter la carte
     * @param points points de prestige rapportés par la carte
     * @param resourceType type de ressource bonus produite par la carte
     */
    public DevCard(int tier, Resources cost, int points, Resource resourceType) {
        this.tier = tier;
        this.cost = cost;
        this.points = points;
        this.resourceType = resourceType;
    }
    
    /**
     * Retourne le tier de la carte.
     * 
     * @return le tier de la carte (1, 2 ou 3)
     */
    public int getTier() {
        return tier;
    }
    
    /**
     * Retourne le coût de la carte en ressources.
     * 
     * @return un objet Resources contenant le nombre de jetons de chaque type nécessaires
     */
    public Resources getCost() {
        return cost;
    }
    
    /**
     * Retourne les points de prestige rapportés par cette carte.
     * 
     * @return le nombre de points (entre 0 et 5)
     */
    public int getPoints() {
        return points;
    }
    
    /**
     * Retourne le type de ressource bonus produite par cette carte.
     * 
     * @return le type de ressource (DIAMOND, SAPPHIRE, EMERALD, RUBY ou ONYX)
     */
    public Resource getResourceType() {
        return resourceType;
    }

    /**
     * Convertit la carte en représentation ASCII art pour l'affichage dans le terminal.
     * 
     * La carte est affichée sous forme de rectangle avec :
     * - En haut à gauche : les points de prestige (si > 0) en caractère encerclé
     * - En haut à droite : le symbole du bonus de ressource
     * - En bas : la liste des coûts (uniquement les ressources dont le coût > 0)
     * 
     * Exemple d'affichage :
     * ┌────────┐
     * │①     ♠S│
     * │        │
     * │        │
     * │2 ♠S    │
     * │2 ♣E    │
     * │3 ♥R    │
     * └────────┘
     * 
     * @return un tableau de 8 String, chaque élément représentant une ligne de la carte
     */
    public String[] toStringArray() {
        String pointStr = "  ";
        
        // Si la carte donne des points, les afficher en caractère encerclé (①②③④⑤)
        if (getPoints() > 0) {
            pointStr = new String(new int[] {getPoints() + 9311}, 0, 1);
        }
        
        // Structure de base de la carte avec bordures Unicode
        String[] cardStr = {
            "\u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510",  //    ┌────────┐
            "\u2502" + pointStr + "    " + resourceType.toSymbol() + "\u2502",  // │①     ♠S│
            "\u2502        \u2502",
            "\u2502        \u2502",
            "\u2502        \u2502",
            "\u2502        \u2502",
            "\u2502        \u2502",
            "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518"   //    └────────┘
        };
        
        // Ajouter les coûts en partant du bas (ligne 6) et en remontant
        int i = 6;
        for (Resource res : Resource.values()) {
            if (getCost().getNbResource(res) > 0) {
                cardStr[i] = "\u2502" + getCost().getNbResource(res) + " " + res.toSymbol() + "    \u2502";
                i--;
            }
        }
        
        return cardStr;
    }

    /**
     * Retourne une représentation ASCII d'un emplacement vide (pas de carte disponible).
     * 
     * Cette méthode statique est utilisée quand une pile de cartes est épuisée et qu'il n'y a
     * plus de carte à afficher à cet emplacement. Le visuel montre un X stylisé pour indiquer
     * l'absence de carte.
     * 
     * Exemple d'affichage :
     * ┌────────┐
     * │ \    / │
     * │  \  /  │
     * │   \/   │
     * │   /\   │
     * │  /  \  │
     * │ /    \ │
     * └────────┘
     * 
     * @return un tableau de 8 String représentant un emplacement vide
     */
    public static String[] noCardStringArray() {
        String[] cardStr = {
            "\u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510",
            "\u2502 \\    / \u2502",
            "\u2502  \\  /  \u2502",
            "\u2502   \\/   \u2502",
            "\u2502   /\\   \u2502",
            "\u2502  /  \\  \u2502",
            "\u2502 /    \\ \u2502",
            "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518"
        };
        
        return cardStr;
    }

    /**
     * Retourne une représentation textuelle simple de la carte.
     * 
     * Cette méthode est utilisée pour le débogage et les logs et quelques rares affichages. 
     * Elle affiche sur une seule ligne :
     * - Les points de prestige
     * - Le type de bonus
     * - Le coût (uniquement les ressources dont le coût > 0)
     * 
     * Exemple : "2pts, type ♦D | coût: 3♠S 2♣E"
     * 
     * @return une chaîne décrivant la carte de manière compacte
     */
    @Override
    public String toString() {
        String cardStr = getPoints() + "pts, type " + resourceType.toSymbol() + " | coût: ";
        
        for (Resource res : Resource.values()) {
            if (getCost().getNbResource(res) > 0) {
                cardStr += getCost().getNbResource(res) + res.toSymbol() + " ";
            }
        }
        
        return cardStr;
    }
}
