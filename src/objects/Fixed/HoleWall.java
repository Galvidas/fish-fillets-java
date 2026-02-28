package objects.Fixed;

import objects.Characters.Krab;
import objects.Characters.SmallFish;
import objects.FixedObject;
import objects.GameObject;
import objects.Movable.Cup;
import objects.interfaces.Traversable;
import pt.iscte.poo.game.Room;

public class HoleWall extends FixedObject implements Traversable {

	public HoleWall(Room room) {
		super(room);
	}

	@Override
	public String getName() {
		return "holedWall";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean canSupport(GameObject obj) {
		return true;
	}

	@Override
	public boolean canBeDestroyed(GameObject obj) {
		return false;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		if (obj instanceof Cup || obj instanceof SmallFish || obj instanceof Krab)
			return false;
		else
			return true;
	}
	
	@Override
	public boolean canBeTraversed(GameObject obj) {
		if (obj instanceof SmallFish)
			return true;
		else
			return false;
	}
	
    public boolean canTraverse(GameObject obj) {
		return false;
	}

}
