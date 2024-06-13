package net.minecraft.world.level.block.entity;

import net.minecraft.world.Container;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

public interface Hopper extends Container {
    AABB f_315244_ = Block.box(0.0, 11.0, 0.0, 16.0, 32.0, 16.0).toAabbs().get(0);

    default AABB m_319170_() {
        return f_315244_;
    }

    double getLevelX();

    double getLevelY();

    double getLevelZ();

    boolean m_320496_();
}