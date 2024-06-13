package net.minecraft.world.level.block.entity;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.ticks.ContainerSingleItem;

public class DecoratedPotBlockEntity extends BlockEntity implements RandomizableContainer, ContainerSingleItem.BlockContainerSingleItem {
    public static final String TAG_SHERDS = "sherds";
    public static final String f_302443_ = "item";
    public static final int f_302211_ = 1;
    public long f_303253_;
    @Nullable
    public DecoratedPotBlockEntity.WobbleStyle f_302709_;
    private PotDecorations decorations;
    private ItemStack f_302494_ = ItemStack.EMPTY;
    @Nullable
    protected ResourceKey<LootTable> f_302912_;
    protected long f_303124_;

    public DecoratedPotBlockEntity(BlockPos pPos, BlockState pState) {
        super(BlockEntityType.DECORATED_POT, pPos, pState);
        this.decorations = PotDecorations.f_316418_;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_327915_) {
        super.saveAdditional(pTag, p_327915_);
        this.decorations.m_319081_(pTag);
        if (!this.m_306148_(pTag) && !this.f_302494_.isEmpty()) {
            pTag.put("item", this.f_302494_.save(p_327915_));
        }
    }

    @Override
    protected void m_318667_(CompoundTag p_332304_, HolderLookup.Provider p_334010_) {
        super.m_318667_(p_332304_, p_334010_);
        this.decorations = PotDecorations.m_319296_(p_332304_);
        if (!this.m_307714_(p_332304_)) {
            if (p_332304_.contains("item", 10)) {
                this.f_302494_ = ItemStack.m_323951_(p_334010_, p_332304_.getCompound("item")).orElse(ItemStack.EMPTY);
            } else {
                this.f_302494_ = ItemStack.EMPTY;
            }
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_334226_) {
        return this.m_320696_(p_334226_);
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public PotDecorations getDecorations() {
        return this.decorations;
    }

    public void setFromItem(ItemStack pItem) {
        this.m_322533_(pItem);
    }

    public ItemStack getItem() {
        ItemStack itemstack = Items.DECORATED_POT.getDefaultInstance();
        itemstack.m_323474_(this.m_321843_());
        return itemstack;
    }

    public static ItemStack createDecoratedPotItem(PotDecorations p_331852_) {
        ItemStack itemstack = Items.DECORATED_POT.getDefaultInstance();
        itemstack.m_322496_(DataComponents.f_316536_, p_331852_);
        return itemstack;
    }

    @Nullable
    @Override
    public ResourceKey<LootTable> m_305426_() {
        return this.f_302912_;
    }

    @Override
    public void setLootTable(@Nullable ResourceKey<LootTable> p_334371_) {
        this.f_302912_ = p_334371_;
    }

    @Override
    public long m_305628_() {
        return this.f_303124_;
    }

    @Override
    public void m_305699_(long p_311200_) {
        this.f_303124_ = p_311200_;
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_333422_) {
        super.m_318837_(p_333422_);
        p_333422_.m_322739_(DataComponents.f_316536_, this.decorations);
        p_333422_.m_322739_(DataComponents.f_316065_, ItemContainerContents.m_320241_(List.of(this.f_302494_)));
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_336045_) {
        super.m_318741_(p_336045_);
        this.decorations = p_336045_.m_319031_(DataComponents.f_316536_, PotDecorations.f_316418_);
        this.f_302494_ = p_336045_.m_319031_(DataComponents.f_316065_, ItemContainerContents.f_316619_).m_322549_();
    }

    @Override
    public void m_318942_(CompoundTag p_332438_) {
        super.m_318942_(p_332438_);
        p_332438_.remove("sherds");
        p_332438_.remove("item");
    }

    @Override
    public ItemStack m_306082_() {
        this.m_306438_(null);
        return this.f_302494_;
    }

    @Override
    public ItemStack m_305214_(int p_313165_) {
        this.m_306438_(null);
        ItemStack itemstack = this.f_302494_.split(p_313165_);
        if (this.f_302494_.isEmpty()) {
            this.f_302494_ = ItemStack.EMPTY;
        }

        return itemstack;
    }

    @Override
    public void m_305072_(ItemStack p_310130_) {
        this.m_306438_(null);
        this.f_302494_ = p_310130_;
    }

    @Override
    public BlockEntity m_304707_() {
        return this;
    }

    public void m_304770_(DecoratedPotBlockEntity.WobbleStyle p_312241_) {
        if (this.level != null && !this.level.isClientSide()) {
            this.level.blockEvent(this.getBlockPos(), this.getBlockState().getBlock(), 1, p_312241_.ordinal());
        }
    }

    @Override
    public boolean triggerEvent(int p_309634_, int p_310889_) {
        if (this.level != null && p_309634_ == 1 && p_310889_ >= 0 && p_310889_ < DecoratedPotBlockEntity.WobbleStyle.values().length) {
            this.f_303253_ = this.level.getGameTime();
            this.f_302709_ = DecoratedPotBlockEntity.WobbleStyle.values()[p_310889_];
            return true;
        } else {
            return super.triggerEvent(p_309634_, p_310889_);
        }
    }

    public static enum WobbleStyle {
        POSITIVE(7),
        NEGATIVE(10);

        public final int f_303394_;

        private WobbleStyle(final int p_311481_) {
            this.f_303394_ = p_311481_;
        }
    }
}