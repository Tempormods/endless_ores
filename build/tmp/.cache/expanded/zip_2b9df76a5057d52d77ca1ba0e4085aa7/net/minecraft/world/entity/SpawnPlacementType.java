package net.minecraft.world.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;

public interface SpawnPlacementType {
    boolean m_322997_(LevelReader p_329488_, BlockPos p_335385_, @Nullable EntityType<?> p_329870_);

    default BlockPos m_321419_(LevelReader p_331949_, BlockPos p_333622_) {
        return p_333622_;
    }
}