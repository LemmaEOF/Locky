package space.bbkr.locky;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;

public class Locky implements ModInitializer {

    public static final Item LOCK = register("lock", new LockItem(new Item.Settings().itemGroup(ItemGroup.TOOLS)));
    public static final Item LOCK_PICK = register("lock_pick", new LockPickItem(new Item.Settings().itemGroup(ItemGroup.TOOLS).durability(32)));

    public static Item register(String name, Item item) {
        Registry.register(Registry.ITEM, "locky:" + name, item);
        return item;
    }

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient) return ActionResult.PASS;
            BlockEntity be = world.getBlockEntity(hit.getBlockPos());
            ItemStack stack = player.getStackInHand(hand);
            if (be instanceof LockableContainerBlockEntity
                    && stack.getItem() == Items.NAME_TAG
                    && stack.hasDisplayName()
                    && player.isSneaking()) {
                ((LockableContainerBlockEntity)be).setCustomName(stack.getDisplayName());
                if (!player.isCreative()) stack.subtractAmount(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }
}
