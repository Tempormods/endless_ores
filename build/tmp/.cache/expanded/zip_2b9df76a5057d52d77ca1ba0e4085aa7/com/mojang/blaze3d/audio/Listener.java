package com.mojang.blaze3d.audio;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.openal.AL10;

/**
 * The Listener class represents the listener in a 3D audio environment.
 * 
 * The listener's position and orientation determine how sounds are perceived by the listener.
 */
@OnlyIn(Dist.CLIENT)
public class Listener {
    private float gain = 1.0F;
    private ListenerTransform f_303580_ = ListenerTransform.f_303103_;

    public void m_306044_(ListenerTransform p_312167_) {
        this.f_303580_ = p_312167_;
        Vec3 vec3 = p_312167_.f_303699_();
        Vec3 vec31 = p_312167_.f_302679_();
        Vec3 vec32 = p_312167_.f_303730_();
        AL10.alListener3f(4100, (float)vec3.x, (float)vec3.y, (float)vec3.z);
        AL10.alListenerfv(
            4111,
            new float[]{
                (float)vec31.x, (float)vec31.y, (float)vec31.z, (float)vec32.x(), (float)vec32.y(), (float)vec32.z()
            }
        );
    }

    public void setGain(float pGain) {
        AL10.alListenerf(4106, pGain);
        this.gain = pGain;
    }

    public float getGain() {
        return this.gain;
    }

    public void reset() {
        this.m_306044_(ListenerTransform.f_303103_);
    }

    public ListenerTransform m_306415_() {
        return this.f_303580_;
    }
}