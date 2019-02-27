package space.bbkr.locky;

import io.github.cottonmc.ecs.api.Component;
import net.minecraft.container.ContainerLock;
import net.minecraft.item.ItemStack;

public interface LockComponent extends Component {

	/**
	 * @return Whether the container is currently locked or not.
	 */
	public boolean isLocked();

	/**
	 * Check if a player/component can unlock a block/side.
	 * @param key The ItemStack used to try to unlock a container.
	 * @return Whether the ItemStack will work to unlock.
	 */
	public default boolean canUnlock(ItemStack key) {
		return key.getDisplayName().toString().equals(getLock().toString());
	}

	/**
	 * @return The lock currently locking the block/side.
	 */
	public ContainerLock getLock();

	/**
	 * Lock a block/side.
	 * @param lock The lock to put on the block/side.
	 */
	public void setLock(ContainerLock lock);

	/**
	 * Remove the lock, setting the ContainerLock to NONE.
	 * Should not drop a lock item; leave that to the unlocking tool.
	 */
	public default void removeLock() {
		setLock(ContainerLock.NONE);
	}

	/**
	 * @return Whether a player can open the block/side that's locked.
	 */
	public boolean ignorePlayer();

	/**
	 * @return Whether another block can interact with the block/side that's locked.
	 */
	public boolean ignoreBlocks();
}
