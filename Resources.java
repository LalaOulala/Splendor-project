/**
 * Classe Resources - Représente une collection de ressources/jetons
 * Utilisée pour : le coût des cartes, les jetons du plateau, les jetons des joueurs
 * 
 * Implémentation : tableau d'entiers avec ordre fixe correspondant à l'énumération Resource
 * 
 * @author [Ton nom]
 * @version 1.0
 */
public class Resources {
    
    // Attribut : tableau de 5 entiers pour stocker les quantités
    private int[] resources;
    
    /**
     * Constructeur par défaut
     * Initialise toutes les ressources à 0
     */
    public Resources() {
        resources = new int[5]; // Tableau de 5 cases, initialisé à [0, 0, 0, 0, 0]
    }
    
    /**
     * Constructeur avec valeurs initiales
     * @param diamond nombre de diamants
     * @param sapphire nombre de saphirs
     * @param emerald nombre d'émeraudes
     * @param onyx nombre d'onyx
     * @param ruby nombre de rubis
     */
    public Resources(int diamond, int sapphire, int emerald, int onyx, int ruby) {
        resources = new int[5];
        resources[0] = diamond;
        resources[1] = sapphire;
        resources[2] = emerald;
        resources[3] = onyx;
        resources[4] = ruby;
    }
    
    /**
     * Retourne le nombre de ressources disponibles pour un type donné
     * @param res le type de ressource
     * @return le nombre de ressources de ce type
     */
    public int getNbResource(Resource res) {
        return resources[res.ordinal()];
    }
    
    /**
     * Modifie le nombre de ressources d'un type donné (mutateur)
     * @param res le type de ressource
     * @param nb le nouveau nombre de ressources
     */
    public void setNbResource(Resource res, int nb) {
        resources[res.ordinal()] = nb;
    }
    
    /**
     * Ajoute ou retire une quantité de ressources d'un type donné
     * @param res le type de ressource
     * @param v la quantité à ajouter (v>0) ou retirer (v<0)
     * CONTRAINTE : le nombre de ressources ne peut jamais être négatif
     */
    public void updateNbResource(Resource res, int v) {
        int newValue = resources[res.ordinal()] + v;
        
        // Contrainte : pas de ressources négatives
        if (newValue < 0) {
            resources[res.ordinal()] = 0;
        } else {
            resources[res.ordinal()] = newValue;
        }
    }
    
    /**
     * Représentation textuelle des ressources
     * Utile pour le débogage
     * @return une chaîne décrivant les ressources
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
