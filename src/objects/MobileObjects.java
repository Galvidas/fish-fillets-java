package objects;

import java.util.List;

import objects.Characters.SmallFish;
import objects.interfaces.Movable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.utils.Weight;

public abstract class MobileObjects extends GameObject implements Movable {

	private Weight weight;

	public MobileObjects(Room room, Weight weight) {
		super(room);
		this.weight = weight;
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

	public void updateFall() {
		Point2D newPosition = getPosition().plus(Direction.DOWN.asVector());
		List<GameObject> objectsAt = getObjectsAt(newPosition);
		for (GameObject obj : objectsAt) {
			if (obj instanceof FixedObject) {
				if (!((FixedObject) obj).canSupport(this))
					return;
			}
			if (obj.canBlockMovement(this) && !(obj instanceof SmallFish)) {
				return; // movement blocked
			}
			if (this.interactWith(obj))
				return;
			
			 if (obj instanceof MobileObjects)
				 return;
			 // movement blocked for stacking on other objects
		}
		move(Direction.DOWN.asVector());
	}

	@Override
	public boolean canBePushed(Vector2D direction) {
		Point2D newPosition = getPosition().plus(direction);
		return true;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		return false;
	}

	@Override
	public boolean needsSupport() {
		return true;
	}

	@Override
	public abstract boolean interactWith(GameObject other);

	public Weight getWeight() {
		return weight;
	}

}
