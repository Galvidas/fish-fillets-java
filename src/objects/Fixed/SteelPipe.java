package objects.Fixed;

import objects.FixedObject;
import objects.GameObject;
import pt.iscte.poo.game.Room;

import java.util.List;

public class SteelPipe extends FixedObject {

	private boolean horizontal;

	public SteelPipe(Room room, boolean horizontal) {
		super(room);
		this.horizontal = horizontal;
	}

	@Override
	public String getName() {

		String orientation = horizontal ? "Horizontal" : "Vertical";
		return "steel" + orientation;
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean canSupport(List<GameObject> objs) {
		return true;
	}

	@Override
	public boolean canBeDestroyed(GameObject obj) {
		return false;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		return true;
	}

	@Override
	public boolean interactWith(GameObject other) {
		return false;
	}

}
