package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ClientboundBlockUpdatePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockUpdatePacket> f_314688_ = StreamCodec.m_320349_(
        BlockPos.f_316462_,
        ClientboundBlockUpdatePacket::getPos,
        ByteBufCodecs.m_323411_(Block.BLOCK_STATE_REGISTRY),
        ClientboundBlockUpdatePacket::getBlockState,
        ClientboundBlockUpdatePacket::new
    );
    private final BlockPos pos;
    private final BlockState blockState;

    public ClientboundBlockUpdatePacket(BlockPos pPos, BlockState pBlockState) {
        this.pos = pPos;
        this.blockState = pBlockState;
    }

    public ClientboundBlockUpdatePacket(BlockGetter pBlockGetter, BlockPos pPos) {
        this(pPos, pBlockGetter.getBlockState(pPos));
    }

    @Override
    public PacketType<ClientboundBlockUpdatePacket> write() {
        return GamePacketTypes.f_314104_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleBlockUpdate(this);
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}