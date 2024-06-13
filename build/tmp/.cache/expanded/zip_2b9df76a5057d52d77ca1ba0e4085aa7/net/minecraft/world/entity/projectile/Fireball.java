package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class Fireball extends AbstractHurtingProjectile implements ItemSupplier {
    private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);

    public Fireball(EntityType<? extends Fireball> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public Fireball(
        EntityType<? extends Fireball> pEntityType,
        double pX,
        double pY,
        double pZ,
        double pOffsetX,
        double pOffsetY,
        double pOffsetZ,
        Level pLevel
    ) {
        super(pEntityType, pX, pY, pZ, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }

    public Fireball(EntityType<? extends Fireball> pEntityType, LivingEntity pShooter, double pOffsetX, double pOffsetY, double pOffsetZ, Level pLevel) {
        super(pEntityType, pShooter, pOffsetX, pOffsetY, pOffsetZ, pLevel);
    }

    public void setItem(ItemStack pStack) {
        if (pStack.isEmpty()) {
            this.getEntityData().set(DATA_ITEM_STACK, this.m_319273_());
        } else {
            this.getEntityData().set(DATA_ITEM_STACK, pStack.copyWithCount(1));
        }
    }

    @Override
    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM_STACK);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_330316_) {
        p_330316_.m_318949_(DATA_ITEM_STACK, this.m_319273_());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Item", this.getItem().save(this.m_321891_()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Item", 10)) {
            this.setItem(ItemStack.m_323951_(this.m_321891_(), pCompound.getCompound("Item")).orElse(this.m_319273_()));
        } else {
            this.setItem(this.m_319273_());
        }
    }

    private ItemStack m_319273_() {
        return new ItemStack(Items.FIRE_CHARGE);
    }

    @Override
    public SlotAccess getSlot(int p_332914_) {
        return p_332914_ == 0 ? SlotAccess.m_320701_(this::getItem, this::setItem) : super.getSlot(p_332914_);
    }
}