package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity implements RandomizableContainer {
    @Nullable
    protected ResourceKey<LootTable> lootTable;
    protected long lootTableSeed = 0L;

    protected RandomizableContainerBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> m_305426_() {
        return this.lootTable;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> p_328444_) {
        this.lootTable = p_328444_;
    }

    @Override
    public long m_305628_() {
        return this.lootTableSeed;
    }

    @Override
    public void m_305699_(long p_311658_) {
        this.lootTableSeed = p_311658_;
    }

    @Override
    public boolean isEmpty() {
        this.m_306438_(null);
        return super.isEmpty();
    }

    @Override
    public ItemStack getItem(int pIndex) {
        this.m_306438_(null);
        return super.getItem(pIndex);
    }

    @Override
    public ItemStack removeItem(int pIndex, int pCount) {
        this.m_306438_(null);
        return super.removeItem(pIndex, pCount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int pIndex) {
        this.m_306438_(null);
        return super.removeItemNoUpdate(pIndex);
    }

    @Override
    public void setItem(int pIndex, ItemStack pStack) {
        this.m_306438_(null);
        super.setItem(pIndex, pStack);
    }

    @Override
    public boolean canOpen(Player pPlayer) {
        return super.canOpen(pPlayer) && (this.lootTable == null || !pPlayer.isSpectator());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        if (this.canOpen(pPlayer)) {
            this.m_306438_(pPlayerInventory.player);
            return this.createMenu(pContainerId, pPlayerInventory);
        } else {
            return null;
        }
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_330597_) {
        super.m_318741_(p_330597_);
        SeededContainerLoot seededcontainerloot = p_330597_.m_319293_(DataComponents.f_314304_);
        if (seededcontainerloot != null) {
            this.lootTable = seededcontainerloot.f_314778_();
            this.lootTableSeed = seededcontainerloot.f_314296_();
        }
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_329123_) {
        super.m_318837_(p_329123_);
        if (this.lootTable != null) {
            p_329123_.m_322739_(DataComponents.f_314304_, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
        }
    }

    @Override
    public void m_318942_(CompoundTag p_331651_) {
        super.m_318942_(p_331651_);
        p_331651_.remove("LootTable");
        p_331651_.remove("LootTableSeed");
    }
}