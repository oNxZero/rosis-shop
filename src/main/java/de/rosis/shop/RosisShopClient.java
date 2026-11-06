package de.rosis.shop;

import static de.rosis.shop.RosisShop.MOD_ID;

import de.rosis.shop.block.ModBlockEntities;
import de.rosis.shop.block.entity.renderer.*;
import de.rosis.shop.screen.ModScreenHandlers;
import de.rosis.shop.screen.ShopScreenCustomer;
import de.rosis.shop.screen.ShopScreenOwner;
import de.rosis.shop.util.CushionModel;
import de.rosis.shop.util.CushionResources;
import de.rosis.shop.util.CushionTextures;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class RosisShopClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            /*
             * ModBlocks.getAllShops().forEach((DeferredHolder<Block, ? extends
             * AbstractShopBlock> shop) -> {
             * ItemBlockRenderTypes.setRenderLayer(shop.get(), RenderType.cutout());
             * });
             */
        });

        CushionTextures.initialiseCushionTextures();
        CushionResources.initialise();

    }

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_OWNER.get(), ShopScreenOwner::new);
        event.register(ModScreenHandlers.SHOP_SCREEN_HANDLER_CUSTOMER.get(), ShopScreenCustomer::new);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ANGLED_SHOP_ENTITY.get(), AngledShopBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WINDOW_SHOP_ENTITY.get(), WindowSillShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.HOOK_SHOP_ENTITY.get(), HookShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.RUG_SHOP_ENTITY.get(), RugShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CRATE_SHOP_ENTITY.get(), CrateShopEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SHELF_SHOP_ENTITY.get(), ShelfShopEntityRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CushionModel.LAYER_LOCATION, CushionModel::getTexturedModelData);
    }
}
