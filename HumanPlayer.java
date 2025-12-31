import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * Classe HumanPlayer
 * Représente un joueur humain qui interagit via le terminal
 * Demande à l'utilisateur de choisir ses actions
 * 
 * @author FONFREIDE Quentin
 * @version 1.0
 */
public class HumanPlayer extends Player {
    
    /**
     * Constructeur
     * @param id identifiant du joueur
     * @param name nom du joueur
     */
    public HumanPlayer(int id, String name) {
        super(id, name);
    }
    
    /**
     * Demande au joueur humain de choisir une action
     * @param board le plateau de jeu
     * @return l'action choisie
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
            Game.display.out.println("Votre choix (1-4) : ");
            
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consommer le retour à la ligne
                
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
     * Demande au joueur de choisir 2 jetons identiques
     * @return l'action choisie, ou null pour retour au menu
     */
    private Action askPickSameTokens(Scanner scanner, Board board) {
        Game.display.out.println("\n=== PRENDRE 2 JETONS IDENTIQUES ===");
        Game.display.out.println("Ressources disponibles :");
        for (Resource res : Resource.values()) {
            Game.display.out.println("- " + res.toString() + " (" + res.toSymbol() + ") : " + board.getNbResource(res) + " jetons");
        }
        
        Game.display.out.println("\nQuelle ressource voulez-vous prendre ? (D/S/E/R/O ou 0 pour retour) : ");
        String input = scanner.nextLine().trim().toUpperCase();
        Game.display.out.print(input);
        
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
        
        // Confirmation finale
        String finalConfirm = "";
        while (finalConfirm.isEmpty()) {
            Game.display.out.println("Confirmer cette action ? (O/N) : ");
            
            finalConfirm = scanner.nextLine().trim().toUpperCase();
        }

        Game.display.out.print(finalConfirm);
        
        if (!finalConfirm.equals("O")) {
            Game.display.out.println("→ Action annulée, retour au menu principal\n");
            return null;
        }
        
        if (!board.canGiveSameTokens(res)) {
            Game.display.out.println("❌ Impossible ! Il faut au moins 4 jetons de ce type.");
            String retry = "";
            while (retry.isEmpty()) {
                Game.display.out.println("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
            if (retry.equals("O")) {
                return askPickSameTokens(scanner, board);
            } else {
                Game.display.out.println("→ Retour au menu principal\n");
                return null;
            }
        }
        
        Game.display.out.println("✓ Action confirmée !\n");
        Game.display.out.println("✓ Vous prenez 2 jetons " + res.toSymbol() + "\n");
        return new PickSameTokensAction(res);
    }


    
    /**
     * Demande au joueur de choisir 3 jetons différents
     * @return l'action choisie, ou null pour retour au menu
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
                Game.display.out.println("Voulez-vous prendre seulement " + nbAvailable + " jeton(s) ? (O/N) : ");
                
                confirm = scanner.nextLine().trim().toUpperCase();
            }
            Game.display.out.print(confirm);
            
            if (!confirm.equals("O")) {
                Game.display.out.println("→ Retour au menu principal\n");
                return null;
            }
        } else {
            Game.display.out.println("Choisissez 3 ressources différentes (ou tapez 0 pour annuler)\n");
        }
        
        // Demander le nombre approprié de ressources (max 3, ou moins si pas assez)
        int nbToChoose = Math.min(3, nbAvailable);
        
        for (int i = 0; i < nbToChoose; i++) {
            Game.display.out.println("Ressource " + (i + 1) + "/" + nbToChoose + " (D/S/E/R/O ou 0 pour annuler) : ");
            String input = scanner.nextLine().trim().toUpperCase();
            Game.display.out.print(input);
            
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
        Game.display.out.println("✓ Récapitulatif - Vous prenez : ");
        for (Resource r : chosen) {
            Game.display.out.print(r.toSymbol() + " ");
        }
        Game.display.out.println();
        
        
        // Confirmation finale
        String finalConfirm = "";
        while (finalConfirm.isEmpty()) {
            Game.display.out.println("\nConfirmer cette action ? (O/N) : ");
            
            finalConfirm = scanner.nextLine().trim().toUpperCase();
        }

        
        if (!finalConfirm.equals("O")) {
            Game.display.out.println("→ Action annulée, retour au menu principal\n");
            return null;
        }
        
        // Vérification finale que les ressources sont toujours disponibles
        if (!board.canGiveDiffTokens(chosen)) {
            Game.display.out.println("❌ Impossible ! Certaines ressources ne sont plus disponibles.");
            String retry = "";
            while (retry.isEmpty()) {
                Game.display.out.println("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
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
     * Demande au joueur de choisir une carte à acheter
     * @return l'action choisie, ou null pour retour au menu
     */
    private Action askBuyCard(Scanner scanner, Board board) {
        Game.display.out.println("\n--- Achat de carte (tapez 0 pour annuler) ---");
        Game.display.out.println("Niveau de la carte (1-3 ou 0 pour retour) : ");
        
        String tierInput = scanner.nextLine();
        Game.display.out.print(tierInput);
        
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
        
        Game.display.out.println("Colonne (1-4 ou 0 pour annuler) : ");
        String colInput = scanner.nextLine();
        Game.display.out.print(colInput);
        
        // Confirmation finale
        String finalConfirm = "";
        while (finalConfirm.isEmpty()) {
            Game.display.out.println("\nConfirmer cette action ? (O/N) : ");
            
            finalConfirm = scanner.nextLine().trim().toUpperCase();
        }
        Game.display.out.print(finalConfirm);
        
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
                Game.display.out.println("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
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
                Game.display.out.println("Voulez-vous réessayer ? (O/N) : ");
                
                retry = scanner.nextLine().trim().toUpperCase();
            }

            Game.display.out.print(retry);
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
            
            Game.display.out.println("\nVoulez-vous choisir une autre carte ? (O/N) : ");
            String retry = scanner.nextLine().trim().toUpperCase();
            Game.display.out.print(retry);
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
     * Convertit une lettre en Resource
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
     * Demande au joueur quels jetons défausser
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
            Game.display.out.println("Jeton " + (i + 1) + " à défausser (D/S/E/R/O) : ");
            String input = scanner.nextLine().trim().toUpperCase();
            Game.display.out.print(input);
            
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
}
