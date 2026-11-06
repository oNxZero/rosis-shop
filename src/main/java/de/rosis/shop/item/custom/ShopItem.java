package de.rosis.shop.item.custom;

import de.rosis.shop.block.custom.AngledShopBlock;
import de.rosis.shop.properties.Colour;
import net.minecraft.world.item.BlockItem;

public class ShopItem extends BlockItem {

    public final Colour colour;


    public ShopItem(AngledShopBlock block, Properties properties, Colour colour) {
        super(block, properties);
        this.colour = colour;
    }

}
