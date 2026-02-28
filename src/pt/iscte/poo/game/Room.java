package pt.iscte.poo.game;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import objects.*;
import objects.Characters.BigFish;
import objects.Characters.Krab;
import objects.Characters.SmallFish;
import objects.Fixed.HoleWall;
import objects.Fixed.SteelPipe;
import objects.Fixed.Trunk;
import objects.Fixed.Wall;
import objects.Movable.*;
import pt.iscte.poo.utils.Point2D;
import java.util.*;

public class Room {

	private String roomName;
	private GameEngine engine;

	private Point2D smallFishStartingPosition;
	private Point2D bigFishStartingPosition;
	private GameObject activeCharacter;
	private int activeCharacterIndex = 0;

	private boolean smallFishExited;
	private boolean bigFishExited;


    private List<GameObject> objects;
    private List<GameObject> objectsToRemove;
    private List<GameObject> objectsToAdd;

	public Room() {
		objects = new ArrayList<GameObject>();
        objectsToAdd = new ArrayList<GameObject>();
		objectsToRemove = new ArrayList<GameObject>();
		smallFishExited = false;
		bigFishExited = false;
	}

    private void setName(String name) {
        roomName = name;
    }

    public String getName() {
        return roomName;
    }

    private void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
    }

    public void removeObject(GameObject obj) {
        objects.remove(obj);
    }

    // Schedule object to be added(use during iteration)
    //happens in processPendingChanges()
    public void scheduleAddObject(GameObject obj) {
        objectsToAdd.add(obj);
    }
    // Schedule object to be removed(use during iteration)
    //happens in processPendingChanges()
    public void scheduleRemoveObject(GameObject obj) {
        objectsToRemove.add(obj);
    }

    //keep for backward compatibility
    public void toAddObject(GameObject obj) {
        scheduleAddObject(obj);
    }
    //keep for backward compatibility
    public void killObject(GameObject obj) {
        scheduleRemoveObject(obj);
    }

    //process pending additions and removals
    //call at start or end of game loop iteration
    public void processPendingChanges() {
        //remove objects (in case of replacement)
        if(!objectsToRemove.isEmpty()) {
            objects.removeAll(objectsToRemove);
            objectsToRemove.clear();
        }

        //add objects
        if(!objectsToAdd.isEmpty()) {
            objects.addAll(objectsToAdd);
            objectsToAdd.clear();
        }

        //update GUI if any changes
        if(engine != null)
            engine.updateGUI();

    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public List<GameObject> getPendingAdditions() {
        return new ArrayList<>(objectsToAdd);
    }

    public List<GameObject> getPendingRemovals() {
        return new ArrayList<>(objectsToRemove);
    }

    public void setSmallFishStartingPosition(Point2D heroStartingPosition) {
        this.smallFishStartingPosition = heroStartingPosition;
    }

    public Point2D getSmallFishStartingPosition() {
        return smallFishStartingPosition;
    }

    public void setBigFishStartingPosition(Point2D heroStartingPosition) {
        this.bigFishStartingPosition = heroStartingPosition;
    }

    public Point2D getBigFishStartingPosition() {
        return bigFishStartingPosition;
    }

    public GameObject getActiveCharacter() {
        return activeCharacter;
    }

    public void setActiveCharacter(GameObject character) {
    	this.activeCharacter = character;
    }

	// Method Created to switch active character
	public void switchActiveCharacter() {
		SmallFish smallFish = SmallFish.getInstance();
        BigFish bigFish = BigFish.getInstance();

        if (activeCharacter == smallFish) {
            if(!bigFish.hasExited())
                activeCharacter = bigFish;
        } else {
            if(!smallFish.hasExited())
                activeCharacter = smallFish;
        }
	}

	// Check Wining Condition for both fishes and removes fish from room
	public void onFishExited(GameObject fish) {
		if (fish instanceof SmallFish) {
			smallFishExited = true;
		} else if (fish instanceof BigFish) {
			bigFishExited = true;
		}
		scheduleRemoveObject(fish);
		// Switch active character if the exited fish was the active one
		if (fish == activeCharacter) {
			switchActiveCharacter();
		}

		// Check if both fishes have exited
		checkWinningCondition();
	}

	// Method Created to check winning condition
	private void checkWinningCondition() {
		if (smallFishExited && bigFishExited) {
			engine.onLevelCompleted();
		}
	}

	public boolean isLevelCompleted() {
		return smallFishExited && bigFishExited;
	}

	public static Room readRoom(File f, GameEngine engine) {

		try {
			Room r = new Room();
			r.setEngine(engine);
			r.setName(f.getName());

			// Fill room with water
			fillWithWater(r);

			Scanner objectScanner = new Scanner(f);
			int row = 0;
			while (objectScanner.hasNextLine() && row < 10) {
				String line = objectScanner.nextLine();

				// Pad line to 10 characters with spaces
				if (line.length() < 10) {
					line = String.format("%-10s", line);
				}

				for (int col = 0; col < line.length(); col++) {
					char objectName = line.charAt(col);
					Point2D pos = new Point2D(col, row);

					switch (objectName) {
					case 'B':
                        r.setBigFishStartingPosition(pos);
						GameObject bigFish = BigFish.getInstance();
                        bigFish.setRoom(r);
						bigFish.setPosition(pos);
						r.addObject(bigFish);
						if(r.getActiveCharacter() == null || r.getActiveCharacter() instanceof SmallFish) {
                            r.setActiveCharacter(bigFish);
                        }
						break;
					case 'S':
                        r.setSmallFishStartingPosition(pos);
						GameObject smallFish = SmallFish.getInstance();
                        smallFish.setRoom(r);
						smallFish.setPosition(pos);
						r.addObject(smallFish);
						if(r.getActiveCharacter() == null) {
                            r.setActiveCharacter(smallFish);
                        }
						break;
					case 'W':
						GameObject wall = new Wall(r);
						wall.setPosition(pos);
						r.addObject(wall);
						break;
					case 'H':
						GameObject steelPipeH = new SteelPipe(r, true);
						steelPipeH.setPosition(pos);
						r.addObject(steelPipeH);
						break;
					case 'V':
						GameObject steelPipeV = new SteelPipe(r, false);
						steelPipeV.setPosition(pos);
						r.addObject(steelPipeV);
						break;
					case 'C':
						GameObject cup = new Cup(r);
						cup.setPosition(pos);
						r.addObject(cup);
						break;
					case 'E':
						GameObject exit = new Exit(r);
						exit.setPosition(pos);
						r.addObject(exit);
						break;
					case 'X':
						GameObject hole = new HoleWall(r);
						hole.setPosition(pos);
						r.addObject(hole);
						break;
					case 'b':
						GameObject bomb = new Bomb(r);
						bomb.setPosition(pos);
						r.addObject(bomb);
						break;
					case 'A':
						GameObject anchor = new Anchor(r);
						anchor.setPosition(pos);
						r.addObject(anchor);
						break;
					case 'R':
						GameObject stone = new Stone(r);
						stone.setPosition(pos);
						r.addObject(stone);
						break;
					case 'T':
						GameObject trap = new Trap(r);
						trap.setPosition(pos);
						r.addObject(trap);
						break;
					case 'K':
						GameObject krab = new Krab(r);
						krab.setPosition(pos);
						r.addObject(krab);
						break;
					case 'O':
						GameObject buoy = new Buoy(r);
						buoy.setPosition(pos);
						r.addObject(buoy);
						break;
					case 'Y':
						GameObject trunk = new Trunk(r);
						trunk.setPosition(pos);
						r.addObject(trunk);
						break;
					}
				}
				row++;
			}
			objectScanner.close();
			return r;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	// Method to get room level
	public int getLevel() {
		// Assuming room names are in the format "roomX.txt"
		String levelStr = roomName.replaceAll("[^0-9]", "");
		try {
			return Integer.parseInt(levelStr);
		} catch (NumberFormatException e) {
			return 0; // Default level if parsing fails
		}
	}

	private static void fillWithWater(Room room) {
		for (int y = 0; y < 10; y++) {
			for (int x = 0; x < 10; x++) {
				Water water = new Water(room);
				water.setPosition(new Point2D(x, y));
				room.addObject(water);
			}
		}
	}

}