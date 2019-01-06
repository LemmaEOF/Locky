package space.bbkr.locky;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.container.LockableContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.ActionResult;

public class LockItem extends Item {

	public LockItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		PlayerEntity player = ctx.getPlayer();
		if (!ctx.getWorld().isClient && player.isSneaking()) {
			ItemStack lock = player.getStackInHand(player.getActiveHand());
			BlockEntity be = ctx.getWorld().getBlockEntity(ctx.getPos());
			if (be instanceof LockableContainer) {
				CompoundTag tag = be.toTag(new CompoundTag());
				if (lock.hasDisplayName() && (!tag.containsKey("Lock") || tag.getString("Lock").equals(""))) {
					tag.putString("Lock", lock.getDisplayName().getString());
					player.addChatMessage(new TranslatableTextComponent("msg.locky.locked", lock.getDisplayName().getString()), true);
					player.getStackInHand(player.getActiveHand()).subtractAmount(1);
					ctx.getWorld().playSound(null, ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ(), SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundCategory.BLOCK, 0.5F, ctx.getWorld().random.nextFloat() * 0.1F + 0.9F);
				} else {
					player.addChatMessage(new TranslatableTextComponent("msg.locky.cantlock"), true);
					ctx.getWorld().playSound(null, ctx.getPos().getX(), ctx.getPos().getY(), ctx.getPos().getZ(), SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCK, 0.5F, ctx.getWorld().random.nextFloat() * 0.1F + 0.9F);
				}
				be.fromTag(tag);
				return ActionResult.SUCCESS;
			}
		}
		return ActionResult.PASS;
	}
}
