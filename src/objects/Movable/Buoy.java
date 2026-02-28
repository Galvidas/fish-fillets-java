package objects.Movable;

import java.util.List;

import objects.GameObject;
import objects.MobileObjects;
import objects.interfaces.Traversable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Weight;

public class Buoy extends MobileObjects implements Traversable {
	private boolean hasDrowned;
	public Buoy(Room room) {
		super(room, Weight.LIGHT);
		hasDrowned = false;
	}

	@Override
	public String getName() {
		return "buoy" + (hasDrowned ? "Drown" : "");
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean canSupport(List<GameObject> objs) {
		hasDrowned = true;
        return false;
    }

	@Override
	public boolean canBeDestroyed(GameObject obj) {
		return false;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		return false;
	}
	
	@Override
	public void updateFall() {
		//no falling
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

}
