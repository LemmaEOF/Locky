package space.bbkr.locky.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.bbkr.locky.Locky;

@Mixin(ItemScatterer.class)
public class MixinItemScatterer {

	@Inject(method = "spawn(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
	private static void cancelProtectedScattering(World world, double x, double y, double z, ItemStack stack, CallbackInfo ci) {
		if (Locky.isProtected(world, world.getBlockEntity(new BlockPos(x, y, z))) && world.getGameRules().getBoolean(Locky.PROTECT_LOCKED_BLOCKS)) ci.cancel();
	}
}
