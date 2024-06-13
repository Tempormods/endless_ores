package net.minecraft.world.entity.vehicle;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public abstract class VehicleEntity extends Entity {
    protected static final EntityDataAccessor<Integer> f_302249_ = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Integer> f_302571_ = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> f_302371_ = SynchedEntityData.defineId(VehicleEntity.class, EntityDataSerializers.FLOAT);

    public VehicleEntity(EntityType<?> p_310168_, Level p_309578_) {
        super(p_310168_, p_309578_);
    }

    @Override
    public boolean hurt(DamageSource p_310829_, float p_310313_) {
        if (this.level().isClientSide || this.isRemoved()) {
            return true;
        } else if (this.isInvulnerableTo(p_310829_)) {
            return false;
        } else {
            this.m_306256_(-this.m_305195_());
            this.m_307446_(10);
            this.markHurt();
            this.m_305563_(this.m_304923_() + p_310313_ * 10.0F);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, p_310829_.getEntity());
            boolean flag = p_310829_.getEntity() instanceof Player && ((Player)p_310829_.getEntity()).getAbilities().instabuild;
            if ((flag || !(this.m_304923_() > 40.0F)) && !this.m_304763_(p_310829_)) {
                if (flag) {
                    this.discard();
                }
            } else {
                this.m_38227_(p_310829_);
            }

            return true;
        }
    }

    boolean m_304763_(DamageSource p_309621_) {
        return false;
    }

    public void m_305179_(Item p_313028_) {
        this.kill();
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack itemstack = new ItemStack(p_313028_);
            itemstack.m_322496_(DataComponents.f_316016_, this.getCustomName());
            this.spawnAtLocation(itemstack);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_332479_) {
        p_332479_.m_318949_(f_302249_, 0);
        p_332479_.m_318949_(f_302571_, 1);
        p_332479_.m_318949_(f_302371_, 0.0F);
    }

    public void m_307446_(int p_312621_) {
        this.entityData.set(f_302249_, p_312621_);
    }

    public void m_306256_(int p_312074_) {
        this.entityData.set(f_302571_, p_312074_);
    }

    public void m_305563_(float p_313007_) {
        this.entityData.set(f_302371_, p_313007_);
    }

    public float m_304923_() {
        return this.entityData.get(f_302371_);
    }

    public int m_305464_() {
        return this.entityData.get(f_302249_);
    }

    public int m_305195_() {
        return this.entityData.get(f_302571_);
    }

    protected void m_38227_(DamageSource p_312900_) {
        this.m_305179_(this.getDropItem());
    }

    abstract Item getDropItem();
}