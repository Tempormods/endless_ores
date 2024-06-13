package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.core.HolderGetter;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

/**
 * Context for validating loot tables. Loot tables are validated recursively by checking that all functions, conditions,
 * etc. (implementing {@link LootContextUser}) are valid according to their LootTable's {@link LootContextParamSet}.
 */
public class ValidationContext {
    private final ProblemReporter f_302601_;
    private final LootContextParamSet params;
    private final HolderGetter.Provider resolver;
    private final Set<ResourceKey<?>> visitedElements;

    public ValidationContext(ProblemReporter p_312350_, LootContextParamSet pParams, HolderGetter.Provider p_331032_) {
        this(p_312350_, pParams, p_331032_, Set.of());
    }

    private ValidationContext(ProblemReporter p_310867_, LootContextParamSet pParams, HolderGetter.Provider p_330853_, Set<ResourceKey<?>> p_311231_) {
        this.f_302601_ = p_310867_;
        this.params = pParams;
        this.resolver = p_330853_;
        this.visitedElements = p_311231_;
    }

    public ValidationContext forChild(String pChildName) {
        return new ValidationContext(this.f_302601_.m_306146_(pChildName), this.params, this.resolver, this.visitedElements);
    }

    public ValidationContext enterElement(String pChildName, ResourceKey<?> p_331211_) {
        Set<ResourceKey<?>> set = ImmutableSet.<ResourceKey<?>>builder().addAll(this.visitedElements).add(p_331211_).build();
        return new ValidationContext(this.f_302601_.m_306146_(pChildName), this.params, this.resolver, set);
    }

    public boolean hasVisitedElement(ResourceKey<?> p_335461_) {
        return this.visitedElements.contains(p_335461_);
    }

    public void reportProblem(String pProblem) {
        this.f_302601_.m_305802_(pProblem);
    }

    public void validateUser(LootContextUser pLootContextUser) {
        this.params.validateUser(this, pLootContextUser);
    }

    public HolderGetter.Provider resolver() {
        return this.resolver;
    }

    public ValidationContext setParams(LootContextParamSet pParams) {
        return new ValidationContext(this.f_302601_, pParams, this.resolver, this.visitedElements);
    }
}