package de.rosis.shop.block.entity.renderer;

import de.rosis.shop.item.ModItems;
import net.minecraft.world.item.ItemStack;

public class ShopIconModels {

    public static ItemStack REG_FULL;
    public static ItemStack NO_STOCK;

    public static void initialise() {
        REG_FULL = new ItemStack(ModItems.PAYMENT_WARNING.get());
        NO_STOCK = new ItemStack(ModItems.STOCK_WARNING.get());
    }
}
