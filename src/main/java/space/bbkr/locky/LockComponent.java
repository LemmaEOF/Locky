package space.bbkr.locky;

import io.github.cottonmc.ecs.api.Component;
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
	public boolean canUnlock(ItemStack key);

	/**
	 * The player-set name of the lock used to lock the block/side.
	 * Can either be used for vanilla LockContainer-style locks,
	 * or helping a player remember which lock was used to lock a container.
	 * @return The name of the lock used.
	 */
	public String getLockName();

	/**
	 * Lock a block/side.
	 * @param lock The ItemStack used to set the lock, inclding the NBT used to specify unlock conditions.
	 */
	public void lock(ItemStack lock);

	/**
	 * Remove the lock, deleting its information instantly.
	 * Should not drop a lock item; leave that to the unlocking tool.
	 */
	public void removeLock();

	/**
	 * @return Whether a player can open the block/side that's locked.
	 */
	public boolean canPlayerAlwaysUse();

	/**
	 * @return Whether another block can interact with the block/side that's locked.
	 */
	public boolean canBlocksAlwaysInteract();
}
