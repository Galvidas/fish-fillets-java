package objects.Characters;

import objects.GameCharacter;
import objects.GameObject;
import objects.interfaces.Deadly;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pt.iscte.poo.game.DeathManager.DeathType;

import objects.interfaces.Movable;

public class Krab extends GameObject implements Movable, Deadly {

	private static Krab kr = new Krab(null);
	Random rng = new Random();
	private boolean hasDrowned;
	public Krab(Room room) {
		super(room);
		hasDrowned = false;
	}

	@Override
	public String getName() {
		return "krab" + (hasDrowned ? "Drown" : "");
	}

	@Override
	public boolean canSupport(List<GameObject> objs) {
		return false;
	}

	@Override
	public boolean interactWith(GameObject other) {
		return false;
	}

	@Override
	public boolean needsSupport() {
		return true;
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean canBeDestroyed(GameObject obj) {
		return false;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		return false;
	}
	
	public boolean isDrowned() {
		return hasDrowned;
	}
	
	@Override
	public boolean move(Vector2D direction) {
		if (hasDrowned)
			return false;
		int rngMove = rng.nextInt(2);
		Point2D curPosition = getPosition();
		Point2D newPosition = null;
		boolean cantMove = false;
		Direction[] dir = {Direction.LEFT,Direction.RIGHT};
		for (int i = 0; i < 2; i++) {
			cantMove = false;
			newPosition = getPosition().plus(dir[(rngMove + i) % 2].asVector());
			if (!isWithinBounds(newPosition))
				cantMove = true;
			List<GameObject> objectsAtCur = getObjectsAt(curPosition);
			List<GameObject> objectsAtNew = getObjectsAt(newPosition);
			for (GameObject obj : objectsAtCur) {
				if (obj == this) continue;
				if (obj instanceof SmallFish) //don't move, go for the kill
					return false;
			}
			for (GameObject obj : objectsAtNew) {
				if (obj == this) continue;
				if (obj.canBlockMovement(this)) {
					cantMove = true; // movement blocked
				}
			}
			if (!cantMove)
				break;
		}
		if (cantMove)
			return false;
		hasDrowned = true;
		List<GameObject> krabs = new ArrayList<>();
		krabs.add(this);
		Point2D belowPos = newPosition.plus(Direction.DOWN.asVector());
		List<GameObject> objectsBelow = getObjectsAt(belowPos);
		for (GameObject obj : objectsBelow) {
			if (obj == this) continue;
			if (obj.canSupport(krabs)) {
				hasDrowned = false;
			}
		}
		setPosition(newPosition);
		return true;
	}

	@Override
	public boolean canBePushed(Vector2D direction) {
		return false;
	}

    @Override
    public boolean isDeadly(GameCharacter character) {
    	if (character instanceof SmallFish && !hasDrowned)
    		return true;
    	else
    		return false;
    }

    @Override
    public DeathType getDeathType() {
        return DeathType.KILLED;
    }
    
    private boolean isWithinBounds(Point2D pos) {
        return pos.getX() >= 0 && pos.getX() < 10
                && pos.getY() >= 0 && pos.getY() < 10;
    }
}