package space.bbkr.locky.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import space.bbkr.locky.Locky;

import java.util.HashMap;
import java.util.Map;

@Mixin(GameRules.class)
public abstract class MixinGameRules {
	@Shadow @Final @Mutable
	private Map<GameRules.RuleKey<?>, GameRules.Rule<?>> rules;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void injectCustomRules(CallbackInfo ci) {
		Map<GameRules.RuleKey<?>, GameRules.Rule<?>> oldRules = new HashMap<>(rules);
		oldRules.putAll(Locky.CUSTOM_RULES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, (entry) -> (entry.getValue()).newRule())));
		rules = oldRules;
	}
}
