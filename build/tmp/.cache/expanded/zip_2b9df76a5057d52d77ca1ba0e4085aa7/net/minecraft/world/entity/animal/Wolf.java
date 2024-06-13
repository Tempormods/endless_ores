package net.minecraft.world.entity.animal;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BegGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;

public class Wolf extends TamableAnimal implements NeutralMob, VariantHolder<Holder<WolfVariant>> {
    private static final EntityDataAccessor<Boolean> DATA_INTERESTED_ID = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Holder<WolfVariant>> f_315779_ = SynchedEntityData.defineId(Wolf.class, EntityDataSerializers.f_314299_);
    public static final Predicate<LivingEntity> PREY_SELECTOR = p_326988_ -> {
        EntityType<?> entitytype = p_326988_.getType();
        return entitytype == EntityType.SHEEP || entitytype == EntityType.RABBIT || entitytype == EntityType.FOX;
    };
    private static final float START_HEALTH = 8.0F;
    private static final float TAME_HEALTH = 40.0F;
    private static final float f_314667_ = 0.125F;
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;

    public Wolf(EntityType<? extends Wolf> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setTame(false, false);
        this.setPathfindingMalus(PathType.POWDER_SNOW, -1.0F);
        this.setPathfindingMalus(PathType.DANGER_POWDER_SNOW, -1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new Wolf.WolfPanicGoal(1.5));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new Wolf.WolfAvoidEntityGoal<>(this, Llama.class, 24.0F, 1.5, 1.5));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new FollowOwnerGoal(this, 1.0, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(9, new BegGoal(this, 8.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new NonTameRandomTargetGoal<>(this, Animal.class, false, PREY_SELECTOR));
        this.targetSelector.addGoal(6, new NonTameRandomTargetGoal<>(this, Turtle.class, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, AbstractSkeleton.class, false));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    public ResourceLocation m_322344_() {
        WolfVariant wolfvariant = this.getVariant().value();
        if (this.isTame()) {
            return wolfvariant.m_323730_();
        } else {
            return this.isAngry() ? wolfvariant.m_323442_() : wolfvariant.m_321466_();
        }
    }

    public Holder<WolfVariant> getVariant() {
        return this.entityData.get(f_315779_);
    }

    public void setVariant(Holder<WolfVariant> p_332660_) {
        this.entityData.set(f_315779_, p_332660_);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 4.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_332186_) {
        super.defineSynchedData(p_332186_);
        p_332186_.m_318949_(f_315779_, this.m_321891_().registryOrThrow(Registries.f_317086_).getHolderOrThrow(WolfVariants.f_316890_));
        p_332186_.m_318949_(DATA_INTERESTED_ID, false);
        p_332186_.m_318949_(DATA_COLLAR_COLOR, DyeColor.RED.getId());
        p_332186_.m_318949_(DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    protected void playStepSound(BlockPos pPos, BlockState pBlock) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putByte("CollarColor", (byte)this.getCollarColor().getId());
        pCompound.putString("variant", this.getVariant().unwrapKey().orElse(WolfVariants.f_316890_).location().toString());
        this.addPersistentAngerSaveData(pCompound);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        Optional.ofNullable(ResourceLocation.tryParse(pCompound.getString("variant")))
            .map(p_326989_ -> ResourceKey.create(Registries.f_317086_, p_326989_))
            .flatMap(p_326990_ -> this.m_321891_().registryOrThrow(Registries.f_317086_).getHolder((ResourceKey<WolfVariant>)p_326990_))
            .ifPresent(this::setVariant);
        if (pCompound.contains("CollarColor", 99)) {
            this.setCollarColor(DyeColor.byId(pCompound.getInt("CollarColor")));
        }

        this.readPersistentAngerSaveData(this.level(), pCompound);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_333916_, DifficultyInstance p_329109_, MobSpawnType p_334609_, @Nullable SpawnGroupData p_328496_) {
        Holder<Biome> holder = p_333916_.getBiome(this.blockPosition());
        Holder<WolfVariant> holder1;
        if (p_328496_ instanceof Wolf.WolfPackData wolf$wolfpackdata) {
            holder1 = wolf$wolfpackdata.f_314325_;
        } else {
            holder1 = WolfVariants.m_320536_(this.m_321891_(), holder);
            p_328496_ = new Wolf.WolfPackData(holder1);
        }

        this.setVariant(holder1);
        return super.finalizeSpawn(p_333916_, p_329109_, p_334609_, p_328496_);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.isAngry()) {
            return SoundEvents.WOLF_GROWL;
        } else if (this.random.nextInt(3) == 0) {
            return this.isTame() && this.getHealth() < 20.0F ? SoundEvents.WOLF_WHINE : SoundEvents.WOLF_PANT;
        } else {
            return SoundEvents.WOLF_AMBIENT;
        }
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return this.m_324962_(pDamageSource) ? SoundEvents.f_315764_ : SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround()) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level().broadcastEntityEvent(this, (byte)8);
        }

        if (!this.level().isClientSide) {
            this.updatePersistentAnger((ServerLevel)this.level(), true);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.interestedAngleO = this.interestedAngle;
            if (this.isInterested()) {
                this.interestedAngle = this.interestedAngle + (1.0F - this.interestedAngle) * 0.4F;
            } else {
                this.interestedAngle = this.interestedAngle + (0.0F - this.interestedAngle) * 0.4F;
            }

            if (this.isInWaterRainOrBubble()) {
                this.isWet = true;
                if (this.isShaking && !this.level().isClientSide) {
                    this.level().broadcastEntityEvent(this, (byte)56);
                    this.cancelShake();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEvents.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.gameEvent(GameEvent.ENTITY_ACTION);
                }

                this.shakeAnimO = this.shakeAnim;
                this.shakeAnim += 0.05F;
                if (this.shakeAnimO >= 2.0F) {
                    this.isWet = false;
                    this.isShaking = false;
                    this.shakeAnimO = 0.0F;
                    this.shakeAnim = 0.0F;
                }

                if (this.shakeAnim > 0.4F) {
                    float f = (float)this.getY();
                    int i = (int)(Mth.sin((this.shakeAnim - 0.4F) * (float) Math.PI) * 7.0F);
                    Vec3 vec3 = this.getDeltaMovement();

                    for (int j = 0; j < i; j++) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        this.level()
                            .addParticle(
                                ParticleTypes.SPLASH,
                                this.getX() + (double)f1,
                                (double)(f + 0.8F),
                                this.getZ() + (double)f2,
                                vec3.x,
                                vec3.y,
                                vec3.z
                            );
                    }
                }
            }
        }
    }

    private void cancelShake() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    @Override
    public void die(DamageSource pCause) {
        this.isWet = false;
        this.isShaking = false;
        this.shakeAnimO = 0.0F;
        this.shakeAnim = 0.0F;
        super.die(pCause);
    }

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float pPartialTicks) {
        return Math.min(0.75F + Mth.lerp(pPartialTicks, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.25F, 1.0F);
    }

    public float getBodyRollAngle(float pPartialTicks, float pOffset) {
        float f = (Mth.lerp(pPartialTicks, this.shakeAnimO, this.shakeAnim) + pOffset) / 1.8F;
        if (f < 0.0F) {
            f = 0.0F;
        } else if (f > 1.0F) {
            f = 1.0F;
        }

        return Mth.sin(f * (float) Math.PI) * Mth.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    public float getHeadRollAngle(float pPartialTicks) {
        return Mth.lerp(pPartialTicks, this.interestedAngleO, this.interestedAngle) * 0.15F * (float) Math.PI;
    }

    @Override
    public int getMaxHeadXRot() {
        return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
    }

    @Override
    public boolean hurt(DamageSource pSource, float pAmount) {
        if (this.isInvulnerableTo(pSource)) {
            return false;
        } else {
            if (!this.level().isClientSide) {
                this.setOrderedToSit(false);
            }

            return super.hurt(pSource, pAmount);
        }
    }

    @Override
    protected void actuallyHurt(DamageSource p_331660_, float p_334536_) {
        if (!this.m_324962_(p_331660_)) {
            super.actuallyHurt(p_331660_, p_334536_);
        } else {
            ItemStack itemstack = this.m_319275_();
            int i = itemstack.getDamageValue();
            int j = itemstack.getMaxDamage();
            itemstack.hurtAndBreak(Mth.ceil(p_334536_), this, EquipmentSlot.BODY);
            if (Crackiness.f_315625_.m_324753_(i, j) != Crackiness.f_315625_.m_318874_(this.m_319275_())) {
                this.playSound(SoundEvents.f_314016_);
                if (this.level() instanceof ServerLevel serverlevel) {
                    serverlevel.sendParticles(
                        new ItemParticleOption(ParticleTypes.ITEM, Items.f_314244_.getDefaultInstance()),
                        this.getX(),
                        this.getY() + 1.0,
                        this.getZ(),
                        20,
                        0.2,
                        0.1,
                        0.2,
                        0.1
                    );
                }
            }
        }
    }

    private boolean m_324962_(DamageSource p_335120_) {
        return this.m_324194_() && !p_335120_.is(DamageTypeTags.f_316562_);
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        boolean flag = pEntity.hurt(this.damageSources().mobAttack(this), (float)((int)this.getAttributeValue(Attributes.ATTACK_DAMAGE)));
        if (flag) {
            this.doEnchantDamageEffects(this, pEntity);
        }

        return flag;
    }

    @Override
    protected void reassessTameGoals() {
        if (this.isTame()) {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
            this.setHealth(40.0F);
        } else {
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(8.0);
        }
    }

    @Override
    protected void hurtArmor(DamageSource p_331879_, float p_331430_) {
        this.m_318635_(p_331879_, p_331430_, new EquipmentSlot[]{EquipmentSlot.BODY});
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        Item item = itemstack.getItem();
        if (!this.level().isClientSide || this.isBaby() && this.isFood(itemstack)) {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    itemstack.m_321439_(1, pPlayer);
                    FoodProperties foodproperties = itemstack.m_323252_(DataComponents.f_315399_);
                    float f = foodproperties != null ? (float)foodproperties.nutrition() : 1.0F;
                    this.heal(2.0F * f);
                    return InteractionResult.sidedSuccess(this.level().isClientSide());
                } else {
                    if (item instanceof DyeItem dyeitem && this.isOwnedBy(pPlayer)) {
                        DyeColor dyecolor = dyeitem.getDyeColor();
                        if (dyecolor != this.getCollarColor()) {
                            this.setCollarColor(dyecolor);
                            itemstack.m_321439_(1, pPlayer);
                            return InteractionResult.SUCCESS;
                        }

                        return super.mobInteract(pPlayer, pHand);
                    }

                    if (itemstack.is(Items.f_314362_) && this.isOwnedBy(pPlayer) && !this.m_324194_() && !this.isBaby()) {
                        this.m_323866_(itemstack.copyWithCount(1));
                        itemstack.m_321439_(1, pPlayer);
                        return InteractionResult.SUCCESS;
                    } else if (itemstack.is(Items.SHEARS)
                        && this.isOwnedBy(pPlayer)
                        && this.m_324194_()
                        && (!EnchantmentHelper.hasBindingCurse(this.m_319275_()) || pPlayer.isCreative())) {
                        itemstack.hurtAndBreak(1, pPlayer, m_322775_(pHand));
                        this.playSound(SoundEvents.f_316724_);
                        ItemStack itemstack1 = this.m_319275_();
                        this.m_323866_(ItemStack.EMPTY);
                        this.spawnAtLocation(itemstack1);
                        return InteractionResult.SUCCESS;
                    } else if (ArmorMaterials.f_315454_.value().f_315867_().get().test(itemstack)
                        && this.isInSittingPose()
                        && this.m_324194_()
                        && this.isOwnedBy(pPlayer)
                        && this.m_319275_().isDamaged()) {
                        itemstack.shrink(1);
                        this.playSound(SoundEvents.f_314050_);
                        ItemStack itemstack2 = this.m_319275_();
                        int i = (int)((float)itemstack2.getMaxDamage() * 0.125F);
                        itemstack2.setDamageValue(Math.max(0, itemstack2.getDamageValue() - i));
                        return InteractionResult.SUCCESS;
                    } else {
                        InteractionResult interactionresult = super.mobInteract(pPlayer, pHand);
                        if (!interactionresult.consumesAction() && this.isOwnedBy(pPlayer)) {
                            this.setOrderedToSit(!this.isOrderedToSit());
                            this.jumping = false;
                            this.navigation.stop();
                            this.setTarget(null);
                            return InteractionResult.SUCCESS_NO_ITEM_USED;
                        } else {
                            return interactionresult;
                        }
                    }
                }
            } else if (itemstack.is(Items.BONE) && !this.isAngry()) {
                itemstack.m_321439_(1, pPlayer);
                this.m_318612_(pPlayer);
                return InteractionResult.SUCCESS;
            } else {
                return super.mobInteract(pPlayer, pHand);
            }
        } else {
            boolean flag = this.isOwnedBy(pPlayer) || this.isTame() || itemstack.is(Items.BONE) && !this.isTame() && !this.isAngry();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
    }

    private void m_318612_(Player p_336244_) {
        if (this.random.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, p_336244_)) {
            this.tame(p_336244_);
            this.navigation.stop();
            this.setTarget(null);
            this.setOrderedToSit(true);
            this.level().broadcastEntityEvent(this, (byte)7);
        } else {
            this.level().broadcastEntityEvent(this, (byte)6);
        }
    }

    @Override
    public void handleEntityEvent(byte pId) {
        if (pId == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (pId == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(pId);
        }
    }

    public float getTailAngle() {
        if (this.isAngry()) {
            return 1.5393804F;
        } else if (this.isTame()) {
            float f = this.getMaxHealth();
            float f1 = (f - this.getHealth()) / f;
            return (0.55F - f1 * 0.4F) * (float) Math.PI;
        } else {
            return (float) (Math.PI / 5);
        }
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.is(ItemTags.f_316425_);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int pTime) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, pTime);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID pTarget) {
        this.persistentAngerTarget = pTarget;
    }

    public DyeColor getCollarColor() {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public boolean m_324194_() {
        return !this.m_319275_().isEmpty();
    }

    private void setCollarColor(DyeColor pCollarColor) {
        this.entityData.set(DATA_COLLAR_COLOR, pCollarColor.getId());
    }

    @Nullable
    public Wolf getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        Wolf wolf = EntityType.WOLF.create(pLevel);
        if (wolf != null && pOtherParent instanceof Wolf wolf1) {
            if (this.random.nextBoolean()) {
                wolf.setVariant(this.getVariant());
            } else {
                wolf.setVariant(wolf1.getVariant());
            }

            if (this.isTame()) {
                wolf.setOwnerUUID(this.getOwnerUUID());
                wolf.setTame(true, true);
                if (this.random.nextBoolean()) {
                    wolf.setCollarColor(this.getCollarColor());
                } else {
                    wolf.setCollarColor(wolf1.getCollarColor());
                }
            }
        }

        return wolf;
    }

    public void setIsInterested(boolean pIsInterested) {
        this.entityData.set(DATA_INTERESTED_ID, pIsInterested);
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(pOtherAnimal instanceof Wolf wolf)) {
            return false;
        } else if (!wolf.isTame()) {
            return false;
        } else {
            return wolf.isInSittingPose() ? false : this.isInLove() && wolf.isInLove();
        }
    }

    public boolean isInterested() {
        return this.entityData.get(DATA_INTERESTED_ID);
    }

    @Override
    public boolean wantsToAttack(LivingEntity pTarget, LivingEntity pOwner) {
        if (pTarget instanceof Creeper || pTarget instanceof Ghast || pTarget instanceof ArmorStand) {
            return false;
        } else if (pTarget instanceof Wolf wolf) {
            return !wolf.isTame() || wolf.getOwner() != pOwner;
        } else {
            if (pTarget instanceof Player player && pOwner instanceof Player player1 && !player1.canHarmPlayer(player)) {
                return false;
            }

            if (pTarget instanceof AbstractHorse abstracthorse && abstracthorse.isTamed()) {
                return false;
            }

            if (pTarget instanceof TamableAnimal tamableanimal && tamableanimal.isTame()) {
                return false;
            }

            return true;
        }
    }

    @Override
    public boolean canBeLeashed(Player pPlayer) {
        return !this.isAngry() && super.canBeLeashed(pPlayer);
    }

    @Override
    public Vec3 getLeashOffset() {
        return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
    }

    public static boolean checkWolfSpawnRules(EntityType<Wolf> pWolf, LevelAccessor pLevel, MobSpawnType pSpawnType, BlockPos pPos, RandomSource pRandom) {
        return pLevel.getBlockState(pPos.below()).is(BlockTags.WOLVES_SPAWNABLE_ON) && isBrightEnoughToSpawn(pLevel, pPos);
    }

    class WolfAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
        private final Wolf wolf;

        public WolfAvoidEntityGoal(final Wolf pWolf, final Class<T> pEntityClassToAvoid, final float pMaxDist, final double pWalkSpeedModifier, final double pSprintSpeedModifier) {
            super(pWolf, pEntityClassToAvoid, pMaxDist, pWalkSpeedModifier, pSprintSpeedModifier);
            this.wolf = pWolf;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.toAvoid instanceof Llama ? !this.wolf.isTame() && this.avoidLlama((Llama)this.toAvoid) : false;
        }

        private boolean avoidLlama(Llama pLlama) {
            return pLlama.getStrength() >= Wolf.this.random.nextInt(5);
        }

        @Override
        public void start() {
            Wolf.this.setTarget(null);
            super.start();
        }

        @Override
        public void tick() {
            Wolf.this.setTarget(null);
            super.tick();
        }
    }

    public static class WolfPackData extends AgeableMob.AgeableMobGroupData {
        public final Holder<WolfVariant> f_314325_;

        public WolfPackData(Holder<WolfVariant> p_333988_) {
            super(false);
            this.f_314325_ = p_333988_;
        }
    }

    class WolfPanicGoal extends PanicGoal {
        public WolfPanicGoal(final double pSpeedModifier) {
            super(Wolf.this, pSpeedModifier);
        }

        @Override
        protected boolean shouldPanic() {
            return this.mob.isFreezing() || this.mob.isOnFire();
        }
    }
}
