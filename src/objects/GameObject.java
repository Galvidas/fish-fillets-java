package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.gui.ImageTile;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Weight;

import java.util.List;
import java.util.stream.Collectors;

public abstract class GameObject implements ImageTile {

	private Point2D position;
	private Room room;

	public GameObject(Room room) {
		this.room = room;
	}

	public GameObject(Point2D position, Room room) {
		this.position = position;
		this.room = room;
	}

	public void setPosition(int i, int j) {
		position = new Point2D(i, j);
	}

	public void setPosition(Point2D position) {
		this.position = position;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public abstract boolean canSupport(List<GameObject> objs);

	public abstract boolean canBeDestroyed(GameObject obj);

	public abstract boolean canBlockMovement(GameObject obj);

	public abstract boolean interactWith(GameObject other);

	public abstract boolean needsSupport();

	public List<GameObject> getObjectsAt(Point2D position) {
		return room.getObjects().stream().filter(obj -> obj.getPosition().equals(position))
				.collect(Collectors.toList());
	}

}
