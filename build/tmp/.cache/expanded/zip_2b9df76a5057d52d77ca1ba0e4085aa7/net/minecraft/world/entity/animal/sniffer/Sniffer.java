package net.minecraft.world.entity.animal.sniffer;

import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Sniffer extends Animal {
    private static final int DIGGING_PARTICLES_DELAY_TICKS = 1700;
    private static final int DIGGING_PARTICLES_DURATION_TICKS = 6000;
    private static final int DIGGING_PARTICLES_AMOUNT = 30;
    private static final int DIGGING_DROP_SEED_OFFSET_TICKS = 120;
    private static final int SNIFFER_BABY_AGE_TICKS = 48000;
    private static final float DIGGING_BB_HEIGHT_OFFSET = 0.4F;
    private static final EntityDimensions DIGGING_DIMENSIONS = EntityDimensions.scalable(EntityType.SNIFFER.getWidth(), EntityType.SNIFFER.getHeight() - 0.4F)
        .m_320568_(0.81F);
    private static final EntityDataAccessor<Sniffer.State> DATA_STATE = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.SNIFFER_STATE);
    private static final EntityDataAccessor<Integer> DATA_DROP_SEED_AT_TICK = SynchedEntityData.defineId(Sniffer.class, EntityDataSerializers.INT);
    public final AnimationState feelingHappyAnimationState = new AnimationState();
    public final AnimationState scentingAnimationState = new AnimationState();
    public final AnimationState sniffingAnimationState = new AnimationState();
    public final AnimationState diggingAnimationState = new AnimationState();
    public final AnimationState risingAnimationState = new AnimationState();

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.1F).add(Attributes.MAX_HEALTH, 14.0);
    }

    public Sniffer(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(PathType.WATER, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DAMAGE_CAUTIOUS, -1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335721_) {
        super.defineSynchedData(p_335721_);
        p_335721_.m_318949_(DATA_STATE, Sniffer.State.IDLING);
        p_335721_.m_318949_(DATA_DROP_SEED_AT_TICK, 0);
    }

    @Override
    public void onPathfindingStart() {
        super.onPathfindingStart();
        if (this.isOnFire() || this.isInWater()) {
            this.setPathfindingMalus(PathType.WATER, 0.0F);
        }
    }

    @Override
    public void onPathfindingDone() {
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    @Override
    public EntityDimensions m_31586_(Pose p_331665_) {
        return this.getState() == Sniffer.State.DIGGING ? DIGGING_DIMENSIONS.scale(this.m_320705_()) : super.m_31586_(p_331665_);
    }

    public boolean isSearching() {
        return this.getState() == Sniffer.State.SEARCHING;
    }

    public boolean isTempted() {
        return this.brain.getMemory(MemoryModuleType.IS_TEMPTED).orElse(false);
    }

    public boolean canSniff() {
        return !this.isTempted() && !this.isPanicking() && !this.isInWater() && !this.isInLove() && this.onGround() && !this.isPassenger() && !this.isLeashed();
    }

    public boolean canPlayDiggingSound() {
        return this.getState() == Sniffer.State.DIGGING || this.getState() == Sniffer.State.SEARCHING;
    }

    private BlockPos getHeadBlock() {
        Vec3 vec3 = this.getHeadPosition();
        return BlockPos.containing(vec3.x(), this.getY() + 0.2F, vec3.z());
    }

    private Vec3 getHeadPosition() {
        return this.position().add(this.getForward().scale(2.25));
    }

    private Sniffer.State getState() {
        return this.entityData.get(DATA_STATE);
    }

    private Sniffer setState(Sniffer.State pState) {
        this.entityData.set(DATA_STATE, pState);
        return this;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if (DATA_STATE.equals(pKey)) {
            Sniffer.State sniffer$state = this.getState();
            this.resetAnimations();
            switch (sniffer$state) {
                case FEELING_HAPPY:
                    this.feelingHappyAnimationState.startIfStopped(this.tickCount);
                    break;
                case SCENTING:
                    this.scentingAnimationState.startIfStopped(this.tickCount);
                    break;
                case SNIFFING:
                    this.sniffingAnimationState.startIfStopped(this.tickCount);
                case SEARCHING:
                default:
                    break;
                case DIGGING:
                    this.diggingAnimationState.startIfStopped(this.tickCount);
                    break;
                case RISING:
                    this.risingAnimationState.startIfStopped(this.tickCount);
            }

            this.refreshDimensions();
        }

        super.onSyncedDataUpdated(pKey);
    }

    private void resetAnimations() {
        this.diggingAnimationState.stop();
        this.sniffingAnimationState.stop();
        this.risingAnimationState.stop();
        this.feelingHappyAnimationState.stop();
        this.scentingAnimationState.stop();
    }

    public Sniffer transitionTo(Sniffer.State pState) {
        switch (pState) {
            case IDLING:
                this.setState(Sniffer.State.IDLING);
                break;
            case FEELING_HAPPY:
                this.playSound(SoundEvents.SNIFFER_HAPPY, 1.0F, 1.0F);
                this.setState(Sniffer.State.FEELING_HAPPY);
                break;
            case SCENTING:
                this.setState(Sniffer.State.SCENTING).onScentingStart();
                break;
            case SNIFFING:
                this.playSound(SoundEvents.SNIFFER_SNIFFING, 1.0F, 1.0F);
                this.setState(Sniffer.State.SNIFFING);
                break;
            case SEARCHING:
                this.setState(Sniffer.State.SEARCHING);
                break;
            case DIGGING:
                this.setState(Sniffer.State.DIGGING).onDiggingStart();
                break;
            case RISING:
                this.playSound(SoundEvents.SNIFFER_DIGGING_STOP, 1.0F, 1.0F);
                this.setState(Sniffer.State.RISING);
        }

        return this;
    }

    private Sniffer onScentingStart() {
        this.playSound(SoundEvents.SNIFFER_SCENTING, 1.0F, this.isBaby() ? 1.3F : 1.0F);
        return this;
    }

    private Sniffer onDiggingStart() {
        this.entityData.set(DATA_DROP_SEED_AT_TICK, this.tickCount + 120);
        this.level().broadcastEntityEvent(this, (byte)63);
        return this;
    }

    public Sniffer onDiggingComplete(boolean pStoreExploredPosition) {
        if (pStoreExploredPosition) {
            this.storeExploredPosition(this.getOnPos());
        }

        return this;
    }

    Optional<BlockPos> calculateDigPosition() {
        return IntStream.range(0, 5)
            .mapToObj(p_273771_ -> LandRandomPos.getPos(this, 10 + 2 * p_273771_, 3))
            .filter(Objects::nonNull)
            .map(BlockPos::containing)
            .filter(p_326998_ -> this.level().getWorldBorder().isWithinBounds(p_326998_))
            .map(BlockPos::below)
            .filter(this::canDig)
            .findFirst();
    }

    boolean canDig() {
        return !this.isPanicking()
            && !this.isTempted()
            && !this.isBaby()
            && !this.isInWater()
            && this.onGround()
            && !this.isPassenger()
            && this.canDig(this.getHeadBlock().below());
    }

    private boolean canDig(BlockPos p_272757_) {
        return this.level().getBlockState(p_272757_).is(BlockTags.SNIFFER_DIGGABLE_BLOCK)
            && this.getExploredPositions().noneMatch(p_327000_ -> GlobalPos.of(this.level().dimension(), p_272757_).equals(p_327000_))
            && Optional.ofNullable(this.getNavigation().createPath(p_272757_, 1)).map(Path::canReach).orElse(false);
    }

    private void dropSeed() {
        if (!this.level().isClientSide() && this.entityData.get(DATA_DROP_SEED_AT_TICK) == this.tickCount) {
            ServerLevel serverlevel = (ServerLevel)this.level();
            LootTable loottable = serverlevel.getServer().m_323018_().m_321428_(BuiltInLootTables.SNIFFER_DIGGING);
            LootParams lootparams = new LootParams.Builder(serverlevel)
                .withParameter(LootContextParams.ORIGIN, this.getHeadPosition())
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .create(LootContextParamSets.GIFT);
            List<ItemStack> list = loottable.getRandomItems(lootparams);
            BlockPos blockpos = this.getHeadBlock();

            for (ItemStack itemstack : list) {
                ItemEntity itementity = new ItemEntity(
                    serverlevel, (double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ(), itemstack
                );
                itementity.setDefaultPickUpDelay();
                serverlevel.addFreshEntity(itementity);
            }

            this.playSound(SoundEvents.SNIFFER_DROP_SEED, 1.0F, 1.0F);
        }
    }

    private Sniffer emitDiggingParticles(AnimationState pAnimationState) {
        boolean flag = pAnimationState.getAccumulatedTime() > 1700L && pAnimationState.getAccumulatedTime() < 6000L;
        if (flag) {
            BlockPos blockpos = this.getHeadBlock();
            BlockState blockstate = this.level().getBlockState(blockpos.below());
            if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
                for (int i = 0; i < 30; i++) {
                    Vec3 vec3 = Vec3.atCenterOf(blockpos).add(0.0, -0.65F, 0.0);
                    this.level()
                        .addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockstate), vec3.x, vec3.y, vec3.z, 0.0, 0.0, 0.0);
                }

                if (this.tickCount % 10 == 0) {
                    this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), blockstate.getSoundType(level(), blockpos.below(), this).getHitSound(), this.getSoundSource(), 0.5F, 0.5F, false);
                }
            }
        }

        if (this.tickCount % 10 == 0) {
            this.level().m_322719_(GameEvent.ENTITY_ACTION, this.getHeadBlock(), GameEvent.Context.of(this));
        }

        return this;
    }

    private Sniffer storeExploredPosition(BlockPos pPos) {
        List<GlobalPos> list = this.getExploredPositions().limit(20L).collect(Collectors.toList());
        list.add(0, GlobalPos.of(this.level().dimension(), pPos));
        this.getBrain().setMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS, list);
        return this;
    }

    private Stream<GlobalPos> getExploredPositions() {
        return this.getBrain().getMemory(MemoryModuleType.SNIFFER_EXPLORED_POSITIONS).stream().flatMap(Collection::stream);
    }

    @Override
    protected void jumpFromGround() {
        super.jumpFromGround();
        double d0 = this.moveControl.getSpeedModifier();
        if (d0 > 0.0) {
            double d1 = this.getDeltaMovement().horizontalDistanceSqr();
            if (d1 < 0.01) {
                this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
            }
        }
    }

    @Override
    public void spawnChildFromBreeding(ServerLevel pLevel, Animal pMate) {
        ItemStack itemstack = new ItemStack(Items.SNIFFER_EGG);
        ItemEntity itementity = new ItemEntity(pLevel, this.position().x(), this.position().y(), this.position().z(), itemstack);
        itementity.setDefaultPickUpDelay();
        this.finalizeSpawnChildFromBreeding(pLevel, pMate, null);
        this.playSound(SoundEvents.SNIFFER_EGG_PLOP, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 0.5F);
        pLevel.addFreshEntity(itementity);
    }

    @Override
    public void die(DamageSource pDamageSource) {
        this.transitionTo(Sniffer.State.IDLING);
        super.die(pDamageSource);
    }

    @Override
    public void tick() {
        switch (this.getState()) {
            case SEARCHING:
                this.playSearchingSound();
                break;
            case DIGGING:
                this.emitDiggingParticles(this.diggingAnimationState).dropSeed();
        }

        super.tick();
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        boolean flag = this.isFood(itemstack);
        InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
        if (interactionresult.consumesAction() && flag) {
            this.level().playSound(null, this, this.getEatingSound(itemstack), SoundSource.NEUTRAL, 1.0F, Mth.randomBetween(this.level().random, 0.8F, 1.2F));
        }

        return interactionresult;
    }

    private void playSearchingSound() {
        if (this.level().isClientSide() && this.tickCount % 20 == 0) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.SNIFFER_SEARCHING, this.getSoundSource(), 1.0F, 1.0F, false);
        }
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        this.playSound(SoundEvents.SNIFFER_STEP, 0.15F, 1.0F);
    }

    @Override
    public SoundEvent getEatingSound(ItemStack pStack) {
        return SoundEvents.SNIFFER_EAT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return Set.of(Sniffer.State.DIGGING, Sniffer.State.SEARCHING).contains(this.getState()) ? null : SoundEvents.SNIFFER_IDLE;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.SNIFFER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SNIFFER_DEATH;
    }

    @Override
    public int getMaxHeadYRot() {
        return 50;
    }

    @Override
    public void setBaby(boolean pBaby) {
        this.setAge(pBaby ? -48000 : 0);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityType.SNIFFER.create(pLevel);
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (!(pOtherAnimal instanceof Sniffer sniffer)) {
            return false;
        } else {
            Set<Sniffer.State> set = Set.of(Sniffer.State.IDLING, Sniffer.State.SCENTING, Sniffer.State.FEELING_HAPPY);
            return set.contains(this.getState()) && set.contains(sniffer.getState()) && super.canMate(pOtherAnimal);
        }
    }

    @Override
    public AABB getBoundingBoxForCulling() {
        return super.getBoundingBoxForCulling().inflate(0.6F);
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(ItemTags.SNIFFER_FOOD);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return SnifferAi.makeBrain(this.brainProvider().makeBrain(pDynamic));
    }

    @Override
    public Brain<Sniffer> getBrain() {
        return (Brain<Sniffer>)super.getBrain();
    }

    @Override
    protected Brain.Provider<Sniffer> brainProvider() {
        return Brain.provider(SnifferAi.MEMORY_TYPES, SnifferAi.SENSOR_TYPES);
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("snifferBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().popPush("snifferActivityUpdate");
        SnifferAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    public static enum State {
        IDLING(0),
        FEELING_HAPPY(1),
        SCENTING(2),
        SNIFFING(3),
        SEARCHING(4),
        DIGGING(5),
        RISING(6);

        public static final IntFunction<Sniffer.State> f_316716_ = ByIdMap.continuous(Sniffer.State::m_324429_, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        public static final StreamCodec<ByteBuf, Sniffer.State> f_313997_ = ByteBufCodecs.m_321301_(f_316716_, Sniffer.State::m_324429_);
        private final int f_315359_;

        private State(final int p_328116_) {
            this.f_315359_ = p_328116_;
        }

        public int m_324429_() {
            return this.f_315359_;
        }
    }
}
