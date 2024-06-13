package net.minecraft.world.level.block.entity;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.CrafterMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterBlockEntity extends RandomizableContainerBlockEntity implements CraftingContainer {
    public static final int f_302244_ = 3;
    public static final int f_303071_ = 3;
    public static final int f_303067_ = 9;
    public static final int f_302805_ = 1;
    public static final int f_302934_ = 0;
    public static final int f_302256_ = 9;
    public static final int f_302301_ = 10;
    private NonNullList<ItemStack> f_303344_ = NonNullList.withSize(9, ItemStack.EMPTY);
    private int f_302459_ = 0;
    protected final ContainerData f_303488_ = new ContainerData() {
        private final int[] f_303128_ = new int[9];
        private int f_303025_ = 0;

        @Override
        public int get(int p_310435_) {
            return p_310435_ == 9 ? this.f_303025_ : this.f_303128_[p_310435_];
        }

        @Override
        public void set(int p_313229_, int p_312585_) {
            if (p_313229_ == 9) {
                this.f_303025_ = p_312585_;
            } else {
                this.f_303128_[p_313229_] = p_312585_;
            }
        }

        @Override
        public int getCount() {
            return 10;
        }
    };

    public CrafterBlockEntity(BlockPos p_309972_, BlockState p_313058_) {
        super(BlockEntityType.f_302698_, p_309972_, p_313058_);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.crafter");
    }

    @Override
    protected AbstractContainerMenu createMenu(int p_312650_, Inventory p_309858_) {
        return new CrafterMenu(p_312650_, p_309858_, this, this.f_303488_);
    }

    public void m_306488_(int p_310046_, boolean p_310331_) {
        if (this.m_305899_(p_310046_)) {
            this.f_303488_.set(p_310046_, p_310331_ ? 0 : 1);
            this.setChanged();
        }
    }

    public boolean m_307238_(int p_312222_) {
        return p_312222_ >= 0 && p_312222_ < 9 ? this.f_303488_.get(p_312222_) == 1 : false;
    }

    @Override
    public boolean canPlaceItem(int p_311324_, ItemStack p_312777_) {
        if (this.f_303488_.get(p_311324_) == 1) {
            return false;
        } else {
            ItemStack itemstack = this.f_303344_.get(p_311324_);
            int i = itemstack.getCount();
            if (i >= itemstack.getMaxStackSize()) {
                return false;
            } else {
                return itemstack.isEmpty() ? true : !this.m_306964_(i, itemstack, p_311324_);
            }
        }
    }

    private boolean m_306964_(int p_312152_, ItemStack p_309554_, int p_312872_) {
        for (int i = p_312872_ + 1; i < 9; i++) {
            if (!this.m_307238_(i)) {
                ItemStack itemstack = this.getItem(i);
                if (itemstack.isEmpty() || itemstack.getCount() < p_312152_ && ItemStack.m_322370_(itemstack, p_309554_)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void m_318667_(CompoundTag p_328373_, HolderLookup.Provider p_328741_) {
        super.m_318667_(p_328373_, p_328741_);
        this.f_302459_ = p_328373_.getInt("crafting_ticks_remaining");
        this.f_303344_ = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.m_307714_(p_328373_)) {
            ContainerHelper.loadAllItems(p_328373_, this.f_303344_, p_328741_);
        }

        int[] aint = p_328373_.getIntArray("disabled_slots");

        for (int i = 0; i < 9; i++) {
            this.f_303488_.set(i, 0);
        }

        for (int j : aint) {
            if (this.m_305899_(j)) {
                this.f_303488_.set(j, 1);
            }
        }

        this.f_303488_.set(9, p_328373_.getInt("triggered"));
    }

    @Override
    protected void saveAdditional(CompoundTag p_309594_, HolderLookup.Provider p_330681_) {
        super.saveAdditional(p_309594_, p_330681_);
        p_309594_.putInt("crafting_ticks_remaining", this.f_302459_);
        if (!this.m_306148_(p_309594_)) {
            ContainerHelper.saveAllItems(p_309594_, this.f_303344_, p_330681_);
        }

        this.m_306746_(p_309594_);
        this.m_306781_(p_309594_);
    }

    @Override
    public int getContainerSize() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.f_303344_) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack getItem(int p_310446_) {
        return this.f_303344_.get(p_310446_);
    }

    @Override
    public void setItem(int p_312882_, ItemStack p_311521_) {
        if (this.m_307238_(p_312882_)) {
            this.m_306488_(p_312882_, true);
        }

        super.setItem(p_312882_, p_311521_);
    }

    @Override
    public boolean stillValid(Player p_311318_) {
        return Container.stillValidBlockEntity(this, p_311318_);
    }

    @Override
    public NonNullList<ItemStack> m_58617_() {
        return this.f_303344_;
    }

    @Override
    protected void m_58609_(NonNullList<ItemStack> p_311420_) {
        this.f_303344_ = p_311420_;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 3;
    }

    @Override
    public void fillStackedContents(StackedContents p_310482_) {
        for (ItemStack itemstack : this.f_303344_) {
            p_310482_.accountSimpleStack(itemstack);
        }
    }

    private void m_306746_(CompoundTag p_309756_) {
        IntList intlist = new IntArrayList();

        for (int i = 0; i < 9; i++) {
            if (this.m_307238_(i)) {
                intlist.add(i);
            }
        }

        p_309756_.putIntArray("disabled_slots", intlist);
    }

    private void m_306781_(CompoundTag p_312165_) {
        p_312165_.putInt("triggered", this.f_303488_.get(9));
    }

    public void m_305342_(boolean p_311394_) {
        this.f_303488_.set(9, p_311394_ ? 1 : 0);
    }

    @VisibleForTesting
    public boolean m_307236_() {
        return this.f_303488_.get(9) == 1;
    }

    public static void m_307890_(Level p_311764_, BlockPos p_309568_, BlockState p_311393_, CrafterBlockEntity p_313070_) {
        int i = p_313070_.f_302459_ - 1;
        if (i >= 0) {
            p_313070_.f_302459_ = i;
            if (i == 0) {
                p_311764_.setBlock(p_309568_, p_311393_.setValue(CrafterBlock.f_302342_, Boolean.valueOf(false)), 3);
            }
        }
    }

    public void m_305296_(int p_312384_) {
        this.f_302459_ = p_312384_;
    }

    public int m_304952_() {
        int i = 0;

        for (int j = 0; j < this.getContainerSize(); j++) {
            ItemStack itemstack = this.getItem(j);
            if (!itemstack.isEmpty() || this.m_307238_(j)) {
                i++;
            }
        }

        return i;
    }

    private boolean m_305899_(int p_309429_) {
        return p_309429_ > -1 && p_309429_ < 9 && this.f_303344_.get(p_309429_).isEmpty();
    }
}