package de.rosis.shop.screen.networking;

import io.netty.buffer.ByteBuf;
import de.rosis.shop.RosisShop;
import de.rosis.shop.screen.ShopScreenOwner;
import de.rosis.shop.screen.ToggleButtonID;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ToggleSyncResponsePkt(ToggleButtonID buttonID, boolean state) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ToggleSyncResponsePkt> TYPE = new CustomPacketPayload.Type<>(
            RosisShop.id("toggle_sync_response"));

    public static final StreamCodec<ByteBuf, ToggleSyncResponsePkt> STREAM_CODEC = StreamCodec.composite(
            ToggleButtonID.STREAM_CODEC,
            ToggleSyncResponsePkt::buttonID,
            ByteBufCodecs.BOOL, ToggleSyncResponsePkt::state,
            ToggleSyncResponsePkt::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof ShopScreenOwner screen) {
                screen.getMenu().updateToggleButtonFromPacket(buttonID, state);
            }
        });
    }
}
