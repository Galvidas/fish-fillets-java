package objects.Movable;

import objects.*;
import objects.Fixed.Wall;
import objects.interfaces.Deadly;
import pt.iscte.poo.game.DeathManager.DeathType;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Vector2D;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Weight;

import java.util.ArrayList;
import java.util.List;

import pt.iscte.poo.utils.Direction;

public class Bomb extends MobileObjects implements Deadly {

    private boolean exploding;
    private boolean hasExploded;
    private int explosionTimer;
    private boolean hasFallen;
    
    public Bomb(Room room) {
        super(room, Weight.LIGHT);
        this.hasFallen = false;
        this.exploding = false;
        this.hasExploded = false;
        this.explosionTimer = 0;
    }

    public void updateExplosionTimer() {
        if (exploding) {
            explosionTimer++;

            if (explosionTimer >= 2) { // Assuming 1 ticks before explosion
                exploding = false;
                // Remove bomb from the room after explosion
                getRoom().killObject(this);
            }
        }
    }

    @Override
    public String getName() {
        return "bomb";
    }

    @Override
    public int getLayer() {
        return 1;
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
    public void updateFall(){
        if(hasExploded){
            return;
        }

        Point2D belowPosition = getPosition().plus(Direction.DOWN.asVector());
        List<GameObject> objectsAt = getObjectsAt(belowPosition);
        for (GameObject obj : objectsAt) {
            if(obj instanceof Water || obj instanceof Bomb){
                continue;
            }

            if(obj instanceof GameCharacter){
                continue;
            }

            if((obj instanceof FixedObject || obj instanceof MobileObjects) && hasFallen){
                setExploding();
                return;
            }
        }
        
        if (move(Direction.DOWN.asVector()))
        	hasFallen = true;
    }


    @Override
    public boolean move(Vector2D direction) {
        if(!canBePushed(direction)) {
            return false;
        }
        Point2D newPosition = getPosition().plus(direction);
        setPosition(newPosition);
        return true;
    }

    @Override
    public boolean canBePushed(Vector2D direction) {
        Point2D newPosition = getPosition().plus(direction);
        List<GameObject> objectsAt = getObjectsAt(newPosition);
        for(GameObject obj : objectsAt) {
            //skip self
            if(obj == this)
                continue;

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

    public boolean isExploding() {
        return exploding;
    }

    public void setExploding() {
        if(hasExploded){
            return;
        }

        exploding = true;
        hasExploded = true;
        explosionTimer = 0;

        createBloodEffects();

        destroyObjectsInBlastRadius();
    }

    private boolean findWalls(Point2D pos) {
    	List<GameObject> objectsAtPos = getObjectsAt(pos);
    	for (GameObject obj : objectsAtPos) {
    		if (obj instanceof Wall)
    			return true;
    	}
    	return false;
    }
    
    private void createBloodEffects() {
        Direction[] directions = {Direction.LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN};

        // Blood in 4 directions
        for (Direction dir : directions) {
            Point2D bloodPos = getPosition().plus(dir.asVector());
            if (isWithinBounds(bloodPos) && !findWalls(bloodPos)) {
                GameObject blood = new Blood(getRoom());
                blood.setPosition(bloodPos);
                getRoom().toAddObject(blood);
            }
        }

        // Blood at center (bomb position)
        GameObject centerBlood = new Blood(getRoom());
        centerBlood.setPosition(getPosition());
        getRoom().toAddObject(centerBlood);
    }

    private void destroyObjectsInBlastRadius() {
        List<Point2D> blastRadius = getBlastRadius();

        for (Point2D pos : blastRadius) {
            List<GameObject> objectsAtPos = getObjectsAt(pos);

            for (GameObject obj : objectsAtPos) {
                // Don't destroy water or blood
                if (obj instanceof Water || obj instanceof Blood || obj instanceof Wall) {
                    continue;
                }

                // Don't destroy fish here - DeathManager handles that
                if (obj instanceof GameCharacter) {
                    continue;
                }

                // Destroy the object
                getRoom().killObject(obj);
            }
        }
    }

    public List<Point2D> getBlastRadius() {
        List<Point2D> radius = new ArrayList<>();

        // Center position (where bomb is)
        radius.add(getPosition());

        // 4 adjacent positions
        Direction[] directions = {Direction.LEFT, Direction.UP, Direction.RIGHT, Direction.DOWN};
        for (Direction dir : directions) {
            Point2D pos = getPosition().plus(dir.asVector());
            if (isWithinBounds(pos)) {
                radius.add(pos);
            }
        }

        return radius;
    }

    public boolean isInBlastRadius(Point2D pos) {
        return getBlastRadius().contains(pos);
    }

    @Override
    public boolean isDeadly(GameCharacter obj) {
        return exploding && isInBlastRadius(obj.getPosition());
    }

    @Override
    public DeathType getDeathType() {
        return DeathType.EXPLOSION;
    }

    public boolean hasExploded() {
        return hasExploded;
    }

    private boolean isWithinBounds(Point2D pos) {
        return pos.getX() >= 0 && pos.getX() < 10
                && pos.getY() >= 0 && pos.getY() < 10;
    }


}
