package de.rosis.shop.compat;

import de.rosis.shop.RosisShop;
import net.minecraft.core.Registry;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.BusBuilder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

public final class LegacyCompatibilityRegression {
    public static void run() throws Exception {
        checkSavedRegistryNames();
        checkConfigMigration();
    }

    private static void checkSavedRegistryNames() throws Exception {
        var key = ResourceKey.<String>createRegistryKey(RosisShop.id("migration_regression"));
        Registry<String> registry = new RegistryBuilder<>(key).disableRegistrationCheck().create();
        DeferredRegister<String> entries = DeferredRegister.create(key, RosisShop.MOD_ID);
        entries.register("shop_b_e_shelf", () -> "saved-shop");
        entries.register("contract_scroll", () -> "saved-contract");
        LegacyCompatibility.addAliases(entries);
        var bus = BusBuilder.builder().build();
        entries.register(bus);
        // RegisterEvent's constructor is internal; exercise the real registration handler.
        var constructor = RegisterEvent.class.getDeclaredConstructor(ResourceKey.class, Registry.class);
        constructor.setAccessible(true);
        bus.post(constructor.newInstance(key, registry));
        registry.freeze();
        for (String path : new String[]{"shop_b_e_shelf", "contract_scroll"}) {
            var oldId = ResourceLocation.fromNamespaceAndPath(LegacyCompatibility.PREVIOUS_NAMESPACE, path);
            String restored = registry.byNameCodec().parse(NbtOps.INSTANCE, StringTag.valueOf(oldId.toString()))
                    .getOrThrow();
            require(restored.equals(registry.get(RosisShop.id(path))), "previous saved ID must resolve");
            require(registry.getKey(restored).equals(RosisShop.id(path)), "new saves must use current IDs");
        }
    }

    private static void checkConfigMigration() throws Exception {
        Path directory = Files.createTempDirectory("rosis-shop-config-");
        try {
            Path original = directory.resolve(LegacyCompatibility.PREVIOUS_NAMESPACE + ".toml");
            Files.writeString(original, "PurchaseVolume = 0.9\n");
            Path current = LegacyCompatibility.migrateConfig(directory);
            require(current.getFileName().toString().equals("rosis_shop.toml"), "config must use current name");
            require(Files.readString(current).equals(Files.readString(original)), "existing settings must migrate");
            Files.writeString(current, "PurchaseVolume = 0.3\n");
            LegacyCompatibility.migrateConfig(directory);
            require(Files.readString(current).equals("PurchaseVolume = 0.3\n"), "new settings must not be overwritten");
            require(Files.readString(original).equals("PurchaseVolume = 0.9\n"), "previous file must remain intact");
            Files.delete(current);
            Path recent = directory.resolve("spuds_shop_reforged.toml");
            Files.writeString(recent, "PurchaseVolume = 0.7\n");
            current = LegacyCompatibility.migrateConfig(directory);
            require(Files.readString(current).equals(Files.readString(recent)), "most recent config takes priority");
        } finally {
            try (var paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) Files.delete(path);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new AssertionError(message);
    }
}
