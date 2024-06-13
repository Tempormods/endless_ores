package net.minecraft.world.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownPotion extends ThrowableItemProjectile implements ItemSupplier {
    public static final double SPLASH_RANGE = 4.0;
    private static final double SPLASH_RANGE_SQ = 16.0;
    public static final Predicate<LivingEntity> WATER_SENSITIVE_OR_ON_FIRE = p_327059_ -> p_327059_.isSensitiveToWater() || p_327059_.isOnFire();

    public ThrownPotion(EntityType<? extends ThrownPotion> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public ThrownPotion(Level pLevel, LivingEntity pShooter) {
        super(EntityType.POTION, pShooter, pLevel);
    }

    public ThrownPotion(Level pLevel, double pX, double pY, double pZ) {
        super(EntityType.POTION, pX, pY, pZ, pLevel);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    protected double m_319564_() {
        return 0.05;
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level().isClientSide) {
            ItemStack itemstack = this.getItem();
            Direction direction = pResult.getDirection();
            BlockPos blockpos = pResult.getBlockPos();
            BlockPos blockpos1 = blockpos.relative(direction);
            PotionContents potioncontents = itemstack.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_);
            if (potioncontents.m_323649_(Potions.WATER)) {
                this.dowseFire(blockpos1);
                this.dowseFire(blockpos1.relative(direction.getOpposite()));

                for (Direction direction1 : Direction.Plane.HORIZONTAL) {
                    this.dowseFire(blockpos1.relative(direction1));
                }
            }
        }
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level().isClientSide) {
            ItemStack itemstack = this.getItem();
            PotionContents potioncontents = itemstack.m_322304_(DataComponents.f_314188_, PotionContents.f_313984_);
            if (potioncontents.m_323649_(Potions.WATER)) {
                this.applyWater();
            } else if (potioncontents.m_323528_()) {
                if (this.isLingering()) {
                    this.makeAreaOfEffectCloud(potioncontents);
                } else {
                    this.applySplash(potioncontents.m_319638_(), pResult.getType() == HitResult.Type.ENTITY ? ((EntityHitResult)pResult).getEntity() : null);
                }
            }

            int i = potioncontents.f_317059_().isPresent() && potioncontents.f_317059_().get().value().hasInstantEffects() ? 2007 : 2002;
            this.level().levelEvent(i, this.blockPosition(), potioncontents.m_318943_());
            this.discard();
        }
    }

    private void applyWater() {
        AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);

        for (LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, aabb, WATER_SENSITIVE_OR_ON_FIRE)) {
            double d0 = this.distanceToSqr(livingentity);
            if (d0 < 16.0) {
                if (livingentity.isSensitiveToWater()) {
                    livingentity.hurt(this.damageSources().indirectMagic(this, this.getOwner()), 1.0F);
                }

                if (livingentity.isOnFire() && livingentity.isAlive()) {
                    livingentity.extinguishFire();
                }
            }
        }

        for (Axolotl axolotl : this.level().getEntitiesOfClass(Axolotl.class, aabb)) {
            axolotl.rehydrate();
        }
    }

    private void applySplash(Iterable<MobEffectInstance> p_329810_, @Nullable Entity pTarget) {
        AABB aabb = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
        List<LivingEntity> list = this.level().getEntitiesOfClass(LivingEntity.class, aabb);
        if (!list.isEmpty()) {
            Entity entity = this.getEffectSource();

            for (LivingEntity livingentity : list) {
                if (livingentity.isAffectedByPotions()) {
                    double d0 = this.distanceToSqr(livingentity);
                    if (d0 < 16.0) {
                        double d1;
                        if (livingentity == pTarget) {
                            d1 = 1.0;
                        } else {
                            d1 = 1.0 - Math.sqrt(d0) / 4.0;
                        }

                        for (MobEffectInstance mobeffectinstance : p_329810_) {
                            Holder<MobEffect> holder = mobeffectinstance.getEffect();
                            if (holder.value().isInstantenous()) {
                                holder.value().applyInstantenousEffect(this, this.getOwner(), livingentity, mobeffectinstance.getAmplifier(), d1);
                            } else {
                                int i = mobeffectinstance.mapDuration(p_267930_ -> (int)(d1 * (double)p_267930_ + 0.5));
                                MobEffectInstance mobeffectinstance1 = new MobEffectInstance(
                                    holder, i, mobeffectinstance.getAmplifier(), mobeffectinstance.isAmbient(), mobeffectinstance.isVisible()
                                );
                                if (!mobeffectinstance1.endsWithin(20)) {
                                    livingentity.addEffect(mobeffectinstance1, entity);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void makeAreaOfEffectCloud(PotionContents p_331360_) {
        AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
        if (this.getOwner() instanceof LivingEntity livingentity) {
            areaeffectcloud.setOwner(livingentity);
        }

        areaeffectcloud.setRadius(3.0F);
        areaeffectcloud.setRadiusOnUse(-0.5F);
        areaeffectcloud.setWaitTime(10);
        areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());
        areaeffectcloud.m_323515_(p_331360_);
        this.level().addFreshEntity(areaeffectcloud);
    }

    private boolean isLingering() {
        return this.getItem().is(Items.LINGERING_POTION);
    }

    private void dowseFire(BlockPos pPos) {
        BlockState blockstate = this.level().getBlockState(pPos);
        if (blockstate.is(BlockTags.FIRE)) {
            this.level().destroyBlock(pPos, false, this);
        } else if (AbstractCandleBlock.isLit(blockstate)) {
            AbstractCandleBlock.extinguish(null, blockstate, this.level(), pPos);
        } else if (CampfireBlock.isLitCampfire(blockstate)) {
            this.level().levelEvent(null, 1009, pPos, 0);
            CampfireBlock.dowse(this.getOwner(), this.level(), pPos, blockstate);
            this.level().setBlockAndUpdate(pPos, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
        }
    }
}