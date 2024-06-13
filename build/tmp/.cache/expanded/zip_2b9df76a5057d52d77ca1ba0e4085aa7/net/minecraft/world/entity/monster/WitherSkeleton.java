package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;

public class WitherSkeleton extends AbstractSkeleton {
    public WitherSkeleton(EntityType<? extends WitherSkeleton> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setPathfindingMalus(PathType.LAVA, 8.0F);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
        super.registerGoals();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        if (pSource.getEntity() instanceof Creeper creeper && creeper.canDropMobsSkull()) {
            creeper.increaseDroppedSkulls();
            this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource pRandom, DifficultyInstance pDifficulty) {
        this.m_21035_(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
    }

    @Override
    protected void populateDefaultEquipmentEnchantments(RandomSource pRandom, DifficultyInstance pDifficulty) {
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData) {
        SpawnGroupData spawngroupdata = super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0);
        this.reassessWeaponGoal();
        return spawngroupdata;
    }

    @Override
    public boolean doHurtTarget(Entity pEntity) {
        if (!super.doHurtTarget(pEntity)) {
            return false;
        } else {
            if (pEntity instanceof LivingEntity) {
                ((LivingEntity)pEntity).addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
            }

            return true;
        }
    }

    @Override
    protected AbstractArrow getArrow(ItemStack pArrowStack, float pDistanceFactor) {
        AbstractArrow abstractarrow = super.getArrow(pArrowStack, pDistanceFactor);
        abstractarrow.m_322706_(100);
        return abstractarrow;
    }

    @Override
    public boolean canBeAffected(MobEffectInstance pPotioneffect) {
        return pPotioneffect.m_323663_(MobEffects.WITHER) ? false : super.canBeAffected(pPotioneffect);
    }
}