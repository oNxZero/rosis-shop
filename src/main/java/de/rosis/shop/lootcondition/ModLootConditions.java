package de.rosis.shop.lootcondition;

import de.rosis.shop.RosisShop;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootConditions {
    public static final DeferredRegister<LootItemConditionType> LOOT_CONDITION_TYPES = DeferredRegister
            .create(Registries.LOOT_CONDITION_TYPE, RosisShop.MOD_ID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> MATCHES_ENUM = LOOT_CONDITION_TYPES
            .register("matches_colour", () -> new LootItemConditionType(MatchingCushionColourCondition.CODEC));

    public static void register(IEventBus eventBus) {
        LOOT_CONDITION_TYPES.register(eventBus);
    }
}
