package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public abstract class ServerboundMovePlayerPacket implements Packet<ServerGamePacketListener> {
    protected final double x;
    protected final double y;
    protected final double z;
    protected final float yRot;
    protected final float xRot;
    protected final boolean onGround;
    protected final boolean hasPos;
    protected final boolean hasRot;

    protected ServerboundMovePlayerPacket(
        double pX, double pY, double pZ, float pYRot, float pXRot, boolean pOnGround, boolean pHasPos, boolean pHasRot
    ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
        this.yRot = pYRot;
        this.xRot = pXRot;
        this.onGround = pOnGround;
        this.hasPos = pHasPos;
        this.hasRot = pHasRot;
    }

    @Override
    public abstract PacketType<? extends ServerboundMovePlayerPacket> write();

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleMovePlayer(this);
    }

    public double getX(double pDefaultValue) {
        return this.hasPos ? this.x : pDefaultValue;
    }

    public double getY(double pDefaultValue) {
        return this.hasPos ? this.y : pDefaultValue;
    }

    public double getZ(double pDefaultValue) {
        return this.hasPos ? this.z : pDefaultValue;
    }

    public float getYRot(float pDefaultValue) {
        return this.hasRot ? this.yRot : pDefaultValue;
    }

    public float getXRot(float pDefaultValue) {
        return this.hasRot ? this.xRot : pDefaultValue;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public boolean hasPosition() {
        return this.hasPos;
    }

    public boolean hasRotation() {
        return this.hasRot;
    }

    public static class Pos extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.Pos> f_314397_ = Packet.m_319422_(
            ServerboundMovePlayerPacket.Pos::m_134158_, ServerboundMovePlayerPacket.Pos::read
        );

        public Pos(double pX, double pY, double pZ, boolean pOnGround) {
            super(pX, pY, pZ, 0.0F, 0.0F, pOnGround, true, false);
        }

        private static ServerboundMovePlayerPacket.Pos read(FriendlyByteBuf pBuffer) {
            double d0 = pBuffer.readDouble();
            double d1 = pBuffer.readDouble();
            double d2 = pBuffer.readDouble();
            boolean flag = pBuffer.readUnsignedByte() != 0;
            return new ServerboundMovePlayerPacket.Pos(d0, d1, d2, flag);
        }

        private void m_134158_(FriendlyByteBuf pBuffer) {
            pBuffer.writeDouble(this.x);
            pBuffer.writeDouble(this.y);
            pBuffer.writeDouble(this.z);
            pBuffer.writeByte(this.onGround ? 1 : 0);
        }

        @Override
        public PacketType<ServerboundMovePlayerPacket.Pos> write() {
            return GamePacketTypes.f_317056_;
        }
    }

    public static class PosRot extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.PosRot> f_316356_ = Packet.m_319422_(
            ServerboundMovePlayerPacket.PosRot::m_134172_, ServerboundMovePlayerPacket.PosRot::read
        );

        public PosRot(double pX, double pY, double pZ, float pYRot, float pXRot, boolean pOnGround) {
            super(pX, pY, pZ, pYRot, pXRot, pOnGround, true, true);
        }

        private static ServerboundMovePlayerPacket.PosRot read(FriendlyByteBuf pBuffer) {
            double d0 = pBuffer.readDouble();
            double d1 = pBuffer.readDouble();
            double d2 = pBuffer.readDouble();
            float f = pBuffer.readFloat();
            float f1 = pBuffer.readFloat();
            boolean flag = pBuffer.readUnsignedByte() != 0;
            return new ServerboundMovePlayerPacket.PosRot(d0, d1, d2, f, f1, flag);
        }

        private void m_134172_(FriendlyByteBuf pBuffer) {
            pBuffer.writeDouble(this.x);
            pBuffer.writeDouble(this.y);
            pBuffer.writeDouble(this.z);
            pBuffer.writeFloat(this.yRot);
            pBuffer.writeFloat(this.xRot);
            pBuffer.writeByte(this.onGround ? 1 : 0);
        }

        @Override
        public PacketType<ServerboundMovePlayerPacket.PosRot> write() {
            return GamePacketTypes.f_314798_;
        }
    }

    public static class Rot extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.Rot> f_315704_ = Packet.m_319422_(
            ServerboundMovePlayerPacket.Rot::m_134183_, ServerboundMovePlayerPacket.Rot::read
        );

        public Rot(float pYRot, float pXRot, boolean pOnGround) {
            super(0.0, 0.0, 0.0, pYRot, pXRot, pOnGround, false, true);
        }

        private static ServerboundMovePlayerPacket.Rot read(FriendlyByteBuf pBuffer) {
            float f = pBuffer.readFloat();
            float f1 = pBuffer.readFloat();
            boolean flag = pBuffer.readUnsignedByte() != 0;
            return new ServerboundMovePlayerPacket.Rot(f, f1, flag);
        }

        private void m_134183_(FriendlyByteBuf pBuffer) {
            pBuffer.writeFloat(this.yRot);
            pBuffer.writeFloat(this.xRot);
            pBuffer.writeByte(this.onGround ? 1 : 0);
        }

        @Override
        public PacketType<ServerboundMovePlayerPacket.Rot> write() {
            return GamePacketTypes.f_316888_;
        }
    }

    public static class StatusOnly extends ServerboundMovePlayerPacket {
        public static final StreamCodec<FriendlyByteBuf, ServerboundMovePlayerPacket.StatusOnly> f_316091_ = Packet.m_319422_(
            ServerboundMovePlayerPacket.StatusOnly::m_179693_, ServerboundMovePlayerPacket.StatusOnly::read
        );

        public StatusOnly(boolean pOnGround) {
            super(0.0, 0.0, 0.0, 0.0F, 0.0F, pOnGround, false, false);
        }

        private static ServerboundMovePlayerPacket.StatusOnly read(FriendlyByteBuf pBuffer) {
            boolean flag = pBuffer.readUnsignedByte() != 0;
            return new ServerboundMovePlayerPacket.StatusOnly(flag);
        }

        private void m_179693_(FriendlyByteBuf pBuffer) {
            pBuffer.writeByte(this.onGround ? 1 : 0);
        }

        @Override
        public PacketType<ServerboundMovePlayerPacket.StatusOnly> write() {
            return GamePacketTypes.f_314981_;
        }
    }
}