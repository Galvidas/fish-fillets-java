package objects.interfaces;

import objects.GameCharacter;
import pt.iscte.poo.game.DeathManager.DeathType;

public interface Deadly {
    public boolean isDeadly(GameCharacter character);
    DeathType getDeathType();

}
