package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ClientboundSetObjectivePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSetObjectivePacket> f_315966_ = Packet.m_319422_(
        ClientboundSetObjectivePacket::m_133267_, ClientboundSetObjectivePacket::new
    );
    public static final int METHOD_ADD = 0;
    public static final int METHOD_REMOVE = 1;
    public static final int METHOD_CHANGE = 2;
    private final String objectiveName;
    private final Component displayName;
    private final ObjectiveCriteria.RenderType renderType;
    private final Optional<NumberFormat> f_302330_;
    private final int method;

    public ClientboundSetObjectivePacket(Objective pObjective, int pMethod) {
        this.objectiveName = pObjective.getName();
        this.displayName = pObjective.getDisplayName();
        this.renderType = pObjective.getRenderType();
        this.f_302330_ = Optional.ofNullable(pObjective.m_306659_());
        this.method = pMethod;
    }

    private ClientboundSetObjectivePacket(RegistryFriendlyByteBuf p_330039_) {
        this.objectiveName = p_330039_.readUtf();
        this.method = p_330039_.readByte();
        if (this.method != 0 && this.method != 2) {
            this.displayName = CommonComponents.EMPTY;
            this.renderType = ObjectiveCriteria.RenderType.INTEGER;
            this.f_302330_ = Optional.empty();
        } else {
            this.displayName = ComponentSerialization.f_316335_.m_318688_(p_330039_);
            this.renderType = p_330039_.readEnum(ObjectiveCriteria.RenderType.class);
            this.f_302330_ = NumberFormatTypes.f_316603_.m_318688_(p_330039_);
        }
    }

    private void m_133267_(RegistryFriendlyByteBuf p_332439_) {
        p_332439_.writeUtf(this.objectiveName);
        p_332439_.writeByte(this.method);
        if (this.method == 0 || this.method == 2) {
            ComponentSerialization.f_316335_.m_318638_(p_332439_, this.displayName);
            p_332439_.writeEnum(this.renderType);
            NumberFormatTypes.f_316603_.m_318638_(p_332439_, this.f_302330_);
        }
    }

    @Override
    public PacketType<ClientboundSetObjectivePacket> write() {
        return GamePacketTypes.f_317023_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleAddObjective(this);
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public int getMethod() {
        return this.method;
    }

    public ObjectiveCriteria.RenderType getRenderType() {
        return this.renderType;
    }

    public Optional<NumberFormat> m_307940_() {
        return this.f_302330_;
    }
}