package net.minecraft.world.entity.projectile;

import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractHurtingProjectile extends Projectile {
    public static final double f_314176_ = 0.1;
    public static final double f_314076_ = 0.05;
    public double xPower;
    public double yPower;
    public double zPower;

    protected AbstractHurtingProjectile(EntityType<? extends AbstractHurtingProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    protected AbstractHurtingProjectile(
        EntityType<? extends AbstractHurtingProjectile> p_310629_, double p_311590_, double p_312782_, double p_309484_, Level p_311660_
    ) {
        this(p_310629_, p_311660_);
        this.setPos(p_311590_, p_312782_, p_309484_);
    }

    public AbstractHurtingProjectile(
        EntityType<? extends AbstractHurtingProjectile> pEntityType,
        double pX,
        double pY,
        double pZ,
        double pOffsetX,
        double pOffsetY,
        double pOffsetZ,
        Level pLevel
    ) {
        this(pEntityType, pLevel);
        this.moveTo(pX, pY, pZ, this.getYRot(), this.getXRot());
        this.reapplyPosition();
        this.m_319987_(pOffsetX, pOffsetY, pOffsetZ);
    }

    public AbstractHurtingProjectile(
        EntityType<? extends AbstractHurtingProjectile> pEntityType, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, Level pLevel
    ) {
        this(pEntityType, pShooter.getX(), pShooter.getY(), pShooter.getZ(), pOffsetX, pOffsetY, pOffsetZ, pLevel);
        this.setOwner(pShooter);
        this.setRot(pShooter.getYRot(), pShooter.getXRot());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_330369_) {
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 4.0;
        if (Double.isNaN(d0)) {
            d0 = 4.0;
        }

        d0 *= 64.0;
        return pDistance < d0 * d0;
    }

    protected ClipContext.Block m_306485_() {
        return ClipContext.Block.COLLIDER;
    }

    @Override
    public void tick() {
        Entity entity = this.getOwner();
        if (this.level().isClientSide || (entity == null || !entity.isRemoved()) && this.level().hasChunkAt(this.blockPosition())) {
            super.tick();
            if (this.shouldBurn()) {
                this.m_322706_(1);
            }

            HitResult hitresult = ProjectileUtil.m_307639_(this, this::canHitEntity, this.m_306485_());
            if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                this.m_320847_(hitresult);
            }

            this.checkInsideBlocks();
            Vec3 vec3 = this.getDeltaMovement();
            double d0 = this.getX() + vec3.x;
            double d1 = this.getY() + vec3.y;
            double d2 = this.getZ() + vec3.z;
            ProjectileUtil.rotateTowardsMovement(this, 0.2F);
            float f;
            if (this.isInWater()) {
                for (int i = 0; i < 4; i++) {
                    float f1 = 0.25F;
                    this.level()
                        .addParticle(
                            ParticleTypes.BUBBLE,
                            d0 - vec3.x * 0.25,
                            d1 - vec3.y * 0.25,
                            d2 - vec3.z * 0.25,
                            vec3.x,
                            vec3.y,
                            vec3.z
                        );
                }

                f = this.m_307145_();
            } else {
                f = this.getInertia();
            }

            this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double)f));
            ParticleOptions particleoptions = this.getTrailParticle();
            if (particleoptions != null) {
                this.level().addParticle(particleoptions, d0, d1 + 0.5, d2, 0.0, 0.0, 0.0);
            }

            this.setPos(d0, d1, d2);
        } else {
            this.discard();
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        return !this.isInvulnerableTo(pSource);
    }

    @Override
    protected boolean canHitEntity(Entity p_36842_) {
        return super.canHitEntity(p_36842_) && !p_36842_.noPhysics;
    }

    protected boolean shouldBurn() {
        return true;
    }

    @Nullable
    protected ParticleOptions getTrailParticle() {
        return ParticleTypes.SMOKE;
    }

    protected float getInertia() {
        return 0.95F;
    }

    protected float m_307145_() {
        return 0.8F;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("power", this.newDoubleList(new double[]{this.xPower, this.yPower, this.zPower}));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("power", 9)) {
            ListTag listtag = pCompound.getList("power", 6);
            if (listtag.size() == 3) {
                this.xPower = listtag.getDouble(0);
                this.yPower = listtag.getDouble(1);
                this.zPower = listtag.getDouble(2);
            }
        }
    }

    @Override
    public float getLightLevelDependentMagicValue() {
        return 1.0F;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.getOwner();
        int i = entity == null ? 0 : entity.getId();
        return new ClientboundAddEntityPacket(
            this.getId(),
            this.getUUID(),
            this.getX(),
            this.getY(),
            this.getZ(),
            this.getXRot(),
            this.getYRot(),
            this.getType(),
            i,
            new Vec3(this.xPower, this.yPower, this.zPower),
            0.0
        );
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        double d0 = pPacket.getXa();
        double d1 = pPacket.getYa();
        double d2 = pPacket.getZa();
        this.m_319987_(d0, d1, d2);
    }

    private void m_319987_(double p_329575_, double p_331475_, double p_330972_) {
        double d0 = Math.sqrt(p_329575_ * p_329575_ + p_331475_ * p_331475_ + p_330972_ * p_330972_);
        if (d0 != 0.0) {
            this.xPower = p_329575_ / d0 * 0.1;
            this.yPower = p_331475_ / d0 * 0.1;
            this.zPower = p_330972_ / d0 * 0.1;
        }
    }

    @Override
    protected void m_318614_(@Nullable Entity p_334459_, boolean p_331188_) {
        super.m_318614_(p_334459_, p_331188_);
        if (p_331188_) {
            this.xPower = this.getDeltaMovement().x * 0.1;
            this.yPower = this.getDeltaMovement().y * 0.1;
            this.zPower = this.getDeltaMovement().z * 0.1;
        } else {
            this.xPower = this.getDeltaMovement().x * 0.05;
            this.yPower = this.getDeltaMovement().y * 0.05;
            this.zPower = this.getDeltaMovement().z * 0.05;
        }
    }
}
