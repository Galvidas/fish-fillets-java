package objects.Movable;

import objects.Characters.BigFish;
import objects.Characters.Krab;
import objects.GameObject;
import objects.MobileObjects;
import objects.interfaces.Traversable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Weight;
import java.util.List;

import pt.iscte.poo.utils.Direction;

public class Stone extends MobileObjects implements Traversable {

	private boolean spawnKrab = false;
	private boolean hasSpawned = false;
    public Stone(Room room) {
        super(room, Weight.HEAVY);
    }

    @Override
    public String getName() {
        return "stone";
    }

    @Override
    public int getLayer() {
        return 2;
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
        return false;
    }

    @Override
    public boolean move(Vector2D direction) {
        if(!canBePushed(direction)) {
            return false;
        }
        Point2D newPosition = getPosition().plus(direction);
        setPosition(newPosition);
        if (direction.equals(Direction.LEFT.asVector()) || direction.equals(Direction.RIGHT.asVector()))
        	spawnKrab = true;
        return true;
    }

    public void spawnKrab() {
    	if (hasSpawned || !spawnKrab)
    		return;
    	Point2D above = getPosition().plus(Direction.UP.asVector());
        List<GameObject> objectsAt = getObjectsAt(above);
        for(GameObject obj : objectsAt) {
        	if (obj.canBlockMovement(this)) {
				return; // don't spawn
			}
        }
        GameObject krab = new Krab(getRoom());
        krab.setPosition(above);
        getRoom().toAddObject(krab);
        hasSpawned = true;
    }
    @Override
    public boolean canBePushed(Vector2D direction) {
        Point2D newPosition = getPosition().plus(direction);
        List<GameObject> objectsAt = getObjectsAt(newPosition);
        for(GameObject obj : objectsAt) {
            //skip self
            if(obj == this)
                continue;
            
            if(obj instanceof Stone)
            {
            	if (getRoom().getActiveCharacter() instanceof BigFish)
            	{
	            	if (((Stone) obj).canBePushed(direction))
	            		((Stone) obj).move(direction);
	            	else
	            		return false;
	            	return true;
            	}
            	else
            		return false;
            }

            if(obj.canBlockMovement(this)) {
                return false; //movement blocked
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
