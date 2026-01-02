import java.util.*;

/**
 * Classe représentant une carte Noble dans le jeu Splendor.
 * 
 * Les nobles sont des personnages qui visitent le joueur lorsque celui-ci
 * possède suffisamment de bonus (cartes achetées) d'un certain type.
 * Chaque noble rapporte 3 points de prestige.
 * 
 * @author Quentin FONFREIDE
 * @version Janvier 2026
 */
public class Noble implements Displayable {
    
    // ==================== ATTRIBUTS ====================
    
    /**
     * Les ressources (bonus de cartes) nécessaires pour obtenir ce noble.
     * 
     * Important : Ce sont des BONUS de cartes, pas des jetons. Par exemple,
     * si le coût est "3 diamants, 3 saphirs", cela signifie que le joueur
     * doit posséder au moins 3 cartes qui produisent du diamant ET 3 cartes
     * qui produisent du saphir. Les jetons possédés n'ont aucune importance
     * pour l'obtention des nobles.
     */
    private Resources cost;
    
    /**
     * Les points de prestige rapportés par ce noble.
     * 
     * Dans Splendor, tous les nobles rapportent toujours 3 points de prestige.
     * Cet attribut est gardé flexible pour permettre d'éventuelles variantes.
     */
    private int points;
    
    // ==================== CONSTRUCTEUR ====================
    
    /**
     * Construit un noble avec un coût et des points donnés.
     * 
     * Dans Splendor standard, points vaut toujours 3, mais le paramètre
     * est laissé flexible pour d'éventuelles extensions du jeu.
     * 
     * @param cost Les ressources (bonus de cartes) nécessaires pour obtenir ce noble.
     *             Par exemple : Resources(3,3,3,0,0) signifie 3 diamants, 3 saphirs, 
     *             3 émeraudes requis.
     * @param points Les points de prestige rapportés (toujours 3 dans Splendor standard)
     */
    public Noble(Resources cost, int points) {
        this.cost = cost;
        this.points = points;
    }
    
    // ==================== ACCESSEURS ====================
    
    /**
     * Retourne le coût en ressources (bonus de cartes) de ce noble.
     * 
     * Rappel : Ce sont des bonus, pas des jetons. Pour obtenir un noble,
     * le joueur doit avoir acheté suffisamment de cartes produisant les
     * ressources demandées.
     * 
     * @return Un objet Resources contenant les bonus requis pour chaque type de ressource
     */
    public Resources getCost() {
        return this.cost;
    }
    
    /**
     * Retourne les points de prestige rapportés par ce noble.
     * 
     * @return Le nombre de points (généralement 3)
     */
    public int getPoints() {
        return this.points;
    }
    
    // ==================== MÉTHODES D'AFFICHAGE ====================
    
    /**
     * Retourne une représentation ASCII visuelle du noble sous forme de tableau de lignes.
     * 
     * Affiche une carte noble avec :
     * - Le symbole ⚜ (fleur de lys) et le titre "NOBLE"
     * - Les ressources (bonus de cartes) nécessaires pour obtenir ce noble
     * 
     * Format :
     * ┌───────────┐
     * │ ⚜ NOBLE   │
     * │  3♦ 3♠ 3♣ │
     * └───────────┘
     * 
     * Largeur fixe : 13 caractères (bordures incluses)
     * Hauteur fixe : 4 lignes
     * 
     * Note : Le symbole ⚜ (U+269C) prend 2 caractères d'affichage dans la police
     * monospace, ce qui nécessite un calcul spécial de la longueur avec
     * displayedLengthWithSymbols() pour l'alignement correct.
     * 
     * Les ressources sont affichées uniquement si leur quantité est > 0.
     * Exemple : si le coût est (3,3,0,0,0), on affiche "3♦ 3♠" et non "3♦ 3♠ 0♣ 0♥ 0●".
     * 
     * @return Un tableau de 4 String représentant la carte noble
     */
    public String[] toStringArray() {
        String[] tab = new String[4];
        
        // Ligne 0 : Bordure supérieure (12 tirets)
        tab[0] = "\u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510";
        
        // Ligne 1 : Titre avec symbole noble
        tab[1] = "\u2502 \u269C NOBLE  \u2502";
        
        // Ligne 3 : Ressources nécessaires (bonus de cartes)
        String costLine = "\u2502";
        
        // Parcourir les ressources et afficher celles qui sont nécessaires
        String line = "";
        boolean first = true;
        for (Resource res : Resource.values()) {
            int nb = cost.getNbResource(res);
            if (nb > 0) {
                if (!first) {
                    line += " ";  // Ajouter un espace AVANT (sauf pour le premier)
                }
                line += nb + res.toSymbol();
                first = false;
            }
        }

        
        // Compléter avec des espaces pour avoir 11 caractères intérieurs
        int displayedLen = displayedLengthWithSymbols(line);
        if (displayedLen < 11) {  // 12 car on compte le ║ initial
            costLine += "  ";
            costLine += line;
            costLine += "  ";
            displayedLen += 4;
        }else{
            costLine += line;
        }
        costLine += "\u2502";
        tab[2] = costLine;
        
        // Ligne 3 : Bordure inférieure
        tab[3] = "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518";
        
        return tab;
    }
    
