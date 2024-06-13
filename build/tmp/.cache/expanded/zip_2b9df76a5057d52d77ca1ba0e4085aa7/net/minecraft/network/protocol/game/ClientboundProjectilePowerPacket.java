package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundProjectilePowerPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundProjectilePowerPacket> f_314474_ = Packet.m_319422_(
        ClientboundProjectilePowerPacket::m_319784_, ClientboundProjectilePowerPacket::new
    );
    private final int f_315634_;
    private final double f_315173_;
    private final double f_314091_;
    private final double f_316624_;

    public ClientboundProjectilePowerPacket(int p_336104_, double p_330780_, double p_328679_, double p_327674_) {
        this.f_315634_ = p_336104_;
        this.f_315173_ = p_330780_;
        this.f_314091_ = p_328679_;
        this.f_316624_ = p_327674_;
    }

    private ClientboundProjectilePowerPacket(FriendlyByteBuf p_328922_) {
        this.f_315634_ = p_328922_.readVarInt();
        this.f_315173_ = p_328922_.readDouble();
        this.f_314091_ = p_328922_.readDouble();
        this.f_316624_ = p_328922_.readDouble();
    }

    private void m_319784_(FriendlyByteBuf p_331545_) {
        p_331545_.writeVarInt(this.f_315634_);
        p_331545_.writeDouble(this.f_315173_);
        p_331545_.writeDouble(this.f_314091_);
        p_331545_.writeDouble(this.f_316624_);
    }

    @Override
    public PacketType<ClientboundProjectilePowerPacket> write() {
        return GamePacketTypes.f_316345_;
    }

    public void handle(ClientGamePacketListener p_329858_) {
        p_329858_.m_319269_(this);
    }

    public int m_321243_() {
        return this.f_315634_;
    }

    public double m_319868_() {
        return this.f_315173_;
    }

    public double m_318701_() {
        return this.f_314091_;
    }

    public double m_321955_() {
        return this.f_316624_;
    }
}