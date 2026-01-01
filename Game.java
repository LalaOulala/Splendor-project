import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe principale du jeu Splendor - Orchestre le déroulement complet d'une partie.
 * 
 * Cette classe est le chef d'orchestre du projet. Elle coordonne tous les éléments
 * créés précédemment (Board, Player, Actions) pour créer une partie jouable du début
 * à la fin. Ses responsabilités incluent :
 * - Initialisation d'une nouvelle partie (plateau + joueurs)
 * - Gestion de la boucle de jeu (tours à tour de rôle)
 * - Application des règles (défausse obligatoire, limite de jetons)
 * - Détection de la fin de partie (15 points de prestige)
 * - Annonce du gagnant avec gestion des égalités
 * 
 * La classe utilise un objet Display statique pour gérer l'affichage dans le terminal
 * et la lecture des entrées utilisateur. Cet objet est accessible depuis toutes les
 * autres classes pour faciliter l'interaction.
 * 
 * Améliorations personnelles :
 * - Configuration flexible du nombre de joueurs humains/robots
 * - Demande personnalisée des noms de tous les joueurs
 * - Messages et affichages clairs et structurés
 * - Gestion robuste des erreurs avec try-catch
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class Game {
    
    /* L'affichage et la lecture d'entrée avec l'interface de jeu se fera entièrement via l'attribut display de la classe Game.
     * Celui-ci est rendu visible à toutes les autres classes par souci de simplicité.
     * L'intéraction avec la classe Display est très similaire à celle que vous auriez avec la classe System :
     *    - affichage de l'état du jeu (méthodes fournies): Game.display.outBoard.println("Nombre de joueurs: 2");
     *    - affichage de messages à l'utilisateur: Game.display.out.println("Bienvenue sur Splendor ! Quel est ton nom?");
     *    - demande d'entrée utilisateur: new Scanner(Game.display.in);
     */
    
    /**
     * Dimensions de la fenêtre d'affichage.
     * ROWS_BOARD : lignes pour le plateau (en haut)
     * ROWS_CONSOLE : lignes pour la console interactive (en bas)
     * COLS : colonnes totales
     */
    private static final int ROWS_BOARD=36, ROWS_CONSOLE=8, COLS=82;
    
    /**
     * Instance unique de Display utilisée par tout le programme.
     * Accessible statiquement depuis toutes les classes pour l'affichage et la saisie.
     */
    public static final  Display display = new Display(ROWS_BOARD, ROWS_CONSOLE, COLS);

    /**
     * Plateau de jeu contenant les cartes et les jetons disponibles.
     */
    private Board board;
    
    /**
     * Liste des joueurs participant à la partie (humains et robots).
     * L'ordre dans cette liste détermine l'ordre de jeu.
     */
    private List<Player> players;

    /**
     * Point d'entrée du programme.
     * Demande le nombre de joueurs, crée une partie, la lance, et ferme l'affichage.
     * 
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        display.outBoard.println("╔═══════════════════════════════════╗");
        display.outBoard.println("║   Bienvenue sur SPLENDOR !        ║");
        display.outBoard.println("╚═══════════════════════════════════╝");
        
        Scanner scanner = new Scanner(display.in);
        int nbPlayers = 0;
        boolean validInput = false;
        
        while (!validInput) {
            display.out.print("\nNombre de joueurs (2-4) : ");
            try {
                nbPlayers = scanner.nextInt();
                scanner.nextLine();
                display.out.print(nbPlayers);
                display.out.println();
                if (nbPlayers >= 2 && nbPlayers <= 4) {
                    validInput = true;
                } else {
                    display.out.println("Erreur : le nombre de joueurs doit être entre 2 et 4");
                }
            } catch (Exception e) {
                display.out.println("Erreur : veuillez entrer un nombre valide");
                scanner.nextLine();
            }
        }
        Game game = new Game(nbPlayers);  
        game.play();                      
        display.close();
    }


    /**
     * Constructeur de Game - Initialise une partie complète.
     * 
     * Ce constructeur met en place tout ce qui est nécessaire pour jouer :
     * 1. Valide le nombre de joueurs (2 à 4)
     * 2. Demande la répartition humains/robots (amélioration personnelle)
     * 3. Demande les noms de tous les joueurs
     * 4. Initialise le plateau de jeu (lit le CSV, crée les cartes, les mélange)
     * 5. Crée tous les joueurs dans l'ordre de jeu
     * 
     * Version de base : 1 joueur humain + robots
     * Amélioration personnelle : configuration flexible avec n'importe quelle combinaison
     * 
     * @param nbOfPlayers nombre total de joueurs (2, 3 ou 4)
     * @throws IllegalArgumentException si le nombre de joueurs n'est pas entre 2 et 4
     */
    public Game(int nbOfPlayers) {
        // ========== VALIDATION ==========
        if (nbOfPlayers < 2 || nbOfPlayers > 4) {
            throw new IllegalArgumentException("Le nombre de joueurs doit être entre 2 et 4 !");
        }
        
        Scanner scanner = new Scanner(display.in);
        
        // ========== DEMANDER LA RÉPARTITION HUMAINS/ROBOTS ==========
        display.out.println("\n=== Bienvenue dans Splendor ===");
        display.out.println();
        display.out.println("Nombre total de joueurs : " + nbOfPlayers);        
        display.out.println();
        
        int nbHumans = 0;
        boolean validInput = false;
        
        while (!validInput) {
            display.out.print("Combien de joueurs humains ? (0-" + nbOfPlayers + ") : ");
            try {
                nbHumans = scanner.nextInt();
                scanner.nextLine();  // Consommer le retour à la ligne
                display.out.print(nbHumans);
                display.out.println();
                if (nbHumans >= 0 && nbHumans <= nbOfPlayers) {
                    validInput = true;
                } else {
                    display.out.println("Erreur : le nombre doit être entre 0 et " + nbOfPlayers);
                }
            } catch (Exception e) {
                display.out.println("Erreur : veuillez entrer un nombre valide");
                scanner.nextLine();  // Vider le buffer
            }
        }
        
        int nbRobots = nbOfPlayers - nbHumans;
        display.out.println("\n→ " + nbHumans + " joueur(s) humain(s)");
        display.out.println("→ " + nbRobots + " robot(s)");
        
        // ========== INITIALISATION DU PLATEAU ==========
        display.outBoard.println("\nInitialisation du plateau de jeu...");
        board = new Board(nbOfPlayers);
        
        // ========== INITIALISATION DES JOUEURS ==========
        players = new ArrayList<>();
        
        display.out.println("\n--- Configuration des joueurs ---");
        
        int playerID = 0;
        
        // ========== CRÉER LES JOUEURS HUMAINS ==========
        if (nbHumans > 0) {
            display.out.println("\n=== Joueurs humains ===");
            for (int i = 0; i < nbHumans; i++) {
                display.out.print("Nom du joueur humain " + (i + 1) + " : ");
                String name = scanner.nextLine();
                display.out.print(name);
                display.out.println();
                players.add(new HumanPlayer(playerID, name));
                display.out.println("✓ Joueur humain '" + name + "' créé");
                playerID++;
            }
        }
        
        // ========== CRÉER LES ROBOTS ==========
        if (nbRobots > 0) {
            display.out.println("\n=== Robots ===");
            for (int i = 0; i < nbRobots; i++) {
                display.out.print("Nom du robot " + (i + 1) + " : ");
                String name = scanner.nextLine();
                display.out.print(name);
                display.out.println();
                players.add(new DumbRobotPlayer(playerID, name));
                display.out.println("✓ Robot '" + name + "' créé");
                playerID++;
            }
        }
        
        display.out.println("\n" + "=".repeat(50));
        display.out.println("Tous les joueurs sont prêts ! La partie commence !");
        display.out.println("=".repeat(50));
    }




    /**
     * Retourne le nombre de joueurs dans la partie.
     * 
     * @return le nombre de joueurs (taille de la liste players)
     */
    public int getNbPlayers(){
        return players.size();
    }
    
    /**
     * Vérifie si la partie est terminée.
     * 
     * La condition de fin de partie dans Splendor est qu'au moins un joueur
     * ait atteint ou dépassé 15 points de prestige. Dès qu'un joueur atteint
     * ce seuil, la partie se termine à la fin du tour en cours.
     * 
     * @return true si au moins un joueur a 15 points ou plus, false sinon
     */
    private boolean isGameOver() {
        for (Player player : players) {
            if (player.getPoints() >= 15) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Annonce le gagnant de la partie et gère les égalités.
     * 
     * Processus de détermination du gagnant :
     * 1. Trouver le score maximum parmi tous les joueurs
     * 2. Lister tous les joueurs ayant ce score (candidats)
     * 3. En cas d'égalité : départage par le nombre de cartes achetées
     *    - Le joueur ayant le MOINS de cartes gagne (efficacité)
     * 4. Si l'égalité persiste : partie nulle
     * 
     * Affiche le résultat avec émojis et détails du score final.
     */
    private void gameOver() {
        display.out.println("\n" + "=".repeat(50));
        display.out.println("========== FIN DE LA PARTIE ==========");
        display.out.println("=".repeat(50));
        
        // ========== TROUVER LE SCORE MAXIMUM ==========
        int maxPoints = 0;
        for (Player player : players) {
            if (player.getPoints() > maxPoints) {
                maxPoints = player.getPoints();
            }
        }
        
        // ========== TROUVER TOUS LES JOUEURS AVEC CE SCORE ==========
        List<Player> candidates = new ArrayList<>();
        for (Player player : players) {
            if (player.getPoints() == maxPoints) {
                candidates.add(player);
            }
        }
        
        // ========== GESTION DE L'ÉGALITÉ ==========
        if (candidates.size() > 1) {
            display.out.println("\nÉgalité à " + maxPoints + " points !");
            display.out.println("Départage par le nombre de cartes achetées...\n");
            
            // Trouver le nombre minimal de cartes parmi les ex-aequo
            int minCards = Integer.MAX_VALUE;
            for (Player p : candidates) {
                display.out.println("- " + p.getName() + " : " + p.getNbPurchasedCards() + " cartes");
                if (p.getNbPurchasedCards() < minCards) {
                    minCards = p.getNbPurchasedCards();
                }
            }
            
            // Garder seulement ceux qui ont le moins de cartes
            List<Player> winners = new ArrayList<>();
            for (Player p : candidates) {
                if (p.getNbPurchasedCards() == minCards) {
                    winners.add(p);
                }
            }
            
            // ========== ANNONCE DU/DES GAGNANT(S) ==========
            if (winners.size() == 1) {
                Player winner = winners.get(0);
                display.out.println("\n🎉 " + winner.getName() + " remporte la partie !");
                display.out.println("Score : " + maxPoints + " points avec " + winner.getNbPurchasedCards() + " cartes");
            } else {
                // Partie nulle (même nombre de points ET même nombre de cartes)
                display.out.print("\n🤝 Partie nulle entre : ");
                for (Player winner : winners) {
                    display.out.print(winner.getName() + " ");
                }
                display.out.println("\nScore : " + maxPoints + " points avec " + winners.get(0).getNbPurchasedCards() + " cartes");
            }
        } else {
            // ========== GAGNANT UNIQUE ==========
            Player winner = candidates.get(0);
            display.out.println("\n🎉🎉🎉 " + winner.getName() + " remporte la partie ! 🎉🎉🎉");
            display.out.println("Score final : " + maxPoints + " points");
            display.out.println("Cartes achetées : " + winner.getNbPurchasedCards());
        }
        
        display.out.println("\n" + "=".repeat(50));
    }

    
    /**
     * Affiche l'état complet du jeu dans la zone supérieure du terminal.
     * 
     * Assemble visuellement :
     * - Le plateau de jeu (gauche) : piles, cartes visibles, ressources
     * - Les informations des joueurs (droite) : points, jetons, bonus
     * 
     * Le joueur actuel est marqué par une flèche → devant son nom.
     * 
     * @param currentPlayer indice du joueur dont c'est le tour
     */
    private void display(int currentPlayer){
        String[] boardDisplay = board.toStringArray();
        String[] playerDisplay = Display.emptyStringArray(0, 0);
        for(int i=0;i<players.size();i++){
            String[] pArr = players.get(i).toStringArray();
            if(i==currentPlayer){
                pArr[0] = "\u27A4 " + pArr[0];
            }
            playerDisplay = Display.concatStringArray(playerDisplay, pArr, true);
            playerDisplay = Display.concatStringArray(playerDisplay, Display.emptyStringArray(1, COLS-54, "\u2509"), true);
        }
        String[] mainDisplay = Display.concatStringArray(boardDisplay, playerDisplay, false);

        display.outBoard.clean();
        display.outBoard.println(String.join("\n", mainDisplay));
    }

    /**
     * Lance la boucle principale du jeu - Le cœur du programme.
     * 
     * Cette méthode fait tourner la partie du début à la fin :
     * 1. Affiche l'état actuel du jeu
     * 2. Le joueur actuel joue son tour (choix et exécution d'une action)
     * 3. Gestion de la défausse obligatoire si > 10 jetons
     * 4. Pause d'une seconde pour laisser le temps de lire
     * 5. Passage au joueur suivant (rotation circulaire avec modulo)
     * 6. Répète jusqu'à ce qu'un joueur atteigne 15 points
     * 7. Affiche l'état final et annonce le gagnant
     * 
     * La boucle utilise des try-catch pour rendre le jeu robuste face aux erreurs.
     */
    public void play() {
        int currentPlayer = 0;
        
        // Boucle de jeu : continue tant qu'il n'y a pas de gagnant
        while (!isGameOver()) {
            
            // ========== AFFICHAGE DE L'ÉTAT DU JEU ==========
            display(currentPlayer);
            
            // ========== TOUR DU JOUEUR ACTUEL ==========
            try {
                move(currentPlayer);
            } catch (Exception e) {
                display.out.println("❌ Erreur pendant le tour : " + e.getMessage());
                e.printStackTrace();
            }
            
            // ========== VÉRIFIER LA DÉFAUSSE ==========
            try {
                discardToken(currentPlayer);
            } catch (Exception e) {
                display.out.println("❌ Erreur pendant la défausse : " + e.getMessage());
                e.printStackTrace();
            }
            
            // ========== PAUSE POUR LIRE L'ÉCRAN ==========
            display.out.println("\n" + "-".repeat(50));
            try {
                Thread.sleep(1000);  // Pause de 1 seconde
            } catch (InterruptedException e) {
                // Ignorer
            }
            
            // ========== JOUEUR SUIVANT ==========
            currentPlayer = (currentPlayer + 1) % players.size();
        }
        
        // ========== FIN DE PARTIE ==========
        display(currentPlayer);  // Afficher l'état final
        gameOver();
    }


    /**
     * Gère le tour complet d'un joueur.
     * 
     * Processus :
     * 1. Récupère le joueur actuel
     * 2. Le joueur choisit une action (via chooseAction qui peut reboucler si annulation)
     * 3. L'action est exécutée (modifie l'état du plateau et/ou du joueur)
     * 4. L'action effectuée est affichée dans la console
     * 
     * Grâce au polymorphisme, cette méthode fonctionne de la même façon pour
     * un humain ou un robot, chacun ayant sa propre implémentation de chooseAction().
     * 
     * @param currentPlayer indice du joueur dans la liste players
     */
    private void move(int currentPlayer) {
        Player player = players.get(currentPlayer);
        
        display.out.println("\n--- Tour de " + player.getName() + " ---");
        
        // Le joueur choisit son action (peut reboucler si retour en arrière)
        Action action = null;
        while (action == null) {
            action = player.chooseAction(board);
        }
        
        // Exécuter l'action
        action.process(board, player);
        
        // Afficher ce qui s'est passé
        display.out.println("→ " + player.getName() + " : " + action.toString());
    }


    /**
     * Gère la défausse obligatoire de jetons si le joueur en possède plus de 10.
     * 
     * Règle du jeu : un joueur ne peut jamais avoir plus de 10 jetons.
     * Si après son action il dépasse cette limite, il doit défausser l'excédent.
     * 
     * Cette méthode boucle tant que le joueur a plus de 10 jetons :
     * 1. Affiche un avertissement avec le nombre actuel
     * 2. Le joueur choisit quels jetons défausser (via chooseDiscardingTokens)
     * 3. Crée et exécute une DiscardTokensAction
     * 4. Affiche les jetons défaussés
     * 5. Vérifie à nouveau (au cas où le joueur ait défaussé moins que nécessaire)
     * 
     * @param currentPlayer indice du joueur dans la liste players
     */
    private void discardToken(int currentPlayer) {
        Player player = players.get(currentPlayer);
        
        // Tant que le joueur a plus de 10 jetons
        while (player.getNbTokens() > 10) {
            display.out.println("\n⚠️ " + player.getName() + " a " + player.getNbTokens() + " jetons (max 10)");
            display.out.println("Défausse obligatoire !");
            
            // Le joueur choisit quels jetons défausser
            Resources toDiscard = player.chooseDiscardingTokens();
            
            // Créer et exécuter l'action de défausse
            Action discardAction = new DiscardTokensAction(toDiscard);
            discardAction.process(board, player);
            
            // Afficher ce qui a été défaussé
            display.out.println("→ " + discardAction.toString());
        }
    }

}
