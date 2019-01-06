package space.bbkr.locky;

import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
    }
}
