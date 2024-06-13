package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KnownPacksManager {
    private final PackRepository f_316908_ = ServerPacksSource.createVanillaTrustedRepository();
    private final Map<KnownPack, String> f_315511_;

    public KnownPacksManager() {
        this.f_316908_.reload();
        Builder<KnownPack, String> builder = ImmutableMap.builder();
        this.f_316908_.getAvailablePacks().forEach(p_334709_ -> {
            PackLocationInfo packlocationinfo = p_334709_.m_320537_();
            packlocationinfo.f_314017_().ifPresent(p_333246_ -> builder.put(p_333246_, packlocationinfo.f_316372_()));
        });
        this.f_315511_ = builder.build();
    }

    public List<KnownPack> m_323907_(List<KnownPack> p_332560_) {
        List<KnownPack> list = new ArrayList<>(p_332560_.size());
        List<String> list1 = new ArrayList<>(p_332560_.size());

        for (KnownPack knownpack : p_332560_) {
            String s = this.f_315511_.get(knownpack);
            if (s != null) {
                list1.add(s);
                list.add(knownpack);
            }
        }

        this.f_316908_.setSelected(list1);
        return list;
    }

    public CloseableResourceManager m_323502_() {
        List<PackResources> list = this.f_316908_.openAllSelected();
        return new MultiPackResourceManager(PackType.SERVER_DATA, list);
    }
}