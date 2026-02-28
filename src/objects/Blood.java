package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Weight;

import java.util.List;

public class Blood extends GameObject {

    private int ticksAlive;
    private static final int LIFETIME_TICKS = 1; // Blood lasts for 3 ticks

	public Blood(Room room) {
		super(room);
        this.ticksAlive = 0;
	}

    public void update() {
        ticksAlive++;

        if (ticksAlive >= LIFETIME_TICKS) {
            getRoom().scheduleRemoveObject(this);
        }
    }

    public int getRemainingTicks(){
        return Math.max(0, LIFETIME_TICKS - ticksAlive);
    }

    public boolean shouldBeRemoved() {
        return ticksAlive >= LIFETIME_TICKS;
    }

	@Override
	public String getName() {
		return "blood";
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
	public boolean canBlockMovement(GameObject obj) {
		return false;
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
