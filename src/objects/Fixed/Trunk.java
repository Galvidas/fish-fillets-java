package objects.Fixed;

import objects.Blood;
import objects.FixedObject;
import objects.GameObject;
import objects.MobileObjects;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Weight;

public class Trunk extends FixedObject {

	public Trunk(Room room) {
		super(room);
	}

	private void die() {
		 GameObject blood = new Blood(getRoom());
		blood.setPosition(this.getPosition());
		getRoom().toAddObject(blood);
		getRoom().scheduleRemoveObject(this);
	}
	
	@Override
	public String getName() {
		return "trunk";
	}	

	@Override
	public int getLayer() {
		return 1;
	}

	@Override
	public boolean canSupport(GameObject obj) {
		if (obj instanceof MobileObjects) {
	        if (((MobileObjects) obj).getWeight() == Weight.HEAVY) {
	            die();
	            return false;
	        }
	        return true;
	    }
	    return true;
	}

	@Override
	public boolean canBeDestroyed(GameObject obj) {
		return true;
	}

	@Override
	public boolean canBlockMovement(GameObject obj) {
		return true;
	}

}
