package net.minecraft.world.ticks;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface ContainerSingleItem extends Container {
    ItemStack m_306082_();

    default ItemStack m_305214_(int p_312245_) {
        return this.m_306082_().split(p_312245_);
    }

    void m_305072_(ItemStack p_310917_);

    default ItemStack m_306595_() {
        return this.m_305214_(this.getMaxStackSize());
    }

    @Override
    default int getContainerSize() {
        return 1;
    }

    @Override
    default boolean isEmpty() {
        return this.m_306082_().isEmpty();
    }

    @Override
    default void clearContent() {
        this.m_306595_();
    }

    @Override
    default ItemStack removeItemNoUpdate(int pSlot) {
        return this.removeItem(pSlot, this.getMaxStackSize());
    }

    @Override
    default ItemStack getItem(int p_309780_) {
        return p_309780_ == 0 ? this.m_306082_() : ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItem(int p_313221_, int p_309913_) {
        return p_313221_ != 0 ? ItemStack.EMPTY : this.m_305214_(p_309913_);
    }

    @Override
    default void setItem(int p_312121_, ItemStack p_312812_) {
        if (p_312121_ == 0) {
            this.m_305072_(p_312812_);
        }
    }

    public interface BlockContainerSingleItem extends ContainerSingleItem {
        BlockEntity m_304707_();

        @Override
        default boolean stillValid(Player p_335018_) {
            return Container.stillValidBlockEntity(this.m_304707_(), p_335018_);
        }
    }
}