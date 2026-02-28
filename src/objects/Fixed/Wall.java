package objects.Fixed;

import objects.FixedObject;
import objects.GameObject;
import pt.iscte.poo.game.Room;

import java.util.List;

public class Wall extends FixedObject {

	public Wall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "wall";
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
