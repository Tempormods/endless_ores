package net.minecraft.data.advancements;

import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

public interface AdvancementSubProvider {
    void generate(HolderLookup.Provider pRegistries, Consumer<AdvancementHolder> pWriter);

    static AdvancementHolder m_306985_(String p_312736_) {
        return Advancement.Builder.advancement().build(new ResourceLocation(p_312736_));
    }
}