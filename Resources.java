import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant une liste de ressources (jetons) dans le jeu Splendor.
 * 
 * Cette classe est utilisée pour stocker des quantités de ressources dans trois contextes :
 * - Le coût d'une carte de développement
 * - Les jetons disponibles sur le plateau de jeu
 * - Les jetons possédés par chaque joueur
 * 
 * Implémentation : utilise un tableau d'entiers avec un ordre fixe correspondant
 * à l'énumération Resource (DIAMOND, SAPPHIRE, EMERALD, ONYX, RUBY, GOLD).
 * L'accès aux ressources se fait via la méthode ordinal() de l'énumération.
 * 
 * @author FONFREIDE Quentin
 * @version 02/01/2026
 */
public class Resources {
    /**
     * Tableau stockant les quantités de chaque type de ressource.
     * Index 0 = DIAMOND, 1 = SAPPHIRE, 2 = EMERALD, 3 = ONYX, 4 = RUBY, 5 = GOLD.
     * Cet ordre correspond exactement à l'ordre de l'énumération Resource.
     */
    private int[] resources;

    /**
     * Constructeur par défaut.
     * Initialise toutes les ressources à 0.
     */
    public Resources() {
        resources = new int[6];
    }

    /**
     * Constructeur avec valeurs initiales.
     * Permet de créer directement un objet Resources avec des quantités spécifiques.
     * 
     * @param diamond nombre de diamants
     * @param sapphire nombre de saphirs
     * @param emerald nombre d'émeraudes
     * @param onyx nombre d'onyx
     * @param ruby nombre de rubis
     */
    public Resources(int diamond, int sapphire, int emerald, int onyx, int ruby) {
        resources = new int[6];
        resources[0] = diamond;
        resources[1] = sapphire;
        resources[2] = emerald;
        resources[3] = onyx;
        resources[4] = ruby;
        resources[5] = 0; 
    }
    
    // ← NOUVEAU : Constructeur avec GOLD
    /**
     * Constructeur avec valeurs initiales incluant les jetons Or.
     * Permet de créer directement un objet Resources avec des quantités spécifiques,
     * y compris les jetons Or (utilisé principalement pour l'initialisation du plateau).
     * 
     * @param diamond nombre de diamants
     * @param sapphire nombre de saphirs
     * @param emerald nombre d'émeraudes
     * @param onyx nombre d'onyx
     * @param ruby nombre de rubis
     * @param gold nombre de jetons Or
     */
    public Resources(int diamond, int sapphire, int emerald, int onyx, int ruby, int gold) {
        resources = new int[6];
        resources[0] = diamond;
        resources[1] = sapphire;
        resources[2] = emerald;
        resources[3] = onyx;
        resources[4] = ruby;
        resources[5] = gold;
    }
    
    /**
     * Retourne le nombre de ressources disponibles pour un type donné.
     * Utilise la méthode ordinal() de l'énumération pour accéder directement
     * à l'index correspondant dans le tableau.
     * 
     * @param res le type de ressource à consulter
     * @return le nombre de ressources de ce type (toujours >= 0)
     */
    public int getNbResource(Resource res) {
        return resources[res.ordinal()];
    }
    
    /**
     * Modifie le nombre de ressources d'un type donné (mutateur).
     * Cette méthode est principalement utilisée lors de l'initialisation
     * du plateau de jeu pour définir le nombre de jetons disponibles
     * selon le nombre de joueurs.
     * 
     * @param res le type de ressource à modifier
     * @param nb le nouveau nombre de ressources
     */
    public void setNbResource(Resource res, int nb) {
        resources[res.ordinal()] = nb;
    }
    
    /**
     * Ajoute ou retire une quantité de ressources d'un type donné.
     * Cette méthode est la plus utilisée pendant le jeu pour :
     * - Ajouter des jetons quand un joueur les prend (v > 0)
     * - Retirer des jetons quand un joueur paie une carte (v < 0)
     * 
     * CONTRAINTE IMPORTANTE : Le nombre de ressources ne peut jamais être négatif.
     * Si le calcul donne un résultat négatif, la quantité est fixée à 0.
     * 
     * @param res le type de ressource à modifier
     * @param v la quantité à ajouter si v > 0, ou à retirer si v < 0
     */
    public void updateNbResource(Resource res, int v) {
        int newValue = resources[res.ordinal()] + v;
        
        if (newValue < 0) {
            resources[res.ordinal()] = 0;
        } else {
            resources[res.ordinal()] = newValue;
        }
    }
    
    /**
     * Retourne la liste des types de ressources disponibles (quantité > 0).
     * Cette méthode est utilisée pour afficher uniquement les ressources pertinentes
     * et pour vérifier quelles actions sont possibles (ex : prendre 3 jetons différents).
     * 
     * @return une liste contenant uniquement les types de ressources dont la quantité est supérieure à 0
     */
    public List<Resource> getAvailableResources() {
        List<Resource> available = new ArrayList<>();
        
        for (Resource res : Resource.values()) {
            if (getNbResource(res) > 0) {
                available.add(res);
            }
        }
        
        return available;
    }
    
    /**
     * Retourne une représentation textuelle des ressources.
     * Affiche uniquement les ressources dont la quantité est supérieure à 0,
     * avec leur symbole compact (ex : "3♦D 2♠S").
     * Utile pour le débogage et les logs.
     * 
     * @return une chaîne décrivant les ressources disponibles avec leurs symboles
     */
    @Override
    public String toString() {
        String result = "Resources: ";
        for (Resource res : Resource.values()) {
            int nb = getNbResource(res);
            if (nb > 0) {
                result += nb + res.toSymbol() + " ";
            }
        }
        return result;
    }
    
}
