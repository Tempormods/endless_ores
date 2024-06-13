package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.util.ExtraCodecs;

public record Criterion<T extends CriterionTriggerInstance>(CriterionTrigger<T> trigger, T triggerInstance) {
    private static final MapCodec<Criterion<?>> f_303322_ = ExtraCodecs.m_306181_(
        "trigger", "conditions", CriteriaTriggers.f_302311_, Criterion::trigger, Criterion::m_304948_
    );
    public static final Codec<Criterion<?>> f_303845_ = f_303322_.codec();

    private static <T extends CriterionTriggerInstance> Codec<Criterion<T>> m_304948_(CriterionTrigger<T> p_312894_) {
        return p_312894_.createInstance().xmap(p_309410_ -> new Criterion<>(p_312894_, (T)p_309410_), Criterion::triggerInstance);
    }
}