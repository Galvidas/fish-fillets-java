package objects;

import java.util.ArrayList;
import java.util.List;

import objects.Characters.BigFish;
import objects.Characters.SmallFish;
import objects.Movable.Trap;
import objects.interfaces.Movable;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject implements Movable {

	private int moveCount;
	private boolean alive;
	private boolean hasExited;

	public GameCharacter(Room room) {
		super(room);
		this.moveCount = 0;
		this.alive = true;
		this.hasExited = false;
	}
	@Override
	public boolean move(Vector2D dir) {

		// If the character is not alive or has already exited, it cannot move
		if (!alive || hasExited)
			return false;

		Point2D currentPosition = getPosition();
		Point2D newPosition = getPosition().plus(dir);
		GameObject activeCharacter = getRoom().getActiveCharacter();

		List<GameObject> objectsAt = getObjectsAt(newPosition);

		// Check if moving to exit
		boolean movingToExit = false;
		for (GameObject obj : objectsAt) {
			if (obj instanceof Exit) {
				movingToExit = true;
				break;
			}
		}

        List<MobileObjects> mobilesToPush = new ArrayList<>();

		for (GameObject obj : objectsAt) {
            // skip self
			if (obj == this) continue;
			// Skip exit check - already handled above
			if (obj instanceof Exit) continue;

            if(this instanceof BigFish && obj instanceof Trap){
                die();
                return false;
            }
            
            if (obj instanceof Trap) continue;

			if (obj.canBlockMovement(this)) {
				return false; // movement blocked
			}

			if (obj instanceof MobileObjects) {
                mobilesToPush.add((MobileObjects) obj);
			}

		}

        if(!mobilesToPush.isEmpty()){
            if(!canPushObjects(mobilesToPush, dir)){

                die();

                return false;
            }

            for(MobileObjects mobObj : mobilesToPush){
                if(!mobObj.move(dir)){
                    return false;
                }
            }
        }

		currentPosition = newPosition;
		setPosition(currentPosition);
		moveCount++;

		if (!isWithinBounds(newPosition))
			die();
		// Check if fish exited
		if (movingToExit) {
			hasExited = true;
			// Notify room that fish exited
			getRoom().onFishExited(this);
		}
		return true;
	}

	
    private boolean canPushObjects(List<MobileObjects> mobilesToPush, Vector2D dir){
        if (mobilesToPush.isEmpty()) {
            return true;
        }
        // Just call canPush with the first object
        // The fish's canPush method will check ALL objects at that position
        return canPush(mobilesToPush.get(0), dir);
    }

	// Method Created to update the fish Images
	public void updateFishImages(Direction dir) {
		if (this instanceof SmallFish) {
			switch (dir) {
			case LEFT:
				SmallFish.getInstance().setDirection(Direction.LEFT);
				break;
			case RIGHT:
				SmallFish.getInstance().setDirection(Direction.RIGHT);
				break;
			case UP:
				SmallFish.getInstance().setDirection(Direction.UP);
				break;
			case DOWN:
				SmallFish.getInstance().setDirection(Direction.DOWN);
				break;
			}
		}
		if (this instanceof BigFish) {
			switch (dir) {
			case LEFT:
				BigFish.getInstance().setDirection(Direction.LEFT);
				break;
			case RIGHT:
				BigFish.getInstance().setDirection(Direction.RIGHT);
				break;
			case UP:
				BigFish.getInstance().setDirection(Direction.UP);
				break;
			case DOWN:
				BigFish.getInstance().setDirection(Direction.DOWN);
				break;
			}
		}
	}

	public int getMoveCount() {
		return moveCount;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public void die() {
        if(!alive) {
            return; // Already dead
        }
        alive = false;
	}

	public void resetMoveCount() {
		this.moveCount = 0;
	}

	public boolean hasExited() {
		return hasExited;
	}

	public void setHasExited(boolean exited) {
		this.hasExited = exited;
	}

	public abstract boolean canPush(GameObject obj, Vector2D direction);

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		// Don't block if this fish has exited
		if (hasExited()) {
			return false;
		}
		return (obj instanceof GameCharacter || obj instanceof MobileObjects);
	}

	@Override
	public boolean canBeDestroyed(GameObject obj) {
		return false;
	}

	@Override
	public abstract boolean canSupport(List<GameObject> objs);

	@Override
	public boolean canBePushed(Vector2D direction) {
		return false;
	}

	private boolean isWithinBounds(Point2D pos) {
        return pos.getX() >= 0 && pos.getX() < 10
                && pos.getY() >= 0 && pos.getY() < 10;
    }
	
}