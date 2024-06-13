package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

public class ComparatorBlockEntity extends BlockEntity {
    private int output;

    public ComparatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(BlockEntityType.COMPARATOR, pPos, pBlockState);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider p_328948_) {
        super.saveAdditional(pTag, p_328948_);
        pTag.putInt("OutputSignal", this.output);
    }

    @Override
    protected void m_318667_(CompoundTag p_334222_, HolderLookup.Provider p_329151_) {
        super.m_318667_(p_334222_, p_329151_);
        this.output = p_334222_.getInt("OutputSignal");
    }

    public int getOutputSignal() {
        return this.output;
    }

    public void setOutputSignal(int pOutput) {
        this.output = pOutput;
    }
}