package net.minecraft.world.entity;

import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;

public class OminousItemSpawner extends Entity {
    private static final int f_314442_ = 60;
    private static final int f_316767_ = 120;
    private static final String f_314027_ = "spawn_item_after_ticks";
    private static final String f_316560_ = "item";
    private static final EntityDataAccessor<ItemStack> f_316760_ = SynchedEntityData.defineId(OminousItemSpawner.class, EntityDataSerializers.ITEM_STACK);
    public static final int f_316999_ = 36;
    private long f_314138_;

    public OminousItemSpawner(EntityType<? extends OminousItemSpawner> p_330436_, Level p_334777_) {
        super(p_330436_, p_334777_);
        this.noPhysics = true;
    }

    public static OminousItemSpawner m_321091_(Level p_328154_, ItemStack p_332415_) {
        OminousItemSpawner ominousitemspawner = new OminousItemSpawner(EntityType.f_314497_, p_328154_);
        ominousitemspawner.f_314138_ = (long)p_328154_.random.nextIntBetweenInclusive(60, 120);
        ominousitemspawner.m_324856_(p_332415_);
        return ominousitemspawner;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            this.m_324374_();
        } else {
            this.m_322175_();
        }
    }

    private void m_322175_() {
        if ((long)this.tickCount == this.f_314138_ - 36L) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.f_316078_, SoundSource.NEUTRAL);
        }

        if ((long)this.tickCount >= this.f_314138_) {
            this.m_320146_();
            this.kill();
        }
    }

    private void m_324374_() {
        if (this.level().getGameTime() % 5L == 0L) {
            this.m_322289_();
        }
    }

    private void m_320146_() {
        Level level = this.level();
        ItemStack itemstack = this.m_318820_();
        if (!itemstack.isEmpty()) {
            Entity entity;
            if (itemstack.getItem() instanceof ProjectileItem projectileitem) {
                Direction direction = Direction.DOWN;
                Projectile projectile = projectileitem.m_319847_(level, this.position(), itemstack, direction);
                ProjectileItem.DispenseConfig projectileitem$dispenseconfig = projectileitem.m_320420_();
                projectileitem.m_319015_(
                    projectile,
                    (double)direction.getStepX(),
                    (double)direction.getStepY(),
                    (double)direction.getStepZ(),
                    projectileitem$dispenseconfig.f_317028_(),
                    projectileitem$dispenseconfig.f_315383_()
                );
                projectileitem$dispenseconfig.f_314791_().ifPresent(p_330249_ -> level.levelEvent(p_330249_, this.blockPosition(), 0));
                entity = projectile;
            } else {
                entity = new ItemEntity(level, this.getX(), this.getY(), this.getZ(), itemstack);
            }

            level.addFreshEntity(entity);
            level.levelEvent(3021, this.blockPosition(), 1);
            level.gameEvent(entity, GameEvent.ENTITY_PLACE, this.position());
            this.m_324856_(ItemStack.EMPTY);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_330200_) {
        p_330200_.m_318949_(f_316760_, ItemStack.EMPTY);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_331393_) {
        ItemStack itemstack = p_331393_.contains("item", 10)
            ? ItemStack.m_323951_(this.m_321891_(), p_331393_.getCompound("item")).orElse(ItemStack.EMPTY)
            : ItemStack.EMPTY;
        this.m_324856_(itemstack);
        p_331393_.getLong("spawn_item_after_ticks");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_329696_) {
        if (!this.m_318820_().isEmpty()) {
            p_329696_.put("item", this.m_318820_().save(this.m_321891_()).copy());
        }

        p_329696_.putLong("spawn_item_after_ticks", this.f_314138_);
    }

    @Override
    protected boolean canAddPassenger(Entity p_332041_) {
        return false;
    }

    @Override
    protected boolean couldAcceptPassenger() {
        return false;
    }

    @Override
    protected void addPassenger(Entity p_333815_) {
        throw new IllegalStateException("Should never addPassenger without checking couldAcceptPassenger()");
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    public void m_322289_() {
        Vec3 vec3 = this.position();
        int i = this.random.nextIntBetweenInclusive(1, 3);

        for (int j = 0; j < i; j++) {
            double d0 = 0.4;
            Vec3 vec31 = new Vec3(
                this.getX() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
                this.getY() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian()),
                this.getZ() + 0.4 * (this.random.nextGaussian() - this.random.nextGaussian())
            );
            Vec3 vec32 = vec3.vectorTo(vec31);
            this.level().addParticle(ParticleTypes.f_314395_, vec3.x(), vec3.y(), vec3.z(), vec32.x(), vec32.y(), vec32.z());
        }
    }

    public ItemStack m_318820_() {
        return this.getEntityData().get(f_316760_);
    }

    private void m_324856_(ItemStack p_328604_) {
        this.getEntityData().set(f_316760_, p_328604_);
    }
}