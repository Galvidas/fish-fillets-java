package objects.interfaces;

import objects.GameObject;

public interface Traversable {

	public boolean canBeTraversed(GameObject obj);

	public boolean canTraverse(GameObject obj);

}
