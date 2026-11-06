package de.rosis.shop.item;

import de.rosis.shop.RosisShop;
import de.rosis.shop.item.custom.ContractScroll;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import static de.rosis.shop.RosisShop.MOD_ID;

import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MOD_ID);

    public static final DeferredHolder<Item, Item> STOCK_WARNING = register(() -> new Item(new Item.Properties()),
            "stock_warning");

    public static final DeferredHolder<Item, Item> PAYMENT_WARNING = register(() -> new Item(new Item.Properties()),
            "payment_warning");

    public static final DeferredHolder<Item, ContractScroll> CONTRACT_SCROLL = register(
            () -> new ContractScroll(new Item.Properties()), "contract_scroll");

    private static <I extends Item> DeferredHolder<Item, I> register(Supplier<I> item, String name) {
        return ITEMS.register(name, item);
    }

    public static void registerModItems(IEventBus modEventBus) {
        RosisShop.LOGGER.info("Registering mod items for " + RosisShop.MOD_ID);
        ITEMS.register(modEventBus);
    }
}
