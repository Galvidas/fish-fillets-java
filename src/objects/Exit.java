package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;

import java.util.List;

public class Exit extends FixedObject{

    public Exit(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "Exit";
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public boolean canSupport(List<GameObject> objs) {
        return false;
    }

    @Override
    public boolean canBlockMovement(GameObject obj) {
    	//if is game character allow movement
        if (obj instanceof GameCharacter) {
        	return false;
        }
        //It blocks the movement of other objects
        return true;
    }

    //Check if a character is at the exit position
    public boolean isFishExiting(GameObject obj) {
        return  obj instanceof GameCharacter;
    }


}
