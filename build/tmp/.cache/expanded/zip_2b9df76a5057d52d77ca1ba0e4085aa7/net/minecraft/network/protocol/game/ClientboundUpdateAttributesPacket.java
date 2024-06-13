package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.core.Holder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class ClientboundUpdateAttributesPacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAttributesPacket> f_316910_ = StreamCodec.m_320349_(
        ByteBufCodecs.f_316730_,
        ClientboundUpdateAttributesPacket::getEntityId,
        ClientboundUpdateAttributesPacket.AttributeSnapshot.f_314821_.m_321801_(ByteBufCodecs.m_324765_()),
        ClientboundUpdateAttributesPacket::getValues,
        ClientboundUpdateAttributesPacket::new
    );
    private final int entityId;
    private final List<ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes;

    public ClientboundUpdateAttributesPacket(int pEntityId, Collection<AttributeInstance> pAttributes) {
        this.entityId = pEntityId;
        this.attributes = Lists.newArrayList();

        for (AttributeInstance attributeinstance : pAttributes) {
            this.attributes
                .add(
                    new ClientboundUpdateAttributesPacket.AttributeSnapshot(
                        attributeinstance.getAttribute(), attributeinstance.getBaseValue(), attributeinstance.getModifiers()
                    )
                );
        }
    }

    private ClientboundUpdateAttributesPacket(int p_332663_, List<ClientboundUpdateAttributesPacket.AttributeSnapshot> p_327701_) {
        this.entityId = p_332663_;
        this.attributes = p_327701_;
    }

    @Override
    public PacketType<ClientboundUpdateAttributesPacket> write() {
        return GamePacketTypes.f_315098_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleUpdateAttributes(this);
    }

    public int getEntityId() {
        return this.entityId;
    }

    public List<ClientboundUpdateAttributesPacket.AttributeSnapshot> getValues() {
        return this.attributes;
    }

    public static record AttributeSnapshot(Holder<Attribute> attribute, double base, Collection<AttributeModifier> modifiers) {
        public static final StreamCodec<ByteBuf, AttributeModifier> f_317148_ = StreamCodec.m_321516_(
            UUIDUtil.f_315346_,
            AttributeModifier::id,
            ByteBufCodecs.f_315477_,
            AttributeModifier::amount,
            AttributeModifier.Operation.f_314272_,
            AttributeModifier::operation,
            (p_333648_, p_329091_, p_331543_) -> new AttributeModifier(p_333648_, "Unknown synced attribute modifier", p_329091_, p_331543_)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAttributesPacket.AttributeSnapshot> f_314821_ = StreamCodec.m_321516_(
            ByteBufCodecs.m_322636_(Registries.ATTRIBUTE),
            ClientboundUpdateAttributesPacket.AttributeSnapshot::attribute,
            ByteBufCodecs.f_315477_,
            ClientboundUpdateAttributesPacket.AttributeSnapshot::base,
            f_317148_.m_321801_(ByteBufCodecs.m_323312_(ArrayList::new)),
            ClientboundUpdateAttributesPacket.AttributeSnapshot::modifiers,
            ClientboundUpdateAttributesPacket.AttributeSnapshot::new
        );
    }
}