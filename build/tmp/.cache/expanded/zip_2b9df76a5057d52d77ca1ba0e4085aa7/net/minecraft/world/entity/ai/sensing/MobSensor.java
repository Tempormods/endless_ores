package net.minecraft.world.entity.ai.sensing;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class MobSensor<T extends LivingEntity> extends Sensor<T> {
    private final BiPredicate<T, LivingEntity> f_316153_;
    private final Predicate<T> f_314543_;
    private final MemoryModuleType<Boolean> f_315603_;
    private final int f_316537_;

    public MobSensor(int p_333366_, BiPredicate<T, LivingEntity> p_329126_, Predicate<T> p_334546_, MemoryModuleType<Boolean> p_334716_, int p_331675_) {
        super(p_333366_);
        this.f_316153_ = p_329126_;
        this.f_314543_ = p_334546_;
        this.f_315603_ = p_334716_;
        this.f_316537_ = p_331675_;
    }

    @Override
    protected void doTick(ServerLevel p_332587_, T p_336316_) {
        if (!this.f_314543_.test(p_336316_)) {
            this.m_320330_(p_336316_);
        } else {
            this.m_321981_(p_336316_);
        }
    }

    @Override
    public Set<MemoryModuleType<?>> requires() {
        return Set.of(MemoryModuleType.NEAREST_LIVING_ENTITIES);
    }

    public void m_321981_(T p_333520_) {
        Optional<List<LivingEntity>> optional = p_333520_.getBrain().getMemory(MemoryModuleType.NEAREST_LIVING_ENTITIES);
        if (!optional.isEmpty()) {
            boolean flag = optional.get().stream().anyMatch(p_329312_ -> this.f_316153_.test(p_333520_, p_329312_));
            if (flag) {
                this.m_323189_(p_333520_);
            }
        }
    }

    public void m_323189_(T p_332120_) {
        p_332120_.getBrain().setMemoryWithExpiry(this.f_315603_, true, (long)this.f_316537_);
    }

    public void m_320330_(T p_336340_) {
        p_336340_.getBrain().eraseMemory(this.f_315603_);
    }
}