package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundSetTitlesAnimationPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetTitlesAnimationPacket> f_317008_ = Packet.m_319422_(
        ClientboundSetTitlesAnimationPacket::m_179409_, ClientboundSetTitlesAnimationPacket::new
    );
    private final int fadeIn;
    private final int stay;
    private final int fadeOut;

    public ClientboundSetTitlesAnimationPacket(int pFadeIn, int pStay, int pFadeOut) {
        this.fadeIn = pFadeIn;
        this.stay = pStay;
        this.fadeOut = pFadeOut;
    }

    private ClientboundSetTitlesAnimationPacket(FriendlyByteBuf pBuffer) {
        this.fadeIn = pBuffer.readInt();
        this.stay = pBuffer.readInt();
        this.fadeOut = pBuffer.readInt();
    }

    private void m_179409_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.fadeIn);
        pBuffer.writeInt(this.stay);
        pBuffer.writeInt(this.fadeOut);
    }

    @Override
    public PacketType<ClientboundSetTitlesAnimationPacket> write() {
        return GamePacketTypes.f_314485_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.setTitlesAnimation(this);
    }

    public int getFadeIn() {
        return this.fadeIn;
    }

    public int getStay() {
        return this.stay;
    }

    public int getFadeOut() {
        return this.fadeOut;
    }
}