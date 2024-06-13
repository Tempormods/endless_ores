package net.minecraft.world.entity.animal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public record FrogVariant(ResourceLocation texture) {
    public static final ResourceKey<FrogVariant> TEMPERATE = m_322036_("temperate");
    public static final ResourceKey<FrogVariant> WARM = m_322036_("warm");
    public static final ResourceKey<FrogVariant> COLD = m_322036_("cold");

    private static ResourceKey<FrogVariant> m_322036_(String p_332326_) {
        return ResourceKey.create(Registries.FROG_VARIANT, new ResourceLocation(p_332326_));
    }

    public static FrogVariant m_322724_(Registry<FrogVariant> p_331705_) {
        register(p_331705_, TEMPERATE, "textures/entity/frog/temperate_frog.png");
        register(p_331705_, WARM, "textures/entity/frog/warm_frog.png");
        return register(p_331705_, COLD, "textures/entity/frog/cold_frog.png");
    }

    private static FrogVariant register(Registry<FrogVariant> p_335641_, ResourceKey<FrogVariant> p_331676_, String pName) {
        return Registry.register(p_335641_, p_331676_, new FrogVariant(new ResourceLocation(pName)));
    }
}