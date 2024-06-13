package net.minecraft.network.protocol.game;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;

public class ServerboundSetJigsawBlockPacket implements Packet<ServerGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ServerboundSetJigsawBlockPacket> f_316452_ = Packet.m_319422_(
        ServerboundSetJigsawBlockPacket::m_134586_, ServerboundSetJigsawBlockPacket::new
    );
    private final BlockPos pos;
    private final ResourceLocation name;
    private final ResourceLocation target;
    private final ResourceLocation pool;
    private final String finalState;
    private final JigsawBlockEntity.JointType joint;
    private final int f_303867_;
    private final int f_303292_;

    public ServerboundSetJigsawBlockPacket(
        BlockPos pPos,
        ResourceLocation pName,
        ResourceLocation pTarget,
        ResourceLocation pPool,
        String pFinalState,
        JigsawBlockEntity.JointType pJoint,
        int p_309767_,
        int p_310524_
    ) {
        this.pos = pPos;
        this.name = pName;
        this.target = pTarget;
        this.pool = pPool;
        this.finalState = pFinalState;
        this.joint = pJoint;
        this.f_303867_ = p_309767_;
        this.f_303292_ = p_310524_;
    }

    private ServerboundSetJigsawBlockPacket(FriendlyByteBuf pBuffer) {
        this.pos = pBuffer.readBlockPos();
        this.name = pBuffer.readResourceLocation();
        this.target = pBuffer.readResourceLocation();
        this.pool = pBuffer.readResourceLocation();
        this.finalState = pBuffer.readUtf();
        this.joint = JigsawBlockEntity.JointType.byName(pBuffer.readUtf()).orElse(JigsawBlockEntity.JointType.ALIGNED);
        this.f_303867_ = pBuffer.readVarInt();
        this.f_303292_ = pBuffer.readVarInt();
    }

    private void m_134586_(FriendlyByteBuf pBuffer) {
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeResourceLocation(this.name);
        pBuffer.writeResourceLocation(this.target);
        pBuffer.writeResourceLocation(this.pool);
        pBuffer.writeUtf(this.finalState);
        pBuffer.writeUtf(this.joint.getSerializedName());
        pBuffer.writeVarInt(this.f_303867_);
        pBuffer.writeVarInt(this.f_303292_);
    }

    @Override
    public PacketType<ServerboundSetJigsawBlockPacket> write() {
        return GamePacketTypes.f_315385_;
    }

    public void handle(ServerGamePacketListener pHandler) {
        pHandler.handleSetJigsawBlock(this);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public ResourceLocation getName() {
        return this.name;
    }

    public ResourceLocation getTarget() {
        return this.target;
    }

    public ResourceLocation getPool() {
        return this.pool;
    }

    public String getFinalState() {
        return this.finalState;
    }

    public JigsawBlockEntity.JointType getJoint() {
        return this.joint;
    }

    public int m_305292_() {
        return this.f_303867_;
    }

    public int m_307441_() {
        return this.f_303292_;
    }
}