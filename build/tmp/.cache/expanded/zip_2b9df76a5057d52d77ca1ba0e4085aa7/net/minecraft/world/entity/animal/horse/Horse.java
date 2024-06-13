package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityAttachments;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;

public class Horse extends AbstractHorse implements VariantHolder<Variant> {
    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.INT);
    private static final EntityDimensions f_315715_ = EntityType.HORSE
        .getDimensions()
        .m_323271_(EntityAttachments.m_321590_().m_322342_(EntityAttachment.PASSENGER, 0.0F, EntityType.HORSE.getHeight() + 0.125F, 0.0F))
        .scale(0.5F);

    public Horse(EntityType<? extends Horse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    protected void randomizeAttributes(RandomSource pRandom) {
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)generateMaxHealth(pRandom::nextInt));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(generateSpeed(pRandom::nextDouble));
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateJumpStrength(pRandom::nextDouble));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335516_) {
        super.defineSynchedData(p_335516_);
        p_335516_.m_318949_(DATA_ID_TYPE_VARIANT, 0);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.putInt("Variant", this.getTypeVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.setTypeVariant(pCompound.getInt("Variant"));
    }

    private void setTypeVariant(int pTypeVariant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, pTypeVariant);
    }

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariantAndMarkings(Variant pVariant, Markings pMarking) {
        this.setTypeVariant(pVariant.getId() & 0xFF | pMarking.getId() << 8 & 0xFF00);
    }

    public Variant getVariant() {
        return Variant.byId(this.getTypeVariant() & 0xFF);
    }

    public void setVariant(Variant pVariant) {
        this.setTypeVariant(pVariant.getId() & 0xFF | this.getTypeVariant() & -256);
    }

    public Markings getMarkings() {
        return Markings.byId((this.getTypeVariant() & 0xFF00) >> 8);
    }

    @Override
    public void containerChanged(Container pInvBasic) {
        ItemStack itemstack = this.m_319275_();
        super.containerChanged(pInvBasic);
        ItemStack itemstack1 = this.m_319275_();
        if (this.tickCount > 20 && this.m_30833_(itemstack1) && itemstack != itemstack1) {
            this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
        }
    }

    @Override
    protected void playGallopSound(SoundType pSoundType) {
        super.playGallopSound(pSoundType);
        if (this.random.nextInt(10) == 0) {
            this.playSound(SoundEvents.HORSE_BREATHE, pSoundType.getVolume() * 0.6F, pSoundType.getPitch());
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HORSE_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getEatingSound() {
        return SoundEvents.HORSE_EAT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return SoundEvents.HORSE_ANGRY;
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        boolean flag = !this.isBaby() && this.isTamed() && pPlayer.isSecondaryUseActive();
        if (!this.isVehicle() && !flag) {
            ItemStack itemstack = pPlayer.getItemInHand(pHand);
            if (!itemstack.isEmpty()) {
                if (this.isFood(itemstack)) {
                    return this.fedFood(pPlayer, itemstack);
                }

                if (!this.isTamed()) {
                    this.makeMad();
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }

            return super.mobInteract(pPlayer, pHand);
        } else {
            return super.mobInteract(pPlayer, pHand);
        }
    }

    @Override
    public boolean canMate(Animal pOtherAnimal) {
        if (pOtherAnimal == this) {
            return false;
        } else {
            return !(pOtherAnimal instanceof Donkey) && !(pOtherAnimal instanceof Horse) ? false : this.canParent() && ((AbstractHorse)pOtherAnimal).canParent();
        }
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        if (pOtherParent instanceof Donkey) {
            Mule mule = EntityType.MULE.create(pLevel);
            if (mule != null) {
                this.setOffspringAttributes(pOtherParent, mule);
            }

            return mule;
        } else {
            Horse horse = (Horse)pOtherParent;
            Horse horse1 = EntityType.HORSE.create(pLevel);
            if (horse1 != null) {
                int i = this.random.nextInt(9);
                Variant variant;
                if (i < 4) {
                    variant = this.getVariant();
                } else if (i < 8) {
                    variant = horse.getVariant();
                } else {
                    variant = Util.getRandom(Variant.values(), this.random);
                }

                int j = this.random.nextInt(5);
                Markings markings;
                if (j < 2) {
                    markings = this.getMarkings();
                } else if (j < 4) {
                    markings = horse.getMarkings();
                } else {
                    markings = Util.getRandom(Markings.values(), this.random);
                }

                horse1.setVariantAndMarkings(variant, markings);
                this.setOffspringAttributes(pOtherParent, horse1);
            }

            return horse1;
        }
    }

    @Override
    public boolean m_30729_() {
        return true;
    }

    @Override
    public boolean m_30833_(ItemStack p_328512_) {
        if (p_328512_.getItem() instanceof AnimalArmorItem animalarmoritem && animalarmoritem.m_319458_() == AnimalArmorItem.BodyType.EQUESTRIAN) {
            return true;
        }

        return false;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        RandomSource randomsource = pLevel.getRandom();
        Variant variant;
        if (pSpawnData instanceof Horse.HorseGroupData) {
            variant = ((Horse.HorseGroupData)pSpawnData).variant;
        } else {
            variant = Util.getRandom(Variant.values(), randomsource);
            pSpawnData = new Horse.HorseGroupData(variant);
        }

        this.setVariantAndMarkings(variant, Util.getRandom(Markings.values(), randomsource));
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
    }

    @Override
    public EntityDimensions m_31586_(Pose p_329389_) {
        return this.isBaby() ? f_315715_ : super.m_31586_(p_329389_);
    }

    public static class HorseGroupData extends AgeableMob.AgeableMobGroupData {
        public final Variant variant;

        public HorseGroupData(Variant pVariant) {
            super(true);
            this.variant = pVariant;
        }
    }
}