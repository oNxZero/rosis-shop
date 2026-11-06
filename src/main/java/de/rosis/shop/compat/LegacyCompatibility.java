package de.rosis.shop.compat;

import de.rosis.shop.RosisShop;
import de.rosis.shop.block.ModBlockEntities;
import de.rosis.shop.block.ModBlocks;
import de.rosis.shop.item.ModItemGroups;
import de.rosis.shop.item.ModItems;
import de.rosis.shop.lootcondition.ModLootConditions;
import de.rosis.shop.screen.ModScreenHandlers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Reads previous world IDs and configuration without registering duplicate content. */
public final class LegacyCompatibility {
    public static final String PREVIOUS_NAMESPACE = "spudaciousshops";
    private static final String[] PREVIOUS_CONFIGS = {
            "spuds_shop_reforged.toml", "spudaciousshops.toml"
    };

    private LegacyCompatibility() {}

    public static void registerAliases() {
        addAliases(ModBlocks.BLOCKS);
        addAliases(ModBlocks.ITEMS);
        addAliases(ModItems.ITEMS);
        addAliases(ModBlockEntities.BLOCK_ENTITIES);
        addAliases(ModScreenHandlers.MENUS);
        addAliases(ModLootConditions.LOOT_CONDITION_TYPES);
        addAliases(ModItemGroups.CREATIVE_MODE_TABS);
    }

    public static <T> void addAliases(DeferredRegister<T> register) {
        for (var entry : register.getEntries()) {
            register.addAlias(ResourceLocation.fromNamespaceAndPath(
                    PREVIOUS_NAMESPACE, entry.getId().getPath()), entry.getId());
        }
    }

    public static Path migrateConfig(Path directory) throws IOException {
        Files.createDirectories(directory);
        Path destination = directory.resolve(RosisShop.MOD_ID + ".toml");
        if (!Files.exists(destination)) {
            for (String filename : PREVIOUS_CONFIGS) {
                Path previous = directory.resolve(filename);
                if (Files.isRegularFile(previous)) {
                    Files.copy(previous, destination);
                    break;
                }
            }
        }
        return destination;
    }
}
