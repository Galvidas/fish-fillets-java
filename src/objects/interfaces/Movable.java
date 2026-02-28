package objects.interfaces;

import pt.iscte.poo.utils.Vector2D;

public interface Movable {

	public boolean move(Vector2D direction);

	public boolean canBePushed(Vector2D direction);
}
