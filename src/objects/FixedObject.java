package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.List;

public abstract class FixedObject extends GameObject {

	public FixedObject(Room room) {
		super(room);
	}

	public boolean canSupport(GameObject obj) {
		return true;
	}

	public boolean canBeDestroyed(GameObject obj) {
		return false;
	}

	public boolean canBlockMovement(GameObject obj) {
		return true;
	}

	public boolean needsSupport() {
		return false;
	}

	public boolean canSupport(List<GameObject> objs) {
		return true;
	}

	public boolean interactWith(GameObject other) {
		return false;
	}
}
