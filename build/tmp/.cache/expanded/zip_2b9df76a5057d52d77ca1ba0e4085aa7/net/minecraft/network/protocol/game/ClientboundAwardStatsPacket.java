package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.stats.Stat;

public record ClientboundAwardStatsPacket(Object2IntMap<Stat<?>> stats) implements Packet<ClientGamePacketListener> {
    private static final StreamCodec<RegistryFriendlyByteBuf, Object2IntMap<Stat<?>>> f_316735_ = ByteBufCodecs.m_322136_(
        Object2IntOpenHashMap::new, Stat.f_315906_, ByteBufCodecs.f_316730_
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAwardStatsPacket> f_314726_ = f_316735_.m_323038_(
        ClientboundAwardStatsPacket::new, ClientboundAwardStatsPacket::stats
    );

    @Override
    public PacketType<ClientboundAwardStatsPacket> write() {
        return GamePacketTypes.f_313912_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleAwardStats(this);
    }
}