package com.mojang.blaze3d.vertex;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tesselator {
    private static final int f_302531_ = 786432;
    private final BufferBuilder builder;
    @Nullable
    private static Tesselator f_302797_;

    public static void m_306948_() {
        RenderSystem.assertOnGameThreadOrInit();
        if (f_302797_ != null) {
            throw new IllegalStateException("Tesselator has already been initialized");
        } else {
            f_302797_ = new Tesselator();
        }
    }

    public static Tesselator getInstance() {
        RenderSystem.assertOnGameThreadOrInit();
        if (f_302797_ == null) {
            throw new IllegalStateException("Tesselator has not been initialized");
        } else {
            return f_302797_;
        }
    }

    public Tesselator(int pCapacity) {
        this.builder = new BufferBuilder(pCapacity);
    }

    public Tesselator() {
        this(786432);
    }

    public void end() {
        BufferUploader.drawWithShader(this.builder.end());
    }

    public BufferBuilder getBuilder() {
        return this.builder;
    }
}