package net.minecraft.network.protocol.common;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.BreezeDebugPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.common.custom.GameEventDebugPayload;
import net.minecraft.network.protocol.common.custom.GameEventListenerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestClearMarkersDebugPayload;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import net.minecraft.network.protocol.common.custom.HiveDebugPayload;
import net.minecraft.network.protocol.common.custom.NeighborUpdatesDebugPayload;
import net.minecraft.network.protocol.common.custom.PathfindingDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiAddedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiRemovedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiTicketCountDebugPayload;
import net.minecraft.network.protocol.common.custom.RaidsDebugPayload;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.network.protocol.common.custom.VillageSectionsDebugPayload;
import net.minecraft.network.protocol.common.custom.WorldGenAttemptDebugPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundCustomPayloadPacket(CustomPacketPayload payload) implements Packet<ClientCommonPacketListener> {
    private static final int MAX_PAYLOAD_SIZE = 1048576;
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCustomPayloadPacket> f_314159_ = CustomPacketPayload.<RegistryFriendlyByteBuf>m_323250_(
            p_330149_ -> net.minecraftforge.common.ForgeHooks.getCustomPayloadCodec(p_330149_, 1048576),
            Util.make(
                Lists.newArrayList(
                    new CustomPacketPayload.TypeAndCodec<>(BrandPayload.f_315254_, BrandPayload.f_315010_),
                    new CustomPacketPayload.TypeAndCodec<>(BeeDebugPayload.f_314812_, BeeDebugPayload.f_315531_),
                    new CustomPacketPayload.TypeAndCodec<>(BrainDebugPayload.f_316333_, BrainDebugPayload.f_316808_),
                    new CustomPacketPayload.TypeAndCodec<>(BreezeDebugPayload.f_316538_, BreezeDebugPayload.f_316917_),
                    new CustomPacketPayload.TypeAndCodec<>(GameEventDebugPayload.f_315465_, GameEventDebugPayload.f_316925_),
                    new CustomPacketPayload.TypeAndCodec<>(GameEventListenerDebugPayload.f_315542_, GameEventListenerDebugPayload.f_316780_),
                    new CustomPacketPayload.TypeAndCodec<>(GameTestAddMarkerDebugPayload.f_314261_, GameTestAddMarkerDebugPayload.f_316392_),
                    new CustomPacketPayload.TypeAndCodec<>(GameTestClearMarkersDebugPayload.f_316988_, GameTestClearMarkersDebugPayload.f_314722_),
                    new CustomPacketPayload.TypeAndCodec<>(GoalDebugPayload.f_314777_, GoalDebugPayload.f_314085_),
                    new CustomPacketPayload.TypeAndCodec<>(HiveDebugPayload.f_315265_, HiveDebugPayload.f_315646_),
                    new CustomPacketPayload.TypeAndCodec<>(NeighborUpdatesDebugPayload.f_316722_, NeighborUpdatesDebugPayload.f_315097_),
                    new CustomPacketPayload.TypeAndCodec<>(PathfindingDebugPayload.f_313916_, PathfindingDebugPayload.f_316062_),
                    new CustomPacketPayload.TypeAndCodec<>(PoiAddedDebugPayload.f_315969_, PoiAddedDebugPayload.f_315048_),
                    new CustomPacketPayload.TypeAndCodec<>(PoiRemovedDebugPayload.f_315180_, PoiRemovedDebugPayload.f_315876_),
                    new CustomPacketPayload.TypeAndCodec<>(PoiTicketCountDebugPayload.f_314448_, PoiTicketCountDebugPayload.f_314785_),
                    new CustomPacketPayload.TypeAndCodec<>(RaidsDebugPayload.f_314056_, RaidsDebugPayload.f_314237_),
                    new CustomPacketPayload.TypeAndCodec<>(StructuresDebugPayload.f_314744_, StructuresDebugPayload.f_314526_),
                    new CustomPacketPayload.TypeAndCodec<>(VillageSectionsDebugPayload.f_315276_, VillageSectionsDebugPayload.f_314252_),
                    new CustomPacketPayload.TypeAndCodec<>(WorldGenAttemptDebugPayload.f_317154_, WorldGenAttemptDebugPayload.f_315328_)
                ),
                p_333496_ -> {
                }
            )
        )
        .m_323038_(ClientboundCustomPayloadPacket::new, ClientboundCustomPayloadPacket::payload);
    public static final StreamCodec<FriendlyByteBuf, ClientboundCustomPayloadPacket> f_316069_ = CustomPacketPayload.<FriendlyByteBuf>m_323250_(
            p_331803_ -> net.minecraftforge.common.ForgeHooks.getCustomPayloadCodec(p_331803_, 1048576),
            List.of(new CustomPacketPayload.TypeAndCodec<>(BrandPayload.f_315254_, BrandPayload.f_315010_))
        )
        .m_323038_(ClientboundCustomPayloadPacket::new, ClientboundCustomPayloadPacket::payload);

    @Override
    public PacketType<ClientboundCustomPayloadPacket> write() {
        return CommonPacketTypes.f_314728_;
    }

    public void handle(ClientCommonPacketListener pHandler) {
        pHandler.handleCustomPayload(this);
    }
}
