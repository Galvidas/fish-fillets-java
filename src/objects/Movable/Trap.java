package objects.Movable;

import java.util.List;

import objects.Characters.BigFish;
import objects.Characters.Krab;
import objects.Characters.SmallFish;
import objects.GameCharacter;
import objects.GameObject;
import objects.MobileObjects;
import objects.interfaces.Deadly;
import objects.interfaces.Traversable;
import pt.iscte.poo.game.DeathManager.DeathType;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Weight;

public class Trap extends MobileObjects implements Traversable, Deadly {

	public Trap(Room room) {
		super(room, Weight.HEAVY);
	}

	@Override
	public String getName() {
		return "trap";
	}	

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public boolean canSupport(List<GameObject> objs) {
        return false;
    }

	@Override
	public boolean canBeDestroyed(GameObject obj) {
		return false;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		if (obj instanceof Cup || obj instanceof SmallFish)
			return false;
		else
			return true;
	}
	
	@Override
	public boolean canBeTraversed(GameObject obj) {
		return true;
	}
	
    public boolean canTraverse(GameObject obj) {
		return false;
	}

	@Override
	public boolean interactWith(GameObject other) {
		return false;
	}

    @Override
    public boolean isDeadly(GameCharacter character) {
        return character instanceof BigFish || (GameObject) character instanceof Krab;
    }

    @Override
    public DeathType getDeathType() {
        return DeathType.TRAPPED;
    }
}
