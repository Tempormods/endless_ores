package net.minecraft.advancements;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record AdvancementHolder(ResourceLocation id, Advancement value) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancementHolder> f_316277_ = StreamCodec.m_320349_(
        ResourceLocation.f_314488_, AdvancementHolder::id, Advancement.f_315156_, AdvancementHolder::value, AdvancementHolder::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, List<AdvancementHolder>> f_313948_ = f_316277_.m_321801_(ByteBufCodecs.m_324765_());

    @Override
    public boolean equals(Object pOther) {
        if (this == pOther) {
            return true;
        } else {
            if (pOther instanceof AdvancementHolder advancementholder && this.id.equals(advancementholder.id)) {
                return true;
            }

            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}