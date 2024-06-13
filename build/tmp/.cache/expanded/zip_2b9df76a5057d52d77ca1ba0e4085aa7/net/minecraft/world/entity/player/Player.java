package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.WardenSpawnTracker;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;

public abstract class Player extends LivingEntity implements net.minecraftforge.common.extensions.IForgePlayer {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final HumanoidArm DEFAULT_MAIN_HAND = HumanoidArm.RIGHT;
    public static final int DEFAULT_MODEL_CUSTOMIZATION = 0;
    public static final int MAX_HEALTH = 20;
    public static final int SLEEP_DURATION = 100;
    public static final int WAKE_UP_DURATION = 10;
    public static final int ENDER_SLOT_OFFSET = 200;
    public static final int f_316671_ = 499;
    public static final int f_313973_ = 500;
    public static final float f_316011_ = 4.5F;
    public static final float f_315769_ = 3.0F;
    public static final float CROUCH_BB_HEIGHT = 1.5F;
    public static final float SWIMMING_BB_WIDTH = 0.6F;
    public static final float SWIMMING_BB_HEIGHT = 0.6F;
    public static final float DEFAULT_EYE_HEIGHT = 1.62F;
    public static final Vec3 f_315404_ = new Vec3(0.0, 0.6, 0.0);
    public static final EntityDimensions STANDING_DIMENSIONS = EntityDimensions.scalable(0.6F, 1.8F)
        .m_320568_(1.62F)
        .m_323271_(EntityAttachments.m_321590_().m_319738_(EntityAttachment.VEHICLE, f_315404_));
    private static final Map<Pose, EntityDimensions> POSES = ImmutableMap.<Pose, EntityDimensions>builder()
        .put(Pose.STANDING, STANDING_DIMENSIONS)
        .put(Pose.SLEEPING, SLEEPING_DIMENSIONS)
        .put(Pose.FALL_FLYING, EntityDimensions.scalable(0.6F, 0.6F).m_320568_(0.4F))
        .put(Pose.SWIMMING, EntityDimensions.scalable(0.6F, 0.6F).m_320568_(0.4F))
        .put(Pose.SPIN_ATTACK, EntityDimensions.scalable(0.6F, 0.6F).m_320568_(0.4F))
        .put(
            Pose.CROUCHING,
            EntityDimensions.scalable(0.6F, 1.5F).m_320568_(1.27F).m_323271_(EntityAttachments.m_321590_().m_319738_(EntityAttachment.VEHICLE, f_315404_))
        )
        .put(Pose.DYING, EntityDimensions.fixed(0.2F, 0.2F).m_320568_(1.62F))
        .build();
    private static final EntityDataAccessor<Float> DATA_PLAYER_ABSORPTION_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_SCORE_ID = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MODE_CUSTOMISATION = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<Byte> DATA_PLAYER_MAIN_HAND = SynchedEntityData.defineId(Player.class, EntityDataSerializers.BYTE);
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_LEFT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    protected static final EntityDataAccessor<CompoundTag> DATA_SHOULDER_RIGHT = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    private long timeEntitySatOnShoulder;
    final Inventory inventory = new Inventory(this);
    protected PlayerEnderChestContainer enderChestInventory = new PlayerEnderChestContainer();
    public final InventoryMenu inventoryMenu;
    public AbstractContainerMenu containerMenu;
    protected FoodData foodData = new FoodData();
    protected int jumpTriggerTime;
    public float oBob;
    public float bob;
    public int takeXpDelay;
    public double xCloakO;
    public double yCloakO;
    public double zCloakO;
    public double xCloak;
    public double yCloak;
    public double zCloak;
    private int sleepCounter;
    protected boolean wasUnderwater;
    private final Abilities abilities = new Abilities();
    public int experienceLevel;
    public int totalExperience;
    public float experienceProgress;
    protected int enchantmentSeed;
    protected final float defaultFlySpeed = 0.02F;
    private int lastLevelUpTime;
    private final GameProfile gameProfile;
    private boolean reducedDebugInfo;
    private ItemStack lastItemInMainHand = ItemStack.EMPTY;
    private final ItemCooldowns cooldowns = this.createItemCooldowns();
    private Optional<GlobalPos> lastDeathLocation = Optional.empty();
    @Nullable
    public FishingHook fishing;
    protected float hurtDir;
    @Nullable
    public Vec3 f_316171_;
    @Nullable
    public Entity f_314551_;
    public boolean f_315903_;
    private final java.util.Collection<MutableComponent> prefixes = new java.util.LinkedList<>();
    private final java.util.Collection<MutableComponent> suffixes = new java.util.LinkedList<>();
    @Nullable private Pose forcedPose;

    public Player(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(EntityType.PLAYER, pLevel);
        this.setUUID(pGameProfile.getId());
        this.gameProfile = pGameProfile;
        this.inventoryMenu = new InventoryMenu(this.inventory, !pLevel.isClientSide, this);
        this.containerMenu = this.inventoryMenu;
        this.moveTo((double)pPos.getX() + 0.5, (double)(pPos.getY() + 1), (double)pPos.getZ() + 0.5, pYRot, 0.0F);
        this.rotOffs = 180.0F;
    }

