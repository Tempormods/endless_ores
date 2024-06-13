package net.minecraft.world.level.block.entity;

import net.minecraft.data.worldgen.BootstrapContext;

public interface UpdateOneTwentyOneBannerPatterns {
    static void bootstrap(BootstrapContext<BannerPattern> p_328119_) {
        BannerPatterns.register(p_328119_, BannerPatterns.FLOW);
        BannerPatterns.register(p_328119_, BannerPatterns.GUSTER);
    }
}