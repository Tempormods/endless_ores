package net.minecraft.advancements.critereon;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderGetter;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class CriterionValidator {
    private final ProblemReporter f_303761_;
    private final HolderGetter.Provider f_302730_;

    public CriterionValidator(ProblemReporter p_311865_, HolderGetter.Provider p_329172_) {
        this.f_303761_ = p_311865_;
        this.f_302730_ = p_329172_;
    }

    public void m_307484_(Optional<ContextAwarePredicate> p_311203_, String p_309703_) {
        p_311203_.ifPresent(p_312443_ -> this.m_304659_(p_312443_, p_309703_));
    }

    public void m_307251_(List<ContextAwarePredicate> p_310532_, String p_310219_) {
        this.m_305625_(p_310532_, LootContextParamSets.ADVANCEMENT_ENTITY, p_310219_);
    }

    public void m_304659_(ContextAwarePredicate p_310373_, String p_309633_) {
        this.m_306042_(p_310373_, LootContextParamSets.ADVANCEMENT_ENTITY, p_309633_);
    }

    public void m_306042_(ContextAwarePredicate p_311627_, LootContextParamSet p_312598_, String p_312977_) {
        p_311627_.m_305566_(new ValidationContext(this.f_303761_.m_306146_(p_312977_), p_312598_, this.f_302730_));
    }

    public void m_305625_(List<ContextAwarePredicate> p_309439_, LootContextParamSet p_311765_, String p_309737_) {
        for (int i = 0; i < p_309439_.size(); i++) {
            ContextAwarePredicate contextawarepredicate = p_309439_.get(i);
            contextawarepredicate.m_305566_(new ValidationContext(this.f_303761_.m_306146_(p_309737_ + "[" + i + "]"), p_311765_, this.f_302730_));
        }
    }
}