package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public abstract class ClientboundMoveEntityPacket implements Packet<ClientGamePacketListener> {
    protected final int entityId;
    protected final short xa;
    protected final short ya;
    protected final short za;
    protected final byte yRot;
    protected final byte xRot;
    protected final boolean onGround;
    protected final boolean hasRot;
    protected final boolean hasPos;

    protected ClientboundMoveEntityPacket(
        int pEntityId,
        short pXa,
        short pYa,
        short pZa,
        byte pYRot,
        byte pXRot,
        boolean pOnGround,
        boolean pHasRot,
        boolean pHasPos
    ) {
        this.entityId = pEntityId;
        this.xa = pXa;
        this.ya = pYa;
        this.za = pZa;
        this.yRot = pYRot;
        this.xRot = pXRot;
        this.onGround = pOnGround;
        this.hasRot = pHasRot;
        this.hasPos = pHasPos;
    }

    @Override
    public abstract PacketType<? extends ClientboundMoveEntityPacket> write();

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleMoveEntity(this);
    }

    @Override
    public String toString() {
        return "Entity_" + super.toString();
    }

    @Nullable
    public Entity getEntity(Level pLevel) {
        return pLevel.getEntity(this.entityId);
    }

    public short getXa() {
        return this.xa;
    }

    public short getYa() {
        return this.ya;
    }

    public short getZa() {
        return this.za;
    }

    public byte getyRot() {
        return this.yRot;
    }

    public byte getxRot() {
        return this.xRot;
    }

    public boolean hasRotation() {
        return this.hasRot;
    }

    public boolean hasPosition() {
        return this.hasPos;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public static class Pos extends ClientboundMoveEntityPacket {
        public static final StreamCodec<FriendlyByteBuf, ClientboundMoveEntityPacket.Pos> f_314194_ = Packet.m_319422_(
            ClientboundMoveEntityPacket.Pos::m_132548_, ClientboundMoveEntityPacket.Pos::read
        );

        public Pos(int pEntityId, short pXa, short pYa, short pZa, boolean pOnGround) {
            super(pEntityId, pXa, pYa, pZa, (byte)0, (byte)0, pOnGround, false, true);
        }

        private static ClientboundMoveEntityPacket.Pos read(FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            short short1 = pBuffer.readShort();
            short short2 = pBuffer.readShort();
            short short3 = pBuffer.readShort();
            boolean flag = pBuffer.readBoolean();
            return new ClientboundMoveEntityPacket.Pos(i, short1, short2, short3, flag);
        }

        private void m_132548_(FriendlyByteBuf pBuffer) {
            pBuffer.writeVarInt(this.entityId);
            pBuffer.writeShort(this.xa);
            pBuffer.writeShort(this.ya);
            pBuffer.writeShort(this.za);
            pBuffer.writeBoolean(this.onGround);
        }

        @Override
        public PacketType<ClientboundMoveEntityPacket.Pos> write() {
            return GamePacketTypes.f_314503_;
        }
    }

    public static class PosRot extends ClientboundMoveEntityPacket {
        public static final StreamCodec<FriendlyByteBuf, ClientboundMoveEntityPacket.PosRot> f_317049_ = Packet.m_319422_(
            ClientboundMoveEntityPacket.PosRot::m_132563_, ClientboundMoveEntityPacket.PosRot::read
        );

        public PosRot(int pEntityId, short pXa, short pYa, short pZa, byte pYRot, byte pXRot, boolean pOnGround) {
            super(pEntityId, pXa, pYa, pZa, pYRot, pXRot, pOnGround, true, true);
        }

        private static ClientboundMoveEntityPacket.PosRot read(FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            short short1 = pBuffer.readShort();
            short short2 = pBuffer.readShort();
            short short3 = pBuffer.readShort();
            byte b0 = pBuffer.readByte();
            byte b1 = pBuffer.readByte();
            boolean flag = pBuffer.readBoolean();
            return new ClientboundMoveEntityPacket.PosRot(i, short1, short2, short3, b0, b1, flag);
        }

        private void m_132563_(FriendlyByteBuf pBuffer) {
            pBuffer.writeVarInt(this.entityId);
            pBuffer.writeShort(this.xa);
            pBuffer.writeShort(this.ya);
            pBuffer.writeShort(this.za);
            pBuffer.writeByte(this.yRot);
            pBuffer.writeByte(this.xRot);
            pBuffer.writeBoolean(this.onGround);
        }

        @Override
        public PacketType<ClientboundMoveEntityPacket.PosRot> write() {
            return GamePacketTypes.f_315449_;
        }
    }

    public static class Rot extends ClientboundMoveEntityPacket {
        public static final StreamCodec<FriendlyByteBuf, ClientboundMoveEntityPacket.Rot> f_315641_ = Packet.m_319422_(
            ClientboundMoveEntityPacket.Rot::m_132575_, ClientboundMoveEntityPacket.Rot::read
        );

        public Rot(int pEntityId, byte pYRot, byte pXRot, boolean pOnGround) {
            super(pEntityId, (short)0, (short)0, (short)0, pYRot, pXRot, pOnGround, true, false);
        }

        private static ClientboundMoveEntityPacket.Rot read(FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            byte b0 = pBuffer.readByte();
            byte b1 = pBuffer.readByte();
            boolean flag = pBuffer.readBoolean();
            return new ClientboundMoveEntityPacket.Rot(i, b0, b1, flag);
        }

        private void m_132575_(FriendlyByteBuf pBuffer) {
            pBuffer.writeVarInt(this.entityId);
            pBuffer.writeByte(this.yRot);
            pBuffer.writeByte(this.xRot);
            pBuffer.writeBoolean(this.onGround);
        }

        @Override
        public PacketType<ClientboundMoveEntityPacket.Rot> write() {
            return GamePacketTypes.f_314818_;
        }
    }
}