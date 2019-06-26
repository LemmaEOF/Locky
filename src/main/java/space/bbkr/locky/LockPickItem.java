package space.bbkr.locky;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;

public class LockPickItem extends Item {

	public LockPickItem(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canRepair(ItemStack target, ItemStack repair) {
		return repair.getItem() == Items.IRON_INGOT || super.canRepair(target, repair);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		if (!ctx.getWorld().isClient && player.isSneaking()) {
			ItemStack pick = player.getStackInHand(player.getActiveHand());
			BlockEntity be = ctx.getWorld().getBlockEntity(ctx.getBlockPos());
			if (be instanceof LockableContainerBlockEntity) {
				LockableContainerBlockEntity container = (LockableContainerBlockEntity) be;
				CompoundTag tag = be.toTag(new CompoundTag());
				if ((player.isCreative() || pick.hasCustomName()) && tag.containsKey("Lock")) {
					if (container.checkUnlocked(player) || player.isCreative()) {
						String lockName = tag.getString("Lock");
						tag.remove("Lock");
						if (!player.isCreative()) {
							player.dropItem(new ItemStack(Locky.LOCK).setCustomName(new LiteralText(lockName)), true);
						}
						player.addChatMessage(new TranslatableText("msg.locky.unlocked"), true);
						if (!player.isCreative())
							player.getStackInHand(player.getActiveHand()).damage(1, player, (user) -> user.sendToolBreakStatus(user.getActiveHand()));
						ctx.getWorld().playSound(null, ctx.getBlockPos().getX(), ctx.getBlockPos().getY(), ctx.getBlockPos().getZ(), SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 0.5F, ctx.getWorld().random.nextFloat() * 0.1F + 0.9F);
					} else {
						player.addChatMessage(new TranslatableText("msg.locky.cantunlock"), true);
						ctx.getWorld().playSound(null, ctx.getBlockPos().getX(), ctx.getBlockPos().getY(), ctx.getBlockPos().getZ(), SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.5F, ctx.getWorld().random.nextFloat() * 0.1F + 0.9F);
					}
				}
				be.fromTag(tag);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}
}

