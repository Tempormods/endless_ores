package net.minecraft.world.entity.animal.horse;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class ZombieHorse extends AbstractHorse {
    private static final EntityDimensions f_314000_ = EntityType.ZOMBIE_HORSE
        .getDimensions()
        .m_323271_(EntityAttachments.m_321590_().m_322342_(EntityAttachment.PASSENGER, 0.0F, EntityType.ZOMBIE_HORSE.getHeight() - 0.03125F, 0.0F))
        .scale(0.5F);

    public ZombieHorse(EntityType<? extends ZombieHorse> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 15.0).add(Attributes.MOVEMENT_SPEED, 0.2F);
    }

    public static boolean m_305739_(
        EntityType<? extends Animal> p_313002_, LevelAccessor p_310822_, MobSpawnType p_312863_, BlockPos p_311078_, RandomSource p_312131_
    ) {
        return !MobSpawnType.m_307588_(p_312863_)
            ? Animal.checkAnimalSpawnRules(p_313002_, p_310822_, p_312863_, p_311078_, p_312131_)
            : MobSpawnType.m_307154_(p_312863_) || isBrightEnoughToSpawn(p_310822_, p_311078_);
    }

    @Override
    protected void randomizeAttributes(RandomSource pRandom) {
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(generateJumpStrength(pRandom::nextDouble));
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ZOMBIE_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_HORSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource pDamageSource) {
        return SoundEvents.ZOMBIE_HORSE_HURT;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel pLevel, AgeableMob pOtherParent) {
        return EntityType.ZOMBIE_HORSE.create(pLevel);
    }

    @Override
    public InteractionResult mobInteract(Player pPlayer, InteractionHand pHand) {
        return !this.isTamed() ? InteractionResult.PASS : super.mobInteract(pPlayer, pHand);
    }

    @Override
    protected void addBehaviourGoals() {
    }

    @Override
    public EntityDimensions m_31586_(Pose p_335204_) {
        return this.isBaby() ? f_314000_ : super.m_31586_(p_335204_);
    }
}