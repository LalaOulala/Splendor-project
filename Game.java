/*
 * @author    FONFREIDE Quentin
 * @version     1.2
 * @since       1.1
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;


public class Game {
    
    /* L'affichage et la lecture d'entrée avec l'interface de jeu se fera entièrement via l'attribut display de la classe Game.
     * Celui-ci est rendu visible à toutes les autres classes par souci de simplicité.
     * L'intéraction avec la classe Display est très similaire à celle que vous auriez avec la classe System :
     *    - affichage de l'état du jeu (méthodes fournies): Game.display.outBoard.println("Nombre de joueurs: 2");
     *    - affichage de messages à l'utilisateur: Game.display.out.println("Bienvenue sur Splendor ! Quel est ton nom?");
     *    - demande d'entrée utilisateur: new Scanner(Game.display.in);
     */
    
    private static final int ROWS_BOARD=36, ROWS_CONSOLE=8, COLS=82;
    public static final  Display display = new Display(ROWS_BOARD, ROWS_CONSOLE, COLS);

    private Board board;
    private List<Player> players;

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
     * Constructeur de Game
     * Initialise une partie avec le nombre de joueurs spécifié
     * Demande le nombre de joueurs humains et robots, puis leurs noms
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
        display.out.println("Nombre total de joueurs : " + nbOfPlayers);
        
        int nbHumans = 0;
        boolean validInput = false;
        
        while (!validInput) {
            display.out.print("Combien de joueurs humains ? (0-" + nbOfPlayers + ") : ");
            try {
                nbHumans = scanner.nextInt();
                scanner.nextLine();  // Consommer le retour à la ligne
                
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
                players.add(new DumbRobotPlayer(playerID, name));
                display.out.println("✓ Robot '" + name + "' créé");
                playerID++;
            }
        }
        
        display.out.println("\n" + "=".repeat(50));
        display.out.println("Tous les joueurs sont prêts ! La partie commence !");
        display.out.println("=".repeat(50));
    }




    public int getNbPlayers(){
        return players.size();
    }
    
    /**
     * Vérifie si la partie est terminée
     * La partie se termine quand un joueur atteint 15 points de prestige
     * 
     * @return true si un joueur a au moins 15 points, false sinon
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
     * Annonce le gagnant de la partie
     * En cas d'égalité : le joueur avec le moins de cartes achetées gagne
     * En cas d'égalité totale : partie nulle
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
     * Lance la boucle principale du jeu
     * Les joueurs jouent à tour de rôle jusqu'à ce qu'un gagnant soit déterminé
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
     * Gère le tour d'un joueur
     * Le joueur choisit une action, l'exécute, et le résultat est affiché
     * 
     * @param currentPlayer indice du joueur actuel
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
     * Gère la défausse de jetons si le joueur en a plus de 10
     * Le joueur doit défausser jusqu'à revenir à 10 jetons maximum
     * 
     * @param currentPlayer indice du joueur actuel
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
