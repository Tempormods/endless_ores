package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public record ClientboundDamageEventPacket(int entityId, Holder<DamageType> f_316647_, int sourceCauseId, int sourceDirectId, Optional<Vec3> sourcePosition)
    implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDamageEventPacket> f_315470_ = Packet.m_319422_(
        ClientboundDamageEventPacket::m_269009_, ClientboundDamageEventPacket::new
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<DamageType>> f_314341_ = ByteBufCodecs.m_322636_(Registries.DAMAGE_TYPE);

    public ClientboundDamageEventPacket(Entity pEntity, DamageSource pDamageSource) {
        this(
            pEntity.getId(),
            pDamageSource.typeHolder(),
            pDamageSource.getEntity() != null ? pDamageSource.getEntity().getId() : -1,
            pDamageSource.getDirectEntity() != null ? pDamageSource.getDirectEntity().getId() : -1,
            Optional.ofNullable(pDamageSource.sourcePositionRaw())
        );
    }

    private ClientboundDamageEventPacket(RegistryFriendlyByteBuf p_328419_) {
        this(
            p_328419_.readVarInt(),
            f_314341_.m_318688_(p_328419_),
            readOptionalEntityId(p_328419_),
            readOptionalEntityId(p_328419_),
            p_328419_.readOptional(p_270813_ -> new Vec3(p_270813_.readDouble(), p_270813_.readDouble(), p_270813_.readDouble()))
        );
    }

    private static void writeOptionalEntityId(FriendlyByteBuf pBuffer, int pOptionalEntityId) {
        pBuffer.writeVarInt(pOptionalEntityId + 1);
    }

    private static int readOptionalEntityId(FriendlyByteBuf pBuffer) {
        return pBuffer.readVarInt() - 1;
    }

    private void m_269009_(RegistryFriendlyByteBuf p_330396_) {
        p_330396_.writeVarInt(this.entityId);
        f_314341_.m_318638_(p_330396_, this.f_316647_);
        writeOptionalEntityId(p_330396_, this.sourceCauseId);
        writeOptionalEntityId(p_330396_, this.sourceDirectId);
        p_330396_.writeOptional(this.sourcePosition, (p_296394_, p_296395_) -> {
            p_296394_.writeDouble(p_296395_.x());
            p_296394_.writeDouble(p_296395_.y());
            p_296394_.writeDouble(p_296395_.z());
        });
    }

    @Override
    public PacketType<ClientboundDamageEventPacket> write() {
        return GamePacketTypes.f_316188_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleDamageEvent(this);
    }

    public DamageSource getSource(Level pLevel) {
        if (this.sourcePosition.isPresent()) {
            return new DamageSource(this.f_316647_, this.sourcePosition.get());
        } else {
            Entity entity = pLevel.getEntity(this.sourceCauseId);
            Entity entity1 = pLevel.getEntity(this.sourceDirectId);
            return new DamageSource(this.f_316647_, entity1, entity);
        }
    }
}