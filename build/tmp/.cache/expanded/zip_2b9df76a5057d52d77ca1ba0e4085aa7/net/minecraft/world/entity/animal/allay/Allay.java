package net.minecraft.world.entity.animal.allay;

import com.google.common.collect.ImmutableList;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.GameEventTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.EntityPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class Allay extends PathfinderMob implements InventoryCarrier, VibrationSystem {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Vec3i ITEM_PICKUP_REACH = new Vec3i(1, 1, 1);
    private static final int LIFTING_ITEM_ANIMATION_DURATION = 5;
    private static final float DANCING_LOOP_DURATION = 55.0F;
    private static final float SPINNING_ANIMATION_DURATION = 15.0F;
    private static final Ingredient DUPLICATION_ITEM = Ingredient.of(Items.AMETHYST_SHARD);
    private static final int DUPLICATION_COOLDOWN_TICKS = 6000;
    private static final int NUM_OF_DUPLICATION_HEARTS = 3;
    private static final EntityDataAccessor<Boolean> DATA_DANCING = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_CAN_DUPLICATE = SynchedEntityData.defineId(Allay.class, EntityDataSerializers.BOOLEAN);
    protected static final ImmutableList<SensorType<? extends Sensor<? super Allay>>> SENSOR_TYPES = ImmutableList.of(
        SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY, SensorType.NEAREST_ITEMS
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
        MemoryModuleType.PATH,
        MemoryModuleType.LOOK_TARGET,
        MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
        MemoryModuleType.WALK_TARGET,
        MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
        MemoryModuleType.HURT_BY,
        MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
        MemoryModuleType.LIKED_PLAYER,
        MemoryModuleType.LIKED_NOTEBLOCK_POSITION,
        MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS,
        MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
        MemoryModuleType.IS_PANICKING
    );
    public static final ImmutableList<Float> THROW_SOUND_PITCHES = ImmutableList.of(
        0.5625F, 0.625F, 0.75F, 0.9375F, 1.0F, 1.0F, 1.125F, 1.25F, 1.5F, 1.875F, 2.0F, 2.25F, 2.5F, 3.0F, 3.75F, 4.0F
    );
    private final DynamicGameEventListener<VibrationSystem.Listener> dynamicVibrationListener;
    private VibrationSystem.Data vibrationData;
    private final VibrationSystem.User vibrationUser;
    private final DynamicGameEventListener<Allay.JukeboxListener> dynamicJukeboxListener;
    private final SimpleContainer inventory = new SimpleContainer(1);
    @Nullable
    private BlockPos jukeboxPos;
    private long duplicationCooldown;
    private float holdingItemAnimationTicks;
    private float holdingItemAnimationTicks0;
    private float dancingAnimationTicks;
    private float spinningAnimationTicks;
    private float spinningAnimationTicks0;

    public Allay(EntityType<? extends Allay> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setCanPickUpLoot(this.canPickUpLoot());
        this.vibrationUser = new Allay.VibrationUser();
        this.vibrationData = new VibrationSystem.Data();
        this.dynamicVibrationListener = new DynamicGameEventListener<>(new VibrationSystem.Listener(this));
        this.dynamicJukeboxListener = new DynamicGameEventListener<>(new Allay.JukeboxListener(this.vibrationUser.getPositionSource(), GameEvent.JUKEBOX_PLAY.value().notificationRadius()));
    }

    @Override
    protected Brain.Provider<Allay> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> pDynamic) {
        return AllayAi.makeBrain(this.brainProvider().makeBrain(pDynamic));
    }

    @Override
    public Brain<Allay> getBrain() {
        return (Brain<Allay>)super.getBrain();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.FLYING_SPEED, 0.1F)
            .add(Attributes.MOVEMENT_SPEED, 0.1F)
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.FOLLOW_RANGE, 48.0);
    }

    @Override
    protected PathNavigation createNavigation(Level pLevel) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, pLevel);
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_332593_) {
        super.defineSynchedData(p_332593_);
        p_332593_.m_318949_(DATA_DANCING, false);
        p_332593_.m_318949_(DATA_CAN_DUPLICATE, true);
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isControlledByLocalInstance()) {
            if (this.isInWater()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.8F));
            } else if (this.isInLava()) {
                this.moveRelative(0.02F, pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.5));
            } else {
                this.moveRelative(this.getSpeed(), pTravelVector);
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.setDeltaMovement(this.getDeltaMovement().scale(0.91F));
            }
        }

        this.calculateEntityAnimation(false);
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (pSource.getEntity() instanceof Player player) {
            Optional<UUID> optional = this.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent() && player.getUUID().equals(optional.get())) {
                return false;
            }
        }

        return super.hurt(pSource, pAmount);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
    }

    @Override
    protected void checkFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos) {
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return this.hasItemInSlot(EquipmentSlot.MAINHAND) ? SoundEvents.ALLAY_AMBIENT_WITH_ITEM : SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ALLAY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ALLAY_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    protected void customServerAiStep() {
        this.level().getProfiler().push("allayBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("allayActivityUpdate");
        AllayAi.updateActivity(this);
        this.level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isAlive() && this.tickCount % 10 == 0) {
            this.heal(1.0F);
        }

        if (this.isDancing() && this.shouldStopDancing() && this.tickCount % 20 == 0) {
            this.setDancing(false);
            this.jukeboxPos = null;
        }

        this.updateDuplicationCooldown();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.holdingItemAnimationTicks0 = this.holdingItemAnimationTicks;
            if (this.hasItemInHand()) {
                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks + 1.0F, 0.0F, 5.0F);
            } else {
                this.holdingItemAnimationTicks = Mth.clamp(this.holdingItemAnimationTicks - 1.0F, 0.0F, 5.0F);
            }

            if (this.isDancing()) {
                this.dancingAnimationTicks++;
                this.spinningAnimationTicks0 = this.spinningAnimationTicks;
                if (this.isSpinning()) {
                    this.spinningAnimationTicks++;
                } else {
                    this.spinningAnimationTicks--;
                }

                this.spinningAnimationTicks = Mth.clamp(this.spinningAnimationTicks, 0.0F, 15.0F);
            } else {
                this.dancingAnimationTicks = 0.0F;
                this.spinningAnimationTicks = 0.0F;
                this.spinningAnimationTicks0 = 0.0F;
            }
        } else {
            VibrationSystem.Ticker.tick(this.level(), this.vibrationData, this.vibrationUser);
            if (this.isPanicking()) {
                this.setDancing(false);
            }
        }
    }

    @Override
    public boolean canPickUpLoot() {
        return !this.isOnPickupCooldown() && this.hasItemInHand();
    }

    public boolean hasItemInHand() {
        return !this.getItemInHand(InteractionHand.MAIN_HAND).isEmpty();
    }

    @Override
    public boolean canTakeItem(ItemStack pItemstack) {
        return false;
    }

    private boolean isOnPickupCooldown() {
        return this.getBrain().checkMemory(MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS, MemoryStatus.VALUE_PRESENT);
    }

    @Override
    protected InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        ItemStack itemstack1 = this.getItemInHand(InteractionHand.MAIN_HAND);
        if (this.isDancing() && this.isDuplicationItem(itemstack) && this.canDuplicate()) {
            this.duplicateAllay();
            this.level().broadcastEntityEvent(this, (byte)18);
            this.level().playSound(pPlayer, this, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.removeInteractionItem(pPlayer, itemstack);
            return InteractionResult.SUCCESS;
        } else if (itemstack1.isEmpty() && !itemstack.isEmpty()) {
            ItemStack itemstack3 = itemstack.copyWithCount(1);
            this.setItemInHand(InteractionHand.MAIN_HAND, itemstack3);
            this.removeInteractionItem(pPlayer, itemstack);
            this.level().playSound(pPlayer, this, SoundEvents.ALLAY_ITEM_GIVEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.getBrain().setMemory(MemoryModuleType.LIKED_PLAYER, pPlayer.getUUID());
            return InteractionResult.SUCCESS;
        } else if (!itemstack1.isEmpty() && pHand == InteractionHand.MAIN_HAND && itemstack.isEmpty()) {
            this.m_21035_(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            this.level().playSound(pPlayer, this, SoundEvents.ALLAY_ITEM_TAKEN, SoundSource.NEUTRAL, 2.0F, 1.0F);
            this.swing(InteractionHand.MAIN_HAND);

            for (ItemStack itemstack2 : this.getInventory().removeAllItems()) {
                BehaviorUtils.throwItem(this, itemstack2, this.position());
            }

            this.getBrain().eraseMemory(MemoryModuleType.LIKED_PLAYER);
            pPlayer.addItem(itemstack1);
            return InteractionResult.SUCCESS;
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    public void setJukeboxPlaying(BlockPos pJukeboxPos, boolean pJukeboxPlaying) {
        if (pJukeboxPlaying) {
            if (!this.isDancing()) {
                this.jukeboxPos = pJukeboxPos;
                this.setDancing(true);
            }
        } else if (pJukeboxPos.equals(this.jukeboxPos) || this.jukeboxPos == null) {
            this.jukeboxPos = null;
            this.setDancing(false);
        }
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    @Override
    protected Vec3i getPickupReach() {
        return ITEM_PICKUP_REACH;
    }

    @Override
    public boolean wantsToPickUp(ItemStack pStack) {
        ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
        return !itemstack.isEmpty() && this.inventory.canAddItem(pStack) && this.allayConsidersItemEqual(itemstack, pStack) && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level(), this);
    }

    private boolean allayConsidersItemEqual(ItemStack pFirst, ItemStack pSecond) {
        return ItemStack.isSameItem(pFirst, pSecond) && !this.hasNonMatchingPotion(pFirst, pSecond);
    }

    private boolean hasNonMatchingPotion(ItemStack pFirst, ItemStack pSecond) {
        PotionContents potioncontents = pFirst.m_323252_(DataComponents.f_314188_);
        PotionContents potioncontents1 = pSecond.m_323252_(DataComponents.f_314188_);
        return !Objects.equals(potioncontents, potioncontents1);
    }

    @Override
    protected void pickUpItem(ItemEntity pItemEntity) {
        InventoryCarrier.pickUpItem(this, this, pItemEntity);
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    public boolean isFlapping() {
        return !this.onGround();
    }

    @Override
    public void updateDynamicGameEventListener(BiConsumer<DynamicGameEventListener<?>, ServerLevel> pListenerConsumer) {
        if (this.level() instanceof ServerLevel serverlevel) {
            pListenerConsumer.accept(this.dynamicVibrationListener, serverlevel);
            pListenerConsumer.accept(this.dynamicJukeboxListener, serverlevel);
        }
    }

    public boolean isDancing() {
        return this.entityData.get(DATA_DANCING);
    }

    public void setDancing(boolean pDancing) {
        if (!this.level().isClientSide && this.isEffectiveAi() && (!pDancing || !this.isPanicking())) {
            this.entityData.set(DATA_DANCING, pDancing);
        }
    }

    private boolean shouldStopDancing() {
        return this.jukeboxPos == null
            || !this.jukeboxPos.closerToCenterThan(this.position(), (double)GameEvent.JUKEBOX_PLAY.value().notificationRadius())
            || !this.level().getBlockState(this.jukeboxPos).is(Blocks.JUKEBOX);
    }

    public float getHoldingItemAnimationProgress(float pPartialTick) {
        return Mth.lerp(pPartialTick, this.holdingItemAnimationTicks0, this.holdingItemAnimationTicks) / 5.0F;
    }

    public boolean isSpinning() {
        float f = this.dancingAnimationTicks % 55.0F;
        return f < 15.0F;
    }

    public float getSpinningProgress(float pPartialTick) {
        return Mth.lerp(pPartialTick, this.spinningAnimationTicks0, this.spinningAnimationTicks) / 15.0F;
    }

    @Override
    public boolean equipmentHasChanged(ItemStack pOldItem, ItemStack pNewItem) {
        return !this.allayConsidersItemEqual(pOldItem, pNewItem);
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        this.inventory.removeAllItems().forEach(this::spawnAtLocation);
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!itemstack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemstack)) {
            this.spawnAtLocation(itemstack);
            this.m_21035_(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean removeWhenFarAway(double pDistanceToClosestPlayer) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        this.writeInventoryToTag(pCompound, this.m_321891_());
        VibrationSystem.Data.CODEC
            .encodeStart(NbtOps.INSTANCE, this.vibrationData)
            .resultOrPartial(LOGGER::error)
            .ifPresent(p_218353_ -> pCompound.put("listener", p_218353_));
        pCompound.putLong("DuplicationCooldown", this.duplicationCooldown);
        pCompound.putBoolean("CanDuplicate", this.canDuplicate());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.readInventoryFromTag(pCompound, this.m_321891_());
        if (pCompound.contains("listener", 10)) {
            VibrationSystem.Data.CODEC
                .parse(new Dynamic<>(NbtOps.INSTANCE, pCompound.getCompound("listener")))
                .resultOrPartial(LOGGER::error)
                .ifPresent(p_281082_ -> this.vibrationData = p_281082_);
        }

        this.duplicationCooldown = (long)pCompound.getInt("DuplicationCooldown");
        this.entityData.set(DATA_CAN_DUPLICATE, pCompound.getBoolean("CanDuplicate"));
    }

    @Override
    protected boolean shouldStayCloseToLeashHolder() {
        return false;
    }

    private void updateDuplicationCooldown() {
        if (this.duplicationCooldown > 0L) {
            this.duplicationCooldown--;
        }

        if (!this.level().isClientSide() && this.duplicationCooldown == 0L && !this.canDuplicate()) {
            this.entityData.set(DATA_CAN_DUPLICATE, true);
        }
    }

    private boolean isDuplicationItem(ItemStack pStack) {
        return DUPLICATION_ITEM.test(pStack);
    }

    private void duplicateAllay() {
        Allay allay = EntityType.ALLAY.create(this.level());
        if (allay != null) {
            allay.moveTo(this.position());
            allay.setPersistenceRequired();
            allay.resetDuplicationCooldown();
            this.resetDuplicationCooldown();
            this.level().addFreshEntity(allay);
        }
    }

    private void resetDuplicationCooldown() {
        this.duplicationCooldown = 6000L;
        this.entityData.set(DATA_CAN_DUPLICATE, false);
    }

    private boolean canDuplicate() {
        return this.entityData.get(DATA_CAN_DUPLICATE);
    }

    private void removeInteractionItem(Player pPlayer, ItemStack pStack) {
        pStack.m_321439_(1, pPlayer);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, (double)this.getEyeHeight() * 0.6, (double)this.getBbWidth() * 0.1);
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 18) {
            for (int i = 0; i < 3; i++) {
                this.spawnHeartParticle();
            }
        } else {
            super.handleEntityEvent(pId);
        }
    }

    private void spawnHeartParticle() {
        double d0 = this.random.nextGaussian() * 0.02;
        double d1 = this.random.nextGaussian() * 0.02;
        double d2 = this.random.nextGaussian() * 0.02;
        this.level().addParticle(ParticleTypes.HEART, this.getRandomX(1.0), this.getRandomY() + 0.5, this.getRandomZ(1.0), d0, d1, d2);
    }

    @Override
    public VibrationSystem.Data getVibrationData() {
        return this.vibrationData;
    }

    @Override
    public VibrationSystem.User getVibrationUser() {
        return this.vibrationUser;
    }

    class JukeboxListener implements GameEventListener {
        private final PositionSource listenerSource;
        private final int listenerRadius;

        public JukeboxListener(final PositionSource pListenerSource, final int pListenerRadius) {
            this.listenerSource = pListenerSource;
            this.listenerRadius = pListenerRadius;
        }

        @Override
        public PositionSource getListenerSource() {
            return this.listenerSource;
        }

        @Override
        public int getListenerRadius() {
            return this.listenerRadius;
        }

        @Override
        public boolean handleGameEvent(ServerLevel pLevel, Holder<GameEvent> p_333132_, GameEvent.Context pContext, Vec3 pPos) {
            if (p_333132_.m_318604_(GameEvent.JUKEBOX_PLAY)) {
                Allay.this.setJukeboxPlaying(BlockPos.containing(pPos), true);
                return true;
            } else if (p_333132_.m_318604_(GameEvent.JUKEBOX_STOP_PLAY)) {
                Allay.this.setJukeboxPlaying(BlockPos.containing(pPos), false);
                return true;
            } else {
                return false;
            }
        }
    }

    class VibrationUser implements VibrationSystem.User {
        private static final int VIBRATION_EVENT_LISTENER_RANGE = 16;
        private final PositionSource positionSource = new EntityPositionSource(Allay.this, Allay.this.getEyeHeight());

        @Override
        public int getListenerRadius() {
            return 16;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public boolean canReceiveVibration(ServerLevel p_282038_, BlockPos p_283385_, Holder<GameEvent> p_334911_, GameEvent.Context p_282208_) {
            if (Allay.this.isNoAi()) {
                return false;
            } else {
                Optional<GlobalPos> optional = Allay.this.getBrain().getMemory(MemoryModuleType.LIKED_NOTEBLOCK_POSITION);
                if (optional.isEmpty()) {
                    return true;
                } else {
                    GlobalPos globalpos = optional.get();
                    return globalpos.dimension().equals(p_282038_.dimension()) && globalpos.pos().equals(p_283385_);
                }
            }
        }

        @Override
        public void onReceiveVibration(
            ServerLevel p_281422_, BlockPos p_281449_, Holder<GameEvent> p_333452_, @Nullable Entity p_281794_, @Nullable Entity p_281864_, float p_281642_
        ) {
            if (p_333452_.m_318604_(GameEvent.NOTE_BLOCK_PLAY)) {
                AllayAi.hearNoteblock(Allay.this, new BlockPos(p_281449_));
            }
        }

        @Override
        public TagKey<GameEvent> getListenableEvents() {
            return GameEventTags.ALLAY_CAN_LISTEN;
        }
    }
}
