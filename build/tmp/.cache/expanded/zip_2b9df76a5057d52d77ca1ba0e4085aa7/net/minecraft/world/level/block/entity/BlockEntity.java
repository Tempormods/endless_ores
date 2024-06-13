package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public abstract class BlockEntity extends net.minecraftforge.common.capabilities.CapabilityProvider<BlockEntity> implements net.minecraftforge.common.extensions.IForgeBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final BlockEntityType<?> type;
    @Nullable
    protected Level level;
    protected final BlockPos worldPosition;
    protected boolean remove;
    private BlockState blockState;
    private DataComponentMap f_314183_ = DataComponentMap.f_314291_;

    public BlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(BlockEntity.class);
        this.type = pType;
        this.worldPosition = pPos.immutable();
        this.blockState = pBlockState;
        this.gatherCapabilities();
    }

    public static BlockPos getPosFromTag(CompoundTag pTag) {
        return new BlockPos(pTag.getInt("x"), pTag.getInt("y"), pTag.getInt("z"));
    }

    @Nullable
    public Level getLevel() {
        return this.level;
    }

    public void setLevel(Level pLevel) {
        this.level = pLevel;
    }

    public boolean hasLevel() {
        return this.level != null;
    }

    protected void m_318667_(CompoundTag p_331149_, HolderLookup.Provider p_333170_) {
        if (getCapabilities() != null && p_331149_.contains("ForgeCaps")) deserializeCaps(p_331149_.getCompound("ForgeCaps"));
    }

    public final void m_320998_(CompoundTag p_331756_, HolderLookup.Provider p_335164_) {
        this.m_318667_(p_331756_, p_335164_);
        BlockEntity.ComponentHelper.f_316981_
            .parse(p_335164_.m_318927_(NbtOps.INSTANCE), p_331756_)
            .resultOrPartial(p_327293_ -> LOGGER.warn("Failed to load components: {}", p_327293_))
            .ifPresent(p_327298_ -> this.f_314183_ = p_327298_);
    }

    public final void m_324273_(CompoundTag p_333694_, HolderLookup.Provider p_332017_) {
        this.m_318667_(p_333694_, p_332017_);
    }

    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_327783_) {
        if (getCapabilities() != null) pTag.put("ForgeCaps", serializeCaps());
    }

    public final CompoundTag saveWithFullMetadata(HolderLookup.Provider p_331193_) {
        CompoundTag compoundtag = this.saveWithoutMetadata(p_331193_);
        this.saveMetadata(compoundtag);
        return compoundtag;
    }

    public final CompoundTag saveWithId(HolderLookup.Provider p_332686_) {
        CompoundTag compoundtag = this.saveWithoutMetadata(p_332686_);
        this.saveId(compoundtag);
        return compoundtag;
    }

    public final CompoundTag saveWithoutMetadata(HolderLookup.Provider p_332372_) {
        CompoundTag compoundtag = new CompoundTag();
        this.saveAdditional(compoundtag, p_332372_);
        BlockEntity.ComponentHelper.f_316981_
            .encodeStart(p_332372_.m_318927_(NbtOps.INSTANCE), this.f_314183_)
            .resultOrPartial(p_327292_ -> LOGGER.warn("Failed to save components: {}", p_327292_))
            .ifPresent(p_327300_ -> compoundtag.merge((CompoundTag)p_327300_));
        return compoundtag;
    }

    public final CompoundTag m_320696_(HolderLookup.Provider p_333091_) {
        CompoundTag compoundtag = new CompoundTag();
        this.saveAdditional(compoundtag, p_333091_);
        return compoundtag;
    }

    public final CompoundTag m_319785_(HolderLookup.Provider p_334487_) {
        CompoundTag compoundtag = this.m_320696_(p_334487_);
        this.saveMetadata(compoundtag);
        return compoundtag;
    }

    private void saveId(CompoundTag pTag) {
        ResourceLocation resourcelocation = BlockEntityType.getKey(this.getType());
        if (resourcelocation == null) {
            throw new RuntimeException(this.getClass() + " is missing a mapping! This is a bug!");
        } else {
            pTag.putString("id", resourcelocation.toString());
        }
    }

    public static void addEntityType(CompoundTag pTag, BlockEntityType<?> pEntityType) {
        pTag.putString("id", BlockEntityType.getKey(pEntityType).toString());
    }

    public void saveToItem(ItemStack pStack, HolderLookup.Provider p_336381_) {
        CompoundTag compoundtag = this.m_320696_(p_336381_);
        this.m_318942_(compoundtag);
        BlockItem.setBlockEntityData(pStack, this.getType(), compoundtag);
        pStack.m_323474_(this.m_321843_());
    }

    private void saveMetadata(CompoundTag pTag) {
        this.saveId(pTag);
        pTag.putInt("x", this.worldPosition.getX());
        pTag.putInt("y", this.worldPosition.getY());
        pTag.putInt("z", this.worldPosition.getZ());
    }

    @Nullable
    public static BlockEntity loadStatic(BlockPos pPos, BlockState pState, CompoundTag pTag, HolderLookup.Provider p_336084_) {
        String s = pTag.getString("id");
        ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
        if (resourcelocation == null) {
            LOGGER.error("Block entity has invalid type: {}", s);
            return null;
        } else {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(resourcelocation).map(p_155240_ -> {
                try {
                    return p_155240_.create(pPos, pState);
                } catch (Throwable throwable) {
                    LOGGER.error("Failed to create block entity {}", s, throwable);
                    return null;
                }
            }).map(p_327297_ -> {
                try {
                    p_327297_.m_320998_(pTag, p_336084_);
                    return (BlockEntity)p_327297_;
                } catch (Throwable throwable) {
                    LOGGER.error("Failed to load data for block entity {}", s, throwable);
                    return null;
                }
            }).orElseGet(() -> {
                LOGGER.warn("Skipping BlockEntity with id {}", s);
                return null;
            });
        }
    }

    public void setChanged() {
        if (this.level != null) {
            setChanged(this.level, this.worldPosition, this.blockState);
        }
    }

    protected static void setChanged(Level pLevel, BlockPos pPos, BlockState pState) {
        pLevel.blockEntityChanged(pPos);
        if (!pState.isAir()) {
            pLevel.updateNeighbourForOutputSignal(pPos, pState.getBlock());
        }
    }

    public BlockPos getBlockPos() {
        return this.worldPosition;
    }

    public BlockState getBlockState() {
        return this.blockState;
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return null;
    }

    public CompoundTag getUpdateTag(HolderLookup.Provider p_329179_) {
        return new CompoundTag();
    }

    public boolean isRemoved() {
        return this.remove;
    }

    public void setRemoved() {
        this.remove = true;
        this.invalidateCaps();
        requestModelDataUpdate();
    }

    @Override
    public void onChunkUnloaded() {
        this.invalidateCaps();
    }

    public void clearRemoved() {
        this.remove = false;
    }

    public boolean triggerEvent(int pId, int pType) {
        return false;
    }

    public void fillCrashReportCategory(CrashReportCategory pReportCategory) {
        pReportCategory.setDetail("Name", () -> BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(this.getType()) + " // " + this.getClass().getCanonicalName());
        if (this.level != null) {
            CrashReportCategory.populateBlockDetails(pReportCategory, this.level, this.worldPosition, this.getBlockState());
            CrashReportCategory.populateBlockDetails(pReportCategory, this.level, this.worldPosition, this.level.getBlockState(this.worldPosition));
        }
    }

    public boolean onlyOpCanSetNbt() {
        return false;
    }

    public BlockEntityType<?> getType() {
        return this.type;
    }

    @Deprecated
    public void setBlockState(BlockState pBlockState) {
        this.blockState = pBlockState;
    }

    protected void m_318741_(BlockEntity.DataComponentInput p_330805_) {
    }

    public final void m_322533_(ItemStack p_328941_) {
        this.m_322221_(p_328941_.m_322741_(), p_328941_.m_324277_());
    }

    public final void m_322221_(DataComponentMap p_335232_, DataComponentPatch p_331646_) {
        final Set<DataComponentType<?>> set = new HashSet<>();
        set.add(DataComponents.f_316520_);
        final DataComponentMap datacomponentmap = PatchedDataComponentMap.m_322493_(p_335232_, p_331646_);
        this.m_318741_(new BlockEntity.DataComponentInput() {
            @Nullable
            @Override
            public <T> T m_319293_(DataComponentType<T> p_335233_) {
                set.add(p_335233_);
                return datacomponentmap.m_318834_(p_335233_);
            }

            @Override
            public <T> T m_319031_(DataComponentType<? extends T> p_334887_, T p_333244_) {
                set.add(p_334887_);
                return datacomponentmap.m_322806_(p_334887_, p_333244_);
            }
        });
        DataComponentPatch datacomponentpatch = p_331646_.m_318691_(set::contains);
        this.f_314183_ = datacomponentpatch.m_324808_().f_314173_();
    }

    protected void m_318837_(DataComponentMap.Builder p_328216_) {
    }

    @Deprecated
    public void m_318942_(CompoundTag p_334718_) {
    }

    public final DataComponentMap m_321843_() {
        DataComponentMap.Builder datacomponentmap$builder = DataComponentMap.m_323371_();
        datacomponentmap$builder.m_321974_(this.f_314183_);
        this.m_318837_(datacomponentmap$builder);
        return datacomponentmap$builder.m_318826_();
    }

    public DataComponentMap m_324356_() {
        return this.f_314183_;
    }

    public void m_323608_(DataComponentMap p_335672_) {
        this.f_314183_ = p_335672_;
    }

    @Nullable
    public static Component m_336414_(String p_336419_, HolderLookup.Provider p_336417_) {
        try {
            return Component.Serializer.fromJson(p_336419_, p_336417_);
        } catch (Exception exception) {
            LOGGER.warn("Failed to parse custom name from string '{}', discarding", p_336419_, exception);
            return null;
        }
    }

    static class ComponentHelper {
        public static final Codec<DataComponentMap> f_316981_ = DataComponentMap.f_315283_.optionalFieldOf("components", DataComponentMap.f_314291_).codec();

        private ComponentHelper() {
        }
    }

    protected interface DataComponentInput {
        @Nullable
        <T> T m_319293_(DataComponentType<T> p_332690_);

        <T> T m_319031_(DataComponentType<? extends T> p_330702_, T p_330858_);
    }
}
