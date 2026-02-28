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

public class SmallFish extends GameCharacter {

	private static SmallFish sf = new SmallFish(null);
	private String horizontalDirection = "Left";// default direction
	private String verticalDirection = "";// Track vertical direction
	private List<GameObject> supportedObjects;

	private SmallFish(Room room) {
		super(room);
	}

	public static SmallFish getInstance() {
		return sf;
	}

	public static void resetInstance() {
		sf = new SmallFish(null);
	}

	@Override
	public String getName() {
		return "smallFish" + verticalDirection + horizontalDirection;
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
        int lightCount = 0;
        int heavyCount = 0;

        // Recursively check all positions in the push chain
        while (true) {
            List<GameObject> objectsAtPos = getObjectsAt(checkPosition);
            boolean foundMobile = false;

            for (GameObject o : objectsAtPos) {
                if (o instanceof MobileObjects) {
                    foundMobile = true;
                    MobileObjects mobObj = (MobileObjects) o;
                    if (mobObj.getWeight() == Weight.HEAVY) {
                        heavyCount++;
                    } else {
                        lightCount++;
                    }
                }
            }

            // If no mobile objects at this position, we're done
            if (!foundMobile) {
                break;
            }

            // Check next position in the chain
            checkPosition = checkPosition.plus(direction);
        }

        // SmallFish dies if pushing:
        // - 1 or more heavy objects
        // - 2 or more light objects
        if (heavyCount >= 1) {
            return false;
        }

        if (lightCount >= 2) {
            return false;
        }

        // SmallFish can push 1 light object
        if (lightCount == 1) {
            return true;
        }

        return true;

	}
	
	@Override
	public boolean interactWith(GameObject other) {
		return false;
	}

	@Override
	public boolean canSupport(java.util.List<GameObject> objs) {
		int lightCount = 0;
		int heavyCount = 0;
		for (GameObject obj : objs) {
			if (obj instanceof MobileObjects) {
				MobileObjects mobObj = (MobileObjects) obj;
				if (mobObj.getWeight() == Weight.LIGHT) {
					lightCount++;
				} else if (mobObj.getWeight() == Weight.HEAVY) {
					heavyCount++;
				}
			}
		}
		return lightCount <= 1 && heavyCount == 0;
	}

	@Override
	public boolean needsSupport() {
		return false;
	}


}
