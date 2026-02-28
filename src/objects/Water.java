package objects;

import pt.iscte.poo.game.Room;

import java.util.List;

public class Water extends GameObject {

	public Water(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "water";
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
		return false;
	}

	@Override
	public boolean interactWith(GameObject other) {
		return false;
	}

	@Override
	public boolean needsSupport() {
		return false;
	}

}
