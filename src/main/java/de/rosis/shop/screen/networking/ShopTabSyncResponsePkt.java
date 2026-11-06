package de.rosis.shop.screen.networking;

import io.netty.buffer.ByteBuf;
import de.rosis.shop.RosisShop;
import de.rosis.shop.screen.ShopScreenOwner;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShopTabSyncResponsePkt(int tab) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShopTabSyncResponsePkt> TYPE = new CustomPacketPayload.Type<>(
            RosisShop.id("shop_tab_sync_response"));

    public static final StreamCodec<ByteBuf, ShopTabSyncResponsePkt> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ShopTabSyncResponsePkt::tab,
            ShopTabSyncResponsePkt::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ShopScreenOwner screen) {
                screen.updateTabSelectionResponse(tab);
            }
        });
    }
}
