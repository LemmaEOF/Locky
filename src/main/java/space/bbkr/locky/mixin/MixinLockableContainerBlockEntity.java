package space.bbkr.locky.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.container.ContainerLock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import space.bbkr.locky.Locky;

@Mixin(LockableContainerBlockEntity.class)
public abstract class MixinLockableContainerBlockEntity extends BlockEntity {
	public MixinLockableContainerBlockEntity(BlockEntityType<?> type) {
		super(type);
	}

	@Inject(method = "checkUnlocked(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/container/ContainerLock;Lnet/minecraft/text/Text;)Z", at = @At("HEAD"), cancellable = true)
	private static void checkCreativeBypass(PlayerEntity player, ContainerLock lock, Text key, CallbackInfoReturnable cir) {
		if (player.isCreative() && player.getEntityWorld().getGameRules().getBoolean(Locky.CREATIVE_LOCK_BYPASS)) cir.setReturnValue(true);
	}
}
