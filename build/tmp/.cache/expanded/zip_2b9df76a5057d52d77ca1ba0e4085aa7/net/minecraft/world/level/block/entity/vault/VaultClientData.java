package net.minecraft.world.level.block.entity.vault;

import net.minecraft.util.Mth;

public class VaultClientData {
    public static final float f_314110_ = 10.0F;
    private float f_316970_;
    private float f_315446_;

    VaultClientData() {
    }

    public float m_323260_() {
        return this.f_316970_;
    }

    public float m_319938_() {
        return this.f_315446_;
    }

    void m_320699_() {
        this.f_315446_ = this.f_316970_;
        this.f_316970_ = Mth.wrapDegrees(this.f_316970_ + 10.0F);
    }
}