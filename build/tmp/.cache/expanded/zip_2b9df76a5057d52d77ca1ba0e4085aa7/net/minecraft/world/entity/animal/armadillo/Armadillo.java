package net.minecraft.world.entity.animal.armadillo;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class Armadillo extends Animal {
    public static final float f_314226_ = 0.6F;
    public static final float f_317124_ = 32.5F;
    public static final int f_314283_ = 80;
    private static final double f_317000_ = 7.0;
    private static final double f_315255_ = 2.0;
    private static final EntityDataAccessor<Armadillo.ArmadilloState> f_316540_ = SynchedEntityData.defineId(Armadillo.class, EntityDataSerializers.f_315197_);
    private long f_315138_ = 0L;
    public final AnimationState f_314820_ = new AnimationState();
    public final AnimationState f_313932_ = new AnimationState();
    public final AnimationState f_316698_ = new AnimationState();
    private int f_314001_;
    private boolean f_315189_ = false;

    public Armadillo(EntityType<? extends Animal> p_331987_, Level p_331498_) {
        super(p_331987_, p_331498_);
        this.getNavigation().setCanFloat(true);
        this.f_314001_ = this.m_322224_();
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel p_330674_, AgeableMob p_330373_) {
        return EntityType.f_316265_.create(p_330674_);
    }

    public static AttributeSupplier.Builder m_324717_() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0).add(Attributes.MOVEMENT_SPEED, 0.14);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335155_) {
        super.defineSynchedData(p_335155_);
        p_335155_.m_318949_(f_316540_, Armadillo.ArmadilloState.IDLE);
    }

    public boolean m_323155_() {
        return this.entityData.get(f_316540_) != Armadillo.ArmadilloState.IDLE;
    }

    public boolean m_322786_() {
        return this.m_318651_().m_320045_(this.f_315138_);
    }

    public boolean m_320135_() {
        return this.m_318651_() == Armadillo.ArmadilloState.ROLLING && this.f_315138_ > (long)Armadillo.ArmadilloState.ROLLING.m_323052_();
    }

    public Armadillo.ArmadilloState m_318651_() {
        return this.entityData.get(f_316540_);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    public void m_323579_(Armadillo.ArmadilloState p_330881_) {
        this.entityData.set(f_316540_, p_330881_);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_328821_) {
        if (f_316540_.equals(p_328821_)) {
            this.f_315138_ = 0L;
        }

        super.onSyncedDataUpdated(p_328821_);
    }

    @Override
    protected Brain.Provider<Armadillo> brainProvider() {
        return ArmadilloAi.m_318723_();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> p_334417_) {
        return ArmadilloAi.m_320021_(this.brainProvider().makeBrain(p_334417_));
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("armadilloBrain");
        ((Brain<Armadillo>)this.brain).tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("armadilloActivityUpdate");
        ArmadilloAi.m_318618_(this);
        this.level().getProfiler().pop();
        if (this.isAlive() && !this.isBaby() && --this.f_314001_ <= 0) {
            this.playSound(SoundEvents.f_315015_, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.spawnAtLocation(Items.f_314244_);
            this.gameEvent(GameEvent.ENTITY_PLACE);
            this.f_314001_ = this.m_322224_();
        }

        super.customServerAiStep();
    }

    private int m_322224_() {
        return this.random.nextInt(20 * TimeUtil.f_315347_ * 5) + 20 * TimeUtil.f_315347_ * 5;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.m_319810_();
        }

        if (this.m_323155_()) {
            this.m_322776_();
        }

        this.f_315138_++;
    }

    @Override
    public float m_320705_() {
        return this.isBaby() ? 0.6F : 1.0F;
    }

    private void m_319810_() {
        switch (this.m_318651_()) {
            case IDLE:
                this.f_314820_.stop();
                this.f_313932_.stop();
                this.f_316698_.stop();
                break;
            case ROLLING:
                this.f_314820_.stop();
                this.f_313932_.startIfStopped(this.tickCount);
                this.f_316698_.stop();
                break;
            case SCARED:
                this.f_314820_.stop();
                this.f_313932_.stop();
                if (this.f_315189_) {
                    this.f_316698_.stop();
                    this.f_315189_ = false;
                }

                if (this.f_315138_ == 0L) {
                    this.f_316698_.start(this.tickCount);
                    this.f_316698_.m_320921_(Armadillo.ArmadilloState.SCARED.m_323052_(), 1.0F);
                } else {
                    this.f_316698_.startIfStopped(this.tickCount);
                }
                break;
            case UNROLLING:
                this.f_314820_.startIfStopped(this.tickCount);
                this.f_313932_.stop();
                this.f_316698_.stop();
        }
    }

    @Override
    public void handleEntityEvent(byte p_330641_) {
        if (p_330641_ == 64 && this.level().isClientSide) {
            this.f_315189_ = true;
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.f_314094_, this.getSoundSource(), 1.0F, 1.0F, false);
        } else {
            super.handleEntityEvent(p_330641_);
        }
    }

    @Override
    public boolean isFood(ItemStack p_333396_) {
        return p_333396_.is(ItemTags.f_316663_);
    }

    public static boolean m_319845_(
        EntityType<Armadillo> p_328712_, LevelAccessor p_330410_, MobSpawnType p_328520_, BlockPos p_328785_, RandomSource p_328859_
    ) {
        return p_330410_.getBlockState(p_328785_.below()).is(BlockTags.f_314780_) && isBrightEnoughToSpawn(p_330410_, p_328785_);
    }

    public boolean m_318898_(LivingEntity p_331619_) {
        if (!this.getBoundingBox().inflate(7.0, 2.0, 7.0).intersects(p_331619_.getBoundingBox())) {
            return false;
        } else if (p_331619_.getType().is(EntityTypeTags.f_303412_)) {
            return true;
        } else if (this.getLastHurtByMob() == p_331619_) {
            return true;
        } else if (p_331619_ instanceof Player player) {
            return player.isSpectator() ? false : player.isSprinting() || player.isPassenger();
        } else {
            return false;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_328280_) {
        super.addAdditionalSaveData(p_328280_);
        p_328280_.putString("state", this.m_318651_().getSerializedName());
        p_328280_.putInt("scute_time", this.f_314001_);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_329448_) {
        super.readAdditionalSaveData(p_329448_);
        this.m_323579_(Armadillo.ArmadilloState.m_322831_(p_329448_.getString("state")));
        if (p_329448_.contains("scute_time")) {
            this.f_314001_ = p_329448_.getInt("scute_time");
        }
    }

    public void m_318878_() {
        if (!this.m_323155_()) {
            this.m_324154_();
            this.resetLove();
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.m_323137_(SoundEvents.f_316604_);
            this.m_323579_(Armadillo.ArmadilloState.ROLLING);
        }
    }

    public void m_324029_() {
        if (this.m_323155_()) {
            this.gameEvent(GameEvent.ENTITY_ACTION);
            this.m_323137_(SoundEvents.f_314177_);
            this.m_323579_(Armadillo.ArmadilloState.IDLE);
        }
    }

    @Override
    public boolean hurt(DamageSource p_332995_, float p_331278_) {
        if (this.m_323155_()) {
            p_331278_ = (p_331278_ - 1.0F) / 2.0F;
        }

        return super.hurt(p_332995_, p_331278_);
    }

    @Override
    protected void actuallyHurt(DamageSource p_328552_, float p_332199_) {
        super.actuallyHurt(p_328552_, p_332199_);
        if (!this.isNoAi() && !this.isDeadOrDying()) {
            if (p_328552_.getEntity() instanceof LivingEntity) {
                this.getBrain().setMemoryWithExpiry(MemoryModuleType.f_315790_, true, 80L);
                if (this.m_323949_()) {
                    this.m_318878_();
                }
            } else if (this.m_322481_()) {
                this.m_324029_();
            }
        }
    }

    public boolean m_322481_() {
        return this.isOnFire() || this.isFreezing();
    }

    @Override
    public InteractionResult mobInteract(Player p_335255_, InteractionHand p_331602_) {
        ItemStack itemstack = p_335255_.getItemInHand(p_331602_);
        if (itemstack.is(Items.BRUSH) && this.m_324341_()) {
            itemstack.hurtAndBreak(16, p_335255_, m_322775_(p_331602_));
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(p_335255_, p_331602_);
        }
    }

    @Override
    public void ageUp(int p_328674_, boolean p_330615_) {
        if (this.isBaby() && p_330615_) {
            this.m_323137_(SoundEvents.f_314762_);
        }

        super.ageUp(p_328674_, p_330615_);
    }

    public boolean m_324341_() {
        if (this.isBaby()) {
            return false;
        } else {
            this.spawnAtLocation(new ItemStack(Items.f_314244_));
            this.gameEvent(GameEvent.ENTITY_INTERACT);
            this.playSound(SoundEvents.f_314079_);
            return true;
        }
    }

    public boolean m_323949_() {
        return !this.isPanicking() && !this.isInLiquid() && !this.isLeashed() && !this.isPassenger() && !this.isVehicle();
    }

    @Override
    public void setInLove(@Nullable Player p_333363_) {
        super.setInLove(p_333363_);
        this.m_323137_(SoundEvents.f_314762_);
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.m_323155_();
    }

    @Override
    public SoundEvent getEatingSound(ItemStack p_333831_) {
        return SoundEvents.f_314762_;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.m_323155_() ? null : SoundEvents.f_315696_;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.f_316928_;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_335086_) {
        return this.m_323155_() ? SoundEvents.f_314660_ : SoundEvents.f_314840_;
    }

    @Override
    protected void playStepSound(BlockPos p_333806_, BlockState p_333410_) {
        this.playSound(SoundEvents.f_315684_, 0.15F, 1.0F);
    }

    @Override
    public int getMaxHeadYRot() {
        return this.m_323155_() ? 0 : 32;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            /**
             * Update the Head and Body rendering angles
             */
            @Override
            public void clientTick() {
                if (!Armadillo.this.m_323155_()) {
                    super.clientTick();
                }
            }
        };
    }

    public static enum ArmadilloState implements StringRepresentable {
        IDLE("idle", false, 0, 0) {
            @Override
            public boolean m_320045_(long p_328572_) {
                return false;
            }
        },
        ROLLING("rolling", true, 10, 1) {
            @Override
            public boolean m_320045_(long p_328385_) {
                return p_328385_ > 5L;
            }
        },
        SCARED("scared", true, 50, 2) {
            @Override
            public boolean m_320045_(long p_327676_) {
                return true;
            }
        },
        UNROLLING("unrolling", true, 30, 3) {
            @Override
            public boolean m_320045_(long p_328518_) {
                return p_328518_ < 26L;
            }
        };

        private static final StringRepresentable.EnumCodec<Armadillo.ArmadilloState> f_316478_ = StringRepresentable.fromEnum(Armadillo.ArmadilloState::values);
        private static final IntFunction<Armadillo.ArmadilloState> f_317053_ = ByIdMap.continuous(
            Armadillo.ArmadilloState::m_320004_, values(), ByIdMap.OutOfBoundsStrategy.ZERO
        );
        public static final StreamCodec<ByteBuf, Armadillo.ArmadilloState> f_315535_ = ByteBufCodecs.m_321301_(f_317053_, Armadillo.ArmadilloState::m_320004_);
        private final String f_315119_;
        private final boolean f_314656_;
        private final int f_314396_;
        private final int f_314528_;

        ArmadilloState(final String p_327882_, final boolean p_330882_, final int p_328662_, final int p_328018_) {
            this.f_315119_ = p_327882_;
            this.f_314656_ = p_330882_;
            this.f_314396_ = p_328662_;
            this.f_314528_ = p_328018_;
        }

        public static Armadillo.ArmadilloState m_322831_(String p_329762_) {
            return f_316478_.byName(p_329762_, IDLE);
        }

        @Override
        public String getSerializedName() {
            return this.f_315119_;
        }

        private int m_320004_() {
            return this.f_314528_;
        }

        public abstract boolean m_320045_(long p_329790_);

        public boolean m_324774_() {
            return this.f_314656_;
        }

        public int m_323052_() {
            return this.f_314396_;
        }
    }
}