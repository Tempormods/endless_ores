package net.minecraft.world.entity;

import java.util.List;
import net.minecraft.world.phys.Vec3;

public enum EntityAttachment {
    PASSENGER(EntityAttachment.Fallback.f_314998_),
    VEHICLE(EntityAttachment.Fallback.f_316255_),
    NAME_TAG(EntityAttachment.Fallback.f_314998_),
    WARDEN_CHEST(EntityAttachment.Fallback.f_314384_);

    private final EntityAttachment.Fallback f_315804_;

    private EntityAttachment(final EntityAttachment.Fallback p_333642_) {
        this.f_315804_ = p_333642_;
    }

    public List<Vec3> m_323391_(float p_330294_, float p_328764_) {
        return this.f_315804_.m_320439_(p_330294_, p_328764_);
    }

    public interface Fallback {
        List<Vec3> f_315390_ = List.of(Vec3.ZERO);
        EntityAttachment.Fallback f_316255_ = (p_331269_, p_331409_) -> f_315390_;
        EntityAttachment.Fallback f_314998_ = (p_331649_, p_328299_) -> List.of(new Vec3(0.0, (double)p_328299_, 0.0));
        EntityAttachment.Fallback f_314384_ = (p_331512_, p_335776_) -> List.of(new Vec3(0.0, (double)p_335776_ / 2.0, 0.0));

        List<Vec3> m_320439_(float p_333086_, float p_331694_);
    }
}