package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

public class ClientboundExplodePacket implements Packet<ClientGamePacketListener> {
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundExplodePacket> f_316417_ = Packet.m_319422_(
        ClientboundExplodePacket::m_132128_, ClientboundExplodePacket::new
    );
    private final double x;
    private final double y;
    private final double z;
    private final float power;
    private final List<BlockPos> toBlow;
    private final float knockbackX;
    private final float knockbackY;
    private final float knockbackZ;
    private final ParticleOptions f_302927_;
    private final ParticleOptions f_302340_;
    private final Explosion.BlockInteraction f_302457_;
    private final Holder<SoundEvent> f_303703_;

    public ClientboundExplodePacket(
        double pX,
        double pY,
        double pZ,
        float pPower,
        List<BlockPos> pToBlow,
        @Nullable Vec3 pKnockback,
        Explosion.BlockInteraction p_309954_,
        ParticleOptions p_311040_,
        ParticleOptions p_309903_,
        Holder<SoundEvent> p_335918_
    ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
        this.power = pPower;
        this.toBlow = Lists.newArrayList(pToBlow);
        this.f_303703_ = p_335918_;
        if (pKnockback != null) {
            this.knockbackX = (float)pKnockback.x;
            this.knockbackY = (float)pKnockback.y;
            this.knockbackZ = (float)pKnockback.z;
        } else {
            this.knockbackX = 0.0F;
            this.knockbackY = 0.0F;
            this.knockbackZ = 0.0F;
        }

        this.f_302457_ = p_309954_;
        this.f_302927_ = p_311040_;
        this.f_302340_ = p_309903_;
    }

    private ClientboundExplodePacket(RegistryFriendlyByteBuf p_335077_) {
        this.x = p_335077_.readDouble();
        this.y = p_335077_.readDouble();
        this.z = p_335077_.readDouble();
        this.power = p_335077_.readFloat();
        int i = Mth.floor(this.x);
        int j = Mth.floor(this.y);
        int k = Mth.floor(this.z);
        this.toBlow = p_335077_.readList(p_178850_ -> {
            int l = p_178850_.readByte() + i;
            int i1 = p_178850_.readByte() + j;
            int j1 = p_178850_.readByte() + k;
            return new BlockPos(l, i1, j1);
        });
        this.knockbackX = p_335077_.readFloat();
        this.knockbackY = p_335077_.readFloat();
        this.knockbackZ = p_335077_.readFloat();
        this.f_302457_ = p_335077_.readEnum(Explosion.BlockInteraction.class);
        this.f_302927_ = ParticleTypes.f_314250_.m_318688_(p_335077_);
        this.f_302340_ = ParticleTypes.f_314250_.m_318688_(p_335077_);
        this.f_303703_ = SoundEvent.f_314687_.m_318688_(p_335077_);
    }

    private void m_132128_(RegistryFriendlyByteBuf p_334035_) {
        p_334035_.writeDouble(this.x);
        p_334035_.writeDouble(this.y);
        p_334035_.writeDouble(this.z);
        p_334035_.writeFloat(this.power);
        int i = Mth.floor(this.x);
        int j = Mth.floor(this.y);
        int k = Mth.floor(this.z);
        p_334035_.writeCollection(this.toBlow, (p_296399_, p_296400_) -> {
            int l = p_296400_.getX() - i;
            int i1 = p_296400_.getY() - j;
            int j1 = p_296400_.getZ() - k;
            p_296399_.writeByte(l);
            p_296399_.writeByte(i1);
            p_296399_.writeByte(j1);
        });
        p_334035_.writeFloat(this.knockbackX);
        p_334035_.writeFloat(this.knockbackY);
        p_334035_.writeFloat(this.knockbackZ);
        p_334035_.writeEnum(this.f_302457_);
        ParticleTypes.f_314250_.m_318638_(p_334035_, this.f_302927_);
        ParticleTypes.f_314250_.m_318638_(p_334035_, this.f_302340_);
        SoundEvent.f_314687_.m_318638_(p_334035_, this.f_303703_);
    }

    @Override
    public PacketType<ClientboundExplodePacket> write() {
        return GamePacketTypes.f_316834_;
    }

    public void handle(ClientGamePacketListener pHandler) {
        pHandler.handleExplosion(this);
    }

    public float getKnockbackX() {
        return this.knockbackX;
    }

    public float getKnockbackY() {
        return this.knockbackY;
    }

    public float getKnockbackZ() {
        return this.knockbackZ;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getPower() {
        return this.power;
    }

    public List<BlockPos> getToBlow() {
        return this.toBlow;
    }

    public Explosion.BlockInteraction m_305992_() {
        return this.f_302457_;
    }

    public ParticleOptions m_307612_() {
        return this.f_302927_;
    }

    public ParticleOptions m_305172_() {
        return this.f_302340_;
    }

    public Holder<SoundEvent> m_307893_() {
        return this.f_303703_;
    }
}