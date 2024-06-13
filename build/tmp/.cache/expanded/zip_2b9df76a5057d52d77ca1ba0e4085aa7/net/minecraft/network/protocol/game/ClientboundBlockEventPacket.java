package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.Block;

/**
 * Triggers a block event on the client.
 * 
 * @see Block#triggerEvent
 * @see Level#blockEvent
 */
public class ClientboundBlockEventPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEventPacket> f_316965_ = Packet.m_319422_(
        ClientboundBlockEventPacket::m_131726_, ClientboundBlockEventPacket::new
    );
    private final BlockPos pos;
    private final int b0;
    private final int b1;
    private final Block block;

    public ClientboundBlockEventPacket(BlockPos pPos, Block pBlock, int pB0, int pB1) {
        this.pos = pPos;
        this.block = pBlock;
        this.b0 = pB0;
        this.b1 = pB1;
    }

    private ClientboundBlockEventPacket(RegistryFriendlyByteBuf p_332473_) {
        this.pos = p_332473_.readBlockPos();
        this.b0 = p_332473_.readUnsignedByte();
        this.b1 = p_332473_.readUnsignedByte();
        this.block = ByteBufCodecs.m_320159_(Registries.BLOCK).m_318688_(p_332473_);
    }

    private void m_131726_(RegistryFriendlyByteBuf p_331626_) {
        p_331626_.writeBlockPos(this.pos);
        p_331626_.writeByte(this.b0);
        p_331626_.writeByte(this.b1);
        ByteBufCodecs.m_320159_(Registries.BLOCK).m_318638_(p_331626_, this.block);
    }

    @Override
    public PacketType<ClientboundBlockEventPacket> write() {
        return GamePacketTypes.f_314776_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleBlockEvent(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getB0() {
        return this.b0;
    }

    public int getB1() {
        return this.b1;
    }

    public Block getBlock() {
        return this.block;
    }
}