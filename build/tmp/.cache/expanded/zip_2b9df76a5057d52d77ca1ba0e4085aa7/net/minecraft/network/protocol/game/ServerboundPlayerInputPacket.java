package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ServerboundPlayerInputPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundPlayerInputPacket> f_314428_ = Packet.m_319422_(
        ServerboundPlayerInputPacket::m_134356_, ServerboundPlayerInputPacket::new
    );
    private static final int FLAG_JUMPING = 1;
    private static final int FLAG_SHIFT_KEY_DOWN = 2;
    private final float xxa;
    private final float zza;
    private final boolean isJumping;
    private final boolean isShiftKeyDown;

    public ServerboundPlayerInputPacket(float pXxa, float pZza, boolean pIsJumping, boolean pIsShiftKeyDown) {
        this.xxa = pXxa;
        this.zza = pZza;
        this.isJumping = pIsJumping;
        this.isShiftKeyDown = pIsShiftKeyDown;
    }

    private ServerboundPlayerInputPacket(FriendlyByteBuf pBuffer) {
        this.xxa = pBuffer.readFloat();
        this.zza = pBuffer.readFloat();
        byte b0 = pBuffer.readByte();
        this.isJumping = (b0 & 1) > 0;
        this.isShiftKeyDown = (b0 & 2) > 0;
    }

    private void m_134356_(FriendlyByteBuf pBuffer) {
        pBuffer.writeFloat(this.xxa);
        pBuffer.writeFloat(this.zza);
        byte b0 = 0;
        if (this.isJumping) {
            b0 = (byte)(b0 | 1);
        }

        if (this.isShiftKeyDown) {
            b0 = (byte)(b0 | 2);
        }

        pBuffer.writeByte(b0);
    }

    @Override
    public PacketType<ServerboundPlayerInputPacket> write() {
        return GamePacketTypes.f_315317_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handlePlayerInput(this);
    }

    public float getXxa() {
        return this.xxa;
    }

    public float getZza() {
        return this.zza;
    }

    public boolean isJumping() {
        return this.isJumping;
    }

    public boolean isShiftKeyDown() {
        return this.isShiftKeyDown;
    }
}