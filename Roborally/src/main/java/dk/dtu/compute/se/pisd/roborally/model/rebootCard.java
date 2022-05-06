package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Components.RebootTokens;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mike
 * entire class made for the purpose of letting the player choose their heading when rebooting
 */
public class rebootCard extends Subject {

    final public RebootTokens.Choose choose;

    public rebootCard(@NotNull RebootTokens.Choose command) {
        this.choose = command;
    }

    public String getName() {
        return choose.displayName;
    }
}
