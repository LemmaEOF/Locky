package space.bbkr.locky.mixin;

import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.TreeMap;


@Mixin(GameRules.class)
public class MixinGameRules {
	@Shadow
	@Final
	public TreeMap<String, GameRules.Value> rules;

	@Shadow @Final private static TreeMap<String, GameRules.Key> KEYS;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void addGameRule(CallbackInfo ci) {
		GameRules.Key protectionRule = new GameRules.Key("true", GameRules.Type.BOOLEAN_VALUE);
		GameRules.Key creativeRule = new GameRules.Key("true", GameRules.Type.BOOLEAN_VALUE);
		getRules().put("locky:protectLockedBlocks", protectionRule.createValue());
		KEYS.put("locky:protectLockedBlocks", protectionRule);
		getRules().put("locky:shouldCreativeBypassLock", creativeRule.createValue());
		KEYS.put("locky:shouldCreativeBypassLock", creativeRule);
	}

	public TreeMap<String, GameRules.Value> getRules() {
		return rules;
	}
}
