package net.minecraft.world.scores;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

class PlayerScores {
    private final Reference2ObjectOpenHashMap<Objective, Score> f_302517_ = new Reference2ObjectOpenHashMap<>(16, 0.5F);

    @Nullable
    public Score m_307163_(Objective p_310183_) {
        return this.f_302517_.get(p_310183_);
    }

    public Score m_306863_(Objective p_310156_, Consumer<Score> p_310669_) {
        return this.f_302517_.computeIfAbsent(p_310156_, p_312480_ -> {
            Score score = new Score();
            p_310669_.accept(score);
            return score;
        });
    }

    public boolean m_305067_(Objective p_312444_) {
        return this.f_302517_.remove(p_312444_) != null;
    }

    public boolean m_307156_() {
        return !this.f_302517_.isEmpty();
    }

    public Object2IntMap<Objective> m_306675_() {
        Object2IntMap<Objective> object2intmap = new Object2IntOpenHashMap<>();
        this.f_302517_.forEach((p_309981_, p_312246_) -> object2intmap.put(p_309981_, p_312246_.m_305685_()));
        return object2intmap;
    }

    void m_305165_(Objective p_312005_, Score p_312306_) {
        this.f_302517_.put(p_312005_, p_312306_);
    }

    Map<Objective, Score> m_307678_() {
        return Collections.unmodifiableMap(this.f_302517_);
    }
}