package pt.iscte.poo.game;

import objects.*;
import objects.Characters.Krab;
import objects.Characters.SmallFish;
import objects.Fixed.HoleWall;
import objects.Movable.Bomb;
import objects.Movable.Buoy;
import objects.Movable.Trap;
import pt.iscte.poo.gui.ImageGUI;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

import java.util.List;
import java.util.stream.Collectors;

public class DeathManager{

    private Room room;
    private ImageGUI gui;

    public DeathManager(Room room) {
        this.room = room;
        this.gui = ImageGUI.getInstance();
    }
    public void setRoom(Room room) {
        this.room = room;
    }

    public boolean checkDeathConditions(GameCharacter fish){

        //Check if fish is already dead or exited
        if(!fish.isAlive() || fish.hasExited()){
            return false;
        }

        //Check Weight above fish
        if(checkDeathByWeight(fish)){
            triggerDeathAnimation(fish, DeathType.CRUSHED);
            return true;
        }
        DeathType deathType = checkDeathByObject(fish);
        if (deathType != null) {
            triggerDeathAnimation(fish, deathType);
            return true;
        }

        if (checkDeathByKrab(fish)){
            triggerDeathAnimation(fish, DeathType.KILLED);
            return true;
        }

        return false;
    }

    private boolean checkDeathByWeight(GameCharacter fish){
    	Point2D fishPos = fish.getPosition();
    	if (fish instanceof SmallFish) { //prevent from killing while on a traversable
    		List<GameObject> objectsAt = getObjectsAt(fishPos);
    		for (GameObject obj : objectsAt) {
    			if (obj instanceof HoleWall || obj instanceof Trap || obj instanceof Buoy) {
    				return false;
    			}
    		}
    	}
    	Point2D above = fishPos.plus(Direction.UP.asVector());
        List<GameObject> objectsAbove = getObjectsAt(above);
        List<GameObject> mobileObjectsAbove = objectsAbove.stream()
                .filter(obj -> obj instanceof MobileObjects)
                .collect(Collectors.toList());
        if (mobileObjectsAbove.isEmpty())
        	return false;
        above = above.plus(Direction.UP.asVector()); //one more check
        objectsAbove = getObjectsAt(above);
        mobileObjectsAbove.addAll(objectsAbove.stream()
        		.filter(obj -> obj instanceof MobileObjects)
        	.collect(Collectors.toList()));
        return !fish.canSupport(mobileObjectsAbove);
    }

    private DeathType checkDeathByObject(GameCharacter fish){

        // Check all bombs in room for explosions
        for (GameObject obj : room.getObjects()) {
            if (obj instanceof Bomb) {
                Bomb bomb = (Bomb) obj;
                if (bomb.isDeadly(fish)) {
                    return DeathType.EXPLOSION;
                }
            }
        }

        // Check objects at fish position
        List<GameObject> objectsAtPos = getObjectsAt(fish.getPosition());

        for (GameObject obj : objectsAtPos) {
            if (obj instanceof Trap) {
                Trap trap = (Trap) obj;
                if (trap.isDeadly(fish)) {
                    return DeathType.TRAPPED;
                }
            }
        }

        return null;

    }

    private boolean checkDeathByKrab(GameCharacter fish){

        List<GameObject> objectsAtPos = getObjectsAt(fish.getPosition());

        for(GameObject object : objectsAtPos){
            if(object instanceof Krab){
                Krab krab = (Krab) object;
                if(krab.isDeadly(fish)){
                    return true;
                }
            }
        }
        return false;
    }

    private void triggerDeathAnimation(GameCharacter fish, DeathType deathType){
        fish.die();
        String fishName = fish instanceof SmallFish ? "Small Fish" : "Big Fish";

        switch(deathType){
            case CRUSHED:
                break;
            case TRAPPED:
                break;
            case EXPLOSION:
                break;
            case KILLED:
                break;
            case EXAUSTION:
                break;
        }

        gui.setStatusMessage("☠️ " + fishName.toUpperCase() + " DIED!");

    }

    /**
     * Helper: Get all objects at a position
     */
    private List<GameObject> getObjectsAt(Point2D pos) {
        if (room == null) {
            return List.of(); // Return empty list if room not set
        }

        return room.getObjects().stream()
                .filter(obj -> obj.getPosition() != null && obj.getPosition().equals(pos))
                .collect(Collectors.toList());
    }

    public enum DeathType{
        CRUSHED,
        TRAPPED,
        EXPLOSION,
        EXAUSTION,
        KILLED
    }
}
