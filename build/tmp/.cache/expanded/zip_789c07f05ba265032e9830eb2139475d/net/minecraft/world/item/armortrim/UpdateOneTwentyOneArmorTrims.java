package net.minecraft.world.item.armortrim;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.Items;

public class UpdateOneTwentyOneArmorTrims {
    public static void bootstrap(BootstrapContext<TrimPattern> p_332644_) {
        TrimPatterns.register(p_332644_, Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.FLOW);
        TrimPatterns.register(p_332644_, Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, TrimPatterns.BOLT);
    }
}