package com.mojang.blaze3d.audio;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public record ListenerTransform(Vec3 f_303699_, Vec3 f_302679_, Vec3 f_303730_) {
    public static final ListenerTransform f_303103_ = new ListenerTransform(Vec3.ZERO, new Vec3(0.0, 0.0, -1.0), new Vec3(0.0, 1.0, 0.0));

    public Vec3 m_306092_() {
        return this.f_302679_.cross(this.f_303730_);
    }
}