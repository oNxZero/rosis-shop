package de.rosis.shop.screen.networking;

import io.netty.buffer.ByteBuf;
import de.rosis.shop.RosisShop;
import de.rosis.shop.screen.ShopScreenHandlerOwner;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ShopSelfDemotePkt() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ShopSelfDemotePkt> TYPE = new CustomPacketPayload.Type<>(
            RosisShop.id("shop_self_demote"));

    public static final StreamCodec<ByteBuf, ShopSelfDemotePkt> STREAM_CODEC = StreamCodec
            .unit(new ShopSelfDemotePkt());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.player() instanceof ServerPlayer player
                    && player.containerMenu instanceof ShopScreenHandlerOwner screenHandler) {
                screenHandler.selfDemotePlayer(player);
                player.doCloseContainer();
            }
        });
    }
}
