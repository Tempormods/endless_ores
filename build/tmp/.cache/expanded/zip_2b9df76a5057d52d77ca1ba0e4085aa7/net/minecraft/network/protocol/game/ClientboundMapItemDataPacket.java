package net.minecraft.network.protocol.game;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public record ClientboundMapItemDataPacket(
    MapId mapId, byte scale, boolean locked, Optional<List<MapDecoration>> decorations, Optional<MapItemSavedData.MapPatch> colorPatch
) implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMapItemDataPacket> f_314295_ = StreamCodec.m_319894_(
        MapId.f_315416_,
        ClientboundMapItemDataPacket::mapId,
        ByteBufCodecs.f_313954_,
        ClientboundMapItemDataPacket::scale,
        ByteBufCodecs.f_315514_,
        ClientboundMapItemDataPacket::locked,
        MapDecoration.f_314705_.m_321801_(ByteBufCodecs.m_324765_()).m_321801_(ByteBufCodecs::m_319027_),
        ClientboundMapItemDataPacket::decorations,
        MapItemSavedData.MapPatch.f_315636_,
        ClientboundMapItemDataPacket::colorPatch,
        ClientboundMapItemDataPacket::new
    );

    public ClientboundMapItemDataPacket(
        MapId p_332536_, byte p_327887_, boolean p_335452_, @Nullable Collection<MapDecoration> p_328950_, @Nullable MapItemSavedData.MapPatch p_329006_
    ) {
        this(p_332536_, p_327887_, p_335452_, p_328950_ != null ? Optional.of(List.copyOf(p_328950_)) : Optional.empty(), Optional.ofNullable(p_329006_));
    }

    @Override
    public PacketType<ClientboundMapItemDataPacket> write() {
        return GamePacketTypes.f_314260_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleMapItemData(this);
    }

    public void applyToMap(MapItemSavedData pMapdata) {
        this.decorations.ifPresent(pMapdata::addClientSideDecorations);
        this.colorPatch.ifPresent(p_326099_ -> p_326099_.applyToMap(pMapdata));
    }
}