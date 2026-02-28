package objects.Movable;

import objects.Characters.BigFish;
import objects.GameObject;
import objects.MobileObjects;
import objects.interfaces.Traversable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Weight;
import java.util.List;

public class Cup extends MobileObjects implements Traversable {

	public Cup(Room room) {
		super(room, Weight.LIGHT);
	}

	@Override
	public String getName() {
		return "cup";
	}

	@Override
	public int getLayer() {
		return 2;
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
		return false;
	}

	@Override
	public boolean move(Vector2D direction) {
		if (!canBePushed(direction)) {
			return false;
		}
		Point2D newPosition = getPosition().plus(direction);
		setPosition(newPosition);
		return true;
	}

	@Override
	public boolean canBePushed(Vector2D direction) {
		Point2D newPosition = getPosition().plus(direction);
		List<GameObject> objectsAt = getObjectsAt(newPosition);
		for (GameObject obj : objectsAt) {
			// skip self
			if (obj == this)
				continue;

			if (obj instanceof MobileObjects) {
				if (getRoom().getActiveCharacter() instanceof BigFish) {
					if (((MobileObjects) obj).canBePushed(direction) && ((MobileObjects) obj).getWeight() == Weight.LIGHT)
						((MobileObjects) obj).move(direction);
					else
						return false;
					return true;
				} else
					return false;
			}

			if (obj.canBlockMovement(this)) {
				return false; // movement blocked
			}
		}
		return true;
	}

	public boolean canBeTraversed(GameObject obj) {
		return false;
	}

	public boolean canTraverse(GameObject obj) {
		return true;
	}

	@Override
	public boolean interactWith(GameObject other) {
		return false;
	}

}
