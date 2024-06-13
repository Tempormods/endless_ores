package net.minecraft.network.protocol.game;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;

public class ClientboundSetDisplayObjectivePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<FriendlyByteBuf, ClientboundSetDisplayObjectivePacket> f_315391_ = Packet.m_319422_(
        ClientboundSetDisplayObjectivePacket::m_133140_, ClientboundSetDisplayObjectivePacket::new
    );
    private final DisplaySlot slot;
    private final String objectiveName;

    public ClientboundSetDisplayObjectivePacket(DisplaySlot p_301315_, @Nullable Objective p_133132_) {
        this.slot = p_301315_;
        if (p_133132_ == null) {
            this.objectiveName = "";
        } else {
            this.objectiveName = p_133132_.getName();
        }
    }

    private ClientboundSetDisplayObjectivePacket(FriendlyByteBuf pBuffer) {
        this.slot = pBuffer.readById(DisplaySlot.BY_ID);
        this.objectiveName = pBuffer.readUtf();
    }

    private void m_133140_(FriendlyByteBuf pBuffer) {
        pBuffer.writeById(DisplaySlot::id, this.slot);
        pBuffer.writeUtf(this.objectiveName);
    }

    @Override
    public PacketType<ClientboundSetDisplayObjectivePacket> write() {
        return GamePacketTypes.f_315256_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleSetDisplayObjective(this);
    }

    public DisplaySlot getSlot() {
        return this.slot;
    }

    @Nullable
    public String getObjectiveName() {
        return Objects.equals(this.objectiveName, "") ? null : this.objectiveName;
    }
}