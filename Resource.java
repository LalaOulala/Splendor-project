public enum Resource {
    DIAMOND,
    SAPPHIRE,
    EMERALD,
    ONYX,
    RUBY;

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
