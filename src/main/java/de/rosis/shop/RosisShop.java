package de.rosis.shop;

import com.mojang.logging.LogUtils;

import de.rosis.shop.block.*;
import de.rosis.shop.block.entity.renderer.ShopIconModels;
import de.rosis.shop.config.ConfigHandler;
import de.rosis.shop.item.ModItemGroups;
import de.rosis.shop.item.ModItems;
import de.rosis.shop.lootcondition.ModLootConditions;
import de.rosis.shop.screen.ModScreenHandlers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import static de.rosis.shop.block.ModBlocks.postRegistryTasks;

import org.slf4j.Logger;

@Mod("rosis_shop")
public class RosisShop {
    public static final String MOD_ID = "rosis_shop";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RosisShop(IEventBus modEventBus) {

        ConfigHandler.initialise();

        ModItems.registerModItems(modEventBus);
        ModBlocks.registerModBlocks(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::enqueueCompatibility);

        ModBlockEntities.registerBlockEntities(modEventBus);
        ModScreenHandlers.registerScreenHandlers(modEventBus);

        ModLootConditions.register(modEventBus);

        ModItemGroups.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void enqueueCompatibility(final InterModEnqueueEvent event) {
        InterModComms.sendTo("invtweaks", "blacklist-screen",
                () -> "de.rosis.shop.screen.ShopScreenOwner");
        InterModComms.sendTo("invtweaks", "blacklist-screen",
                () -> "de.rosis.shop.screen.ShopScreenCustomer");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Setting up Rosis Shop");

        postRegistryTasks.forEach(Runnable::run);
        PostRegAssigner.runAllAssigners();
        ShopIconModels.initialise();
        VariantResources.register();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Rosis Shop server starting");
    }

    private final java.util.Map<net.minecraft.world.entity.player.Player, Integer> lastQuickBuy =
            new java.util.WeakHashMap<>();

    @SubscribeEvent(priority = net.neoforged.bus.api.EventPriority.LOWEST)
    public void onQuickBuy(net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock event) {
        var player = event.getEntity();
        if (!player.isShiftKeyDown() || player.isSpectator()
                || !(event.getLevel().getBlockEntity(event.getPos()) instanceof
                de.rosis.shop.block.entity.AbstractShopEntity shop)
                || shop.quickUserSignIn(player) != de.rosis.shop.properties.PermissionLevel.CUSTOMER) {
            return;
        }
        // Cancel mining on both sides; the normal START packet still reaches the server.
        event.setCanceled(true);
        if (event.getLevel().isClientSide()
                && event.getAction() == net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action.START) {
            // Track the held mouse button locally so it cannot send repeated START purchases.
            event.setUseItem(net.neoforged.neoforge.common.util.TriState.DEFAULT);
        }
        if (event.getLevel().isClientSide()
                || event.getAction() != net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.LeftClickBlock.Action.START
                || !player.canInteractWithBlock(event.getPos(), 0)
                || !event.getLevel().mayInteract(player, event.getPos())) {
            return;
        }
        var hit = player.pick(player.blockInteractionRange(), 1, false);
        if (!(hit instanceof net.minecraft.world.phys.BlockHitResult blockHit)
                || hit.getType() != net.minecraft.world.phys.HitResult.Type.BLOCK
                || !blockHit.getBlockPos().equals(event.getPos())) return;
        Integer previous = lastQuickBuy.get(player);
        if (previous != null && player.tickCount - previous < 5) return;
        lastQuickBuy.put(player, player.tickCount);
        boolean top = false;
        var state = shop.getBlockState();
        if (state.hasProperty(de.rosis.shop.block.custom.ShelfShopBlock.SHELVES_ENABLED)) {
            var shelves = state.getValue(de.rosis.shop.block.custom.ShelfShopBlock.SHELVES_ENABLED);
            top = shelves == net.minecraft.world.level.block.state.properties.SlabType.TOP
                    || (shelves == net.minecraft.world.level.block.state.properties.SlabType.DOUBLE
                    && blockHit.getLocation().y - event.getPos().getY() > 0.5);
        }
        var inventory = top ? shop.getOtherInventoryDelegate(player) : shop.getInventoryDelegate(player);
        if (inventory != null) inventory.trade(player.getInventory());
        player.containerMenu.broadcastChanges();
    }

    public static ResourceLocation getResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

}