    /**
     * Calcule la longueur d'affichage réelle d'une chaîne contenant des symboles de ressources.
     * 
     * (Inspirée et sortie de la classe Display donnée par le Professeur)
     * 
     * Les symboles de ressources Unicode (♦♠♣♥●) prennent 2 caractères d'affichage
     * au lieu de 1 dans la police monospace. Cette méthode compte correctement
     * la largeur visuelle pour permettre un alignement précis dans toStringArray().
     * 
     * Symboles comptés comme 2 caractères :
     * - ♦ (U+2666 = 9830) : Diamant
     * - ♠ (U+2660 = 9824) : Saphir
     * - ♣ (U+2663 = 9827) : Émeraude
     * - ♥ (U+2665 = 9829) : Rubis
     * - ● (U+25CF = 9679) : Onyx
     * 
     * Tous les autres caractères (chiffres, espaces, lettres) comptent comme 1.
     * 
     * @param str La chaîne dont on veut calculer la longueur d'affichage
     * @return La longueur d'affichage en nombre de caractères monospace
     */
    private static int displayedLengthWithSymbols(String str) {
        int length = 0;
        for (int i = 0; i < str.length(); i++) {
            int cp = str.codePointAt(i);
            if (cp == 9830 || cp == 9824 || cp == 9827 || cp == 9829 || cp == 9679) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length;
    }


    
    /**
     * Retourne une représentation ASCII d'un emplacement de noble vide.
     * 
     * Utilisé pour afficher visuellement qu'un noble a été pris par un joueur.
     * Au lieu de réorganiser les nobles restants, on garde l'emplacement vide
     * avec des lignes horizontales pour montrer qu'il manque un noble.
     * 
     * Format :
     * ╔══════════╗
     * ║  ══════  ║
     * ║  ══════  ║
     * ╚══════════╝
     * 
     * Largeur : 12 caractères (bordures incluses), identique à un noble normal
     * Hauteur : 4 lignes, identique à un noble normal
     * 
     * Cette méthode statique peut être appelée sans instance de Noble,
     * ce qui est pratique pour l'affichage du plateau dans Board.boardToStringArray().
     * 
     * @return Un tableau de 4 String représentant un emplacement de noble vide
     */
    public static String[] noNobleStringArray() {
        String[] nobleStr = {
            "╔═══════════╗",
            "║  ═══════  ║",
            "║  ═══════  ║",
            "╚═══════════╝"
        };
        
        return nobleStr;
    }


    
    /**
     * Retourne une représentation textuelle simple du noble.
     * 
     * Format : "Noble (3 pts) - Coût: 3♦ 3♠ 3♣"
     * 
     * Affiche uniquement les ressources dont la quantité est > 0.
     * Utile pour le débogage, les logs, ou l'affichage console simple.
     * 
     * @return Une chaîne décrivant le noble avec ses points et son coût
     */
    @Override
    public String toString() {
        String result = "Noble (" + this.points + " pts) - Coût: ";
        for (Resource res : Resource.values()) {
            int nb = cost.getNbResource(res);
            if (nb > 0) {
                result += nb + res.toSymbol() + " ";
            }
        }
        return result.trim();
    }
}
