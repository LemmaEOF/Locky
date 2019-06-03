package space.bbkr.locky.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import space.bbkr.locky.api.Protectable;

@Mixin(DispenserBlock.class)
public abstract class MixinDispenserBlock extends Block implements Protectable {

	public MixinDispenserBlock(Settings settings) {
		super(settings);
	}

	@Inject(method = "onBlockRemoved",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/util/ItemScatterer;spawn(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/inventory/Inventory;)V"),
			locals = LocalCapture.CAPTURE_FAILEXCEPTION,
			cancellable = true)
	private void skipItemDrop(BlockState state, World world, BlockPos pos, BlockState newState, boolean bool, CallbackInfo ci, BlockEntity be) {
		if (Protectable.shouldProtect(world, state, be)) {
			world.updateHorizontalAdjacent(pos, this);
			super.onBlockRemoved(state, world, pos, newState, bool);
			ci.cancel();
		}
	}
}
