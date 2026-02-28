package objects.Characters;

import objects.GameCharacter;
import objects.GameObject;
import objects.MobileObjects;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.utils.Weight;
import java.util.List;

public class BigFish extends GameCharacter {

	private static BigFish bf = new BigFish(null);
	private String horizontalDirection = "Left";// default direction
	private String verticalDirection = "";// Track vertical direction

	private BigFish(Room room) {
		super(room);
	}

	public static BigFish getInstance() {
		return bf;
	}

	public static void resetInstance() {
		bf = new BigFish(null);
	}

	@Override
	public String getName() {
		return "bigFish" + verticalDirection + horizontalDirection;
	}

	// Method to set direction
	public void setDirection(Direction dir) {
		if (dir == Direction.LEFT) {
			this.horizontalDirection = "Left";
			this.verticalDirection = "";
		} else if (dir == Direction.RIGHT) {
			this.horizontalDirection = "Right";
			this.verticalDirection = "";
		} else if (dir == Direction.UP) {
			this.verticalDirection = "Up";
		} else if (dir == Direction.DOWN) {
			this.verticalDirection = "Down";
		}
	}

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean canPush(GameObject obj, Vector2D direction) {
        Point2D checkPosition = getPosition().plus(direction);
        int mobileCount = 0;

        // Recursively check all positions in the push chain
        while (true) {
            List<GameObject> objectsAtPos = getObjectsAt(checkPosition);
            boolean foundMobile = false;

            for (GameObject o : objectsAtPos) {
                if (o instanceof MobileObjects) {
                    foundMobile = true;
                    mobileCount++;
                }
            }

            // If no mobile objects at this position, we're done
            if (!foundMobile) {
                break;
            }

            // Check next position in the chain
            checkPosition = checkPosition.plus(direction);
        }

        boolean isVertical = (direction.equals(Direction.DOWN.asVector()) ||
                direction.equals(Direction.UP.asVector()));

        // BigFish dies if pushing 2+ objects vertically
        if (isVertical && mobileCount >= 2) {
            return false;
        }

        // BigFish can push 1 object vertically
        if (isVertical && mobileCount == 1) {
            return true;
        }

        // BigFish can push any number horizontally
        if (!isVertical) {
            return true;
        }

        return true;
	}

	@Override
	public boolean canSupport(List<GameObject> objs) {
		int heavyCount = 0;

        for (GameObject obj : objs) {
            if (obj instanceof MobileObjects) {
                MobileObjects mobObj = (MobileObjects) obj;
                if (mobObj.getWeight() == Weight.HEAVY) {
                    heavyCount++;
                }
            }
        }
        //Dies if more than one heavy object is supported
		return heavyCount <= 1;
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
