package space.bbkr.locky;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Locky implements ModInitializer {
    public static final Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> CUSTOM_RULES = Maps.newTreeMap(Comparator.comparing(GameRules.RuleKey::getName));

    public static final Item LOCK = register("lock", new LockItem(new Item.Settings().group(ItemGroup.TOOLS)));
    public static final Item LOCK_PICK = register("lock_pick", new LockPickItem(new Item.Settings().group(ItemGroup.TOOLS).maxDamage(32)));

    public static final GameRules.RuleKey<GameRules.BooleanRule> PROTECT_LOCKED_BLOCKS = register("locky:protectLockedBlocks", booleanOf(true, (server, rule) -> {}));
    public static final GameRules.RuleKey<GameRules.BooleanRule> CREATIVE_LOCK_BYPASS = register("locky:creativeLockBypass", booleanOf(true, (server, rule) -> {}));

    public static Item register(String name, Item item) {
        Registry.register(Registry.ITEM, "locky:" + name, item);
        return item;
    }

    @SuppressWarnings("unchecked")
    public static <T extends GameRules.Rule<T>> GameRules.RuleKey<T> register(String name, GameRules.RuleType<T> type) {
        GameRules.RuleKey<T> key = new GameRules.RuleKey(name);
        GameRules.RuleType<?> existingType = CUSTOM_RULES.put(key, type);
        if (existingType != null) {
            throw new IllegalStateException("Duplicate game rule registration for " + name);
        } else {
            return key;
        }
    }

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
            if (world.isClient) return ActionResult.PASS;
            BlockEntity be = world.getBlockEntity(hit.getBlockPos());
            ItemStack stack = player.getStackInHand(hand);
            if (be instanceof LockableContainerBlockEntity
                    && stack.getItem() == Items.NAME_TAG
                    && stack.hasCustomName()
                    && player.isSneaking()) {
                ((LockableContainerBlockEntity)be).setCustomName(stack.getName());
                if (!player.isCreative()) stack.decrement(1);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        });
    }

    @SuppressWarnings("unchecked")
    private static GameRules.RuleType booleanOf(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanRule> notifier) {
        return createRule(BoolArgumentType::bool, (type) -> new GameRules.BooleanRule((GameRules.RuleType<GameRules.BooleanRule>) type, defaultValue), notifier);
    }

    public static GameRules.RuleType<?> createRule(Supplier<ArgumentType<?>> argumentType, Function<GameRules.RuleType<?>, ?> factory, BiConsumer<MinecraftServer, ?> notifier) {
        try {
            java.lang.reflect.Constructor<GameRules.RuleType> constructor = GameRules.RuleType.class.getDeclaredConstructor(Supplier.class, Function.class, BiConsumer.class);
            constructor.setAccessible(true);
            return constructor.newInstance(argumentType, factory, notifier);
        } catch (IllegalAccessException | InstantiationException | ClassCastException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isProtected(World world, BlockEntity be) {
        if (world.getGameRules().getBoolean(PROTECT_LOCKED_BLOCKS)) {
            if (be instanceof LockableContainerBlockEntity) return be.toTag(new CompoundTag()).containsKey("Lock");
        }
        return false;
    }
}
