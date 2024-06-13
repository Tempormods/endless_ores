package net.minecraft.network.protocol.game;

import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ClientboundBlockEntityDataPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBlockEntityDataPacket> f_315210_ = StreamCodec.m_321516_(
        BlockPos.f_316462_,
        ClientboundBlockEntityDataPacket::getPos,
        ByteBufCodecs.m_320159_(Registries.BLOCK_ENTITY_TYPE),
        ClientboundBlockEntityDataPacket::getType,
        ByteBufCodecs.f_315964_,
        ClientboundBlockEntityDataPacket::getTag,
        ClientboundBlockEntityDataPacket::new
    );
    private final BlockPos pos;
    private final BlockEntityType<?> type;
    private final CompoundTag tag;

    public static ClientboundBlockEntityDataPacket create(BlockEntity pBlockEntity, BiFunction<BlockEntity, RegistryAccess, CompoundTag> p_335361_) {
        RegistryAccess registryaccess = pBlockEntity.getLevel().registryAccess();
        return new ClientboundBlockEntityDataPacket(pBlockEntity.getBlockPos(), pBlockEntity.getType(), p_335361_.apply(pBlockEntity, registryaccess));
    }

    public static ClientboundBlockEntityDataPacket create(BlockEntity pBlockEntity) {
        return create(pBlockEntity, BlockEntity::getUpdateTag);
    }

    private ClientboundBlockEntityDataPacket(BlockPos pPos, BlockEntityType<?> pType, CompoundTag pTag) {
        this.pos = pPos;
        this.type = pType;
        this.tag = pTag;
    }

    @Override
    public PacketType<ClientboundBlockEntityDataPacket> write() {
        return GamePacketTypes.f_316158_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleBlockEntityData(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    public CompoundTag getTag() {
        return this.tag;
    }
}