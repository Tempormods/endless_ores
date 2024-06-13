package net.minecraft.world.entity.monster;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class Bogged extends AbstractSkeleton implements Shearable {
    private static final int f_316368_ = 50;
    private static final int f_315805_ = 70;
    private static final EntityDataAccessor<Boolean> f_314507_ = SynchedEntityData.defineId(Bogged.class, EntityDataSerializers.BOOLEAN);
    public static final String f_314426_ = "sheared";

    public static AttributeSupplier.Builder m_321579_() {
        return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0);
    }

    public Bogged(EntityType<? extends Bogged> p_333629_, Level p_333576_) {
        super(p_333629_, p_333576_);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_335065_) {
        super.defineSynchedData(p_335065_);
        p_335065_.m_318949_(f_314507_, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_334451_) {
        super.addAdditionalSaveData(p_334451_);
        p_334451_.putBoolean("sheared", this.m_324815_());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_335260_) {
        super.readAdditionalSaveData(p_335260_);
        this.m_320526_(p_335260_.getBoolean("sheared"));
    }

    public boolean m_324815_() {
        return this.entityData.get(f_314507_);
    }

    public void m_320526_(boolean p_329679_) {
        this.entityData.set(f_314507_, p_329679_);
    }

    @Override
    protected InteractionResult mobInteract(Player p_335303_, InteractionHand p_335145_) {
        ItemStack itemstack = p_335303_.getItemInHand(p_335145_);
        if (itemstack.is(Items.SHEARS) && this.readyForShearing()) {
            this.shear(SoundSource.PLAYERS);
            this.gameEvent(GameEvent.SHEAR, p_335303_);
            if (!this.level().isClientSide) {
                itemstack.hurtAndBreak(1, p_335303_, m_322775_(p_335145_));
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            return super.mobInteract(p_335303_, p_335145_);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.f_316155_;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_331504_) {
        return SoundEvents.f_317090_;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.f_314018_;
    }

    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.f_316866_;
    }

    @Override
    protected AbstractArrow getArrow(ItemStack p_333556_, float p_332689_) {
        AbstractArrow abstractarrow = super.getArrow(p_333556_, p_332689_);
        if (abstractarrow instanceof Arrow arrow) {
            arrow.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
        }

        return abstractarrow;
    }

    @Override
    protected int m_320361_() {
        return 50;
    }

    @Override
    protected int m_324015_() {
        return 70;
    }

    @Override
    public void shear(SoundSource p_332421_) {
        this.level().playSound(null, this, SoundEvents.f_314257_, p_332421_, 1.0F, 1.0F);
        this.m_324290_();
        this.m_320526_(true);
    }

    private void m_324290_() {
        if (this.level() instanceof ServerLevel serverlevel && serverlevel.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            LootTable loottable = serverlevel.getServer().m_323018_().m_321428_(BuiltInLootTables.f_315144_);
            LootParams lootparams = new LootParams.Builder(serverlevel)
                .withParameter(LootContextParams.ORIGIN, this.position())
                .withParameter(LootContextParams.THIS_ENTITY, this)
                .create(LootContextParamSets.f_316219_);

            for (ItemStack itemstack : loottable.getRandomItems(lootparams)) {
                this.spawnAtLocation(itemstack);
            }
        }
    }

    @Override
    public boolean readyForShearing() {
        return !this.m_324815_() && this.isAlive();
    }
}