    public boolean blockActionRestricted(Level pLevel, BlockPos pPos, GameType pGameMode) {
        if (!pGameMode.isBlockPlacingRestricted()) {
            return false;
        } else if (pGameMode == GameType.SPECTATOR) {
            return true;
        } else if (this.mayBuild()) {
            return false;
        } else {
            ItemStack itemstack = this.getMainHandItem();
            return itemstack.isEmpty() || !itemstack.m_323082_(new BlockInWorld(pLevel, pPos, false));
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
            .add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.MOVEMENT_SPEED, 0.1F)
            .add(Attributes.ATTACK_SPEED)
            .add(Attributes.LUCK)
            .add(Attributes.f_316914_, 4.5)
            .add(Attributes.f_315802_, 3.0)
            .add(Attributes.f_314942_)
            .add(Attributes.ATTACK_KNOCKBACK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335298_) {
        super.defineSynchedData(p_335298_);
        p_335298_.m_318949_(DATA_PLAYER_ABSORPTION_ID, 0.0F);
        p_335298_.m_318949_(DATA_SCORE_ID, 0);
        p_335298_.m_318949_(DATA_PLAYER_MODE_CUSTOMISATION, (byte)0);
        p_335298_.m_318949_(DATA_PLAYER_MAIN_HAND, (byte)DEFAULT_MAIN_HAND.getId());
        p_335298_.m_318949_(DATA_SHOULDER_LEFT, new CompoundTag());
        p_335298_.m_318949_(DATA_SHOULDER_RIGHT, new CompoundTag());
    }

    @Override
    public void tick() {
        net.minecraftforge.event.ForgeEventFactory.onPlayerPreTick(this);
        this.noPhysics = this.isSpectator();
        if (this.isSpectator()) {
            this.setOnGround(false);
        }

        if (this.takeXpDelay > 0) {
            this.takeXpDelay--;
        }

        if (this.isSleeping()) {
            this.sleepCounter++;
            if (this.sleepCounter > 100) {
                this.sleepCounter = 100;
            }

            if (!this.level().isClientSide && !net.minecraftforge.event.ForgeEventFactory.onSleepingTimeCheck(this, getSleepingPos())) {
                this.stopSleepInBed(false, true);
            }
        } else if (this.sleepCounter > 0) {
            this.sleepCounter++;
            if (this.sleepCounter >= 110) {
                this.sleepCounter = 0;
            }
        }

        this.updateIsUnderwater();
        super.tick();
        if (!this.level().isClientSide && this.containerMenu != null && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }

        this.moveCloak();
        if (!this.level().isClientSide) {
            this.foodData.tick(this);
            this.awardStat(Stats.PLAY_TIME);
            this.awardStat(Stats.TOTAL_WORLD_TIME);
            if (this.isAlive()) {
                this.awardStat(Stats.TIME_SINCE_DEATH);
            }

            if (this.isDiscrete()) {
                this.awardStat(Stats.CROUCH_TIME);
            }

            if (!this.isSleeping()) {
                this.awardStat(Stats.TIME_SINCE_REST);
            }
        }

        int i = 29999999;
        double d0 = Mth.clamp(this.getX(), -2.9999999E7, 2.9999999E7);
        double d1 = Mth.clamp(this.getZ(), -2.9999999E7, 2.9999999E7);
        if (d0 != this.getX() || d1 != this.getZ()) {
            this.setPos(d0, this.getY(), d1);
        }

        this.attackStrengthTicker++;
        ItemStack itemstack = this.getMainHandItem();
        if (!ItemStack.matches(this.lastItemInMainHand, itemstack)) {
            if (!ItemStack.isSameItem(this.lastItemInMainHand, itemstack)) {
                this.resetAttackStrengthTicker();
            }

            this.lastItemInMainHand = itemstack.copy();
        }

        this.turtleHelmetTick();
        this.cooldowns.tick();
        this.updatePlayerPose();
        net.minecraftforge.event.ForgeEventFactory.onPlayerPostTick(this);
    }

    @Override
    protected float m_307017_() {
        return this.isBlocking() ? 15.0F : super.m_307017_();
    }

    public boolean isSecondaryUseActive() {
        return this.isShiftKeyDown();
    }

    protected boolean wantsToStopRiding() {
        return this.isShiftKeyDown();
    }

    protected boolean isStayingOnGroundSurface() {
        return this.isShiftKeyDown();
    }

    protected boolean updateIsUnderwater() {
        this.wasUnderwater = this.isEyeInFluid(FluidTags.WATER);
        return this.wasUnderwater;
    }

    private void turtleHelmetTick() {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.HEAD);
        if (itemstack.is(Items.TURTLE_HELMET) && !this.isEyeInFluid(FluidTags.WATER)) {
            this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200, 0, false, false, true));
        }
    }

    protected ItemCooldowns createItemCooldowns() {
        return new ItemCooldowns();
    }

    private void moveCloak() {
        this.xCloakO = this.xCloak;
        this.yCloakO = this.yCloak;
        this.zCloakO = this.zCloak;
        double d0 = this.getX() - this.xCloak;
        double d1 = this.getY() - this.yCloak;
        double d2 = this.getZ() - this.zCloak;
        double d3 = 10.0;
        if (d0 > 10.0) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 > 10.0) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 > 10.0) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        if (d0 < -10.0) {
            this.xCloak = this.getX();
            this.xCloakO = this.xCloak;
        }

        if (d2 < -10.0) {
            this.zCloak = this.getZ();
            this.zCloakO = this.zCloak;
        }

        if (d1 < -10.0) {
            this.yCloak = this.getY();
            this.yCloakO = this.yCloak;
        }

        this.xCloak += d0 * 0.25;
        this.zCloak += d2 * 0.25;
        this.yCloak += d1 * 0.25;
    }

    protected void updatePlayerPose() {
        if (forcedPose != null) {
            this.setPose(forcedPose);
            return;
        }
        if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.SWIMMING)) {
            Pose pose;
            if (this.isFallFlying()) {
                pose = Pose.FALL_FLYING;
            } else if (this.isSleeping()) {
                pose = Pose.SLEEPING;
            } else if (this.isSwimming()) {
                pose = Pose.SWIMMING;
            } else if (this.isAutoSpinAttack()) {
                pose = Pose.SPIN_ATTACK;
            } else if (this.isShiftKeyDown() && !this.abilities.flying) {
                pose = Pose.CROUCHING;
            } else {
                pose = Pose.STANDING;
            }

            Pose pose1;
            if (this.isSpectator() || this.isPassenger() || this.canPlayerFitWithinBlocksAndEntitiesWhen(pose)) {
                pose1 = pose;
            } else if (this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)) {
                pose1 = Pose.CROUCHING;
            } else {
                pose1 = Pose.SWIMMING;
            }

            this.setPose(pose1);
        }
    }

    protected boolean canPlayerFitWithinBlocksAndEntitiesWhen(Pose pPose) {
        return this.level().noCollision(this, this.getDimensions(pPose).makeBoundingBox(this.position()).deflate(1.0E-7));
    }

    @Override
    public int getPortalWaitTime() {
        return Math.max(1, this.level().getGameRules().getInt(this.abilities.invulnerable ? GameRules.f_303073_ : GameRules.f_302481_));
    }

    @Override
    protected SoundEvent getSwimSound() {
        return SoundEvents.PLAYER_SWIM;
    }

    @Override
    protected SoundEvent getSwimSplashSound() {
        return SoundEvents.PLAYER_SPLASH;
    }

    @Override
    protected SoundEvent getSwimHighSpeedSplashSound() {
        return SoundEvents.PLAYER_SPLASH_HIGH_SPEED;
    }

    @Override
    public int getDimensionChangingDelay() {
        return 10;
    }

    @Override
    public void playSound(SoundEvent pSound, float pVolume, float pPitch) {
        this.level().playSound(this, this.getX(), this.getY(), this.getZ(), pSound, this.getSoundSource(), pVolume, pPitch);
    }

    public void playNotifySound(SoundEvent pSound, SoundSource pSource, float pVolume, float pPitch) {
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.PLAYERS;
    }

    @Override
    protected int getFireImmuneTicks() {
        return 20;
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 9) {
            this.completeUsingItem();
        } else if (pId == 23) {
            this.reducedDebugInfo = false;
        } else if (pId == 22) {
            this.reducedDebugInfo = true;
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public void closeContainer() {
        this.containerMenu = this.inventoryMenu;
    }

    protected void doCloseContainer() {
    }

    @Override
    public void rideTick() {
        if (!this.level().isClientSide && this.wantsToStopRiding() && this.isPassenger()) {
            this.stopRiding();
            this.setShiftKeyDown(false);
        } else {
            super.rideTick();
            this.oBob = this.bob;
            this.bob = 0.0F;
        }
    }

    @Override
    protected void serverAiStep() {
        super.serverAiStep();
        this.updateSwingTime();
        this.yHeadRot = this.getYRot();
    }

    @Override
    public void aiStep() {
        if (this.jumpTriggerTime > 0) {
            this.jumpTriggerTime--;
        }

        if (this.level().getDifficulty() == Difficulty.PEACEFUL && this.level().getGameRules().getBoolean(GameRules.RULE_NATURAL_REGENERATION)) {
            if (this.getHealth() < this.getMaxHealth() && this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            if (this.foodData.needsFood() && this.tickCount % 10 == 0) {
                this.foodData.setFoodLevel(this.foodData.getFoodLevel() + 1);
            }
        }

        this.inventory.tick();
        this.oBob = this.bob;
        super.aiStep();
        this.setSpeed((float)this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        float f;
        if (this.onGround() && !this.isDeadOrDying() && !this.isSwimming()) {
            f = Math.min(0.1F, (float)this.getDeltaMovement().horizontalDistance());
        } else {
            f = 0.0F;
        }

        this.bob = this.bob + (f - this.bob) * 0.4F;
        if (this.getHealth() > 0.0F && !this.isSpectator()) {
            AABB aabb;
            if (this.isPassenger() && !this.getVehicle().isRemoved()) {
                aabb = this.getBoundingBox().minmax(this.getVehicle().getBoundingBox()).inflate(1.0, 0.0, 1.0);
            } else {
                aabb = this.getBoundingBox().inflate(1.0, 0.5, 1.0);
            }

            List<Entity> list = this.level().getEntities(this, aabb);
            List<Entity> list1 = Lists.newArrayList();

            for (Entity entity : list) {
                if (entity.getType() == EntityType.EXPERIENCE_ORB) {
                    list1.add(entity);
                } else if (!entity.isRemoved()) {
                    this.touch(entity);
                }
            }

            if (!list1.isEmpty()) {
                this.touch(Util.getRandom(list1, this.random));
            }
        }

        this.playShoulderEntityAmbientSound(this.getShoulderEntityLeft());
        this.playShoulderEntityAmbientSound(this.getShoulderEntityRight());
        if (!this.level().isClientSide && (this.fallDistance > 0.5F || this.isInWater()) || this.abilities.flying || this.isSleeping() || this.isInPowderSnow) {
            this.removeEntitiesOnShoulder();
        }
    }

    private void playShoulderEntityAmbientSound(@Nullable CompoundTag pEntityCompound) {
        if (pEntityCompound != null && (!pEntityCompound.contains("Silent") || !pEntityCompound.getBoolean("Silent")) && this.level().random.nextInt(200) == 0) {
            String s = pEntityCompound.getString("id");
            EntityType.byString(s)
                .filter(p_36280_ -> p_36280_ == EntityType.PARROT)
                .ifPresent(
                    p_327054_ -> {
                        if (!Parrot.imitateNearbyMobs(this.level(), this)) {
                            this.level()
                                .playSound(
                                    null,
                                    this.getX(),
                                    this.getY(),
                                    this.getZ(),
                                    Parrot.getAmbient(this.level(), this.level().random),
                                    this.getSoundSource(),
                                    1.0F,
                                    Parrot.getPitch(this.level().random)
                                );
                        }
                    }
                );
        }
    }

    private void touch(Entity pEntity) {
        pEntity.playerTouch(this);
    }

    public int getScore() {
        return this.entityData.get(DATA_SCORE_ID);
    }

    public void setScore(int pScore) {
        this.entityData.set(DATA_SCORE_ID, pScore);
    }

    public void increaseScore(int pScore) {
        int i = this.getScore();
        this.entityData.set(DATA_SCORE_ID, i + pScore);
    }

    public void startAutoSpinAttack(int pAttackTicks) {
        this.autoSpinAttackTicks = pAttackTicks;
        if (!this.level().isClientSide) {
            this.removeEntitiesOnShoulder();
            this.setLivingEntityFlag(4, true);
        }
    }

    @Override
    public void die(DamageSource pCause) {
        if (net.minecraftforge.event.ForgeEventFactory.onLivingDeath(this, pCause)) return;
        super.die(pCause);
        this.reapplyPosition();
        if (!this.isSpectator()) {
            this.dropAllDeathLoot(pCause);
        }

        if (pCause != null) {
            this.setDeltaMovement(
                (double)(-Mth.cos((this.getHurtDir() + this.getYRot()) * (float) (Math.PI / 180.0)) * 0.1F),
                0.1F,
                (double)(-Mth.sin((this.getHurtDir() + this.getYRot()) * (float) (Math.PI / 180.0)) * 0.1F)
            );
        } else {
            this.setDeltaMovement(0.0, 0.1, 0.0);
        }

        this.awardStat(Stats.DEATHS);
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_DEATH));
        this.resetStat(Stats.CUSTOM.get(Stats.TIME_SINCE_REST));
        this.clearFire();
        this.setSharedFlagOnFire(false);
        this.setLastDeathLocation(Optional.of(GlobalPos.of(this.level().dimension(), this.blockPosition())));
    }

    @Override
    protected void dropEquipment() {
        super.dropEquipment();
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            this.destroyVanishingCursedItems();
            this.inventory.dropAll();
        }
    }

    protected void destroyVanishingCursedItems() {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
            ItemStack itemstack = this.inventory.getItem(i);
            if (!itemstack.isEmpty() && EnchantmentHelper.hasVanishingCurse(itemstack)) {
                this.inventory.removeItemNoUpdate(i);
            }
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return pDamageSource.type().effects().sound();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }

    @Nullable
    public ItemEntity drop(ItemStack pItemStack, boolean pIncludeThrowerName) {
        return net.minecraftforge.common.ForgeHooks.onPlayerTossEvent(this, pItemStack, pIncludeThrowerName);
    }

    @Nullable
    public ItemEntity drop(ItemStack pDroppedItem, boolean pDropAround, boolean pIncludeThrowerName) {
        if (pDroppedItem.isEmpty()) {
            return null;
        } else {
            if (this.level().isClientSide) {
                this.swing(InteractionHand.MAIN_HAND);
            }

            double d0 = this.getEyeY() - 0.3F;
            ItemEntity itementity = new ItemEntity(this.level(), this.getX(), d0, this.getZ(), pDroppedItem);
            itementity.setPickUpDelay(40);
            if (pIncludeThrowerName) {
                itementity.setThrower(this);
            }

            if (pDropAround) {
                float f = this.random.nextFloat() * 0.5F;
                float f1 = this.random.nextFloat() * (float) (Math.PI * 2);
                itementity.setDeltaMovement((double)(-Mth.sin(f1) * f), 0.2F, (double)(Mth.cos(f1) * f));
            } else {
                float f7 = 0.3F;
                float f8 = Mth.sin(this.getXRot() * (float) (Math.PI / 180.0));
                float f2 = Mth.cos(this.getXRot() * (float) (Math.PI / 180.0));
                float f3 = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
                float f4 = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
                float f5 = this.random.nextFloat() * (float) (Math.PI * 2);
                float f6 = 0.02F * this.random.nextFloat();
                itementity.setDeltaMovement(
                    (double)(-f3 * f2 * 0.3F) + Math.cos((double)f5) * (double)f6,
                    (double)(-f8 * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
                    (double)(f4 * f2 * 0.3F) + Math.sin((double)f5) * (double)f6
                );
            }

            return itementity;
        }
    }

    /** @deprecated Use {@link #getDestroySpeed(BlockState,BlockPos) */
    public float getDestroySpeed(BlockState pState) {
        return getDestroySpeed(pState, null);
    }

    public float getDestroySpeed(BlockState pState, @Nullable BlockPos pos) {
        float f = this.inventory.getDestroySpeed(pState);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(this);
            ItemStack itemstack = this.getMainHandItem();
            if (i > 0 && !itemstack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(this)) {
            f *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            f *= switch (this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };
        }

        f *= (float)this.getAttributeValue(Attributes.f_314942_);
        if (this.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
            f /= 5.0F;
        }

        if (!this.onGround()) {
            f /= 5.0F;
        }

        f = net.minecraftforge.event.ForgeEventFactory.getBreakSpeed(this, pState, f, pos);

        return f;
    }

    public boolean hasCorrectToolForDrops(BlockState pState) {
        var vanilla = !pState.requiresCorrectToolForDrops() || this.inventory.getSelected().isCorrectToolForDrops(pState);
        return net.minecraftforge.event.ForgeEventFactory.doPlayerHarvestCheck(this, pState, vanilla);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setUUID(this.gameProfile.getId());
        ListTag listtag = pCompound.getList("Inventory", 10);
        this.inventory.load(listtag);
        this.inventory.selected = pCompound.getInt("SelectedItemSlot");
        this.sleepCounter = pCompound.getShort("SleepTimer");
        this.experienceProgress = pCompound.getFloat("XpP");
        this.experienceLevel = pCompound.getInt("XpLevel");
        this.totalExperience = pCompound.getInt("XpTotal");
        this.enchantmentSeed = pCompound.getInt("XpSeed");
        if (this.enchantmentSeed == 0) {
            this.enchantmentSeed = this.random.nextInt();
        }

        this.setScore(pCompound.getInt("Score"));
        this.foodData.readAdditionalSaveData(pCompound);
        this.abilities.loadSaveData(pCompound);
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double)this.abilities.getWalkingSpeed());
        if (pCompound.contains("EnderItems", 9)) {
            this.enderChestInventory.fromTag(pCompound.getList("EnderItems", 10), this.m_321891_());
        }

        if (pCompound.contains("ShoulderEntityLeft", 10)) {
            this.setShoulderEntityLeft(pCompound.getCompound("ShoulderEntityLeft"));
        }

        if (pCompound.contains("ShoulderEntityRight", 10)) {
            this.setShoulderEntityRight(pCompound.getCompound("ShoulderEntityRight"));
        }

        if (pCompound.contains("LastDeathLocation", 10)) {
            this.setLastDeathLocation(GlobalPos.CODEC.parse(NbtOps.INSTANCE, pCompound.get("LastDeathLocation")).resultOrPartial(LOGGER::error));
        }

        if (pCompound.contains("current_explosion_impact_pos", 9)) {
            Vec3.CODEC
                .parse(NbtOps.INSTANCE, pCompound.get("current_explosion_impact_pos"))
                .resultOrPartial(LOGGER::error)
                .ifPresent(p_327052_ -> this.f_316171_ = p_327052_);
        }

        this.f_315903_ = pCompound.getBoolean("ignore_fall_damage_from_current_explosion");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        NbtUtils.addCurrentDataVersion(pCompound);
        pCompound.put("Inventory", this.inventory.save(new ListTag()));
        pCompound.putInt("SelectedItemSlot", this.inventory.selected);
        pCompound.putShort("SleepTimer", (short)this.sleepCounter);
        pCompound.putFloat("XpP", this.experienceProgress);
        pCompound.putInt("XpLevel", this.experienceLevel);
        pCompound.putInt("XpTotal", this.totalExperience);
        pCompound.putInt("XpSeed", this.enchantmentSeed);
        pCompound.putInt("Score", this.getScore());
        this.foodData.addAdditionalSaveData(pCompound);
        this.abilities.addSaveData(pCompound);
        pCompound.put("EnderItems", this.enderChestInventory.createTag(this.m_321891_()));
        if (!this.getShoulderEntityLeft().isEmpty()) {
            pCompound.put("ShoulderEntityLeft", this.getShoulderEntityLeft());
        }

        if (!this.getShoulderEntityRight().isEmpty()) {
            pCompound.put("ShoulderEntityRight", this.getShoulderEntityRight());
        }

        this.getLastDeathLocation()
            .flatMap(p_327055_ -> GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, p_327055_).resultOrPartial(LOGGER::error))
            .ifPresent(p_219756_ -> pCompound.put("LastDeathLocation", p_219756_));
        if (this.f_316171_ != null) {
            pCompound.put("current_explosion_impact_pos", Vec3.CODEC.encodeStart(NbtOps.INSTANCE, this.f_316171_).getOrThrow());
        }

        pCompound.putBoolean("ignore_fall_damage_from_current_explosion", this.f_315903_);
    }

    @Override
    public boolean isInvulnerableTo(DamageSource pSource) {
        if (super.isInvulnerableTo(pSource)) {
            return true;
        } else if (pSource.is(DamageTypeTags.IS_DROWNING)) {
            return !this.level().getGameRules().getBoolean(GameRules.RULE_DROWNING_DAMAGE);
        } else if (pSource.is(DamageTypeTags.IS_FALL)) {
            return !this.level().getGameRules().getBoolean(GameRules.RULE_FALL_DAMAGE);
        } else if (pSource.is(DamageTypeTags.IS_FIRE)) {
            return !this.level().getGameRules().getBoolean(GameRules.RULE_FIRE_DAMAGE);
        } else {
            return pSource.is(DamageTypeTags.IS_FREEZING) ? !this.level().getGameRules().getBoolean(GameRules.RULE_FREEZE_DAMAGE) : false;
        }
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttack(this, pSource, pAmount)) return false;
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else if (this.abilities.invulnerable && !pSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return false;
        } else {
            this.noActionTime = 0;
            if (this.isDeadOrDying()) {
                return false;
            } else {
                if (!this.level().isClientSide) {
                    this.removeEntitiesOnShoulder();
                }

                if (pSource.scalesWithDifficulty()) {
                    if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
                        pAmount = 0.0F;
                    }

                    if (this.level().getDifficulty() == Difficulty.EASY) {
                        pAmount = Math.min(pAmount / 2.0F + 1.0F, pAmount);
                    }

                    if (this.level().getDifficulty() == Difficulty.HARD) {
                        pAmount = pAmount * 3.0F / 2.0F;
                    }
                }

                return pAmount == 0.0F ? false : super.hurt(pSource, pAmount);
            }
        }
    }

    @Override
    protected void blockUsingShield(LivingEntity pEntity) {
        super.blockUsingShield(pEntity);
        if (pEntity.getMainHandItem().canDisableShield(this.useItem, this, pEntity)) {
            this.disableShield();
        }
    }

    @Override
    public boolean canBeSeenAsEnemy() {
        return !this.getAbilities().invulnerable && super.canBeSeenAsEnemy();
    }

    public boolean canHarmPlayer(Player pOther) {
        Team team = this.getTeam();
        Team team1 = pOther.getTeam();
        if (team == null) {
            return true;
        } else {
            return !team.isAlliedTo(team1) ? true : team.isAllowFriendlyFire();
        }
    }

    @Override
    protected void hurtArmor(DamageSource pDamageSource, float pDamage) {
        this.m_318635_(pDamageSource, pDamage, new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD});
    }

    @Override
    protected void hurtHelmet(DamageSource pDamageSource, float pDamageAmount) {
        this.m_318635_(pDamageSource, pDamageAmount, new EquipmentSlot[]{EquipmentSlot.HEAD});
    }

    @Override
    protected void hurtCurrentlyUsedShield(float pDamage) {
        if (this.useItem.canPerformAction(net.minecraftforge.common.ToolActions.SHIELD_BLOCK)) {
            if (!this.level().isClientSide) {
                this.awardStat(Stats.ITEM_USED.get(this.useItem.getItem()));
            }

            if (pDamage >= 3.0F) {
                int i = 1 + Mth.floor(pDamage);
                InteractionHand interactionhand = this.getUsedItemHand();
                this.useItem.hurtAndBreak(i, this, m_322775_(interactionhand));
                if (this.useItem.isEmpty()) {
                    if (interactionhand == InteractionHand.MAIN_HAND) {
                        this.m_21035_(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    } else {
                        this.m_21035_(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                    }

                    this.useItem = ItemStack.EMPTY;
                    this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
                }
            }
        }
    }

    @Override
    protected void actuallyHurt(DamageSource pDamageSrc, float pDamageAmount) {
        if (!this.isInvulnerableTo(pDamageSrc)) {
            pDamageAmount = net.minecraftforge.common.ForgeHooks.onLivingHurt(this, pDamageSrc, pDamageAmount);
            if (pDamageAmount <= 0) return;
            pDamageAmount = this.getDamageAfterArmorAbsorb(pDamageSrc, pDamageAmount);
            pDamageAmount = this.getDamageAfterMagicAbsorb(pDamageSrc, pDamageAmount);
            float f1 = Math.max(pDamageAmount - this.getAbsorptionAmount(), 0.0F);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (pDamageAmount - f1));
            f1 = net.minecraftforge.common.ForgeHooks.onLivingDamage(this, pDamageSrc, f1);
            float f = pDamageAmount - f1;
            if (f > 0.0F && f < 3.4028235E37F) {
                this.awardStat(Stats.DAMAGE_ABSORBED, Math.round(f * 10.0F));
            }

            if (f1 != 0.0F) {
                this.causeFoodExhaustion(pDamageSrc.getFoodExhaustion());
                this.getCombatTracker().recordDamage(pDamageSrc, f1);
                this.setHealth(this.getHealth() - f1);
                if (f1 < 3.4028235E37F) {
                    this.awardStat(Stats.DAMAGE_TAKEN, Math.round(f1 * 10.0F));
                }

                this.gameEvent(GameEvent.ENTITY_DAMAGE);
            }
        }
    }

    @Override
    protected boolean onSoulSpeedBlock() {
        return !this.abilities.flying && super.onSoulSpeedBlock();
    }

    public boolean isTextFilteringEnabled() {
        return false;
    }

    public void openTextEdit(SignBlockEntity pSignEntity, boolean pIsFrontText) {
    }

    public void openMinecartCommandBlock(BaseCommandBlock pCommandEntity) {
    }

    public void openCommandBlock(CommandBlockEntity pCommandBlockEntity) {
    }

    public void openStructureBlock(StructureBlockEntity pStructureEntity) {
    }

    public void openJigsawBlock(JigsawBlockEntity pJigsawBlockEntity) {
    }

    public void openHorseInventory(AbstractHorse pHorse, Container pInventory) {
    }

    public OptionalInt openMenu(@Nullable MenuProvider pMenu) {
        return OptionalInt.empty();
    }

    public void sendMerchantOffers(int pContainerId, MerchantOffers pOffers, int pVillagerLevel, int pVillagerXp, boolean pShowProgress, boolean pCanRestock) {
    }

    public void openItemGui(ItemStack pStack, InteractionHand pHand) {
    }

    public InteractionResult interactOn(Entity pEntityToInteractOn, InteractionHand pHand) {
        if (this.isSpectator()) {
            if (pEntityToInteractOn instanceof MenuProvider) {
                this.openMenu((MenuProvider)pEntityToInteractOn);
            }

            return InteractionResult.PASS;
        } else {
            var event = net.minecraftforge.event.ForgeEventFactory.onEntityInteract(this, pEntityToInteractOn, pHand);
            if (event.isCanceled()) {
                return event.getCancellationResult();
            }
            ItemStack itemstack = this.getItemInHand(pHand);
            ItemStack itemstack1 = itemstack.copy();
            InteractionResult interactionresult = pEntityToInteractOn.interact(this, pHand);
            if (interactionresult.consumesAction()) {
                if (this.abilities.instabuild && itemstack == this.getItemInHand(pHand) && itemstack.getCount() < itemstack1.getCount()) {
                    itemstack.setCount(itemstack1.getCount());
                }

                if (!this.abilities.instabuild && itemstack.isEmpty()) {
                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, itemstack1, m_322775_(pHand));
                }

                return interactionresult;
            } else {
                if (!itemstack.isEmpty() && pEntityToInteractOn instanceof LivingEntity) {
                    if (this.abilities.instabuild) {
                        itemstack = itemstack1;
                    }

                    InteractionResult interactionresult1 = itemstack.interactLivingEntity(this, (LivingEntity)pEntityToInteractOn, pHand);
                    if (interactionresult1.consumesAction()) {
                        this.level().gameEvent(GameEvent.ENTITY_INTERACT, pEntityToInteractOn.position(), GameEvent.Context.of(this));
                        if (itemstack.isEmpty() && !this.abilities.instabuild) {
                            net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, itemstack1, m_322775_(pHand));
                            this.setItemInHand(pHand, ItemStack.EMPTY);
                        }

                        return interactionresult1;
                    }
                }

                return InteractionResult.PASS;
            }
        }
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.boardingCooldown = 0;
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.isSleeping();
    }

    @Override
    public boolean isAffectedByFluids() {
        return !this.abilities.flying;
    }

    @Override
    protected Vec3 maybeBackOffFromEdge(Vec3 pVec, MoverType pMover) {
        float f = this.maxUpStep();
        if (!this.abilities.flying
            && !(pVec.y > 0.0)
            && (pMover == MoverType.SELF || pMover == MoverType.PLAYER)
            && this.isStayingOnGroundSurface()
            && this.isAboveGround(f)) {
            double d0 = pVec.x;
            double d1 = pVec.z;
            double d2 = 0.05;
            double d3 = Math.signum(d0) * 0.05;

            double d4;
            for (d4 = Math.signum(d1) * 0.05; d0 != 0.0 && this.m_323310_(d0, 0.0, f); d0 -= d3) {
                if (Math.abs(d0) <= 0.05) {
                    d0 = 0.0;
                    break;
                }
            }

            while (d1 != 0.0 && this.m_323310_(0.0, d1, f)) {
                if (Math.abs(d1) <= 0.05) {
                    d1 = 0.0;
                    break;
                }

                d1 -= d4;
            }

            while (d0 != 0.0 && d1 != 0.0 && this.m_323310_(d0, d1, f)) {
                if (Math.abs(d0) <= 0.05) {
                    d0 = 0.0;
                } else {
                    d0 -= d3;
                }

                if (Math.abs(d1) <= 0.05) {
                    d1 = 0.0;
                } else {
                    d1 -= d4;
                }
            }

            return new Vec3(d0, pVec.y, d1);
        } else {
            return pVec;
        }
    }

    private boolean isAboveGround(float p_328745_) {
        return this.onGround() || this.fallDistance < p_328745_ && !this.m_323310_(0.0, 0.0, p_328745_ - this.fallDistance);
    }

    private boolean m_323310_(double p_333341_, double p_331138_, float p_333865_) {
        AABB aabb = this.getBoundingBox();
        return this.level()
            .noCollision(
                this,
                new AABB(
                    aabb.minX + p_333341_,
                    aabb.minY - (double)p_333865_ - 1.0E-5F,
                    aabb.minZ + p_331138_,
                    aabb.maxX + p_333341_,
                    aabb.minY,
                    aabb.maxZ + p_331138_
                )
            );
    }

    public void attack(Entity pTarget) {
        if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(this, pTarget)) return;
        if (pTarget.isAttackable()) {
            if (!pTarget.skipAttackInteraction(this)) {
                float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                float f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), pTarget.getType());
                float f2 = this.getAttackStrengthScale(0.5F);
                f *= 0.2F + f2 * f2 * 0.8F;
                f1 *= f2;
                if (pTarget.getType().is(EntityTypeTags.f_314896_) && pTarget instanceof Projectile projectile) {
                    projectile.m_318938_(ProjectileDeflection.f_314043_, this, this, true);
                } else {
                    if (f > 0.0F || f1 > 0.0F) {
                        boolean flag = f2 > 0.9F;
                        boolean flag1 = false;
                        float i = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK); // Forge: Initialize this value to the attack knockback attribute of the player, which is by default 0
                        i += EnchantmentHelper.getKnockbackBonus(this);
                        if (this.isSprinting() && flag) {
                            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                            i++;
                            flag1 = true;
                        }

                        f += this.getItemInHand(InteractionHand.MAIN_HAND).getItem().m_319585_(this, f);
                        boolean flag2 = flag
                            && this.fallDistance > 0.0F
                            && !this.onGround()
                            && !this.onClimbable()
                            && !this.isInWater()
                            && !this.hasEffect(MobEffects.BLINDNESS)
                            && !this.isPassenger()
                            && pTarget instanceof LivingEntity
                            && !this.isSprinting();
                        var hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(this, pTarget, flag2, flag2 ? 1.5F : 1.0F);
                        flag2 = hitResult != null;
                        if (flag2) {
                            f *= hitResult.getDamageModifier();
                        }

                        f += f1;
                        boolean flag3 = false;
                        double d0 = (double)(this.walkDist - this.walkDistO);
                        if (flag && !flag2 && !flag1 && this.onGround() && d0 < (double)this.getSpeed()) {
                            ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
                            if (itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SWORD_SWEEP)) {
                                flag3 = true;
                            }
                        }

                        float f4 = 0.0F;
                        boolean flag4 = false;
                        int j = EnchantmentHelper.getFireAspect(this);
                        if (pTarget instanceof LivingEntity) {
                            f4 = ((LivingEntity)pTarget).getHealth();
                            if (j > 0 && !pTarget.isOnFire()) {
                                flag4 = true;
                                pTarget.m_322706_(1);
                            }
                        }

                        Vec3 vec3 = pTarget.getDeltaMovement();
                        boolean flag5 = pTarget.hurt(this.damageSources().playerAttack(this), f);
                        if (flag5) {
                            if (i > 0) {
                                if (pTarget instanceof LivingEntity) {
                                    ((LivingEntity)pTarget)
                                        .knockback(
                                            (double)((float)i * 0.5F),
                                            (double)Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                                            (double)(-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)))
                                        );
                                } else {
                                    pTarget.push(
                                        (double)(-Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)) * (float)i * 0.5F),
                                        0.1,
                                        (double)(Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)) * (float)i * 0.5F)
                                    );
                                }

                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
                                this.setSprinting(false);
                            }

                            if (flag3) {
                                float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;

                                for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(this, pTarget))) {
                                    if (livingentity != this
                                        && livingentity != pTarget
                                        && !this.isAlliedTo(livingentity)
                                        && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker())
                                        && this.distanceToSqr(livingentity) < 9.0) {
                                        livingentity.knockback(
                                            0.4F,
                                            (double)Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)),
                                            (double)(-Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)))
                                        );
                                        livingentity.hurt(this.damageSources().playerAttack(this), f3);
                                    }
                                }

                                this.level()
                                    .playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                                this.sweepAttack();
                            }

                            if (pTarget instanceof ServerPlayer && pTarget.hurtMarked) {
                                ((ServerPlayer)pTarget).connection.send(new ClientboundSetEntityMotionPacket(pTarget));
                                pTarget.hurtMarked = false;
                                pTarget.setDeltaMovement(vec3);
                            }

                            if (flag2) {
                                this.level()
                                    .playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                                this.crit(pTarget);
                            }

                            if (!flag2 && !flag3) {
                                if (flag) {
                                    this.level()
                                        .playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                                } else {
                                    this.level()
                                        .playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                                }
                            }

                            if (f1 > 0.0F) {
                                this.magicCrit(pTarget);
                            }

                            this.setLastHurtMob(pTarget);
                            if (pTarget instanceof LivingEntity) {
                                EnchantmentHelper.doPostHurtEffects((LivingEntity)pTarget, this);
                            }

                            EnchantmentHelper.doPostDamageEffects(this, pTarget);
                            ItemStack itemstack1 = this.getMainHandItem();
                            Entity entity = pTarget;
                            if (pTarget instanceof net.minecraftforge.entity.PartEntity<?> part) {
                                entity = part.getParent();
                            }

                            if (!this.level().isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                                ItemStack copy = itemstack1.copy();
                                itemstack1.hurtEnemy((LivingEntity)entity, this);
                                if (itemstack1.isEmpty()) {
                                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, copy, InteractionHand.MAIN_HAND);
                                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                                }
                            }

                            if (pTarget instanceof LivingEntity) {
                                float f5 = f4 - ((LivingEntity)pTarget).getHealth();
                                this.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                                if (j > 0) {
                                    pTarget.m_322706_(j * 4);
                                }

                                if (this.level() instanceof ServerLevel && f5 > 2.0F) {
                                    int k = (int)((double)f5 * 0.5);
                                    ((ServerLevel)this.level())
                                        .sendParticles(
                                            ParticleTypes.DAMAGE_INDICATOR, pTarget.getX(), pTarget.getY(0.5), pTarget.getZ(), k, 0.1, 0.0, 0.1, 0.2
                                        );
                                }
                            }

                            this.causeFoodExhaustion(0.1F);
                        } else {
                            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                            if (flag4) {
                                pTarget.clearFire();
                            }
                        }
                    }
                }
                this.resetAttackStrengthTicker(); // FORGE: Moved from beginning of attack() so that getAttackStrengthScale() returns an accurate value during all attack events
            }
        }
    }

    @Override
    protected void doAutoAttackOnTouch(LivingEntity pTarget) {
        this.attack(pTarget);
    }

    public void disableShield() {
        this.getCooldowns().addCooldown(this.getUseItem().getItem(), 100);
        this.stopUsingItem();
        this.level().broadcastEntityEvent(this, (byte)30);
    }

    public void crit(Entity pEntityHit) {
    }

    public void magicCrit(Entity pEntityHit) {
    }

    public void sweepAttack() {
        double d0 = (double)(-Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)));
        double d1 = (double)Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + d0, this.getY(0.5), this.getZ() + d1, 0, d0, 0.0, d1, 0.0);
        }
    }

    public void respawn() {
    }

    @Override
    public void remove(Entity.RemovalReason pReason) {
        super.remove(pReason);
        this.inventoryMenu.removed(this);
        if (this.containerMenu != null && this.hasContainerOpen()) {
            this.doCloseContainer();
        }
    }

    public boolean isLocalPlayer() {
        return false;
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Abilities getAbilities() {
        return this.abilities;
    }

    @Override
    public boolean m_322042_() {
        return this.abilities.instabuild;
    }

    public void updateTutorialInventoryAction(ItemStack pCarried, ItemStack pClicked, ClickAction pAction) {
    }

    public boolean hasContainerOpen() {
        return this.containerMenu != this.inventoryMenu;
    }

    public Either<Player.BedSleepingProblem, Unit> startSleepInBed(BlockPos pBedPos) {
        this.startSleeping(pBedPos);
        this.sleepCounter = 0;
        return Either.right(Unit.INSTANCE);
    }

    public void stopSleepInBed(boolean pWakeImmediately, boolean pUpdateLevelForSleepingPlayers) {
        net.minecraftforge.event.ForgeEventFactory.onPlayerWakeup(this, pWakeImmediately, pUpdateLevelForSleepingPlayers);
        super.stopSleeping();
        if (this.level() instanceof ServerLevel && pUpdateLevelForSleepingPlayers) {
            ((ServerLevel)this.level()).updateSleepingPlayerList();
        }

        this.sleepCounter = pWakeImmediately ? 0 : 100;
    }

    @Override
    public void stopSleeping() {
        this.stopSleepInBed(true, true);
    }

    public static Optional<Vec3> findRespawnPositionAndUseSpawnBlock(ServerLevel pServerLevel, BlockPos pSpawnBlockPos, float pPlayerOrientation, boolean pIsRespawnForced, boolean pRespawnAfterWinningTheGame) {
        BlockState blockstate = pServerLevel.getBlockState(pSpawnBlockPos);
        Block block = blockstate.getBlock();
        if (block instanceof RespawnAnchorBlock && (pIsRespawnForced || blockstate.getValue(RespawnAnchorBlock.CHARGE) > 0) && RespawnAnchorBlock.canSetSpawn(pServerLevel)) {
            Optional<Vec3> optional = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, pServerLevel, pSpawnBlockPos);
            if (!pIsRespawnForced && !pRespawnAfterWinningTheGame && optional.isPresent()) {
                pServerLevel.setBlock(
                    pSpawnBlockPos, blockstate.setValue(RespawnAnchorBlock.CHARGE, Integer.valueOf(blockstate.getValue(RespawnAnchorBlock.CHARGE) - 1)), 3
                );
            }

            return optional;
        } else if (block instanceof BedBlock && BedBlock.canSetSpawn(pServerLevel)) {
            return BedBlock.findStandUpPosition(EntityType.PLAYER, pServerLevel, pSpawnBlockPos, blockstate.getValue(BedBlock.FACING), pPlayerOrientation);
        } else if (!pIsRespawnForced) {
            return blockstate.getRespawnPosition(EntityType.PLAYER, pServerLevel, pSpawnBlockPos, pPlayerOrientation, null);
        } else {
            boolean flag = block.isPossibleToRespawnInThis(blockstate);
            BlockState blockstate1 = pServerLevel.getBlockState(pSpawnBlockPos.above());
            boolean flag1 = blockstate1.getBlock().isPossibleToRespawnInThis(blockstate1);
            return flag && flag1
                ? Optional.of(new Vec3((double)pSpawnBlockPos.getX() + 0.5, (double)pSpawnBlockPos.getY() + 0.1, (double)pSpawnBlockPos.getZ() + 0.5))
                : Optional.empty();
        }
    }

    public boolean isSleepingLongEnough() {
        return this.isSleeping() && this.sleepCounter >= 100;
    }

    public int getSleepTimer() {
        return this.sleepCounter;
    }

    public void displayClientMessage(Component pChatComponent, boolean pActionBar) {
    }

    public void awardStat(ResourceLocation pStatKey) {
        this.awardStat(Stats.CUSTOM.get(pStatKey));
    }

    public void awardStat(ResourceLocation pStat, int pIncrement) {
        this.awardStat(Stats.CUSTOM.get(pStat), pIncrement);
    }

    public void awardStat(Stat<?> pStat) {
        this.awardStat(pStat, 1);
    }

    public void awardStat(Stat<?> pStat, int pIncrement) {
    }

    public void resetStat(Stat<?> pStat) {
    }

    public int awardRecipes(Collection<RecipeHolder<?>> pRecipes) {
        return 0;
    }

    public void triggerRecipeCrafted(RecipeHolder<?> pRecipe, List<ItemStack> pItems) {
    }

    public void awardRecipesByKey(List<ResourceLocation> p_312830_) {
    }

    public int resetRecipes(Collection<RecipeHolder<?>> pRecipes) {
        return 0;
    }

    @Override
    public void jumpFromGround() {
        super.jumpFromGround();
        this.awardStat(Stats.JUMP);
        if (this.isSprinting()) {
            this.causeFoodExhaustion(0.2F);
        } else {
            this.causeFoodExhaustion(0.05F);
        }
    }

    @Override
    public void travel(Vec3 pTravelVector) {
        if (this.isSwimming() && !this.isPassenger()) {
            double d0 = this.getLookAngle().y;
            double d1 = d0 < -0.2 ? 0.085 : 0.06;
            if (d0 <= 0.0
                || this.jumping
                || !this.level().getBlockState(BlockPos.containing(this.getX(), this.getY() + 1.0 - 0.1, this.getZ())).getFluidState().isEmpty()) {
                Vec3 vec3 = this.getDeltaMovement();
                this.setDeltaMovement(vec3.add(0.0, (d0 - vec3.y) * d1, 0.0));
            }
        }

        if (this.abilities.flying && !this.isPassenger()) {
            double d2 = this.getDeltaMovement().y;
            super.travel(pTravelVector);
            Vec3 vec31 = this.getDeltaMovement();
            this.setDeltaMovement(vec31.x, d2 * 0.6, vec31.z);
            this.resetFallDistance();
            this.setSharedFlag(7, false);
        } else {
            super.travel(pTravelVector);
        }
    }

    @Override
    public void updateSwimming() {
        if (this.abilities.flying) {
            this.setSwimming(false);
        } else {
            super.updateSwimming();
        }
    }

    protected boolean freeAt(BlockPos pPos) {
        return !this.level().getBlockState(pPos).isSuffocating(this.level(), pPos);
    }

    @Override
    public float getSpeed() {
        return (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        if (this.abilities.mayfly) {
            net.minecraftforge.event.ForgeEventFactory.onPlayerFall(this, pFallDistance, pFallDistance);
            return false;
        } else {
            if (pFallDistance >= 2.0F) {
                this.awardStat(Stats.FALL_ONE_CM, (int)Math.round((double)pFallDistance * 100.0));
            }

            if (this.f_315903_ && this.f_316171_ != null) {
                double d0 = this.f_316171_.y;
                this.m_320755_();
                return d0 < this.getY() ? false : super.causeFallDamage((float)(d0 - this.getY()), pMultiplier, pSource);
            } else {
                return super.causeFallDamage(pFallDistance, pMultiplier, pSource);
            }
        }
    }

    public boolean tryToStartFallFlying() {
        if (!this.onGround() && !this.isFallFlying() && !this.isInWater() && !this.hasEffect(MobEffects.LEVITATION)) {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.CHEST);
            if (itemstack.canElytraFly(this)) {
                this.startFallFlying();
                return true;
            }
        }

        return false;
    }

    public void startFallFlying() {
        this.setSharedFlag(7, true);
    }

    public void stopFallFlying() {
        this.setSharedFlag(7, true);
        this.setSharedFlag(7, false);
    }

    @Override
    protected void doWaterSplashEffect() {
        if (!this.isSpectator()) {
            super.doWaterSplashEffect();
        }
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pState) {
        if (this.isInWater()) {
            this.waterSwimSound();
            this.playMuffledStepSound(pState, pPos);
        } else {
            BlockPos blockpos = this.getPrimaryStepSoundBlockPos(pPos);
            if (!pPos.equals(blockpos)) {
                BlockState blockstate = this.level().getBlockState(blockpos);
                if (blockstate.is(BlockTags.COMBINATION_STEP_SOUND_BLOCKS)) {
                    this.playCombinationStepSounds(blockstate, pState, blockpos, pPos);
                } else {
                    super.playStepSound(blockpos, blockstate);
                }
            } else {
                super.playStepSound(pPos, pState);
            }
        }
    }

    @Override
    public LivingEntity.Fallsounds getFallSounds() {
        return new LivingEntity.Fallsounds(SoundEvents.PLAYER_SMALL_FALL, SoundEvents.PLAYER_BIG_FALL);
    }

    @Override
    public boolean killedEntity(ServerLevel pLevel, LivingEntity pEntity) {
        this.awardStat(Stats.ENTITY_KILLED.get(pEntity.getType()));
        return true;
    }

    @Override
    public void makeStuckInBlock(BlockState pState, Vec3 pMotionMultiplier) {
        if (!this.abilities.flying) {
            super.makeStuckInBlock(pState, pMotionMultiplier);
        }

        this.m_320755_();
    }

    public void giveExperiencePoints(int pXpPoints) {
        var event = net.minecraftforge.event.ForgeEventFactory.onPlayerXpChange(this, pXpPoints);
        if (event.isCanceled()) {
            return;
        }
        pXpPoints = event.getAmount();

        this.increaseScore(pXpPoints);
        this.experienceProgress = this.experienceProgress + (float)pXpPoints / (float)this.getXpNeededForNextLevel();
        this.totalExperience = Mth.clamp(this.totalExperience + pXpPoints, 0, Integer.MAX_VALUE);

        while (this.experienceProgress < 0.0F) {
            float f = this.experienceProgress * (float)this.getXpNeededForNextLevel();
            if (this.experienceLevel > 0) {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 1.0F + f / (float)this.getXpNeededForNextLevel();
            } else {
                this.giveExperienceLevels(-1);
                this.experienceProgress = 0.0F;
            }
        }

        while (this.experienceProgress >= 1.0F) {
            this.experienceProgress = (this.experienceProgress - 1.0F) * (float)this.getXpNeededForNextLevel();
            this.giveExperienceLevels(1);
            this.experienceProgress = this.experienceProgress / (float)this.getXpNeededForNextLevel();
        }
    }

    public int getEnchantmentSeed() {
        return this.enchantmentSeed;
    }

    public void onEnchantmentPerformed(ItemStack pEnchantedItem, int pLevelCost) {
        giveExperienceLevels(-pLevelCost);
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        this.enchantmentSeed = this.random.nextInt();
    }

    public void giveExperienceLevels(int pLevels) {
        var event = net.minecraftforge.event.ForgeEventFactory.onPlayerLevelChange(this, pLevels);
        if (event.isCanceled()) {
            return;
        }
        pLevels = event.getLevels();

        this.experienceLevel += pLevels;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experienceProgress = 0.0F;
            this.totalExperience = 0;
        }

        if (pLevels > 0 && this.experienceLevel % 5 == 0 && (float)this.lastLevelUpTime < (float)this.tickCount - 100.0F) {
            float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_LEVELUP, this.getSoundSource(), f * 0.75F, 1.0F);
            this.lastLevelUpTime = this.tickCount;
        }
    }

    public int getXpNeededForNextLevel() {
        if (this.experienceLevel >= 30) {
            return 112 + (this.experienceLevel - 30) * 9;
        } else {
            return this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2;
        }
    }

    public void causeFoodExhaustion(float pExhaustion) {
        if (!this.abilities.invulnerable) {
            if (!this.level().isClientSide) {
                this.foodData.addExhaustion(pExhaustion);
            }
        }
    }

    public Optional<WardenSpawnTracker> getWardenSpawnTracker() {
        return Optional.empty();
    }

    public FoodData getFoodData() {
        return this.foodData;
    }

    public boolean canEat(boolean pCanAlwaysEat) {
        return this.abilities.invulnerable || pCanAlwaysEat || this.foodData.needsFood();
    }

    public boolean isHurt() {
        return this.getHealth() > 0.0F && this.getHealth() < this.getMaxHealth();
    }

    public boolean mayBuild() {
        return this.abilities.mayBuild;
    }

    public boolean mayUseItemAt(BlockPos pPos, Direction pFacing, ItemStack pStack) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            BlockPos blockpos = pPos.relative(pFacing.getOpposite());
            BlockInWorld blockinworld = new BlockInWorld(this.level(), blockpos, false);
            return pStack.m_321400_(blockinworld);
        }
    }

    @Override
    public int getExperienceReward() {
        if (!this.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) && !this.isSpectator()) {
            int i = this.experienceLevel * 7;
            return i > 100 ? 100 : i;
        } else {
            return 0;
        }
    }

    @Override
    protected boolean isAlwaysExperienceDropper() {
        return true;
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return this.abilities.flying || this.onGround() && this.isDiscrete() ? Entity.MovementEmission.NONE : Entity.MovementEmission.ALL;
    }

    public void onUpdateAbilities() {
    }

    @Override
    public Component getName() {
        return Component.literal(this.gameProfile.getName());
    }

    public PlayerEnderChestContainer getEnderChestInventory() {
        return this.enderChestInventory;
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot pSlot) {
        if (pSlot == EquipmentSlot.MAINHAND) {
            return this.inventory.getSelected();
        } else if (pSlot == EquipmentSlot.OFFHAND) {
            return this.inventory.offhand.get(0);
        } else {
            return pSlot.getType() == EquipmentSlot.Type.ARMOR ? this.inventory.armor.get(pSlot.getIndex()) : ItemStack.EMPTY;
        }
    }

    @Override
    protected boolean doesEmitEquipEvent(EquipmentSlot pSlot) {
        return pSlot.getType() == EquipmentSlot.Type.ARMOR;
    }

    @Override
    public void m_21035_(EquipmentSlot pSlot, ItemStack pStack) {
        this.verifyEquippedItem(pStack);
        if (pSlot == EquipmentSlot.MAINHAND) {
            this.onEquipItem(pSlot, this.inventory.items.set(this.inventory.selected, pStack), pStack);
        } else if (pSlot == EquipmentSlot.OFFHAND) {
            this.onEquipItem(pSlot, this.inventory.offhand.set(0, pStack), pStack);
        } else if (pSlot.getType() == EquipmentSlot.Type.ARMOR) {
            this.onEquipItem(pSlot, this.inventory.armor.set(pSlot.getIndex(), pStack), pStack);
        }
    }

    public boolean addItem(ItemStack pStack) {
        return this.inventory.add(pStack);
    }

    @Override
    public Iterable<ItemStack> m_21487_() {
        return Lists.newArrayList(this.getMainHandItem(), this.getOffhandItem());
    }

    @Override
    public Iterable<ItemStack> m_21151_() {
        return this.inventory.armor;
    }

    @Override
    public boolean m_320440_(EquipmentSlot p_333717_) {
        return p_333717_ != EquipmentSlot.BODY;
    }

    public boolean setEntityOnShoulder(CompoundTag pEntityCompound) {
        if (this.isPassenger() || !this.onGround() || this.isInWater() || this.isInPowderSnow) {
            return false;
        } else if (this.getShoulderEntityLeft().isEmpty()) {
            this.setShoulderEntityLeft(pEntityCompound);
            this.timeEntitySatOnShoulder = this.level().getGameTime();
            return true;
        } else if (this.getShoulderEntityRight().isEmpty()) {
            this.setShoulderEntityRight(pEntityCompound);
            this.timeEntitySatOnShoulder = this.level().getGameTime();
            return true;
        } else {
            return false;
        }
    }

    protected void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20L < this.level().getGameTime()) {
            this.respawnEntityOnShoulder(this.getShoulderEntityLeft());
            this.setShoulderEntityLeft(new CompoundTag());
            this.respawnEntityOnShoulder(this.getShoulderEntityRight());
            this.setShoulderEntityRight(new CompoundTag());
        }
    }

    private void respawnEntityOnShoulder(CompoundTag pEntityCompound) {
        if (!this.level().isClientSide && !pEntityCompound.isEmpty()) {
            EntityType.create(pEntityCompound, this.level()).ifPresent(p_327053_ -> {
                if (p_327053_ instanceof TamableAnimal) {
                    ((TamableAnimal)p_327053_).setOwnerUUID(this.uuid);
                }

                p_327053_.setPos(this.getX(), this.getY() + 0.7F, this.getZ());
                ((ServerLevel)this.level()).addWithUUID(p_327053_);
            });
        }
    }

    @Override
    public abstract boolean isSpectator();

    @Override
    public boolean canBeHitByProjectile() {
        return !this.isSpectator() && super.canBeHitByProjectile();
    }

    @Override
    public boolean isSwimming() {
        return !this.abilities.flying && !this.isSpectator() && super.isSwimming();
    }

    public abstract boolean isCreative();

    @Override
    public boolean isPushedByFluid() {
        return !this.abilities.flying;
    }

    public Scoreboard getScoreboard() {
        return this.level().getScoreboard();
    }

    @Override
    public Component getDisplayName() {
        if (this.displayname == null) {
            this.displayname = net.minecraftforge.event.ForgeEventFactory.getPlayerDisplayName(this, this.getName());
        }
        MutableComponent mutablecomponent = Component.literal("");
        mutablecomponent = prefixes.stream().reduce(mutablecomponent, MutableComponent::append);
        mutablecomponent = mutablecomponent.append(PlayerTeam.formatNameForTeam(this.getTeam(), this.displayname));
        mutablecomponent = suffixes.stream().reduce(mutablecomponent, MutableComponent::append);
        return this.decorateDisplayNameComponent(mutablecomponent);
    }

    private MutableComponent decorateDisplayNameComponent(MutableComponent pDisplayName) {
        String s = this.getGameProfile().getName();
        return pDisplayName.withStyle(
            p_327057_ -> p_327057_.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + s + " ")).withHoverEvent(this.createHoverEvent()).withInsertion(s)
        );
    }

    @Override
    public String getScoreboardName() {
        return this.getGameProfile().getName();
    }

    @Override
    protected void internalSetAbsorptionAmount(float pAbsorptionAmount) {
        this.getEntityData().set(DATA_PLAYER_ABSORPTION_ID, pAbsorptionAmount);
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getEntityData().get(DATA_PLAYER_ABSORPTION_ID);
    }

    public boolean isModelPartShown(PlayerModelPart pPart) {
        return (this.getEntityData().get(DATA_PLAYER_MODE_CUSTOMISATION) & pPart.getMask()) == pPart.getMask();
    }

    @Override
    public SlotAccess getSlot(int pSlot) {
        if (pSlot == 499) {
            return new SlotAccess() {
                @Override
                public ItemStack get() {
                    return Player.this.containerMenu.getCarried();
                }

                @Override
                public boolean set(ItemStack p_333834_) {
                    Player.this.containerMenu.setCarried(p_333834_);
                    return true;
                }
            };
        } else {
            final int i = pSlot - 500;
            if (i >= 0 && i < 4) {
                return new SlotAccess() {
                    @Override
                    public ItemStack get() {
                        return Player.this.inventoryMenu.getCraftSlots().getItem(i);
                    }

                    @Override
                    public boolean set(ItemStack p_333999_) {
                        Player.this.inventoryMenu.getCraftSlots().setItem(i, p_333999_);
                        Player.this.inventoryMenu.slotsChanged(Player.this.inventory);
                        return true;
                    }
                };
            } else if (pSlot >= 0 && pSlot < this.inventory.items.size()) {
                return SlotAccess.forContainer(this.inventory, pSlot);
            } else {
                int j = pSlot - 200;
                return j >= 0 && j < this.enderChestInventory.getContainerSize() ? SlotAccess.forContainer(this.enderChestInventory, j) : super.getSlot(pSlot);
            }
        }
    }

    public boolean isReducedDebugInfo() {
        return this.reducedDebugInfo;
    }

    public void setReducedDebugInfo(boolean pReducedDebugInfo) {
        this.reducedDebugInfo = pReducedDebugInfo;
    }

    @Override
    public void setRemainingFireTicks(int pTicks) {
        super.setRemainingFireTicks(this.abilities.invulnerable ? Math.min(pTicks, 1) : pTicks);
    }

    @Override
    public HumanoidArm getMainArm() {
        return this.entityData.get(DATA_PLAYER_MAIN_HAND) == 0 ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
    }

    public void setMainArm(HumanoidArm pHand) {
        this.entityData.set(DATA_PLAYER_MAIN_HAND, (byte)(pHand == HumanoidArm.LEFT ? 0 : 1));
    }

    public CompoundTag getShoulderEntityLeft() {
        return this.entityData.get(DATA_SHOULDER_LEFT);
    }

    protected void setShoulderEntityLeft(CompoundTag pEntityCompound) {
        this.entityData.set(DATA_SHOULDER_LEFT, pEntityCompound);
    }

    public CompoundTag getShoulderEntityRight() {
        return this.entityData.get(DATA_SHOULDER_RIGHT);
    }

    protected void setShoulderEntityRight(CompoundTag pEntityCompound) {
        this.entityData.set(DATA_SHOULDER_RIGHT, pEntityCompound);
    }

    public float getCurrentItemAttackStrengthDelay() {
        return (float)(1.0 / this.getAttributeValue(Attributes.ATTACK_SPEED) * 20.0);
    }

    public float getAttackStrengthScale(float pAdjustTicks) {
        return Mth.clamp(((float)this.attackStrengthTicker + pAdjustTicks) / this.getCurrentItemAttackStrengthDelay(), 0.0F, 1.0F);
    }

    public void resetAttackStrengthTicker() {
        this.attackStrengthTicker = 0;
    }

    public ItemCooldowns getCooldowns() {
        return this.cooldowns;
    }

    @Override
    protected float getBlockSpeedFactor() {
        return !this.abilities.flying && !this.isFallFlying() ? super.getBlockSpeedFactor() : 1.0F;
    }

    public float getLuck() {
        return (float)this.getAttributeValue(Attributes.LUCK);
    }

    public boolean canUseGameMasterBlocks() {
        return this.abilities.instabuild && this.getPermissionLevel() >= 2;
    }

    @Override
    public boolean canTakeItem(ItemStack pItemstack) {
        EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(pItemstack);
        return this.getItemBySlot(equipmentslot).isEmpty();
    }

    @Override
    public EntityDimensions m_31586_(Pose pPose) {
        return POSES.getOrDefault(pPose, STANDING_DIMENSIONS);
    }

    @Override
    public ImmutableList<Pose> getDismountPoses() {
        return ImmutableList.of(Pose.STANDING, Pose.CROUCHING, Pose.SWIMMING);
    }

    @Override
    public ItemStack getProjectile(ItemStack pShootable) {
        if (!(pShootable.getItem() instanceof ProjectileWeaponItem)) {
            return ItemStack.EMPTY;
        } else {
            Predicate<ItemStack> predicate = ((ProjectileWeaponItem)pShootable.getItem()).getSupportedHeldProjectiles();
            ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(this, predicate);
            if (!itemstack.isEmpty()) {
                return net.minecraftforge.common.ForgeHooks.getProjectile(this, pShootable, itemstack);
            } else {
                predicate = ((ProjectileWeaponItem)pShootable.getItem()).getAllSupportedProjectiles();

                for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                    ItemStack itemstack1 = this.inventory.getItem(i);
                    if (predicate.test(itemstack1)) {
                        return net.minecraftforge.common.ForgeHooks.getProjectile(this, pShootable, itemstack1);
                    }
                }

                var vanilla = this.abilities.instabuild ? new ItemStack(Items.ARROW) : ItemStack.EMPTY;
                return net.minecraftforge.common.ForgeHooks.getProjectile(this, pShootable, vanilla);
            }
        }
    }

    @Override
    public ItemStack eat(Level pLevel, ItemStack pFood) {
        this.getFoodData().eat(pFood);
        this.awardStat(Stats.ITEM_USED.get(pFood.getItem()));
        pLevel.playSound(
            null,
            this.getX(),
            this.getY(),
            this.getZ(),
            SoundEvents.PLAYER_BURP,
            SoundSource.PLAYERS,
            0.5F,
            pLevel.random.nextFloat() * 0.1F + 0.9F
        );
        if (this instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)this, pFood);
        }

        return super.eat(pLevel, pFood);
    }

    @Override
    protected boolean shouldRemoveSoulSpeed(BlockState pState) {
        return this.abilities.flying || super.shouldRemoveSoulSpeed(pState);
    }

    @Override
    public Vec3 getRopeHoldPosition(float pPartialTicks) {
        double d0 = 0.22 * (this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0);
        float f = Mth.lerp(pPartialTicks * 0.5F, this.getXRot(), this.xRotO) * (float) (Math.PI / 180.0);
        float f1 = Mth.lerp(pPartialTicks, this.yBodyRotO, this.yBodyRot) * (float) (Math.PI / 180.0);
        if (this.isFallFlying() || this.isAutoSpinAttack()) {
            Vec3 vec31 = this.getViewVector(pPartialTicks);
            Vec3 vec3 = this.getDeltaMovement();
            double d6 = vec3.horizontalDistanceSqr();
            double d3 = vec31.horizontalDistanceSqr();
            float f2;
            if (d6 > 0.0 && d3 > 0.0) {
                double d4 = (vec3.x * vec31.x + vec3.z * vec31.z) / Math.sqrt(d6 * d3);
                double d5 = vec3.x * vec31.z - vec3.z * vec31.x;
                f2 = (float)(Math.signum(d5) * Math.acos(d4));
            } else {
                f2 = 0.0F;
            }

            return this.getPosition(pPartialTicks).add(new Vec3(d0, -0.11, 0.85).zRot(-f2).xRot(-f).yRot(-f1));
        } else if (this.isVisuallySwimming()) {
            return this.getPosition(pPartialTicks).add(new Vec3(d0, 0.2, -0.15).xRot(-f).yRot(-f1));
        } else {
            double d1 = this.getBoundingBox().getYsize() - 1.0;
            double d2 = this.isCrouching() ? -0.2 : 0.07;
            return this.getPosition(pPartialTicks).add(new Vec3(d0, d1, d2).yRot(-f1));
        }
    }

    @Override
    public boolean isAlwaysTicking() {
        return true;
    }

    public boolean isScoping() {
        return this.isUsingItem() && this.getUseItem().is(Items.SPYGLASS);
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public Optional<GlobalPos> getLastDeathLocation() {
        return this.lastDeathLocation;
    }

    public void setLastDeathLocation(Optional<GlobalPos> pLastDeathLocation) {
        this.lastDeathLocation = pLastDeathLocation;
    }

    @Override
    public float getHurtDir() {
        return this.hurtDir;
    }

    @Override
    public void animateHurt(float pYaw) {
        super.animateHurt(pYaw);
        this.hurtDir = pYaw;
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    protected float getFlyingSpeed() {
        if (this.abilities.flying && !this.isPassenger()) {
            return this.isSprinting() ? this.abilities.getFlyingSpeed() * 2.0F : this.abilities.getFlyingSpeed();
        } else {
            return this.isSprinting() ? 0.025999999F : 0.02F;
        }
    }

    public double m_319993_() {
        return this.getAttributeValue(Attributes.f_316914_);
    }

    public double m_323410_() {
        return this.getAttributeValue(Attributes.f_315802_);
    }

    public boolean m_321636_(Entity p_333619_, double p_330803_) {
        return p_333619_.isRemoved() ? false : this.m_323803_(p_333619_.getBoundingBox(), p_330803_);
    }

    public boolean m_323803_(AABB p_329456_, double p_332906_) {
        double d0 = this.m_323410_() + p_332906_;
        return p_329456_.distanceToSqr(this.getEyePosition()) < d0 * d0;
    }

    public boolean m_319363_(BlockPos p_331132_, double p_328439_) {
        double d0 = this.m_319993_() + p_328439_;
        return new AABB(p_331132_).distanceToSqr(this.getEyePosition()) < d0 * d0;
    }

    public void m_320755_() {
        this.f_314551_ = null;
        this.f_316171_ = null;
        this.f_315903_ = false;
    }

    public Collection<MutableComponent> getPrefixes() {
        return this.prefixes;
    }

    public Collection<MutableComponent> getSuffixes() {
        return this.suffixes;
    }

    private Component displayname = null;

    /**
     * Force the displayed name to refresh, by firing {@link net.minecraftforge.event.entity.player.PlayerEvent.NameFormat}, using the real player name as event parameter.
     */
    public void refreshDisplayName() {
        this.displayname = net.minecraftforge.event.ForgeEventFactory.getPlayerDisplayName(this, this.getName());
    }

    private final net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandler>
        playerMainHandler = net.minecraftforge.common.util.LazyOptional.of(
            () -> new net.minecraftforge.items.wrapper.PlayerMainInvWrapper(inventory));

    private final net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandler>
        playerEquipmentHandler = net.minecraftforge.common.util.LazyOptional.of(
            () -> new net.minecraftforge.items.wrapper.CombinedInvWrapper(
                  new net.minecraftforge.items.wrapper.PlayerArmorInvWrapper(inventory),
                  new net.minecraftforge.items.wrapper.PlayerOffhandInvWrapper(inventory)));

    private final net.minecraftforge.common.util.LazyOptional<net.minecraftforge.items.IItemHandler>
        playerJoinedHandler = net.minecraftforge.common.util.LazyOptional.of(
            () -> new net.minecraftforge.items.wrapper.PlayerInvWrapper(inventory));

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
        if (capability == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && this.isAlive()) {
            if (facing == null) return playerJoinedHandler.cast();
            else if (facing.getAxis().isVertical()) return playerMainHandler.cast();
            else if (facing.getAxis().isHorizontal()) return playerEquipmentHandler.cast();
        }
        return super.getCapability(capability, facing);
    }

    /**
     * Force a pose for the player. If set, the vanilla pose determination and clearance check is skipped. Make sure the pose is clear yourself (e.g. in PlayerTick).
     * This has to be set just once, do not set it every tick.
     * Make sure to clear (null) the pose if not required anymore and only use if necessary.
     */
    public void setForcedPose(@Nullable Pose pose) {
        this.forcedPose = pose;
    }

    /**
     * @return The forced pose if set, null otherwise
     */
    @Nullable
    public Pose getForcedPose() {
        return this.forcedPose;
    }


    public static enum BedSleepingProblem {
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW(Component.translatable("block.minecraft.bed.no_sleep")),
        TOO_FAR_AWAY(Component.translatable("block.minecraft.bed.too_far_away")),
        OBSTRUCTED(Component.translatable("block.minecraft.bed.obstructed")),
        OTHER_PROBLEM,
        NOT_SAFE(Component.translatable("block.minecraft.bed.not_safe"));

        @Nullable
        private final Component message;

        private BedSleepingProblem() {
            this.message = null;
        }

        private BedSleepingProblem(final Component pMessage) {
            this.message = pMessage;
        }

        @Nullable
        public Component getMessage() {
            return this.message;
        }
    }
}
