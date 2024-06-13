package net.minecraft.world.level.chunk.storage;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record RegionStorageInfo(String f_314351_, ResourceKey<Level> f_316873_, String f_314842_) {
    public RegionStorageInfo m_324592_(String p_334043_) {
        return new RegionStorageInfo(this.f_314351_, this.f_316873_, this.f_314842_ + p_334043_);
    }
}