public class DevCard implements Displayable {
        
    private int tier;              // Niveau de la carte (1, 2 ou 3)
    private Resources cost;        // Coût en ressources
    private int points;            // Points de prestige
    private Resource resourceType; // Bonus de la carte
    
    /**
     * Constructeur de DevCard
     * @param tier niveau de la carte (1, 2 ou 3)
     * @param cost coût en ressources
     * @param points points de prestige
     * @param resourceType type de bonus
     */
    public DevCard(int tier, Resources cost, int points, Resource resourceType) {
        this.tier = tier;
        this.cost = cost;
        this.points = points;
        this.resourceType = resourceType;
    }
    
    /**
     * @return le niveau de la carte
     */
    public int getTier() {
        return tier;
    }
    
    /**
     * @return le coût de la carte
     */
    public Resources getCost() {
        return cost;
    }
    
    /**
     * @return les points de prestige
     */
    public int getPoints() {
        return points;
    }
    
    /**
     * @return le type de ressource bonus
     */
    public Resource getResourceType() {
        return resourceType;
    }

    public String[] toStringArray(){
        /** EXAMPLE
         * ┌────────┐
         * │1     ♠S│
         * │        │
         * │        │
         * │2 ♠S    │
         * │2 ♣E    │ 
         * │3 ♥R    │
         * └────────┘
         */
        String pointStr = "  ";
        
        if(getPoints()>0){
            pointStr = new String(new int[] {getPoints()+9311}, 0, 1);
        }
        String[] cardStr = {"\u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510",
                            "\u2502"+pointStr+"    "+resourceType.toSymbol()+"\u2502",
                            "\u2502        \u2502",
                            "\u2502        \u2502",
                            "\u2502        \u2502",
                            "\u2502        \u2502",
                            "\u2502        \u2502",
                            "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518"};
        //update cost of the repr
        int i=6;
        for(Resource res : Resource.values()){ //-- parcourir l'ensemble des resources (res)en utilisant l'énumération Resource
            if(getCost().getNbResource(res)>0){
                cardStr[i] = "\u2502"+getCost().getNbResource(res)+" "+res.toSymbol()+"    \u2502";
                i--;
            }
        } 
        return cardStr;
    }

    public static String[] noCardStringArray(){
        /** EXAMPLE
         * ┌────────┐
         * │ \    / │
         * │  \  /  │
         * │   \/   │
         * │   /\   │
         * │  /  \  │
         * │ /    \ │
         * └────────┘
         */
        String[] cardStr = {"\u250C\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510",
                            "\u2502 \\    / \u2502",
                            "\u2502  \\  /  \u2502",
                            "\u2502   \\/   \u2502",
                            "\u2502   /\\   \u2502",
                            "\u2502  /  \\  \u2502",
                            "\u2502 /    \\ \u2502",
                            "\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518"};
        
        return cardStr;
    }

    public String toString(){
        String cardStr = getPoints()+"pts, type "+resourceType.toSymbol()+" | coût: ";
        for(Resource res : Resource.values()){ //-- parcourir l'ensemble des resources (res) en utilisant l'énumération Resource
            if(getCost().getNbResource(res)>0){
                cardStr += getCost().getNbResource(res)+res.toSymbol()+" ";
            }
        }
        return cardStr;
    }
}