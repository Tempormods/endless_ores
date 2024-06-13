package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.effect.MobEffect;

public record ServerboundSetBeaconPacket(Optional<Holder<MobEffect>> primary, Optional<Holder<MobEffect>> secondary)
    implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetBeaconPacket> f_314389_ = StreamCodec.m_320349_(
        ByteBufCodecs.m_322636_(Registries.MOB_EFFECT).m_321801_(ByteBufCodecs::m_319027_),
        ServerboundSetBeaconPacket::primary,
        ByteBufCodecs.m_322636_(Registries.MOB_EFFECT).m_321801_(ByteBufCodecs::m_319027_),
        ServerboundSetBeaconPacket::secondary,
        ServerboundSetBeaconPacket::new
    );

    @Override
    public PacketType<ServerboundSetBeaconPacket> write() {
        return GamePacketTypes.f_317052_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleSetBeaconPacket(this);
    }
}