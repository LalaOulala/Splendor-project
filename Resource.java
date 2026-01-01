/**
 * Énumération représentant les cinq types de ressources dans le jeu Splendor.
 *
 * Chaque type de ressource correspond à une pierre précieuse utilisée comme monnaie
 * dans le jeu. Les joueurs collectent ces ressources sous forme de jetons pour acheter
 * des cartes de développement. Chaque ressource possède un symbole Unicode unique
 * pour l'affichage dans le terminal.
 * 
 * Les cinq types de ressources sont :
 *
 *   DIAMOND (Diamant) - symbole ♦D
 *   SAPPHIRE (Saphir) - symbole ♠S
 *   EMERALD (Émeraude) - symbole ♣E
 *   RUBY (Rubis) - symbole ♥R
 *   ONYX (Onyx) - symbole ●O
 * 
 * L'ordre des valeurs dans cette énumération est important car il correspond à l'ordre
 * utilisé dans la classe Resources pour l'indexation du tableau interne (via la méthode
 * ordinal()).
 * 
 * @author Fourni par l'enseignant
 * @version 01/01/2026
 */
public enum Resource {
    DIAMOND,
    SAPPHIRE,
    EMERALD,
    ONYX,
    RUBY;

    /**
     * Retourne une représentation textuelle complète de la ressource.
     * 
     * Cette méthode retourne le nom complet de la ressource en français
     * suivi de son symbole Unicode, par exemple "DIAMANT ♦" ou "SAPHIR ♠".
     * Elle est utilisée pour l'affichage détaillé dans l'interface utilisateur.
     * 
     * @return une chaîne contenant le nom complet et le symbole de la ressource
     */
    public String toString(){
        switch(this){
            case EMERALD:
                return "EMERAUDE \u2663"; // EMERAUDE ♣
            case DIAMOND:
                return "DIAMANT \u2666"; // DIAMANT ♦
            case SAPPHIRE:
                return "SAPHIR \u2660"; // SAPHIR ♠
            case ONYX:
                return "ONYX \u25CF"; // ONYX ●
            case RUBY:
                return "RUBIS \u2665"; // RUBIS ♥
            default:
                return "";
        }
    }

    /**
     * Retourne le symbole compact de la ressource pour l'affichage.
     * 
     * Cette méthode retourne une version abrégée de la ressource composée
     * de son symbole Unicode suivi de sa lettre initiale, par exemple "♦D"
     * pour diamant ou "♠S" pour saphir. Cette représentation compacte est
     * utilisée pour l'affichage des cartes et du plateau de jeu dans le terminal.
     * 
     * 
     * @return une chaîne contenant le symbole et l'initiale de la ressource
     */
    public String toSymbol(){
        switch(this){
            case EMERALD:
                return "\u2663E"; // ♣E
            case DIAMOND:
                return "\u2666D"; // ♦D
            case SAPPHIRE:
                return "\u2660S"; // ♠S
            case ONYX:
                return "\u25CFO"; // ●O
            case RUBY:
                return "\u2665R"; // ♥R
            default:
                return "";
        }
    }
}
