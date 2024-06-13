package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RegistryDataCollector {
    @Nullable
    private RegistryDataCollector.ContentsCollector f_315664_;
    @Nullable
    private TagCollector f_315374_;

    public void m_320164_(ResourceKey<? extends Registry<?>> p_331647_, List<RegistrySynchronization.PackedRegistryEntry> p_327881_) {
        if (this.f_315664_ == null) {
            this.f_315664_ = new RegistryDataCollector.ContentsCollector();
        }

        this.f_315664_.m_319872_(p_331647_, p_327881_);
    }

    public void m_319875_(Map<ResourceKey<? extends Registry<?>>, TagNetworkSerialization.NetworkPayload> p_329188_) {
        if (this.f_315374_ == null) {
            this.f_315374_ = new TagCollector();
        }

        p_329188_.forEach(this.f_315374_::m_322781_);
    }

    public RegistryAccess.Frozen m_323733_(ResourceProvider p_333941_, RegistryAccess p_334865_, boolean p_328462_) {
        LayeredRegistryAccess<ClientRegistryLayer> layeredregistryaccess = ClientRegistryLayer.createRegistryAccess();
        RegistryAccess registryaccess;
        if (this.f_315664_ != null) {
            RegistryAccess.Frozen registryaccess$frozen = layeredregistryaccess.getAccessForLoading(ClientRegistryLayer.REMOTE);
            RegistryAccess.Frozen registryaccess$frozen1 = this.f_315664_.m_323338_(p_333941_, registryaccess$frozen).freeze();
            registryaccess = layeredregistryaccess.replaceFrom(ClientRegistryLayer.REMOTE, registryaccess$frozen1).compositeAccess();
        } else {
            registryaccess = p_334865_;
        }

        if (this.f_315374_ != null) {
            this.f_315374_.m_324385_(registryaccess, p_328462_);
        }

        return registryaccess.freeze();
    }

    @OnlyIn(Dist.CLIENT)
    static class ContentsCollector {
        private final Map<ResourceKey<? extends Registry<?>>, List<RegistrySynchronization.PackedRegistryEntry>> f_313963_ = new HashMap<>();

        public void m_319872_(ResourceKey<? extends Registry<?>> p_331127_, List<RegistrySynchronization.PackedRegistryEntry> p_331340_) {
            this.f_313963_.computeIfAbsent(p_331127_, p_332834_ -> new ArrayList<>()).addAll(p_331340_);
        }

        public RegistryAccess m_323338_(ResourceProvider p_331350_, RegistryAccess p_331174_) {
            return RegistryDataLoader.m_321840_(this.f_313963_, p_331350_, p_331174_, RegistryDataLoader.f_314951_);
        }
    }
}