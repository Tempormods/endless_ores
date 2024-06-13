package net.minecraft.world.level.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult.Error;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.TrialSpawnerBlock;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawner;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.slf4j.Logger;

public class TrialSpawnerBlockEntity extends BlockEntity implements Spawner, TrialSpawner.StateAccessor {
    private static final Logger f_303577_ = LogUtils.getLogger();
    private TrialSpawner f_303644_;

    public TrialSpawnerBlockEntity(BlockPos p_309527_, BlockState p_312341_) {
        super(BlockEntityType.f_303206_, p_309527_, p_312341_);
        PlayerDetector playerdetector = PlayerDetector.f_314248_;
        PlayerDetector.EntitySelector playerdetector$entityselector = PlayerDetector.EntitySelector.f_315930_;
        this.f_303644_ = new TrialSpawner(this, playerdetector, playerdetector$entityselector);
    }

    @Override
    protected void m_318667_(CompoundTag p_330602_, HolderLookup.Provider p_329868_) {
        super.m_318667_(p_330602_, p_329868_);
        if (p_330602_.contains("normal_config")) {
            CompoundTag compoundtag = p_330602_.getCompound("normal_config").copy();
            p_330602_.put("ominous_config", compoundtag.merge(p_330602_.getCompound("ominous_config")));
        }

        this.f_303644_.m_307687_().parse(NbtOps.INSTANCE, p_330602_).resultOrPartial(f_303577_::error).ifPresent(p_311010_ -> this.f_303644_ = p_311010_);
        if (this.level != null) {
            this.m_306374_();
        }
    }

    @Override
    protected void saveAdditional(CompoundTag p_310285_, HolderLookup.Provider p_332039_) {
        super.saveAdditional(p_310285_, p_332039_);
        this.f_303644_
            .m_307687_()
            .encodeStart(NbtOps.INSTANCE, this.f_303644_)
            .ifSuccess(p_312114_ -> p_310285_.merge((CompoundTag)p_312114_))
            .ifError(p_327324_ -> f_303577_.warn("Failed to encode TrialSpawner {}", p_327324_.message()));
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider p_335483_) {
        return this.f_303644_.m_305472_().m_307504_(this.getBlockState().getValue(TrialSpawnerBlock.f_303541_));
    }

    @Override
    public boolean onlyOpCanSetNbt() {
        return true;
    }

    @Override
    public void setEntityId(EntityType<?> p_312357_, RandomSource p_313173_) {
        this.f_303644_.m_305472_().m_307184_(this.f_303644_, p_313173_, p_312357_);
        this.setChanged();
    }

    public TrialSpawner m_307437_() {
        return this.f_303644_;
    }

    @Override
    public TrialSpawnerState m_306453_() {
        return !this.getBlockState().hasProperty(BlockStateProperties.f_302708_)
            ? TrialSpawnerState.INACTIVE
            : this.getBlockState().getValue(BlockStateProperties.f_302708_);
    }

    @Override
    public void m_305970_(Level p_313150_, TrialSpawnerState p_310751_) {
        this.setChanged();
        p_313150_.setBlockAndUpdate(this.worldPosition, this.getBlockState().setValue(BlockStateProperties.f_302708_, p_310751_));
    }

    @Override
    public void m_306374_() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }
}