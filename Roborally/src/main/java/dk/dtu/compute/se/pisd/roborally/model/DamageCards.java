package dk.dtu.compute.se.pisd.roborally.model;

public class DamageCards {

    public static void SPAMCard(Player player){
        player.setSPAMCards(player.getSPAMCards() + 1);
    }

    public static void WormCard(Player player){
        player.setSPAMCards(player.getSPAMCards() + 2);
    }
}
