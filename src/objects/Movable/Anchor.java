package objects.Movable;

import java.util.List;

import objects.Characters.BigFish;
import objects.Characters.SmallFish;
import objects.GameObject;
import objects.MobileObjects;
import objects.Water;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.utils.Weight;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Anchor extends MobileObjects {
	private boolean wasPushed;
	
	public Anchor(Room room) {
        super(room, Weight.HEAVY);
        wasPushed = false;
    }


    @Override
    public String getName() {
        return "anchor";
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
    	if (obj instanceof BigFish)
    		return wasPushed;
    	else return true;
    }
    
    @Override
    public boolean move(Vector2D direction) {
        if(!canBePushed(direction)) {
            return false;
        }

        // Mark as pushed if moved horizontally
        boolean isVertical = direction.equals(Direction.DOWN.asVector()) ||
                direction.equals(Direction.UP.asVector());

        Point2D newPosition = getPosition().plus(direction);
        List<GameObject> objectsAt = getObjectsAt(newPosition);
		for (GameObject obj : objectsAt) {
			if (!(obj instanceof Water))
				return false;
		}
        if (!isVertical && !(getRoom().getActiveCharacter() instanceof SmallFish)) {
            wasPushed = true;
        }

        setPosition(newPosition);

        return true;
    }

    @Override
    public boolean canBePushed(Vector2D direction) {


        // Check if it's vertical movement (falling)
        boolean isVertical = direction.equals(Direction.DOWN.asVector()) ||
                direction.equals(Direction.UP.asVector());

        // Block horizontal movement if already pushed
        if (!isVertical && wasPushed) {
            return false;
        }
        Point2D newPosition = getPosition().plus(direction);
        List<GameObject> objectsAt = getObjectsAt(newPosition);
        for(GameObject obj : objectsAt) {
            if(obj == this) {
                continue;
            }

            if(obj.canBlockMovement(this)) {
                return false;
            }
        }
        return true;
    }
    @Override
    public boolean interactWith(GameObject other) {
        return false;
    }
}
