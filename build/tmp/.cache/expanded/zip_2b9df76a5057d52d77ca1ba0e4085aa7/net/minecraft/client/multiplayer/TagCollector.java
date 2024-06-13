package net.minecraft.client.multiplayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TagCollector {
    private final Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> f_315318_ = new HashMap<>();

    public void m_322781_(ResourceKey<? extends Registry<?>> p_327817_, TagNetworkSerialization.NetworkPayload p_332646_) {
        this.f_315318_.put(p_327817_, p_332646_);
    }

    private static void m_319026_() {
        CreativeModeTabs.allTabs().stream().filter(tab -> tab.hasSearchBar()).forEach(tab -> tab.rebuildSearchTree());
    }

    private static void m_319038_() {
        AbstractFurnaceBlockEntity.m_323223_();
        Blocks.rebuildCache();
    }

    private void m_322703_(RegistryAccess p_327703_, Predicate<ResourceKey<? extends Registry<?>>> p_334924_) {
        this.f_315318_.forEach((p_335891_, p_332296_) -> {
            if (p_334924_.test((ResourceKey<? extends Registry<?>>)p_335891_)) {
                p_332296_.m_323556_(p_327703_.registryOrThrow((ResourceKey<? extends Registry<?>>)p_335891_));
            }
        });
    }

    public void m_324385_(RegistryAccess p_333230_, boolean p_331570_) {
        if (p_331570_) {
            this.m_322703_(p_333230_, RegistrySynchronization.NETWORKABLE_REGISTRIES::contains);
        } else {
            p_333230_.registries()
                .filter(p_331412_ -> !RegistrySynchronization.NETWORKABLE_REGISTRIES.contains(p_331412_.key()))
                .forEach(p_328076_ -> p_328076_.value().resetTags());
            this.m_322703_(p_333230_, p_328746_ -> true);
            m_319038_();
        }

        m_319026_();
    }
}