package de.rosis.shop.block;


import de.rosis.shop.RosisShop;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {
    public static final TagKey<Block> SHOPS = TagKey.create(Registries.BLOCK, RosisShop.getResource("shops"));

    public static void initialise() {}
}
