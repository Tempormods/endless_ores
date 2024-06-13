package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DispenserMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DispenserBlockEntity extends RandomizableContainerBlockEntity {
    public static final int CONTAINER_SIZE = 9;
    private NonNullList<ItemStack> items = NonNullList.withSize(9, ItemStack.EMPTY);

    protected DispenserBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public DispenserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(BlockEntityType.DISPENSER, pPos, pBlockState);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    public int getRandomSlot(RandomSource pRandom) {
        this.m_306438_(null);
        int i = -1;
        int j = 1;

        for (int k = 0; k < this.items.size(); k++) {
            if (!this.items.get(k).isEmpty() && pRandom.nextInt(j++) == 0) {
                i = k;
            }
        }

        return i;
    }

    public int addItem(ItemStack pStack) {
        for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i).isEmpty()) {
                this.setItem(i, pStack);
                return i;
            }
        }

        return -1;
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.dispenser");
    }

    @Override
    protected void m_318667_(CompoundTag p_332674_, HolderLookup.Provider p_335532_) {
        super.m_318667_(p_332674_, p_335532_);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.m_307714_(p_332674_)) {
            ContainerHelper.loadAllItems(p_332674_, this.items, p_335532_);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_328492_) {
        super.saveAdditional(pTag, p_328492_);
        if (!this.m_306148_(pTag)) {
            ContainerHelper.saveAllItems(pTag, this.items, p_328492_);
        }
    }

    @Override
    protected NonNullList<ItemStack> m_58617_() {
        return this.items;
    }

    @Override
    protected void m_58609_(NonNullList<ItemStack> pItems) {
        this.items = pItems;
    }

    @Override
    protected AbstractContainerMenu createMenu(int pId, Inventory pPlayer) {
        return new DispenserMenu(pId, pPlayer, this);
    }
}