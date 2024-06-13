package net.minecraft.world.entity.monster.breeze;

import com.mojang.serialization.Dynamic;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Breeze extends Monster {
    private static final int f_302827_ = 20;
    private static final int f_303035_ = 1;
    private static final int f_303159_ = 20;
    private static final int f_303108_ = 3;
    private static final int f_302751_ = 5;
    private static final int f_303204_ = 10;
    private static final float f_303079_ = 3.0F;
    private static final int f_314401_ = 1;
    private static final int f_315807_ = 80;
    public AnimationState f_302231_ = new AnimationState();
    public AnimationState f_303623_ = new AnimationState();
    public AnimationState f_314343_ = new AnimationState();
    public AnimationState f_303038_ = new AnimationState();
    public AnimationState f_302318_ = new AnimationState();
    public AnimationState f_303192_ = new AnimationState();
    private int f_303683_ = 0;
    private int f_314081_ = 0;
    private static final ProjectileDeflection f_314490_ = (p_331050_, p_333637_, p_328921_) -> {
        p_333637_.level().m_307553_(p_333637_, SoundEvents.f_315294_, p_333637_.getSoundSource(), 1.0F, 1.0F);
        ProjectileDeflection.f_314640_.m_322705_(p_331050_, p_333637_, p_328921_);
    };

    public static AttributeSupplier.Builder m_307321_() {
        return Mob.createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.63F)
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.FOLLOW_RANGE, 24.0)
            .add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    public Breeze(EntityType<? extends Monster> p_310338_, Level p_309512_) {
        super(p_310338_, p_309512_);
        this.setPathfindingMalus(PathType.DANGER_TRAPDOOR, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, -1.0F);
        this.xpReward = 10;
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> p_311857_) {
        return BreezeAi.m_307451_(this.brainProvider().makeBrain(p_311857_));
    }

    @Override
    public Brain<Breeze> getBrain() {
        return (Brain<Breeze>)super.getBrain();
    }

    @Override
    protected Brain.Provider<Breeze> brainProvider() {
        return Brain.provider(BreezeAi.f_303399_, BreezeAi.f_303564_);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_309800_) {
        if (this.level().isClientSide() && DATA_POSE.equals(p_309800_)) {
            this.m_306154_();
            Pose pose = this.getPose();
            switch (pose) {
                case SHOOTING:
                    this.f_302318_.startIfStopped(this.tickCount);
                    break;
                case INHALING:
                    this.f_303038_.startIfStopped(this.tickCount);
                    break;
                case SLIDING:
                    this.f_303623_.startIfStopped(this.tickCount);
            }
        }

        super.onSyncedDataUpdated(p_309800_);
    }

    private void m_306154_() {
        this.f_302318_.stop();
        this.f_302231_.stop();
        this.f_303192_.stop();
        this.f_303038_.stop();
    }

    @Override
    public void tick() {
        Pose pose = this.getPose();
        switch (pose) {
            case SHOOTING:
            case INHALING:
            case STANDING:
                this.m_306411_().m_304639_(1 + this.getRandom().nextInt(1));
                break;
            case SLIDING:
                this.m_304639_(20);
                break;
            case LONG_JUMPING:
                this.m_306923_();
        }

        if (pose != Pose.SLIDING && this.f_303623_.isStarted()) {
            this.f_314343_.start(this.tickCount);
            this.f_303623_.stop();
        }

        this.f_314081_ = this.f_314081_ == 0 ? this.random.nextIntBetweenInclusive(1, 80) : this.f_314081_ - 1;
        if (this.f_314081_ == 0) {
            this.m_325032_();
        }

        super.tick();
    }

    public Breeze m_306411_() {
        this.f_303683_ = 0;
        return this;
    }

    public void m_306923_() {
        if (++this.f_303683_ <= 5) {
            BlockState blockstate = !this.m_321101_().isAir() ? this.m_321101_() : this.getBlockStateOn();
            Vec3 vec3 = this.getDeltaMovement();
            Vec3 vec31 = this.position().add(vec3).add(0.0, 0.1F, 0.0);

            for (int i = 0; i < 3; i++) {
                this.level()
                    .addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), vec31.x, vec31.y, vec31.z, 0.0, 0.0, 0.0);
            }
        }
    }

    public void m_304639_(int p_310885_) {
        if (!this.isPassenger()) {
            Vec3 vec3 = this.getBoundingBox().getCenter();
            Vec3 vec31 = new Vec3(vec3.x, this.position().y, vec3.z);
            BlockState blockstate = !this.m_321101_().isAir() ? this.m_321101_() : this.getBlockStateOn();
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                for (int i = 0; i < p_310885_; i++) {
                    this.level()
                        .addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), vec31.x, vec31.y, vec31.z, 0.0, 0.0, 0.0);
                }
            }
        }
    }

    @Override
    public void playAmbientSound() {
        if (this.getTarget() == null || !this.onGround()) {
            this.level().m_307553_(this, this.getAmbientSound(), this.getSoundSource(), 1.0F, 1.0F);
        }
    }

    public void m_325032_() {
        float f = 0.7F + 0.4F * this.random.nextFloat();
        float f1 = 0.8F + 0.2F * this.random.nextFloat();
        this.level().m_307553_(this, SoundEvents.f_316809_, this.getSoundSource(), f1, f);
    }

    @Override
    public ProjectileDeflection m_321219_(Projectile p_335920_) {
        return p_335920_.getType() != EntityType.f_315936_ && p_335920_.getType() != EntityType.f_303421_ ? f_314490_ : ProjectileDeflection.f_316951_;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.f_303419_;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_311322_) {
        return SoundEvents.f_302255_;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.onGround() ? SoundEvents.f_303445_ : SoundEvents.f_302646_;
    }

    public Optional<LivingEntity> m_320928_() {
        return this.getBrain()
            .getMemory(MemoryModuleType.HURT_BY)
            .map(DamageSource::getEntity)
            .filter(p_333499_ -> p_333499_ instanceof LivingEntity)
            .map(p_332795_ -> (LivingEntity)p_332795_);
    }

    public boolean m_307736_(Vec3 p_311473_) {
        Vec3 vec3 = this.blockPosition().getCenter();
        return p_311473_.m_306338_(vec3, 4.0, 10.0);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("breezeBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().popPush("breezeActivityUpdate");
        BreezeAi.m_320429_(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
        DebugPackets.m_307177_(this);
    }

    @Override
    public boolean canAttackType(EntityType<?> p_310232_) {
        return p_310232_ == EntityType.PLAYER || p_310232_ == EntityType.IRON_GOLEM;
    }

    @Override
    public int getMaxHeadYRot() {
        return 30;
    }

    @Override
    public int getHeadRotSpeed() {
        return 25;
    }

    public double m_307822_() {
        return this.getEyeY() - 0.4;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource p_309859_) {
        return p_309859_.is(DamageTypeTags.f_302961_) || p_309859_.getEntity() instanceof Breeze || super.isInvulnerableTo(p_309859_);
    }

    @Override
    public double getFluidJumpThreshold() {
        return (double)this.getEyeHeight();
    }

    @Override
    public boolean causeFallDamage(float p_310250_, float p_310041_, DamageSource p_311921_) {
        if (p_310250_ > 3.0F) {
            this.playSound(SoundEvents.f_303831_, 1.0F, 1.0F);
        }

        return super.causeFallDamage(p_310250_, p_310041_, p_311921_);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.EVENTS;
    }

    @Nullable
    @Override
    public LivingEntity getTarget() {
        return this.m_319699_();
    }
}