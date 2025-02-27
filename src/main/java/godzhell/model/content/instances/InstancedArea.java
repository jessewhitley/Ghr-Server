package godzhell.model.content.instances;

import godzhell.model.players.Boundary;

public abstract class InstancedArea {

	/**
	 * The boundary or location for this instanced area
	 */
	protected Boundary boundary;

	/**
	 * The height of this area
	 */
	protected int height;

	/**
	 * Creates a new area with a boundary
	 *
	 * @param boundary the boundary or location
	 * @param height the height of the area
	 */
	public InstancedArea(Boundary boundary, int height) {
		this.boundary = boundary;
		this.height = height;
	}

	/**
	 * When an instanced area is disposed it should be common to clean up that instanced area so that it can be re-used by others.
	 * <p>
	 * This function is called when the {@link InstancedAreaManager#disposeOf(InstancedArea)} function is referenced.
	 * </p>
	 */
	public abstract void onDispose();

	/**
	 * Determines the height of this area
	 *
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * The boundary or location of this instanced area
	 *
	 * @return the boundary
	 */
	public Boundary getBoundary() {
		return boundary;
	}

}
