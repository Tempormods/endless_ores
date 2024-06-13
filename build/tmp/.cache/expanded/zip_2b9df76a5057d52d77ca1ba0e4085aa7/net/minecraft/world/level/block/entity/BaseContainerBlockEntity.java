package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseContainerBlockEntity extends BlockEntity implements Container, MenuProvider, Nameable {
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private Component name;

    protected BaseContainerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    protected void m_318667_(CompoundTag p_335335_, HolderLookup.Provider p_329555_) {
        super.m_318667_(p_335335_, p_329555_);
        this.lockKey = LockCode.fromTag(p_335335_);
        if (p_335335_.contains("CustomName", 8)) {
            this.name = m_336414_(p_335335_.getString("CustomName"), p_329555_);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_335192_) {
        super.saveAdditional(pTag, p_335192_);
        this.lockKey.addToTag(pTag);
        if (this.name != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.name, p_335192_));
        }
    }

    @Override
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    protected abstract Component getDefaultName();

    public boolean canOpen(Player pPlayer) {
        return canUnlock(pPlayer, this.lockKey, this.getDisplayName());
    }

    public static boolean canUnlock(Player pPlayer, LockCode pCode, Component pDisplayName) {
        if (!pPlayer.isSpectator() && !pCode.unlocksWith(pPlayer.getMainHandItem())) {
            pPlayer.displayClientMessage(Component.translatable("container.isLocked", pDisplayName), true);
            pPlayer.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    protected abstract NonNullList<ItemStack> m_58617_();

    protected abstract void m_58609_(NonNullList<ItemStack> p_330472_);

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.m_58617_()) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int p_334660_) {
        return this.m_58617_().get(p_334660_);
    }

    @Override
    public ItemStack removeItem(int p_333934_, int p_332088_) {
        ItemStack itemstack = ContainerHelper.removeItem(this.m_58617_(), p_333934_, p_332088_);
        if (!itemstack.isEmpty()) {
            this.setChanged();
        }

        return itemstack;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_329940_) {
        return ContainerHelper.takeItem(this.m_58617_(), p_329940_);
    }

    @Override
    public void setItem(int p_331067_, ItemStack p_333112_) {
        this.m_58617_().set(p_331067_, p_333112_);
        p_333112_.m_324521_(this.m_322387_(p_333112_));
        this.setChanged();
    }

    @Override
    public boolean stillValid(Player p_330935_) {
        return Container.stillValidBlockEntity(this, p_330935_);
    }

    @Override
    public void clearContent() {
        this.m_58617_().clear();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return this.canOpen(pPlayer) ? this.createMenu(pContainerId, pPlayerInventory) : null;
    }

    protected abstract AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory);

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_329127_) {
        super.m_318741_(p_329127_);
        this.name = p_329127_.m_319293_(DataComponents.f_316016_);
        this.lockKey = p_329127_.m_319031_(DataComponents.f_315242_, LockCode.NO_LOCK);
        p_329127_.m_319031_(DataComponents.f_316065_, ItemContainerContents.f_316619_).m_322022_(this.m_58617_());
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_336292_) {
        super.m_318837_(p_336292_);
        p_336292_.m_322739_(DataComponents.f_316016_, this.name);
        if (!this.lockKey.equals(LockCode.NO_LOCK)) {
            p_336292_.m_322739_(DataComponents.f_315242_, this.lockKey);
        }

        p_336292_.m_322739_(DataComponents.f_316065_, ItemContainerContents.m_320241_(this.m_58617_()));
    }

    @Override
    public void m_318942_(CompoundTag p_329140_) {
        p_329140_.remove("CustomName");
        p_329140_.remove("Lock");
        p_329140_.remove("Items");
    }

    private net.minecraftforge.common.util.LazyOptional<?> itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> createUnSidedHandler());
    protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @org.jetbrains.annotations.Nullable net.minecraft.core.Direction side) {
        if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER && !this.remove)
            return itemHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        itemHandler = net.minecraftforge.common.util.LazyOptional.of(() -> createUnSidedHandler());
    }
}
