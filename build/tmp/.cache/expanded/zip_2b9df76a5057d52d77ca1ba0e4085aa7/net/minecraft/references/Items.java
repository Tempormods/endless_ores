package net.minecraft.references;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class Items {
    public static final ResourceKey<Item> f_303496_ = m_304674_("pumpkin_seeds");
    public static final ResourceKey<Item> f_302293_ = m_304674_("melon_seeds");

    private static ResourceKey<Item> m_304674_(String p_311525_) {
        return ResourceKey.create(Registries.ITEM, new ResourceLocation(p_311525_));
    }
}