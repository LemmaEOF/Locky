package space.bbkr.locky.api;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;

/**
 * An interface for marking a given block as protectable.
 * Protectable blocks must have a BlockEntity and be lockable.
 * When a Protectable block is locked, breaking it will store the inventory inside the item stack.
 * This will respect loot tables, and will prevent duplication exploits.
 * When implementing this, add a clause to your `onBlockRemoved` to prevent dropping items into the world.
 */
public interface Protectable {
	/**
	 * Returns true if the block entity at the specified world/pos is lockable, is locked, and the "locky:protectLockedBlocks" gamerule is true
	 */
	static boolean shouldProtect(World world, BlockState state, BlockEntity be) {
		if (world.getGameRules().getBoolean("protectLockedBlocks")) {
			if (be instanceof LockableContainerBlockEntity && state.getBlock() instanceof Protectable) {
				return (be.toTag(new CompoundTag()).containsKey("Lock"));
			}
		}
		return false;
	}
}
