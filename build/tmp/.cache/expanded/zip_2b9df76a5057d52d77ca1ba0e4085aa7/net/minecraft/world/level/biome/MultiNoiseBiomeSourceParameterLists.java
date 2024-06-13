package net.minecraft.world.level.biome;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class MultiNoiseBiomeSourceParameterLists {
    public static final ResourceKey<MultiNoiseBiomeSourceParameterList> NETHER = register("nether");
    public static final ResourceKey<MultiNoiseBiomeSourceParameterList> OVERWORLD = register("overworld");

    public static void bootstrap(BootstrapContext<MultiNoiseBiomeSourceParameterList> p_327712_) {
        HolderGetter<Biome> holdergetter = p_327712_.m_255434_(Registries.BIOME);
        p_327712_.m_321889_(NETHER, new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.NETHER, holdergetter));
        p_327712_.m_321889_(OVERWORLD, new MultiNoiseBiomeSourceParameterList(MultiNoiseBiomeSourceParameterList.Preset.OVERWORLD, holdergetter));
    }

    private static ResourceKey<MultiNoiseBiomeSourceParameterList> register(String pName) {
        return ResourceKey.create(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, new ResourceLocation(pName));
    }
}