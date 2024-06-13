package net.minecraft.network.protocol.common.custom;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record GoalDebugPayload(int entityId, BlockPos pos, List<GoalDebugPayload.DebugGoal> goals) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, GoalDebugPayload> f_314085_ = CustomPacketPayload.m_320054_(
        GoalDebugPayload::m_294343_, GoalDebugPayload::new
    );
    public static final CustomPacketPayload.Type<GoalDebugPayload> f_314777_ = CustomPacketPayload.m_319865_("debug/goal_selector");

    private GoalDebugPayload(FriendlyByteBuf pBuffer) {
        this(pBuffer.readInt(), pBuffer.readBlockPos(), pBuffer.readList(GoalDebugPayload.DebugGoal::new));
    }

    private void m_294343_(FriendlyByteBuf pBuffer) {
        pBuffer.writeInt(this.entityId);
        pBuffer.writeBlockPos(this.pos);
        pBuffer.writeCollection(this.goals, (p_298191_, p_298011_) -> p_298011_.write(p_298191_));
    }

    @Override
    public CustomPacketPayload.Type<GoalDebugPayload> m_293297_() {
        return f_314777_;
    }

    public static record DebugGoal(int priority, boolean isRunning, String name) {
        public DebugGoal(FriendlyByteBuf pBuffer) {
            this(pBuffer.readInt(), pBuffer.readBoolean(), pBuffer.readUtf(255));
        }

        public void write(FriendlyByteBuf pBuffer) {
            pBuffer.writeInt(this.priority);
            pBuffer.writeBoolean(this.isRunning);
            pBuffer.writeUtf(this.name);
        }
    }
}