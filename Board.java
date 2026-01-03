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
     * Liste des nobles actuellement disponibles sur le plateau.
     * 
     * Le nombre initial de nobles dépend du nombre de joueurs :
     * - 2 joueurs : 3 nobles
     * - 3 joueurs : 4 nobles
     * - 4 joueurs : 5 nobles
     * 
     * Quand un joueur obtient un noble, celui-ci est retiré de cette liste
     * avec removeNoble(). L'emplacement vide est visuellement représenté
     * par un rectangle avec des tirets dans l'affichage.
     */
    private ArrayList<Noble> visibleNobles;
    
    /**
     * Nombre initial d'emplacements de nobles au début de la partie.
     * 
     * Cet attribut stocke le nombre de nobles tirés initialement et sert
     * à afficher le bon nombre d'emplacements (vides ou occupés) même après
     * que des nobles ont été pris.
     * 
     * Par exemple, dans une partie à 3 joueurs, nbNoblesSlots vaut 4.
     * Si 2 nobles ont été pris, visibleNobles.size() vaut 2, mais on continue
     * d'afficher 4 emplacements (2 nobles + 2 rectangles vides).
     * 
     * Nécessaire car les méthodes private du constructeur empêchent de récupérer
     * le nombre de joueurs plus tard pour calculer dynamiquement le nombre d'emplacements.
     */
    private int nbNoblesSlots;
    
    /**
     * Constructeur du plateau de jeu.
     * Initialise complètement le plateau en plusieurs étapes :
     * 
     * CARTES :
     * 1. Création des structures de données (piles, cartes visibles, ressources)
     * 2. Lecture du fichier stats.csv et création de toutes les cartes de développement
     * 3. Mélange aléatoire des 3 piles de cartes
     * 4. Révélation de 4 cartes par niveau
     * 
     * JETONS :
     * 5. Initialisation des jetons selon le nombre de joueurs :
     *    - 2 joueurs : 4 jetons par type
     *    - 3 joueurs : 5 jetons par type
     *    - 4 joueurs : 7 jetons par type
     * Initialisation des jetons Or : toujours 5 jetons (indépendant du nombre de joueurs)
     * 
     * NOBLES :
     * 6. Lecture des nobles dans stats.csv (lignes avec tier = 0)
     * 7. Mélange aléatoire de tous les nobles disponibles
     * 8. Tirage du bon nombre de nobles selon le nombre de joueurs :
     *    - 2 joueurs : 3 nobles
     *    - 3 joueurs : 4 nobles
     *    - 4 joueurs : 5 nobles
     * 9. Stockage du nombre initial de nobles dans nbNoblesSlots
     * 
     * @param nbPlayers nombre de joueurs (2, 3 ou 4)
     * @throws FileNotFoundException si le fichier stats.csv n'est pas trouvé
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
        resources = new Resources(nombreJetons, nombreJetons, nombreJetons, nombreJetons, nombreJetons, 5);
        
        // Initialisation Nobles
        this.visibleNobles = new ArrayList<Noble>();
        ArrayList<Noble> allNobles = new ArrayList<Noble>();
        
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
                
                // Cartes developpement (pas Nobles)
                if (tierCarte != 0) {
                    // Extraction du type de ressource
                    Resource typeCarte = Resource.valueOf(donnees[7]);
                    
                    // Création de la carte et ajout à sa pile respective
                    DevCard carte = new DevCard(tierCarte, coutCarte, pointsCarte, typeCarte);
                    stackCards[tierCarte - 1].push(carte);
                    
                    // Cartes Nobles
                } else{
                    // Créer le noble et l'ajouter à la liste temporaire
                    Noble noble = new Noble(coutCarte, pointsCarte);
                    allNobles.add(noble);
                }
            }
            
            scanner.close();
            
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : fichier stats.csv contenant les cartes introuvable !");
            e.printStackTrace();
        }
        
        // Cartes :
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
        
        // Nobles :
        // Mélanger tous les nobles
        Collections.shuffle(allNobles);
           
        // Tirer le bon nombre de nobles selon le nombre de joueurs
        int nbNoblesToDraw = nbPlayers + 1;
        for (int i = 0; i < nbNoblesToDraw; i++) {
            this.visibleNobles.add(allNobles.get(i));
        }
        
        this.nbNoblesSlots = nbNoblesToDraw;
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
    
    // ============= GESTION DES NOBLES ===============
    
    /**
     * Retourne la liste des nobles actuellement disponibles sur le plateau.
     * 
     * Cette liste diminue au fur et à mesure que les joueurs obtiennent des nobles.
     * Elle ne contient que les nobles encore disponibles (pas les emplacements vides).
     * 
     * @return ArrayList contenant uniquement les nobles non encore obtenus
     */
    public ArrayList<Noble> getVisibleNobles() {
        return this.visibleNobles;
    }
    
    /**
     * Retire un noble du plateau quand un joueur l'obtient.
     * 
     * Recherche le noble dans visibleNobles et le supprime de la liste.
     * Cette méthode est appelée automatiquement par Player.checkAndObtainNobles()
     * après qu'un noble a été attribué à un joueur.
     * 
     * L'emplacement du noble retiré sera visuellement représenté par un rectangle
     * vide dans boardToStringArray() grâce à nbNoblesSlots qui conserve le nombre
     * initial d'emplacements.
     * 
     * @param noble Le noble à retirer de la liste des nobles disponibles
     * @return true si le noble a été trouvé et retiré avec succès, 
     *         false si le noble n'était pas dans la liste
     */
    public boolean removeNoble(Noble noble) {
        return this.visibleNobles.remove(noble);
    }
    
    /**
     * Vérifie si un joueur peut obtenir un noble donné.
     * 
     * Un joueur peut obtenir un noble s'il possède suffisamment de bonus de cartes
     * (cartes achetées) pour chaque type de ressource requis par le noble.
     * 
     * Important : Ce sont les BONUS de cartes qui comptent, pas les jetons possédés.
     * Par exemple, si un noble coûte "3 diamants, 3 saphirs", le joueur doit avoir
     * au moins 3 cartes qui produisent du diamant ET 3 cartes qui produisent du saphir.
     * 
     * Processus de vérification :
     * 1. Récupère le coût du noble (Resources contenant les bonus requis)
     * 2. Pour chaque type de ressource dans le coût :
     *    - Compare le nombre requis avec le nombre de bonus du joueur (via getResFromCards())
     *    - Si une seule ressource est insuffisante : retourne false
     * 3. Si toutes les ressources sont suffisantes : retourne true
     * 
     * @param noble Le noble dont on vérifie l'éligibilité
     * @param player Le joueur dont on vérifie les bonus de cartes
     * @return true si le joueur a tous les bonus requis pour obtenir ce noble,
     *         false si au moins un bonus est insuffisant
     */
    public boolean canObtainNoble(Noble noble, Player player) {
        Resources cost = noble.getCost();
        
        // Vérifier pour chaque type de ressource
        for (Resource res : Resource.values()) {
            int required = cost.getNbResource(res);
            int owned = player.getResFromCards(res);
            
            // Si le joueur n'a pas assez de bonus pour cette ressource
            if (owned < required) {
                return false;
            }
        }
        
        return true; // Le joueur a tous les bonus requis
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
        if (res == Resource.GOLD) {
            return false;
        }
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
            if (res == Resource.GOLD) {
                return false;
            }
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
        String[] resStr = {"Resources disponibles :  "};
        
        for (Resource res : Resource.values()) {
            resStr[0] += resources.getNbResource(res) + res.toSymbol() + "  ";
        }

        return resStr;
    }
    
    /**
     * Génère la représentation complète du plateau de jeu en ASCII art.
     * 
     * Assemble visuellement en 5 étapes :
     * 
     * ÉTAPE 1 - NOBLES (en haut, centrés) :
     * - Affiche les nobles disponibles et les emplacements vides (nobles pris)
     * - Nombre total d'emplacements = Math.max(nobles actuels, nbNoblesSlots)
     * - Centrage horizontal calculé selon le nombre d'emplacements (3, 4 ou 5)
     * - Utilise Noble.toStringArray() pour les nobles disponibles
     * - Utilise Noble.noNobleStringArray() pour les emplacements vides
     * - Concaténation MANUELLE sans Display.concatStringArray() pour éviter le padding parasite
     * 
     * ÉTAPE 2 - PILES FACES CACHÉES (à gauche, verticalement) :
     * - Affiche les 3 piles dans l'ordre tier 3, 2, 1 (de haut en bas)
     * - Chaque pile montre le nombre de cartes restantes
     * - Utilise deckToStringArray(tier)
     * 
     * ÉTAPE 3 - CARTES VISIBLES (au centre, par niveau) :
     * - Affiche 3 rangées de 4 cartes (niveaux 3, 2, 1 de haut en bas)
     * - Chaque carte visible utilise DevCard.toStringArray()
     * - Les emplacements vides (pile épuisée) utilisent DevCard.noCardStringArray()
     * 
     * ÉTAPE 4 - ASSEMBLAGE :
     * - Concatène horizontalement les piles et les cartes
     * - Ajoute un décalage vers la droite pour l'alignement visuel
     * - Le décalage varie selon le nombre de nobles (1 ou 6 espaces)
     * 
     * ÉTAPE 5 - RESSOURCES ET BORDURES :
     * - Affiche les jetons disponibles en bas
     * - Ajoute les bordures décoratives (lignes verticales et horizontales)
     * - Correction manuelle de la 3e ligne pour éviter les espaces parasites
     *   liés aux symboles Unicode larges des nobles
     * 
     * Dimensions finales :
     * - Largeur : 58-69 caractères selon le nombre de nobles
     * - Hauteur : 35-36 lignes selon le nombre de nobles
     * 
     * Note importante : L'affichage des nobles utilise une concaténation manuelle
     * pour éviter les problèmes de padding causés par le symbole ⚜ (2 caractères
     * d'affichage) qui n'est pas reconnu par Display.displayedLength().
     * 
     * @return Un tableau de String représentant tout le plateau de jeu
     */
    private String[] boardToStringArray() {
        String[] res = Display.emptyStringArray(0, 0);
        
        // ========== ÉTAPE 1 : Affichage des NOBLES (EN HAUT, CENTRÉS) ==========
        
        int nbNobles = visibleNobles.size();
        
        // Calculer le nombre total d'emplacements nobles (pris ou non)
        int totalSlots = Math.max(nbNobles, nbNoblesSlots);
        
        if (nbNobles > 0 || nbNoblesSlots > 0) {
            // Créer l'affichage des nobles
            String[] noblesDisplay = Display.emptyStringArray(4, 0);  // 4 lignes pour les nobles
            
            for (int i = 0; i < totalSlots; i++) {
                String[] nobleSlot;
                
                // Vérifier si un noble existe à cet index
                if (i < visibleNobles.size()) {
                    // Noble existe encore
                    nobleSlot = visibleNobles.get(i).toStringArray();
                } else {
                    // Noble a été pris, afficher un emplacement vide
                    nobleSlot = Noble.noNobleStringArray();
                }
                
                // Concaténer horizontalement (false = côte à côte)
                if (noblesDisplay.length == 0) {
                    noblesDisplay = nobleSlot;
                } else {
                    // Concaténation manuelle horizontale SANS padding
                    String[] temp = new String[nobleSlot.length];
                    for (int j = 0; j < nobleSlot.length; j++) {
                        temp[j] = noblesDisplay[j] + nobleSlot[j];
                    }
                    noblesDisplay = temp;
                }
            }
            
            // ✅ CENTRER les nobles
            int padding = 0;
            if (totalSlots == 3) {
                padding = 9;  // (56 - 39) / 2 = 8
            } else if (totalSlots == 4) {
                padding = 2;  // (56 - 52) / 2 = 2
            } else if (totalSlots == 5) {
                padding = 1;
            }
            
            if (padding > 0) {
                String gPadding = " ".repeat(padding);
                for (int i = 0; i < noblesDisplay.length; i++) {
                    noblesDisplay[i] = gPadding + noblesDisplay[i] + gPadding;
                }
            }
            
            // Ajouter une ligne vide avant les nobles
            res = Display.concatStringArray(res, Display.emptyStringArray(1, 55), true);
            
            // Ajouter les nobles au plateau
            res = Display.concatStringArray(res, noblesDisplay, true);
            
            // Ajouter une ligne vide après les nobles
            res = Display.concatStringArray(res, Display.emptyStringArray(1, 55), true);
        }
        
        // ========== ÉTAPE 2 : Affichage des piles faces cachées (niveau 3, 2, 1 de haut en bas) ==========
        String[] deckDisplay = Display.emptyStringArray(0, 0);
        for (int i = 3; i > 0; i--) {
            deckDisplay = Display.concatStringArray(deckDisplay, deckToStringArray(i), true);
        }
        
        // ========== ÉTAPE 3 : Affichage des cartes visibles (niveau 3, 2, 1 de haut en bas) ==========
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
        
        // ========== ÉTAPE 4 : Assemblage piles + cartes ==========
        // ✅ Ajouter un espace entre les piles et les cartes
        for (int i = 0; i < deckDisplay.length; i++) {
            deckDisplay[i] = deckDisplay[i] + " ";
        }
        
        String[] plateauCartes = Display.concatStringArray(deckDisplay, carteDisplay, false);
        
        int leftPaddingPlateau = 1;
        if (totalSlots == 5){
            leftPaddingPlateau = 6;
        }
        
        // ✅ Décaler TOUT de 1 espace vers la droite : ça rends mieux niveau visuel
        for (int i = 0; i < plateauCartes.length; i++) {
            plateauCartes[i] = " ".repeat(leftPaddingPlateau) + plateauCartes[i];
        }
        
        // Ajouter les cartes sous les nobles
        res = Display.concatStringArray(res, plateauCartes, true);
        
        // ========== ÉTAPE 5 : Affichage des ressources + bordures finales ==========
        res = Display.concatStringArray(res, Display.emptyStringArray(1, 55), true);
        
        String[] resourcesDisplay = resourcesToStringArray();

        for (int i = 0; i < resourcesDisplay.length; i++) {
            resourcesDisplay[i] = " ".repeat(leftPaddingPlateau) + resourcesDisplay[i];
        }
        
        res = Display.concatStringArray(res, resourcesDisplay, true);
        
        int vertical = 36;
        int horizontal = 59;
        if (totalSlots == 4){
            horizontal = 58;
        } else if (totalSlots == 5){
            vertical = 35;
            horizontal = 68;
        }
        
        res = Display.concatStringArray(res, Display.emptyStringArray(1, horizontal, "\u2509"), true);
        res = Display.concatStringArray(res, Display.emptyStringArray(vertical, 1, " \u250A"), false);
        res[35] = res[35].substring(0, res[35].length() - 2) + "\u2509\u250A";
        
        if (res.length > 2) {
            String line = res[2];
            res[2] = line.substring(0, line.length() - (visibleNobles.size()+1)) + "\u250A";
        }
        
        return res;
    }



    
    /**
     * Implémentation de l'interface Displayable.
     * Retourne la représentation complète du plateau pour l'affichage dans le terminal.
     * 
     * Délègue simplement à boardToStringArray() qui génère l'ASCII art complet
     * incluant les nobles, les cartes, et les ressources.
     * 
     * @return Un tableau de String représentant le plateau complet
     */
    @Override
    public String[] toStringArray() {
        return boardToStringArray();
    }
}
