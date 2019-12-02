package space.bbkr.locky.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import space.bbkr.locky.Locky;

import java.util.List;

@Mixin(Block.class)
public class MixinBlock {

	@Inject(method = "onBreak", at = @At("HEAD"))
	public void dropOnBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo ci) {
		BlockEntity be = world.getBlockEntity(pos);
		if (Locky.isProtected(world, be)) {
			LockableContainerBlockEntity container = (LockableContainerBlockEntity) be;
			CompoundTag tag = container.toTag(new CompoundTag());
			if (player.isCreative()) {
				ItemStack stack = new ItemStack(((Block)(Object)this).asItem());
				stack.putSubTag("BlockEntityTag", tag);
				CompoundTag displayTag = new CompoundTag();
				ListTag loreList = new ListTag();
				loreList.add(StringTag.of("\"(Locked)\""));
				displayTag.put("Lore", loreList);
				stack.putSubTag("display", displayTag);
				if (container.hasCustomName()) stack.setCustomName(container.getDisplayName());
				ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				item.setToDefaultPickupDelay();
				world.spawnEntity(item);
			}
		}
	}

	@Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/loot/context/LootContext$Builder;)Ljava/util/List;",
			at = @At(value = "RETURN", ordinal = 1),
			cancellable = true,
			locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void dropProtectedStack(BlockState state, LootContext.Builder builder, CallbackInfoReturnable<List<ItemStack>> cir,
									Identifier id, LootContext context, ServerWorld server, LootTable supplier) {
		BlockEntity be = builder.getNullable(LootContextParameters.BLOCK_ENTITY);
		List<ItemStack> stacks =  supplier.getDrops(context);
		if (be != null && Locky.isProtected(builder.getWorld(), be)) {
			ItemStack extraStack = ItemStack.EMPTY;
			for (ItemStack stack : stacks) {
				if (stack.getItem() == ((Block)(Object)this).asItem()) {
					LockableContainerBlockEntity container = (LockableContainerBlockEntity) be;
					CompoundTag tag = container.toTag(new CompoundTag());
					CompoundTag displayTag = new CompoundTag();
					ListTag listTag_1 = new ListTag();
					listTag_1.add(StringTag.of("\"(Locked)\""));
					displayTag.put("Lore", listTag_1);
					if (stack.getCount() == 1) {
						stack.putSubTag("BlockEntityTag", tag);
						stack.putSubTag("display", displayTag);
						if (container.hasCustomName()) stack.setCustomName(container.getDisplayName());
						break;
					} else {
						stack.decrement(1);
						ItemStack newStack = new ItemStack(((Block)(Object)this).asItem());
						newStack.putSubTag("BlockEntityTag", tag);
						stack.putSubTag("display", displayTag);
						if (container.hasCustomName()) stack.setCustomName(container.getDisplayName());
						extraStack = newStack;
						break;
					}

				}
			}
			if (!extraStack.isEmpty()) stacks.add(extraStack);
		}
		cir.setReturnValue(stacks);
	}
}
