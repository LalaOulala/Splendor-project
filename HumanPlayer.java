import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * Joueur humain qui interagit via le terminal.
 * 
 * Cette classe gère l'interaction avec un joueur humain réel qui choisit ses actions
 * en tapant ses choix au clavier. Elle affiche des menus, lit les entrées, valide
 * les choix, et gère les erreurs de saisie.
 * 
 * Fonctionnalités principales :
 * - Affichage d'un menu principal avec 4 options (prendre jetons, acheter, passer)
 * - Validation complète des entrées utilisateur avec messages d'erreur explicites
 * - Système de retour en arrière (tapez 0 pour annuler à tout moment)
 * - Confirmation avant chaque action pour éviter les erreurs
 * - Affichage détaillé des ressources disponibles et des coûts de cartes
 * 
 * Améliorations personnelles :
 * - Possibilité de retourner au menu principal à tout moment
 * - Confirmations avant validation finale
 * - Récapitulatifs clairs avant chaque action
 * - Gestion des cas limites (moins de 3 ressources disponibles)
 * 
 * @author FONFREIDE Quentin
 * @version 01/01/2026
 */
public class HumanPlayer extends Player {
    
    /**
     * Constructeur.
     * Appelle le constructeur parent pour initialiser l'identité et l'état du joueur.
     * 
     * @param id identifiant unique du joueur (0 à 3)
     * @param name nom du joueur (demandé au début de la partie)
     */
    public HumanPlayer(int id, String name) {
        super(id, name);
    }
    
    /**
     * Demande au joueur humain de choisir une action pour son tour.
     * 
     * Affiche un menu avec 4 options et boucle jusqu'à ce qu'une action valide
     * soit choisie. Gère les erreurs de saisie (lettres au lieu de nombres)
     * et les choix hors limites.
     * 
     * Pour chaque type d'action, appelle une méthode privée ask...() qui gère
     * les détails de l'interaction spécifique. Si l'utilisateur annule (retour 0),
     * la méthode ask...() retourne null et le menu principal est réaffiché.
     * 
     * @param board le plateau de jeu (pour consulter les cartes et jetons disponibles)
     * @return l'action choisie et validée par le joueur
     */
    @Override
    public Action chooseAction(Board board) {
        Scanner scanner = new Scanner(Game.display.in);
        
        while (true) {
            // Afficher le menu
            Game.display.out.println("\n=== Votre tour, " + getName() + " ===");
            Game.display.out.println("1. Prendre 2 jetons identiques");
            Game.display.out.println("2. Prendre 3 jetons différents");
            Game.display.out.println("3. Acheter une carte");
            Game.display.out.println("4. Passer votre tour");
            Game.display.out.print("Votre choix (1-4) : ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consommer le retour à la ligne
                Game.display.out.print(choice);
                Game.display.out.println();
                
                switch (choice) {
                    case 1:
                        return askPickSameTokens(scanner, board);
                    case 2:
                        return askPickDiffTokens(scanner, board);
                    case 3:
                        return askBuyCard(scanner, board);
                    case 4:
                        return new PassAction();
                    default:
                        Game.display.out.println("Choix invalide ! Choisissez entre 1 et 4.");
                }
            } catch (Exception e) {
                Game.display.out.println("Erreur de saisie ! Veuillez entrer un nombre.");
                scanner.nextLine();  // Vider le buffer
            }
        }
    }
    
