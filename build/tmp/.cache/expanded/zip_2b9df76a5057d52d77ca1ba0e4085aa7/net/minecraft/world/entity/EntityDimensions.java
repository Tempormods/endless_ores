package net.minecraft.world.entity;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public record EntityDimensions(float width, float height, float f_316573_, EntityAttachments f_315362_, boolean fixed) {
    private EntityDimensions(float pWidth, float pHeight, boolean pFixed) {
        this(pWidth, pHeight, m_321612_(pHeight), EntityAttachments.m_319952_(pWidth, pHeight), pFixed);
    }

    private static float m_321612_(float p_331315_) {
        return p_331315_ * 0.85F;
    }

    public AABB makeBoundingBox(Vec3 pPos) {
        return this.makeBoundingBox(pPos.x, pPos.y, pPos.z);
    }

    public AABB makeBoundingBox(double pX, double pY, double pZ) {
        float f = this.width / 2.0F;
        float f1 = this.height;
        return new AABB(pX - (double)f, pY, pZ - (double)f, pX + (double)f, pY + (double)f1, pZ + (double)f);
    }

    public EntityDimensions scale(float pFactor) {
        return this.scale(pFactor, pFactor);
    }

    public EntityDimensions scale(float pWidthFactor, float pHeightFactor) {
        return !this.fixed && (pWidthFactor != 1.0F || pHeightFactor != 1.0F)
            ? new EntityDimensions(
                this.width * pWidthFactor, this.height * pHeightFactor, this.f_316573_ * pHeightFactor, this.f_315362_.m_322872_(pWidthFactor, pHeightFactor, pWidthFactor), false
            )
            : this;
    }

    public static EntityDimensions scalable(float pWidth, float pHeight) {
        return new EntityDimensions(pWidth, pHeight, false);
    }

    public static EntityDimensions fixed(float pWidth, float pHeight) {
        return new EntityDimensions(pWidth, pHeight, true);
    }

    public EntityDimensions m_320568_(float p_333362_) {
        return new EntityDimensions(this.width, this.height, p_333362_, this.f_315362_, this.fixed);
    }

    public EntityDimensions m_323271_(EntityAttachments.Builder p_328127_) {
        return new EntityDimensions(this.width, this.height, this.f_316573_, p_328127_.m_318758_(this.width, this.height), this.fixed);
    }
}