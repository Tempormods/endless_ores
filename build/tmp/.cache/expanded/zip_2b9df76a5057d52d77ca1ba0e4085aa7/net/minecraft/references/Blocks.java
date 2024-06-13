package net.minecraft.references;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class Blocks {
    public static final ResourceKey<Block> f_303347_ = m_306381_("pumpkin");
    public static final ResourceKey<Block> f_303375_ = m_306381_("pumpkin_stem");
    public static final ResourceKey<Block> f_303705_ = m_306381_("attached_pumpkin_stem");
    public static final ResourceKey<Block> f_303780_ = m_306381_("melon");
    public static final ResourceKey<Block> f_303757_ = m_306381_("melon_stem");
    public static final ResourceKey<Block> f_302763_ = m_306381_("attached_melon_stem");

    private static ResourceKey<Block> m_306381_(String p_311130_) {
        return ResourceKey.create(Registries.BLOCK, new ResourceLocation(p_311130_));
    }
}