    /**
     * Gère l'interaction pour prendre 2 jetons identiques.
     * 
     * Affiche toutes les ressources disponibles avec leurs quantités, demande
     * au joueur de choisir un type, vérifie que le plateau a au moins 4 jetons
     * de ce type, et demande confirmation avant de créer l'action.
     * 
     * L'utilisateur peut taper 0 à tout moment pour annuler et retourner au menu principal.
     * En cas d'erreur (ressource invalide ou pas assez de jetons), propose de réessayer.
     * 
     * @param scanner le scanner pour lire les entrées utilisateur
     * @param board le plateau de jeu pour vérifier les disponibilités
     * @return l'action PickSameTokensAction créée, ou null pour retour au menu
     */
    private Action askPickSameTokens(Scanner scanner, Board board) {
        Game.display.out.println("\n=== PRENDRE 2 JETONS IDENTIQUES ===");
        Game.display.out.println("Ressources disponibles :");
        for (Resource res : Resource.values()) {
            Game.display.out.println("- " + res.toString() + " (" + res.toSymbol() + ") : " + board.getNbResource(res) + " jetons");
        }
        
        Game.display.out.print("\nQuelle ressource voulez-vous prendre ? (D/S/E/R/O ou 0 pour retour) : ");
        String input = scanner.nextLine().trim().toUpperCase();
        Game.display.out.print(input);
        Game.display.out.println();
        
        if (input.equals("0")) {
            Game.display.out.println("→ Retour au menu principal\n");
            return null;
        }
        
        Resource res = parseResource(input);
        
        if (res == null) {
            Game.display.out.println("❌ Ressource invalide !\n");
            return askPickSameTokens(scanner, board);
        }
        
        // ← AFFICHER LE CHOIX IMMÉDIATEMENT
        Game.display.out.println("→ Vous avez choisi : " + res.toSymbol());
        Game.display.out.println();
        
        // Récapitulatif final
        Game.display.out.println("✓ Récapitulatif - Vous prenez : 2 JETONS " + res.toSymbol());
        Game.display.out.println();
        
        // Confirmation finale
        String finalConfirm = "";
        while (finalConfirm.isEmpty()) {
            Game.display.out.print("Confirmer cette action ? (O/N) : ");
            
            finalConfirm = scanner.nextLine().trim().toUpperCase();
        }

        Game.display.out.print(finalConfirm);
        Game.display.out.println();
        
        if (!finalConfirm.equals("O")) {
            Game.display.out.println("→ Action annulée, retour au menu principal\n");
            return null;
        }
        
        if (!board.canGiveSameTokens(res)) {
            Game.display.out.println("❌ Impossible ! Il faut au moins 4 jetons de ce type.");
            String retry = "";
            while (retry.isEmpty()) {
                Game.display.out.print("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
            Game.display.out.println();
            if (retry.equals("O")) {
                return askPickSameTokens(scanner, board);
            } else {
                Game.display.out.println("→ Retour au menu principal\n");
                return null;
            }
        }
        
        Game.display.out.println("✓ Action confirmée !\n");
        return new PickSameTokensAction(res);
    }


    
    /**
     * Gère l'interaction pour prendre 3 jetons différents.
     * 
     * Demande au joueur de choisir 3 types de ressources différents un par un.
     * Gère le cas spécial où moins de 3 types sont disponibles sur le plateau
     * (amélioration personnelle pour les fins de partie).
     * 
     * Validations effectuées :
     * - Ressource valide (D/S/E/R/O)
     * - Ressource disponible sur le plateau (au moins 1 jeton)
     * - Ressource non déjà choisie (les 3 doivent être différentes)
     * 
     * Affiche un récapitulatif et demande confirmation finale avant de créer l'action.
     * 
     * @param scanner le scanner pour lire les entrées utilisateur
     * @param board le plateau de jeu pour vérifier les disponibilités
     * @return l'action PickDiffTokensAction créée, ou null pour retour au menu
     */
    private Action askPickDiffTokens(Scanner scanner, Board board) {
        List<Resource> chosen = new ArrayList<>();
        
        Game.display.out.println("\n=== PRENDRE 3 JETONS DIFFÉRENTS ===");
        
        
        // Vérifier combien de types de ressources sont disponibles
        List<Resource> availableResources = board.getResources().getAvailableResources();
        int nbAvailable = availableResources.size();
        
        if (nbAvailable == 0) {
            Game.display.out.println("❌ Aucune ressource disponible sur le plateau !");
            Game.display.out.println("→ Retour au menu principal\n");
            return null;
        }
        
        if (nbAvailable < 3) {
            Game.display.out.println("⚠️  Attention : Il n'y a que " + nbAvailable + " type(s) de ressources disponibles.");
            String confirm = "";
            while (confirm.isEmpty()) {
                Game.display.out.print("Voulez-vous prendre seulement " + nbAvailable + " jeton(s) ? (O/N) : ");
                
                confirm = scanner.nextLine().trim().toUpperCase();
            }
            Game.display.out.print(confirm);
            Game.display.out.println();
            if (!confirm.equals("O")) {
                Game.display.out.println("→ Retour au menu principal\n");
                return null;
            } else {
                List<Resource> available = board.getResources().getAvailableResources();
                for (Resource res : available){
                    chosen.add(res);
                }
                
                // Récapitulatif final
                Game.display.out.print("✓ Récapitulatif - Vous prenez : ");
                for (Resource r : chosen) {
                    Game.display.out.print(r.toSymbol() + " ");
                }
                Game.display.out.println();
                
                
                // Confirmation finale
                String finalConfirm = "";
                while (finalConfirm.isEmpty()) {
                    Game.display.out.print("\nConfirmer cette action ? (O/N) : ");
                    
                    finalConfirm = scanner.nextLine().trim().toUpperCase();
                }
                
                Game.display.out.print(finalConfirm);
                Game.display.out.println();
                
                if (!finalConfirm.equals("O")) {
                    Game.display.out.println("→ Action annulée, retour au menu principal\n");
                    return null;
                } else {
                    Game.display.out.println("✓ Action confirmée !\n");
                    return new PickDiffTokensAction(chosen);
                } 
            }
        } else {
            Game.display.out.println("Choisissez 3 ressources différentes (ou tapez 0 pour annuler)\n");
        }
        
        // Demander le nombre approprié de ressources (max 3, ou moins si pas assez)
        int nbToChoose = Math.min(3, nbAvailable);
        Game.display.out.println();
        
        for (int i = 0; i < nbToChoose; i++) {
            Game.display.out.print("Ressource " + (i + 1) + "/" + nbToChoose + " (D/S/E/R/O ou 0 pour annuler) : ");
            String input = scanner.nextLine().trim().toUpperCase();
            Game.display.out.print(input);
            Game.display.out.println();
            if (input.equals("0")) {
                Game.display.out.println("→ Retour au menu principal\n");
                return null;
            }
            
            Resource res = parseResource(input);
            
            if (res == null) {
                Game.display.out.println("❌ Ressource invalide !");
                i--;
                continue;
            }
            
            // Vérifier que cette ressource est disponible sur le plateau
            if (board.getNbResource(res) < 1) {
                Game.display.out.println("❌ Cette ressource n'est pas disponible sur le plateau !");
                i--;
                continue;
            }
            
            if (chosen.contains(res)) {
                Game.display.out.println("❌ Vous avez déjà choisi cette ressource !");
                i--;
                continue;
            }
            
            chosen.add(res);
            Game.display.out.println("→ Vous avez choisi : " + res.toSymbol());
            Game.display.out.println();
        }
        
        // Récapitulatif final
        Game.display.out.print("✓ Récapitulatif - Vous prenez : ");
        for (Resource r : chosen) {
            Game.display.out.print(r.toSymbol() + " ");
        }
        Game.display.out.println();
        
        
        // Confirmation finale
        String finalConfirm = "";
        while (finalConfirm.isEmpty()) {
            Game.display.out.print("\nConfirmer cette action ? (O/N) : ");
            
            finalConfirm = scanner.nextLine().trim().toUpperCase();
        }
        
        Game.display.out.print(finalConfirm);
        Game.display.out.println();
        
        if (!finalConfirm.equals("O")) {
            Game.display.out.println("→ Action annulée, retour au menu principal\n");
            return null;
        }
        
        // Vérification finale que les ressources sont toujours disponibles
        if (!board.canGiveDiffTokens(chosen)) {
            Game.display.out.println("❌ Impossible ! Certaines ressources ne sont plus disponibles.");
            String retry = "";
            while (retry.isEmpty()) {
                Game.display.out.print("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
            Game.display.out.println();
            if (retry.equals("O")) {
                return askPickDiffTokens(scanner, board);
            } else {
                Game.display.out.println("→ Retour au menu principal\n");
                return null;
            }
        }
        
        Game.display.out.println("✓ Action confirmée !\n");
        return new PickDiffTokensAction(chosen);
    }


    
    /**
     * Gère l'interaction pour acheter une carte.
     * 
     * Demande les coordonnées de la carte (niveau 1-3 et colonne 1-4), demande
     * confirmation de l'action, puis vérifie qu'une carte existe à cette position
     * et que le joueur a suffisamment de ressources (jetons + bonus) pour l'acheter.
     * 
     * Processus détaillé :
     * 1. Demande le niveau (1-3) - 0 pour annuler
     * 2. Demande la colonne (1-4) - 0 pour annuler
     * 3. Demande confirmation finale (O/N)
     * 4. Vérifie que les coordonnées sont valides
     * 5. Récupère la carte à cette position
     * 6. Vérifie que le joueur peut acheter la carte avec canBuyCard()
     * 
     * En cas d'échec d'achat, affiche un détail complet :
     * - Les ressources du joueur (jetons + bonus par type)
     * - Le coût requis de la carte (par type de ressource)
     * Ce qui permet au joueur de comprendre exactement ce qui lui manque.
     * 
     * Gestion des erreurs :
     * - Coordonnées invalides : propose de réessayer
     * - Pas de carte à cette position : propose de réessayer
     * - Ressources insuffisantes : affiche le détail et propose de choisir une autre carte
     * 
     * L'utilisateur peut taper 0 à tout moment (niveau ou colonne) pour annuler
     * et retourner au menu principal.
     * 
     * @param scanner le scanner pour lire les entrées utilisateur
     * @param board le plateau de jeu pour récupérer la carte
     * @return l'action BuyCardAction créée avec la carte choisie, ou null pour retour au menu
     */
    private Action askBuyCard(Scanner scanner, Board board) {
        Game.display.out.println("\n--- Achat de carte (tapez 0 pour annuler) ---");
        
        Game.display.out.print("Niveau de la carte (1-3 ou 0 pour retour) : ");
        
        String tierInput = scanner.nextLine();
        Game.display.out.print(tierInput);
        Game.display.out.println();
        
        // Retour au menu
        if (tierInput.equals("0")) {
            return null;
        }
        
        int tier;
        try {
            tier = Integer.parseInt(tierInput);
        } catch (NumberFormatException e) {
            Game.display.out.println("Entrée invalide !");
            return askBuyCard(scanner, board);
        }
        
        Game.display.out.print("Colonne (1-4 ou 0 pour annuler) : ");
        
        String colInput = scanner.nextLine();
        Game.display.out.print(colInput);
        Game.display.out.println();
        
        // Confirmation finale
        String finalConfirm = "";
        while (finalConfirm.isEmpty()) {
            Game.display.out.print("\nConfirmer cette action ? (O/N) : ");
            
            finalConfirm = scanner.nextLine().trim().toUpperCase();
        }
        Game.display.out.print(finalConfirm);
        Game.display.out.println();
        if (!finalConfirm.equals("O")) {
            Game.display.out.println("→ Action annulée, retour au menu principal\n");
            return null;
        }
        
        // Retour au menu
        if (colInput.equals("0")) {
            return null;
        }
        
        int col;
        try {
            col = Integer.parseInt(colInput);
        } catch (NumberFormatException e) {
            Game.display.out.println("Entrée invalide !");
            return askBuyCard(scanner, board);
        }
        
        if (tier < 1 || tier > 3 || col < 1 || col > 4) {
            Game.display.out.println("Coordonnées invalides !");
            String retry = "";
            while (retry.isEmpty()) {
                Game.display.out.print("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
            Game.display.out.println();

            if (retry.equals("O")) {
                return askBuyCard(scanner, board);
            } else {
                return null;
            }
        }
        
        DevCard card = board.getCard(tier, col - 1);
        
        if (card == null) {
            Game.display.out.println("Il n'y a pas de carte à cette position !");
            String retry = "";
            while (retry.isEmpty()) {
                Game.display.out.print("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
            Game.display.out.println();

            if (retry.equals("O")) {
                return askBuyCard(scanner, board);
            } else {
                return null;
            }
        }
        
        if (!canBuyCard(card)) {
            Game.display.out.println("Vous n'avez pas assez de ressources pour acheter cette carte !");
            Game.display.out.println("Carte : " + card.toString());
            
            Game.display.out.println("\n--- Détails ---");
            Game.display.out.println("Vos ressources :");
            for (Resource res : Resource.values()) {
                int owned = getNbResource(res);
                int bonus = getResFromCards(res);
                if (owned > 0 || bonus > 0) {
                    Game.display.out.println("  " + res.toSymbol() + " : " + owned + " jetons + " + bonus + " bonus = " + (owned + bonus));
                }
            }
            
            Game.display.out.println("\nCoût de la carte :");
            Resources cost = card.getCost();
            for (Resource res : Resource.values()) {
                int required = cost.getNbResource(res);
                if (required > 0) {
                    Game.display.out.println("  " + res.toSymbol() + " : " + required + " requis");
                }
            }
            
            Game.display.out.print("\nVoulez-vous choisir une autre carte ? (O/N) : ");
            String retry = scanner.nextLine().trim().toUpperCase();
            Game.display.out.print(retry);
            Game.display.out.println();
            if (retry.equals("O")) {
                return askBuyCard(scanner, board);
            } else {
                return null;  // Retour au menu
            }
        }
        
        Game.display.out.println("✓ Action confirmée !\n");
        return new BuyCardAction(card);
    }

    
    /**
     * Convertit une lettre en énumération Resource.
     * Méthode utilitaire pour parser les entrées utilisateur (D/S/E/R/O).
     * 
     * @param input la lettre saisie par l'utilisateur (D, S, E, R ou O)
     * @return la Resource correspondante, ou null si l'entrée est invalide
     */
    private Resource parseResource(String input) {
        switch (input) {
            case "D": return Resource.DIAMOND;
            case "S": return Resource.SAPPHIRE;
            case "E": return Resource.EMERALD;
            case "R": return Resource.RUBY;
            case "O": return Resource.ONYX;
            default: return null;
        }
    }
    
    /**
     * Demande au joueur quels jetons défausser pour revenir à 10.
     * 
     * Affiche les jetons actuels du joueur et demande de choisir un par un
     * les jetons à défausser jusqu'à atteindre la limite de 10.
     * 
     * Validations :
     * - Ressource valide (D/S/E/R/O)
     * - Le joueur possède encore ce type de jeton
     * - Tient compte des jetons déjà choisis pour défausse
     * 
     * @return un objet Resources contenant les quantités à défausser de chaque type
     */
    @Override
    public Resources chooseDiscardingTokens() {
        Scanner scanner = new Scanner(Game.display.in);
        
        int totalTokens = getNbTokens();
        int toRemove = totalTokens - 10;
        
        Resources discard = new Resources();
        
        Game.display.out.println("\nVous avez " + totalTokens + " jetons. Vous devez en défausser " + toRemove + ".");
        Game.display.out.println("Vos jetons actuels :");
        for (Resource res : Resource.values()) {
            int nb = getNbResource(res);
            if (nb > 0) {
                Game.display.out.println("- " + res.toString() + " : " + nb);
            }
        }
        
        for (int i = 0; i < toRemove; i++) {
            Game.display.out.print("Jeton " + (i + 1) + " à défausser (D/S/E/R/O) : ");
            String input = scanner.nextLine().trim().toUpperCase();
            Game.display.out.print(input);
            Game.display.out.println();
            
            Resource res = parseResource(input);
            
            if (res == null) {
                Game.display.out.println("Ressource invalide !");
                i--;  // Redemander
                continue;
            }
            
            // Vérifier qu'on a encore ce jeton (en tenant compte des défausses déjà choisies)
            int owned = getNbResource(res);
            int alreadyDiscarded = discard.getNbResource(res);
            
            if (owned <= alreadyDiscarded) {
                Game.display.out.println("Vous n'avez plus ce jeton !");
                i--;  // Redemander
                continue;
            }
            
            discard.updateNbResource(res, 1);
        }
        
        return discard;
    }
    
    /**
     * Permet au joueur humain de choisir un noble parmi plusieurs nobles éligibles.
     * 
     * Cette méthode est appelée uniquement quand le joueur devient éligible pour
     * plusieurs nobles en même temps après avoir acheté une carte. Elle affiche
     * la liste des nobles disponibles avec leur coût et demande au joueur de choisir
     * interactivement lequel obtenir.
     * 
     * Processus :
     * 1. Affiche un message indiquant le nombre de nobles éligibles
     * 2. Liste tous les nobles avec leurs numéros et leurs coûts en bonus
     * 3. Demande au joueur de choisir un numéro entre 1 et N (N = nombre de nobles)
     * 4. Valide l'entrée (doit être un nombre dans la plage correcte)
     * 5. Redemande en cas d'erreur (lettre, nombre hors limites, etc.)
     * 6. Retourne le noble choisi
     * 
     * Exemple d'affichage :
     * ⚜ Vous pouvez obtenir 2 noble(s) !
     * 
     * Noble 1 : 3♦ 3♠ 3♣
     * Noble 2 : 4♥ 4●
     * 
     * Lequel voulez-vous ? (1-2) : _
     * 
     * Gestion des erreurs :
     * - Entrée non numérique : affiche "❌ Erreur : veuillez entrer un nombre valide"
     * - Nombre hors limites : affiche "❌ Choix invalide. Veuillez entrer entre 1 et N"
     * - Redemande jusqu'à obtenir une entrée valide
     * 
     * Note : Cette méthode ne sera JAMAIS appelée si un seul noble est éligible.
     * Dans ce cas, Player.checkAndObtainNobles() attribue directement le noble
     * sans appeler cette méthode.
     * 
     * @param eligibleNobles Liste des nobles pour lesquels le joueur est éligible.
     *                       Taille toujours >= 2 (sinon cette méthode n'est pas appelée)
     * @return Le noble choisi par le joueur (un élément de eligibleNobles)
     */
    @Override
    protected Noble chooseNoble(List<Noble> eligibleNobles) {
        Game.display.out.println("\n \u269C Vous pouvez obtenir " + eligibleNobles.size() + " noble(s) !");
        
        // Afficher les nobles éligibles
        for (int i = 0; i < eligibleNobles.size(); i++) {
            Game.display.out.print("\n Noble " + (i + 1) + " : ");
            // Afficher le coût du noble
            Resources cost = eligibleNobles.get(i).getCost();
            Game.display.out.print(cost.toString());
            Game.display.out.println();
        }
        
        // Demander le choix
        Scanner scanner = new Scanner(Game.display.in);
        int choice = 0;
        boolean validInput = false;
        
        while (!validInput) {
            Game.display.out.print("\nLequel voulez-vous ? (1-" + eligibleNobles.size() + ") : ");
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consommer le retour à la ligne
                Game.display.out.print(choice);
                Game.display.out.println();
                
                if (choice >= 1 && choice <= eligibleNobles.size()) {
                    validInput = true;
                } else {
                    Game.display.out.println("❌ Choix invalide. Veuillez entrer entre 1 et " + eligibleNobles.size());
                }
            } catch (Exception e) {
                Game.display.out.println("❌ Erreur : veuillez entrer un nombre valide");
                scanner.nextLine(); // Vider le buffer
            }
        }
        
        return eligibleNobles.get(choice - 1);  // choice est 1-indexé, List est 0-indexé
    }

}
