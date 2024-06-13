package net.minecraft.world.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class EntityAttachments {
    private final Map<EntityAttachment, List<Vec3>> f_315816_;

    EntityAttachments(Map<EntityAttachment, List<Vec3>> p_331204_) {
        this.f_315816_ = p_331204_;
    }

    public static EntityAttachments m_319952_(float p_329032_, float p_333755_) {
        return m_321590_().m_318758_(p_329032_, p_333755_);
    }

    public static EntityAttachments.Builder m_321590_() {
        return new EntityAttachments.Builder();
    }

    public EntityAttachments m_322872_(float p_332347_, float p_335416_, float p_329295_) {
        Map<EntityAttachment, List<Vec3>> map = new EnumMap<>(EntityAttachment.class);

        for (Entry<EntityAttachment, List<Vec3>> entry : this.f_315816_.entrySet()) {
            map.put(entry.getKey(), m_323539_(entry.getValue(), p_332347_, p_335416_, p_329295_));
        }

        return new EntityAttachments(map);
    }

    private static List<Vec3> m_323539_(List<Vec3> p_333569_, float p_336335_, float p_333811_, float p_329631_) {
        List<Vec3> list = new ArrayList<>(p_333569_.size());

        for (Vec3 vec3 : p_333569_) {
            list.add(vec3.multiply((double)p_336335_, (double)p_333811_, (double)p_329631_));
        }

        return list;
    }

    @Nullable
    public Vec3 m_318717_(EntityAttachment p_327874_, int p_334745_, float p_333621_) {
        List<Vec3> list = this.f_315816_.get(p_327874_);
        return p_334745_ >= 0 && p_334745_ < list.size() ? m_323891_(list.get(p_334745_), p_333621_) : null;
    }

    public Vec3 m_324387_(EntityAttachment p_329241_, int p_328790_, float p_333537_) {
        Vec3 vec3 = this.m_318717_(p_329241_, p_328790_, p_333537_);
        if (vec3 == null) {
            throw new IllegalStateException("Had no attachment point of type: " + p_329241_ + " for index: " + p_328790_);
        } else {
            return vec3;
        }
    }

    public Vec3 m_321580_(EntityAttachment p_332337_, int p_333181_, float p_335290_) {
        List<Vec3> list = this.f_315816_.get(p_332337_);
        if (list.isEmpty()) {
            throw new IllegalStateException("Had no attachment points of type: " + p_332337_);
        } else {
            Vec3 vec3 = list.get(Mth.clamp(p_333181_, 0, list.size() - 1));
            return m_323891_(vec3, p_335290_);
        }
    }

    private static Vec3 m_323891_(Vec3 p_329033_, float p_331796_) {
        return p_329033_.yRot(-p_331796_ * (float) (Math.PI / 180.0));
    }

    public static class Builder {
        private final Map<EntityAttachment, List<Vec3>> f_316923_ = new EnumMap<>(EntityAttachment.class);

        Builder() {
        }

        public EntityAttachments.Builder m_322342_(EntityAttachment p_333943_, float p_333061_, float p_333157_, float p_328995_) {
            return this.m_319738_(p_333943_, new Vec3((double)p_333061_, (double)p_333157_, (double)p_328995_));
        }

        public EntityAttachments.Builder m_319738_(EntityAttachment p_328839_, Vec3 p_328743_) {
            this.f_316923_.computeIfAbsent(p_328839_, p_333992_ -> new ArrayList<>(1)).add(p_328743_);
            return this;
        }

        public EntityAttachments m_318758_(float p_334466_, float p_334856_) {
            Map<EntityAttachment, List<Vec3>> map = new EnumMap<>(EntityAttachment.class);

            for (EntityAttachment entityattachment : EntityAttachment.values()) {
                List<Vec3> list = this.f_316923_.get(entityattachment);
                map.put(entityattachment, list != null ? List.copyOf(list) : entityattachment.m_323391_(p_334466_, p_334856_));
            }

            return new EntityAttachments(map);
        }
    }
}