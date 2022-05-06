package dk.dtu.compute.se.pisd.roborally.model.Components;

/**
 * ENUM for upgrade cards. Each has a different cost.
 * @author s205444, Lucas
 */
public enum Upgrade {

    BRAKES("Brakes",3),
    FIREWALL("Firewall", 3),
    HOVER_UNIT("Hover Unit", 1);

    final public String displayName;
    final public int cost;


    Upgrade(String displayName, int cost) {
        this.displayName = displayName;
        this.cost = cost;
    }
}
