/**
 * Interface pour les éléments affichables du jeu Splendor.
 * <p>
 * Cette interface définit le contrat pour tous les objets qui doivent être
 * affichés dans le terminal sous forme de représentation ASCII. Elle est
 * utilisée en conjonction avec la classe Display pour créer l'interface
 * utilisateur textuelle du jeu, avec un affichage en deux zones :
 * la zone supérieure affiche l'état du plateau de jeu, et la zone inférieure
 * affiche la console d'interaction avec le joueur.
 * </p>
 * <p>
 * Les classes implémentant cette interface incluent notamment Board, Player,
 * et DevCard, permettant d'afficher respectivement le plateau complet, les
 * informations d'un joueur, et une carte de développement.
 * </p>
 * 
 * @author Fourni par l'enseignant
 * @version 01/01/2026
 */
public interface Displayable {
    
    /**
     * Convertit l'objet en un tableau de chaînes de caractères pour l'affichage.
     * 
     * Cette méthode retourne une représentation visuelle de l'objet sous forme
     * de tableau de String, où chaque élément correspond à une ligne de
     * l'affichage dans le terminal. Cette représentation utilise des caractères
     * ASCII/Unicode pour créer un rendu visuel structuré (bordures avec
     * │ ─ ┌ └ ┐ ┘, symboles de ressources ♦D ♠S ♣E ♥R ●O, etc.).
     * 
     * Chaque ligne du tableau retourné est ensuite assemblée par la classe
     * Display pour créer l'affichage complet du jeu dans le terminal.
     * 
     * 
     * @return un tableau de String où chaque élément représente une ligne
     *         de l'affichage de l'objet
     */
    public String[] toStringArray();
}
