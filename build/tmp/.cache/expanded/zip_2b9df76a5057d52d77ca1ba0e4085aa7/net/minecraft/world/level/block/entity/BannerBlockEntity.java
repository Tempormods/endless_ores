package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractBannerBlock;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class BannerBlockEntity extends BlockEntity implements Nameable {
    private static final Logger f_315436_ = LogUtils.getLogger();
    public static final int MAX_PATTERNS = 6;
    private static final String TAG_PATTERNS = "patterns";
    @Nullable
    private Component name;
    private DyeColor baseColor;
    private BannerPatternLayers patterns = BannerPatternLayers.f_316086_;

    public BannerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.BANNER, pPos, pBlockState);
        this.baseColor = ((AbstractBannerBlock)pBlockState.getBlock()).getColor();
    }

    public BannerBlockEntity(BlockPos pPos, BlockState pBlockState, DyeColor pBaseColor) {
        this(pPos, pBlockState);
        this.baseColor = pBaseColor;
    }

    public void fromItem(ItemStack pStack, DyeColor pColor) {
        this.baseColor = pColor;
        this.m_322533_(pStack);
    }

    @Override
    public Component getName() {
        return (Component)(this.name != null ? this.name : Component.translatable("block.minecraft.banner"));
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.name;
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_329292_) {
        super.saveAdditional(pTag, p_329292_);
        if (!this.patterns.equals(BannerPatternLayers.f_316086_)) {
            pTag.put("patterns", BannerPatternLayers.f_315309_.encodeStart(p_329292_.m_318927_(NbtOps.INSTANCE), this.patterns).getOrThrow());
        }

        if (this.name != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.name, p_329292_));
        }
    }

    @Override
    protected void m_318667_(CompoundTag p_334165_, HolderLookup.Provider p_330621_) {
        super.m_318667_(p_334165_, p_330621_);
        if (p_334165_.contains("CustomName", 8)) {
            this.name = m_336414_(p_334165_.getString("CustomName"), p_330621_);
        }

        if (p_334165_.contains("patterns")) {
            BannerPatternLayers.f_315309_
                .parse(p_330621_.m_318927_(NbtOps.INSTANCE), p_334165_.get("patterns"))
                .resultOrPartial(p_331027_ -> f_315436_.error("Failed to parse banner patterns: '{}'", p_331027_))
                .ifPresent(p_332298_ -> this.patterns = p_332298_);
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_335241_) {
        return this.saveWithoutMetadata(p_335241_);
    }

    public BannerPatternLayers getPatterns() {
        return this.patterns;
    }

    public ItemStack getItem() {
        ItemStack itemstack = new ItemStack(BannerBlock.byColor(this.baseColor));
        itemstack.m_323474_(this.m_321843_());
        return itemstack;
    }

    public DyeColor getBaseColor() {
        return this.baseColor;
    }

    @Override
    protected void m_318741_(BlockEntity.DataComponentInput p_328647_) {
        super.m_318741_(p_328647_);
        this.patterns = p_328647_.m_319031_(DataComponents.f_314522_, BannerPatternLayers.f_316086_);
        this.name = p_328647_.m_319293_(DataComponents.f_316016_);
    }

    @Override
    protected void m_318837_(DataComponentMap.Builder p_332512_) {
        super.m_318837_(p_332512_);
        p_332512_.m_322739_(DataComponents.f_314522_, this.patterns);
        p_332512_.m_322739_(DataComponents.f_316016_, this.name);
    }

    @Override
    public void m_318942_(CompoundTag p_336055_) {
        p_336055_.remove("patterns");
        p_336055_.remove("CustomName");
    }
}